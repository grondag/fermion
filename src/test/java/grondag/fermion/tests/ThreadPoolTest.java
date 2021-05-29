package grondag.fermion.tests;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import grondag.fermion.sc.Sc;
import grondag.fermion.sc.concurrency.ScatterGatherThreadPool;
import grondag.fermion.sc.concurrency.SimpleConcurrentList;


public class ThreadPoolTest {
    SimpleConcurrentList<TestSubject> bigThings = SimpleConcurrentList.create(TestSubject.class, false, "blort", null);

    SimpleConcurrentList<TestSubject> smallThings = SimpleConcurrentList.create(TestSubject.class, false, "blort",
            null);

    {
        for (int i = 0; i < 100000000; i++) {
            bigThings.add(new TestSubject());
        }

        for (int i = 0; i < 10000; i++) {
            smallThings.add(new TestSubject());
        }
    }

    private class TestSubject {
        @SuppressWarnings("unused")
        private int data;

        public void doSomething() {
            data++;
        }
    }

    final ForkJoinPool SIMULATION_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
            new ForkJoinWorkerThreadFactory() {
                private final AtomicInteger count = new AtomicInteger(1);

                @Override
                public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
                    final ForkJoinWorkerThread result = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
                    result.setName("Exotic Matter Simulation Thread -" + count.getAndIncrement());
                    return result;
                }
            }, new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    Sc.LOG.error("Simulator thread terminated due to uncaught exception.  Badness may ensue.", e);
                }
            }, true);

    final ScatterGatherThreadPool SIMPLE_POOL = new ScatterGatherThreadPool();

    @Test
    public void test() throws InterruptedException, ExecutionException {

        System.out.println("Warm ups");
        for (int i = 0; i < 50; i++) {
            SIMPLE_POOL.completeTask(smallThings, t -> t.doSomething());
            SIMPLE_POOL.completeTask(smallThings, smallThings.size(), t -> t.doSomething());
            SIMULATION_POOL.submit(() -> smallThings.stream(true).forEach(t -> t.doSomething())).get();
            SIMPLE_POOL.completeTask(bigThings, t -> t.doSomething());
            SIMULATION_POOL.submit(() -> bigThings.stream(true).forEach(t -> t.doSomething())).get();
        }
        System.out.println("");
        System.out.println("");

        long iSmall = 0;
        long iBig = 0;
        long scatterGatherSmallLoad = 0;
        long scatterGatherSingleBatch = 0;
        long forkJoinStreamSmallLoad = 0;
        long scatterGatherLargeLoad = 0;
        long forkJoinStreamLargeLoad = 0;

        while (true) {
            long start = System.nanoTime();
            SIMPLE_POOL.completeTask(smallThings, t -> t.doSomething());
            long end = System.nanoTime();
            scatterGatherSmallLoad += (end - start);

            start = System.nanoTime();
            SIMPLE_POOL.completeTask(smallThings, smallThings.size(), t -> t.doSomething());
            end = System.nanoTime();
            scatterGatherSingleBatch += (end - start);

            start = System.nanoTime();
            SIMULATION_POOL.submit(() -> smallThings.stream(true).forEach(t -> t.doSomething())).get();
            end = System.nanoTime();
            forkJoinStreamSmallLoad += (end - start);

            start = System.nanoTime();
            SIMPLE_POOL.completeTask(bigThings, t -> t.doSomething());
            end = System.nanoTime();
            scatterGatherLargeLoad += (end - start);

            start = System.nanoTime();
            SIMULATION_POOL.submit(() -> bigThings.stream(true).forEach(t -> t.doSomething())).get();
            end = System.nanoTime();
            forkJoinStreamLargeLoad += (end - start);

            iSmall += smallThings.size();
            iBig += bigThings.size();
            System.out.println(String.format("ForkJoin Stream Small Job = %,dns (%fns per task)", forkJoinStreamSmallLoad, forkJoinStreamSmallLoad / (double) iSmall));
            System.out.println(String.format("Scatter/Gather Pool Small Job = %,dns (%fns per task)", scatterGatherSmallLoad, scatterGatherSmallLoad / (double) iSmall));
            System.out.println(String.format("Scatter/Gather Single Task = %,dns (%fns per task)", scatterGatherSingleBatch, scatterGatherSingleBatch / (double) iSmall));
            System.out.println(String.format("ForkJoin Stream Large Job = %,dns (%fns per task)", forkJoinStreamLargeLoad, forkJoinStreamLargeLoad / (double) iBig));
            System.out.println(String.format("Scatter/Gather Large Job = %,dns (%fns per task)", scatterGatherLargeLoad, scatterGatherLargeLoad / (double) iBig));
            System.out.println("");
        }
    }
}