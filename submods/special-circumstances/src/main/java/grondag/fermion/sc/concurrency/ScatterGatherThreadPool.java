package grondag.fermion.sc.concurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import grondag.fermion.sc.Sc;
import grondag.fermion.sc.unordered.AbstractUnorderedArrayList;
import sun.misc.Unsafe;

/**
 * Thread pool optimized for scatter-gather processing patterns with an array, list or
 * other linearly-addressable data structure. Performance is equivalent to a Java fork-join
 * pool for large work loads and seems to have somewhat lower overhead and lower latency for small batches.<p>
 * 
 * The main motivation is simplicity: it is much easier (for the author at least) to understand and debug
 * than a custom counted-completer fork join task. (Based on actual experience doing creating same.)
 * It's also easier to use and requires less code for its intended use cases. <p>
 * 
 * The pool does not have a queue, and all calls to the various flavors of completeTask() are blocking.
 * This design is consistent with the scatter-gather patterns for which this pool is used - the intention
 * is to complete the entire task <em>now</em>, as quickly as possible, and then move on with another 
 * task that may depend on those results.<p>
 * 
 * Calls into the pool for execution are not thread-safe! (Again, no queue - it can only do one thing at a time.)
 * While usage could be externally synchronized, the intended usage pattern is to call into the pool
 * from a consumer thread that generates tasks dynamically and/or drain a queue of tasks into the pool.<p>
 * 
 * Size of the pool is always the system parallelism level, less one, because the calling thread is
 * recruited to do some of the work.<p>
 * 
 * Without a queue, there is no work stealing, however tasks are apportioned incrementally, with worker threads
 * claiming work only as they get scheduled.  Generally it will not be worthwhile to use the pool
 * unless the submitted tasks have enough tasks to keep all threads occupied. Some execution methods include
 * concurrency thresholds that, if not met, will simply execute the entire task on the calling thread so that
 * special case logic isn't needed in the calling code.<p>
 * 
 * A perfectly efficient pool would always have all threads finishing at the same time.
 * Even with dynamic work assignment, some thread will always finish some finite amount of time after
 * the other threads finish.  This waste can be minimized by slicing the work into smaller batches but
 * this comes with the price of increased overhead because shared state must be updated with each new batch.
 * Some execution parameters can be used to tune the batch size for a particular work load.<p>
 * 
 * Note this pool is <em>NOT</em> suitable as a generic thread pool for tasks that cannot be shared across
 * multiple cores and/or that are meant to be completed asynchronously. For that, the common ForkJoin pool, 
 * a fixed thread pool, dedicated threads, will all be better.
 */
@SuppressWarnings("restriction")
public class ScatterGatherThreadPool
{
    /**
     * Will be number of cores less one because calling thread also does work.
     */
    public static final int POOL_SIZE = Runtime.getRuntime().availableProcessors() - 1;
    
    /**
     * By default, each thread will have four "batches" of work it can pick up, assuming
     * each thread does equal work.  More likely, some threads will do more and some will do fewer.
     */
    public static final int DEFAULT_BATCHES_PER_THREAD = 4;

    /**
     * Total batches for all threads, including calling thread, unless a custom batch size is given.
     */
    public static final int DEFAULT_BATCH_COUNT = (POOL_SIZE + 1) * DEFAULT_BATCHES_PER_THREAD;
    
    /**
     * Arbitrary.  For quick tasks with large number of elements, larger numbers would be better.
     */
    public static final int DEFAULT_MINIMUM_TASKS_PER_BATCH = 64;
    
    /**
     * If using the default batch size, this the is number of task elements needed to make
     * scatter-gather worthwhile. Tasks with fewer elements will simply be run on the calling thread.
     */
    public static final int DEFAULT_CONCURRENCY_THRESHOLD = DEFAULT_BATCH_COUNT * DEFAULT_MINIMUM_TASKS_PER_BATCH;
    
