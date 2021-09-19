package grondag.fermion.sc.concurrency;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.util.Mth;
import grondag.fermion.sc.unordered.AbstractUnorderedArrayList;


/**
 * Provides functionality similar to an array list, but with low overhead and high concurrency
 * for a specific set of use cases.
 * Intended for use where insertion and indexing are parallel but other housekeeping tasks will
 * only occur on a single thread, while nothing else is being done.
 * Iteration is not guaranteed to provide consistent results while addition or removal operations are ongoing. <p>
 *
 * It has *significant* limitations:
 *  1) Items can be added only at the end of the list, and only individually. Adding items is non-blocking.
 *  2) Items can be removed only by providing a predicate function to do so.
 *  3) Removal methods are NOT thread-safe.  Caller must ensure other methods are not called while removal is in progress.
 *  4) Insertion order is NOT maintained if items are removed.
 *
 *   * @author grondag
 *
 */

public class SimpleConcurrentList<T> implements Iterable<T>
{
	protected T[]  items;
	private final AtomicInteger size = new AtomicInteger(0);
	private int nextDeletionStartIndex = 0;

	private static final int DELETION_BATCH_SIZE = 1024;

	public static <V> SimpleConcurrentList<V> create(Class<V> clazz, boolean enablePerformanceCounting, String listName, PerformanceCollector perfCollector)
	{
		return enablePerformanceCounting ? new Instrumented<V>(clazz, listName, perfCollector) : new SimpleConcurrentList<V>(clazz);
	}

	public static <V> SimpleConcurrentList<V> create(Class<V> clazz, PerformanceCounter perfCounter)
	{
		return perfCounter == null ? new SimpleConcurrentList<V>(clazz) : new Instrumented<V>(clazz, perfCounter);
	}

	public SimpleConcurrentList(Class<T> clazz)
	{
		this(clazz, 16);
	}

	public SimpleConcurrentList(Class<T> clazz, int initialCapacity)
	{
		initialCapacity = Mth.smallestEncompassingPowerOfTwo(initialCapacity);
		@SuppressWarnings("unchecked")
		final T[] a = (T[]) Array.newInstance(clazz, initialCapacity);
		this.items = a;
	}

	public PerformanceCounter removalPerfCounter() { return null; }

	/**
	 * Current number of items in the list.  Note
	 * that it cannot be fully trusted as a limit
	 * for numeric operation due to concurrent updates.
	 */
	public int size()
	{
		return this.size.get();
	}

	public boolean isEmpty()
	{
		return this.size.get() == 0;
	}

	/**
	 * Adds item at end of list.
	 * Safe for concurrent use with other adds.
	 * Not safe for concurrent use with other operations.
	 * @param item Thing to add
	 */
	public void add(T item)
	{
		final int index = this.size.getAndIncrement();
		if(index < this.items.length)
		{
			items[index] = item;
		}
		else
		{
			synchronized(this)
			{
				if(index >= this.items.length)
				{
					final int newCapacity = this.items.length * 2;
					this.items = Arrays.copyOf(this.items, newCapacity);
				}
			}
			items[index] = item;
		}
	}

	/**
	 * Adds all item at end of list.
	 * Safe for concurrent use with other adds.
	 * Not safe for concurrent use with other operations.
	 */
	public void addAll(AbstractUnorderedArrayList<T> items)
	{
		final int endExclusive = this.size.addAndGet(items.size());
		if(endExclusive > this.items.length)
		{
			synchronized(this)
			{
				if(endExclusive > this.items.length)
				{
					final int newCapacity = Mth.smallestEncompassingPowerOfTwo(endExclusive);
					this.items = Arrays.copyOf(this.items, newCapacity);
				}
			}
		}
		items.copyToArray(this.items,  endExclusive - items.size());
	}

	public void addAll(final T[] itemsIn, final int startFrom, final int size)
	{
		final int endExclusive = this.size.addAndGet(size);
		if(endExclusive > this.items.length)
		{
			synchronized(this)
			{
				if(endExclusive > this.items.length)
				{
					final int newCapacity = Mth.smallestEncompassingPowerOfTwo(endExclusive);
					this.items = Arrays.copyOf(this.items, newCapacity);
				}
			}
		}
		System.arraycopy(itemsIn, startFrom, this.items, endExclusive - size, size);
	}

