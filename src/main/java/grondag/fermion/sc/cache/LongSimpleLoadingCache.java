package grondag.fermion.sc.cache;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import it.unimi.dsi.fastutil.HashCommon;


public class LongSimpleLoadingCache<V> implements ISimpleLoadingCache
{
	public final int capacity;
	public final int maxFill;
	protected final int positionMask;

	protected final LongSimpleCacheLoader<V> loader;

	private final AtomicInteger backupMissCount = new AtomicInteger(0);

	protected volatile LongCacheState<V> activeState;
	private final AtomicReference<LongCacheState<V>> backupState = new AtomicReference<LongCacheState<V>>();

	private final Object writeLock = new Object();

	public LongSimpleLoadingCache(LongSimpleCacheLoader<V> loader, int maxSize)
	{
		this.capacity = 1 << (Long.SIZE - Long.numberOfLeadingZeros((long) (maxSize / ISimpleLoadingCache.LOAD_FACTOR)));
		this.maxFill = (int) (capacity * ISimpleLoadingCache.LOAD_FACTOR);
		this.positionMask = capacity - 1;
		this.loader = loader;
		this.activeState = new LongCacheState<V>(this.capacity);
		this.clear();
	}

	@Override
	public int size() { return activeState.size.get(); }

	@Override
	public void clear()
	{
		this.activeState = new LongCacheState<V>(this.capacity);
	}

	public V get(long key)
	{
		final LongCacheState<V> localState = activeState;

		// Zero value normally indicates an unused spot in key array
		// so requires privileged handling to prevent search weirdness.
		if(key == 0)
		{
			V value = localState.zeroValue.get();
			if(value == null)
			{
				value = loader.load(0);
				assert value != null;
				if(localState.zeroValue.compareAndSet(null, value))
					return value;
				else
					//another thread got there first
					return localState.zeroValue.get();
			}
			return value;
		}

		int position = (int) (HashCommon.mix(key) & positionMask);

		do
		{
			if(localState.keys[position] == key)
			{
				V result = localState.values[position];
				// rare but because array members aren't volatile
				// can have the key from another thread but not the value (yet)
				if(result == null)
				{
					final V[] values = localState.values;
					result = values[position];
					assert result != null;
				}
				return result;
			}

			if(localState.keys[position] == 0)
				return load(localState, key, position);

			position = (position + 1) & positionMask;

		} while (true);
	}

	protected V loadFromBackup(LongCacheState<V> backup, final long key)
	{
		int position = (int) (HashCommon.mix(key) & positionMask);
		do
		{
			if(backup.keys[position] == key) return backup.values[position];
			if(backup.keys[position] == 0)
			{
				if((backupMissCount.incrementAndGet() & 0xFF) == 0xFF)
				{
					if(backupMissCount.get() > activeState.size.get() / 2)
					{
						backupState.compareAndSet(backup, null);
					}
				}
				return loader.load(key);
			}
			position = (position + 1) & positionMask;
		} while(true);
	}


	protected V load(LongCacheState<V> localState, long key, int position)
	{
		// no need to handle zero key here - is handled as privileged case in get();

		final LongCacheState<V> backupState = this.backupState.get();

		final V result = backupState == null ? loader.load(key) : loadFromBackup(backupState, key);
		assert result != null;

		do
		{
			long currentKey;
			synchronized(writeLock)
			{
				currentKey = localState.keys[position];
				if(currentKey == 0)
				{
					//write value start in case another thread tries to read it based on key before we can write it
					localState.values[position] = result;
					localState.keys[position] = key;
					break;
				}
			}

			// small chance another thread added our value before we got our lock
			if(currentKey == key)
			{
				assert localState.values[position] != null;
				return localState.values[position];
			}

			position = (position + 1) & positionMask;

		} while(true);

		if(localState.size.incrementAndGet() == this.maxFill)
		{
			final LongCacheState<V> newState = new LongCacheState<V>(this.capacity);
			// doing this means we don't have to handle zero value in backup cache value lookup
			newState.zeroValue.set(this.activeState.zeroValue.get());
			this.backupState.set(this.activeState);
			this.activeState = newState;
			this.backupMissCount.set(0);
		}
		assert result != null;
		return result;
	}

	/** for test harness */
	public LongSimpleLoadingCache<V> createNew(LongSimpleCacheLoader<V> loader, int startingCapacity)
	{
		return new LongSimpleLoadingCache<V>(loader, startingCapacity);
	}
}