    /**
     * Compute the number of elements per batch if no specific batch size is provided.
     * Will ensure there are exactly {@link #DEFAULT_BATCH_COUNT} batches, unless
     * the number of elements is less than that, in which case the batch size will be 1.
     */
    public static final int defaultBatchSize(final int elementCount)
    {
        return (elementCount + DEFAULT_BATCH_COUNT - 1) / DEFAULT_BATCH_COUNT;
    }
            
    public static interface ISharableTask
    {
        /**
         * Return true if more work remains. Must be safe
         * to call if there is no work or all work is done.<p>
         * 
         * The provided batch index is an atomically increasing zero-based
         * positive integer.  batchIndex 0 is always sent to the initiating thread.<p>
         * 
         * The task is responsible for knowing how many batches of work it has 
         * and must ignore (and return false) for batches beyond that range.<p>
         * 
         * The task is also responsible for knowing what state is affected 
         * by the batch identified by the given index and for managing the 
         * synchronization of shared state affected by processing the batch.
         */
        public boolean doSomeWork(int batchIndex);
        
        /**
         * Called on each thread after it has completed all work it
         * will do for the current task being executed. Meant as a hook
         * for aggregating results, clean up etc. Will not be
         * called if the thread did not participate, but cannot
         * guarantee the thread actually completed any work. 
         */
        public void onThreadComplete();
    }

    /**
     * Signals no task and guards against NPE from errant threads by doing nothing
     * and indicating no work if somehow called.
     */
    private static final ISharableTask DUMMY_TASK = new ISharableTask()
    {
        @Override
        public boolean doSomeWork(int batchIndex) { return false; }

        @Override
        public void onThreadComplete() { }
    };
    
    /**
     * Used here to avoid a pointer chase for the atomic batch counter at the core of the implementation.
     */
    private static final Unsafe UNSAFE = Danger.UNSAFE;
    
    /**
     * Unsafe address for atomic access to {@link #nextBatchIndex}
     */
    private static final long nextBatchIndexOffset;

    static
    {
        try 
        {
            nextBatchIndexOffset = UNSAFE.objectFieldOffset
                    (ScatterGatherThreadPool.class.getDeclaredField("nextBatchIndex"));
        } catch (Exception ex) { throw new Error(ex); }
    }
    
    /**
     * Keep references to worker threads for debugging.
     */
    @SuppressWarnings("unused")
    private final ImmutableList<Thread> threads;
    
    /**
     * Essentially a single-element work queue. Set to {@link #DUMMY_TASK} when empty.
     */
    private ISharableTask thingNeedingDone = DUMMY_TASK;
    
    /**
     * Signal for shutdown.
     */
    private boolean running = true;
    
    /**
     * Used to wake up worker threads when there is new work.
     */
    private final Object startLock = new Object();

    /**
     * Worker threads hold a read lock on this for as long as they are working. Calling
     * thread will block until it can get a write lock, meaning all worker threads
     * have completed.
     */
    private final ReadWriteLock completionLock = new ReentrantReadWriteLock();
    
    /**
     * Efficient access to write lock for calling thread.
     */
    private final Lock completionWriteLock = ScatterGatherThreadPool.this.completionLock.writeLock();
    
    /**
     * The core mechanism for dynamic work assignment.  Set to 1 at the start of each task
     * (because batch 0 is reserved for calling thread) and atomically incremented as workers
     * claim batches until the task is complete.
     */
    @SuppressWarnings("unused")
    private volatile int nextBatchIndex;
    
    public ScatterGatherThreadPool()
    {
        ImmutableList.Builder<Thread> builder = ImmutableList.builder();
        
        for(int i = 0; i < POOL_SIZE; i++)
        {
            Thread thread = new Thread(
                    new Worker(), 
                    "Exotic Matter Simulation Thread - " + i);
            thread.setDaemon(true);
            builder.add(thread);
            thread.start();
        }
        this.threads = builder.build();
    }
    
    /**
     * See {@link #nextBatchIndex}
     */
    private final int getNextBatchIndex()
    {
        return UNSAFE.getAndAddInt(this, nextBatchIndexOffset, 1);
    }
    
    /**
     * Signals worker threads to stop and immediately returns. 
     * Pool provides no means to be restarted once stopped.
     */
    public void stop()
    {
        this.running = false;
        synchronized(startLock)
        {
            startLock.notifyAll();
        }
    }
    
