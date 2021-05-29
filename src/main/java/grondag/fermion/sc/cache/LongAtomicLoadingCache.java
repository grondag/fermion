package grondag.fermion.sc.cache;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import it.unimi.dsi.fastutil.HashCommon;

import grondag.fermion.Fermion;

public class LongAtomicLoadingCache<V> implements ISimpleLoadingCache
{
	private final int capacity;
	private final int maxFill;
	protected final int positionMask;

	protected final LongSimpleCacheLoader<V> loader;

	private final AtomicInteger backupMissCount = new AtomicInteger(0);

	private static final VarHandle longArrayHandle =  MethodHandles.arrayElementVarHandle(long[].class);
	private static final VarHandle objArrayHandle =  MethodHandles.arrayElementVarHandle(Object[].class);

	protected volatile LongCacheState<V> activeState;
	private final AtomicReference<LongCacheState<V>> backupState = new AtomicReference<>();


	public LongAtomicLoadingCache(LongSimpleCacheLoader<V> loader, int maxSize)
	{
		this.capacity = 1 << (Long.SIZE - Long.numberOfLeadingZeros((long) (maxSize / ISimpleLoadingCache.LOAD_FACTOR)));
		this.maxFill = (int) (capacity * ISimpleLoadingCache.LOAD_FACTOR);
		this.positionMask = capacity - 1;
		this.loader = loader;
		this.activeState = new LongCacheState<>(this.capacity);
		this.clear();
	}

	@Override
	public int size() { return activeState.size.get(); }

	@Override
	public void clear()
	{
		this.activeState = new LongCacheState<>(this.capacity);
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
				if(localState.zeroValue.compareAndSet(null, value))
					return value;
				else
					//another thread got there start
					return localState.zeroValue.get();
			}
			return value;
		}

		int position = (int) (HashCommon.mix(key) & positionMask);

		do {
			final long currentKey = (long) longArrayHandle.getVolatile(localState.keys, position);

			if(currentKey == key) return getValueEventually(localState, position, key);

			if(currentKey == 0) return load(localState, key, position);

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

		do {
			if(longArrayHandle.compareAndSet(localState.keys, position, 0, key)) {
				objArrayHandle.setVolatile(localState.values, position, result);
				break;
			}

			// small chance another thread added our value before we got our lock
			if ((long) longArrayHandle.getVolatile(localState.keys, position) == key) {
				return getValueEventually(localState, position, key);
			}

			position = (position + 1) & positionMask;
		} while(true);

		if(localState.size.incrementAndGet() == this.maxFill) {
			final LongCacheState<V> newState = new LongCacheState<>(this.capacity);
			// doing this means we don't have to handle zero value in backup cache value lookup
			newState.zeroValue.set(this.activeState.zeroValue.get());
			this.backupState.set(this.activeState);
			this.activeState = newState;
			this.backupMissCount.set(0);
		}

		return result;
	}

	private V getValueEventually(LongCacheState<V> localState, int position, long key)
	{
		V result = (V) objArrayHandle.getVolatile(localState.values, position);
		if(result != null) return result;

		// Another thread has updated key but hasn't yet updated the value.
		// Should be very rare.  Retry several times until value appears.

		result = (V) objArrayHandle.getVolatile(localState.values, position);
		if(result != null) return result;

		result = (V) objArrayHandle.getVolatile(localState.values, position);
		if(result != null) return result;

		result = (V) objArrayHandle.getVolatile(localState.values, position);
		if(result != null) return result;

		result = (V) objArrayHandle.getVolatile(localState.values, position);
		if(result != null) return result;

		result = (V) objArrayHandle.getVolatile(localState.values, position);
		if(result != null) return result;

		result = (V) objArrayHandle.getVolatile(localState.values, position);
		if(result != null) return result;

		result = (V) objArrayHandle.getVolatile(localState.values, position);
		if(result != null) return result;

		result = (V) objArrayHandle.getVolatile(localState.values, position);
		if(result != null) return result;

		result = (V) objArrayHandle.getVolatile(localState.values, position);
		if(result != null) return result;

		result = (V) objArrayHandle.getVolatile(localState.values, position);
		if(result != null) return result;

		result = (V) objArrayHandle.getVolatile(localState.values, position);
		if(result != null) return result;

		assert trueButWarn();

		// abort and return loaded value directly
		return loader.load(key);
	}

	private static boolean trueButWarn() {
		Fermion.LOG.info("LongAtomicLoadingCache: returning new loaded value despite key hit because cached value not yet written by other thread.");
		return true;
	}

	// for test harness
	public LongAtomicLoadingCache<V> createNew(LongSimpleCacheLoader<V> loader, int startingCapacity)
	{
		return new LongAtomicLoadingCache<>(loader, startingCapacity);
	}
}