	public T get(int index)
	{
		return items[index];
	}

	/**
	 * Returns the backing array including null padding at end.
	 */
	public T[] getOperands()
	{
		return this.items;
	}

	/**
	 * Removes *some* deleted items in the list and compacts storage.
	 * Call periodically to keep list clean.
	 * ORDER OF ITEMS MAY CHANGE.
	 * NOT THREAD SAFE
	 * Caller must ensure no other methods are called while this method is ongoing.
	 */
	public void removeSomeDeletedItems(Predicate<T> trueIfDeleted)
	{
		if(this.size.get() == 0) return;

		// note - will not prevent add or iteration
		// so does not, by itself, ensure thread safety
		synchronized(this)
		{
			int newSize = this.size.get();
			int start;
			int end;

			if(newSize > DELETION_BATCH_SIZE)
			{
				start = this.nextDeletionStartIndex;
				if(start >= newSize) {
					start = 0;
				}

				end = start + DELETION_BATCH_SIZE;
				if(end >= newSize)
				{
					end = newSize;
					this.nextDeletionStartIndex = 0;
				}
				else
				{
					this.nextDeletionStartIndex = end;
				}
			}
			else
			{
				start = 0;
				end = newSize;
			}

			for(int i = start; i < newSize; i++)
			{
				final T item =  this.items[i];

				if(trueIfDeleted.test(item))
				{
					items[i] = items[--newSize];
					items[newSize] = null;
				}
			}

			this.size.set(newSize);
		}
	}

	/**
	 * Removes *all* deleted items in the list and compacts storage.
	 * Use when you can't have nulls in an operation about to happen.
	 * ORDER OF ITEMS MAY CHANGE.
	 * NOT THREAD SAFE
	 * Caller must ensure no other methods are called while this method is ongoing.
	 */
	public void removeAllDeletedItems(Predicate<T> trueIfDeleted)
	{
		if(this.size.get() == 0) return;

		// note - will not prevent add or iteration
		// so does not, by itself, ensure thread safety
		synchronized(this)
		{
			int newSize = this.size();

			for(int i = 0; i < newSize; i++)
			{
				final T item =  this.items[i];

				if(trueIfDeleted.test(item))
				{
					items[i] = items[--newSize];
					items[newSize] = null;
				}
			}

			this.size.set(newSize);
		}
	}

	/**
	 * Removes all items in the list, ensuring no references are held.
	 * NOT THREAD SAFE
	 * Caller must ensure no other methods are called while this method is ongoing.
	 */
	public void clear()
	{
		// note - will not prevent add or iteration
		// so does not, by itself, ensure thread safety
		synchronized(this)
		{
			if(this.size.get() != 0)
			{
				Arrays.fill(items, null);
				this.size.set(0);
			}
		}
	}

	public Stream<T> stream(boolean isParallel)
	{
		return StreamSupport.stream(Arrays.spliterator(items, 0, this.size.get()), isParallel);
	}

	/**
	 * May return null elements or be inconsistent if called during maintenance operations.
	 */
	@Override
	public Iterator<T> iterator()
	{
		return this.stream(false).iterator();
	}


	private static class Instrumented<T> extends SimpleConcurrentList<T>
	{
		private final PerformanceCounter removalPerfCounter;

		private Instrumented(Class<T> clazz, String listName, PerformanceCollector perfCollector)
		{
			super(clazz);
			this.removalPerfCounter = PerformanceCounter.create(true, listName + " list item removal", perfCollector);
		}

		private Instrumented(Class<T> clazz, PerformanceCounter perfCounter)
		{
			super(clazz);
			this.removalPerfCounter = perfCounter;
		}

		@Override
		public PerformanceCounter removalPerfCounter() { return this.removalPerfCounter; }

		@Override
		public void removeSomeDeletedItems(Predicate<T> trueIfDeleted)
		{
			final int startCount = size();
			this.removalPerfCounter.startRun();
			super.removeSomeDeletedItems(trueIfDeleted);
			this.removalPerfCounter.endRun();
			this.removalPerfCounter.addCount(startCount - size());
		}
	}

	public T[] toArray()
	{
		return Arrays.copyOf(this.items, this.size());
	}

	public T[] toArray(final int fromInclusive, final int toExclusive)
	{
		return Arrays.copyOfRange(this.items, fromInclusive, toExclusive);
	}
}