    private class Worker implements Runnable
    {
        @Override
        public void run()
        {
            final Object lock = ScatterGatherThreadPool.this.startLock;
            final Lock completionLock = ScatterGatherThreadPool.this.completionLock.readLock();
            
            while(running)
            {
                final ISharableTask t = thingNeedingDone;
                
                if(t != DUMMY_TASK)
                {
                    completionLock.lock();
                    try
                    { 
                        while(t.doSomeWork(getNextBatchIndex())) {};
                        t.onThreadComplete();
                    }
                    catch (Exception e) 
                    { 
                        Sc.LOG.error("Unhandled error during concurrent processing. Impact unknown.", e);
                    }
                    completionLock.unlock();
                }
                
                synchronized(lock)
                {
                    try
                    {
                        do
                        {
                            lock.wait();
                        } while (running && thingNeedingDone == DUMMY_TASK);
                    }
                    catch (InterruptedException e)  { }
                }
            }
        }
    }
    
    /**
     * Applies the given operation to every in-range element of the array.  If the number of elements to be
     * processed is less than the given concurrency threshold, the operations will happen on the calling thread.
     * In either case, will block until all elements are processed.<p>
     * 
     * Use larger batch sizes (and larger thresholds) for fast operations on many elements.  Use smaller values 
     * for long-running elements. 
     */
    public final <V> void completeTask (V[] inputs, int startIndex, int count, int concurrencyThreshold, Consumer<V> operation, int batchSize)
    {
        if(count <= concurrencyThreshold)
        {
            final int endIndex = startIndex + count;
            for(int i = startIndex; i < endIndex; i++)
            {
                operation.accept(inputs[i]);
            }
        }
        else
        {
            this.completeTask(new ArrayTask<>(inputs, startIndex, count, operation, batchSize));
        }
    }
   
    public final <V> void completeTask (V[] inputs, int startIndex, int count, int concurrencyThreshold, Consumer<V> operation)
    {
        completeTask(inputs, startIndex, count, concurrencyThreshold,  operation, defaultBatchSize(count));
    }
    
    public final <V> void completeTask(V[] inputs, int startIndex, int count, Consumer<V> operation)
    {
        completeTask(inputs, startIndex, count, DEFAULT_CONCURRENCY_THRESHOLD, operation, defaultBatchSize(count));
    }
    
    public final <V> void completeTask(V[] inputs, int startIndex, int count, Consumer<V> operation, int batchSize)
    {
        completeTask(inputs, startIndex, count, (POOL_SIZE + 1) * batchSize, operation, batchSize);
    }
    
    public final <V> void completeTask(V[] inputs, Consumer<V> operation)
    {
        completeTask(inputs, 0, inputs.length, operation);
    }
    
    public final <V> void completeTask(V[] inputs, Consumer<V> operation, int batchSize)
    {
        completeTask(inputs, 0, inputs.length, operation, batchSize);
    }
    
    public final <V> void completeTask(V[] inputs, int concurrencyThreshold, Consumer<V> operation)
    {
        completeTask(inputs, 0, inputs.length, concurrencyThreshold,  operation, defaultBatchSize(inputs.length));
    }
    
    public final <V> void completeTask(V[] inputs, int concurrencyThreshold, Consumer<V> operation, int batchSize)
    {
        completeTask(inputs, 0, inputs.length, concurrencyThreshold,  operation, batchSize);
    }
    
    public final <V> void completeTask(SimpleConcurrentList<V> list, int concurrencyThreshold, Consumer<V> operation)
    {
        completeTask(list.getOperands(), 0, list.size(), concurrencyThreshold, operation, defaultBatchSize(list.size()));
    }
    
    public final <V> void completeTask(SimpleConcurrentList<V> list, Consumer<V> operation)
    {
        completeTask(list.getOperands(), 0, list.size(), DEFAULT_CONCURRENCY_THRESHOLD, operation, defaultBatchSize(list.size()));
    }
    
    /**
     * Like {@link #completeTask(Object[], int, int, int, Consumer, int)} but with a mapping consumer.
     */
    public final <T, V> void completeTask (final T[] inputs, final int startIndex, final int count, final int concurrencyThreshold, final ArrayMappingConsumer<T,V> operation, int batchSize)
    {
        if(count <= concurrencyThreshold)
        {
            final int endIndex = startIndex + count;
            final Consumer<T> consumer = operation.getWorkerConsumer();
            for(int i = startIndex; i < endIndex; i++)
            {
                consumer.accept(inputs[i]);
            }
            operation.completeThread();
        }
        else
        {
            this.completeTask(new ArrayMappingTask<>(inputs, startIndex, count, operation, batchSize));
        }
    }
    
    public final <T, V> void completeTask (T[] inputs, int startIndex, int count, int concurrencyThreshold, ArrayMappingConsumer<T,V>operation)
    {
        completeTask(inputs, startIndex, count, concurrencyThreshold,  operation, defaultBatchSize(count));
    }
    
    public final <T, V> void completeTask(T[] inputs, int startIndex, int count, final ArrayMappingConsumer<T,V>operation)
    {
        completeTask(inputs, startIndex, count, DEFAULT_CONCURRENCY_THRESHOLD, operation, defaultBatchSize(count));
    }
    
    public final <T, V> void completeTask(T[] inputs, int startIndex, int count, final ArrayMappingConsumer<T,V>operation, int batchSize)
    {
        completeTask(inputs, startIndex, count, (POOL_SIZE + 1) * batchSize, operation, batchSize);
    }
    
    public final <T, V> void completeTask(T[] inputs, final ArrayMappingConsumer<T,V> operation)
    {
        completeTask(inputs, 0, inputs.length, operation);
    }
    
    public final <T, V> void completeTask(T[] inputs, final ArrayMappingConsumer<T,V> operation, int batchSize)
    {
        completeTask(inputs, 0, inputs.length, operation, batchSize);
    }
    
    public final <T, V> void completeTask(T[] inputs, int concurrencyThreshold, final ArrayMappingConsumer<T,V> operation)
    {
        completeTask(inputs, 0, inputs.length, concurrencyThreshold, operation, defaultBatchSize(inputs.length));
    }
    
    public final <T, V> void completeTask(T[] inputs, int concurrencyThreshold, final ArrayMappingConsumer<T,V> operation, int batchSize)
    {
        completeTask(inputs, 0, inputs.length, concurrencyThreshold, operation, batchSize);
    }
    
    /**
     * Process a specialized task.  Will always attempt to use the pool because no information is
     * provided that would allow evaluation of fitness for concurrency.  Blocks until all done.
     */
    public final void completeTask(ISharableTask task)
    {
        this.thingNeedingDone = task;
        
        // first batch always belongs to control thread
        this.nextBatchIndex = 1;
        
        // wake up worker threads
        synchronized(startLock)
        {
            startLock.notifyAll();
        }
        
        try
        { 
            if(task.doSomeWork(0))
            {
                while(task.doSomeWork(getNextBatchIndex())) {};
            }
            task.onThreadComplete();
        }
        catch (Exception e) 
        { 
            Sc.LOG.error("Unhandled error during concurrent processing. Impact unknown.", e);
        }
       
        // don't hold reference & prevent restart of worker threads
        this.thingNeedingDone = DUMMY_TASK;

        // await completion of worker threads
        completionWriteLock.lock();
        completionWriteLock.unlock();
    }
    
    public static abstract  class AbstractArrayTask<T> implements ISharableTask
    {
        protected final T[] theArray;
        protected final int startIndex;
        protected final int endIndex;
        protected final int batchSize;
        protected final int batchCount;
        
        protected abstract Consumer<T> getConsumer();
        
        protected AbstractArrayTask(final T[] inputs, final int startIndex, final int count, final int batchSize)
        {
            this.theArray = inputs;
            this.startIndex = startIndex;
            this.endIndex = startIndex + count;
            this.batchSize = batchSize;
            this.batchCount  = (count + batchSize - 1) / batchSize;
        }
        
        @Override
        public final boolean doSomeWork(final int batchIndex)
        {
            if(batchIndex < batchCount)
            {
                final Consumer<T> operation = getConsumer();
                int start = startIndex + batchIndex * batchSize;
                final int end = Math.min(endIndex, start + batchSize);
                for(; start < end; start++)
                {
                    operation.accept(theArray[start]);
                }
                return end < endIndex;
            } 
            else return false;
        }
    }
    
    private static class ArrayTask<T> extends AbstractArrayTask<T>
    {
        protected final Consumer<T> operation;
        
        protected ArrayTask(T[] inputs, int startIndex, int count, Consumer<T> operation, int batchSize)
        {
            super(inputs, startIndex, count, batchSize);
            this.operation = operation;
        }
        
        @Override
        public final void onThreadComplete() { }
        
        @Override
        public final Consumer<T> getConsumer()
        {
            return this.operation;
        }
    }
    
    /**
     * Similar to a Collector in a Java stream - accumulates results from the mapping function in each thread
     * and then dumps them into a collector after all batches are completed.<p>
     * 
     * The right half of the BiConsumer (another consumer) provides access to the in-thread sink for map outputs.
     * It's not represented as a map function in order to support functions that might not be 1:1 maps.
     */
    public static class ArrayMappingConsumer<T,V>
    {
        private final BiConsumer<T, Consumer<V>> operation;
        private final Consumer<AbstractUnorderedArrayList<V>> collector;
        
        protected final ThreadLocal<WorkerState> workerStates = new ThreadLocal<WorkerState>()
        {
            @Override
            protected ArrayMappingConsumer<T, V>.WorkerState initialValue()
            {
                return new WorkerState();
            }
        };
        
        /**
         * For custom collectors - the collector provided must accept a SimpleUnorderedArrayList and will be
         * called in each thread where work as done after all batches are complete. <p>
         * 
         * The collector MUST be thread safe.
         */
        public ArrayMappingConsumer(BiConsumer<T, Consumer<V>> operation, Consumer<AbstractUnorderedArrayList<V>> collector)
        {
            this.operation = operation;
            this.collector = collector;
        }
        
        /**
         * The easy way - provide a simple concurrent list a the collector.  Note that
         * this implementation does not clear the list between runs. If a consumer is reused, this
         * will need to be handled externally if necessary.
         */
        public ArrayMappingConsumer(BiConsumer<T, Consumer<V>> operation, SimpleConcurrentList<V> target)
        {
            this.operation = operation;
            this.collector = (r) -> {if(!r.isEmpty()) target.addAll(r);};
        }
        
        /**
         * Holds the per-thread results and provides access to the mapping function.
         */
        private class WorkerState extends AbstractUnorderedArrayList<V> implements Consumer<T>
        {
            @Override
            public final void accept(T t)
            {
                operation.accept(t, v -> this.add(v));
            }
            
            /**
             * Called in each thread after all batches (for that thread) are complete.
             */
            protected final void completeThread()
            {
                collector.accept(this);
                this.clear();
            }
        }
        
        /**
         * Gets the mapping function for this thread. Using the function will collect output
         * in the calling thread for later consolidation via {@link #completeThread()}
         */
        protected final Consumer<T> getWorkerConsumer()
        {
            return workerStates.get();
        }
        
        /**
         * Signals worker state to perform result consolidation for this thread.
         */
        protected final void completeThread()
        {
            workerStates.get().completeThread();
        }
    }
    
    /**
     * All of the important work is done in the consumer implementation.  
     * This just gives it the shape of a task.
     */
    private static class ArrayMappingTask<T, V> extends AbstractArrayTask<T>
    {
        protected final ArrayMappingConsumer<T,V> operation;
        
        protected ArrayMappingTask(T[] inputs, int startIndex, int count, ArrayMappingConsumer<T,V> operation, int batchSize)
        {
            super(inputs, startIndex, count, batchSize);
            this.operation = operation;
        }

        @Override
        protected Consumer<T> getConsumer()
        {
            return operation.getWorkerConsumer();
        }

        @Override
        public void onThreadComplete()
        {
            operation.completeThread();
        }
    }
}