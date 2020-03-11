package grondag.fermion.tests;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import grondag.fermion.sc.Sc;
import grondag.fermion.sc.concurrency.SimpleConcurrentList;
import grondag.fermion.sc.unordered.SimpleUnorderedArrayList;


public class SimpleConcurrentListTest {
    @Test
    public void test() {
        MicroTimer timer = new MicroTimer("SimpleConcurrentList", 1);

        SimpleUnorderedArrayList<Integer> inputs = new SimpleUnorderedArrayList<Integer>();

        for (int i = 0; i < 10000000; i++) {
            inputs.add(i);
        }

        System.out.println("Add without preallocation.");
        for (int i = 0; i < 13; i++) {
            timer.start();
            doTestAdd(inputs, 16);
            timer.stop();
            System.gc();
        }
        System.out.println(" ");

        System.out.println("Add single thread.");
        for (int i = 0; i < 13; i++) {
            timer.start();
            doTestAddSingle(inputs, 16);
            timer.stop();
            System.gc();
        }
        System.out.println(" ");

        System.out.println("Add single thread to non-concurrent simple list (for comparison)");
        for (int i = 0; i < 13; i++) {
            timer.start();
            doTestAddSingleNonConcurrent(inputs);
            timer.stop();
            System.gc();
        }
        System.out.println(" ");

        System.out.println("Add with preallocation.");
        for (int i = 0; i < 13; i++) {
            timer.start();
            doTestAdd(inputs, 10000000);
            timer.stop();
            System.gc();
        }
        System.out.println(" ");

        System.out.println("Add via array copy, no preallocation");
        for (int i = 0; i < 13; i++) {
            timer.start();
            doTestAddAll(inputs, 10000000);
            timer.stop();
            System.gc();
        }
    }

    private void doTestAdd(SimpleUnorderedArrayList<Integer> inputs, int startingCapacity) {
        SimpleConcurrentList<Integer> list = new SimpleConcurrentList<>(Integer.class, startingCapacity);

        inputs.parallelStream().forEach(i -> list.add(i));

        assert (list.size() == inputs.size());
    }

    private void doTestAddSingle(SimpleUnorderedArrayList<Integer> inputs, int startingCapacity) {
        SimpleConcurrentList<Integer> list = new SimpleConcurrentList<>(Integer.class, startingCapacity);

        inputs.stream().forEach(i -> list.add(i));

        assert (list.size() == inputs.size());
    }

    private void doTestAddSingleNonConcurrent(SimpleUnorderedArrayList<Integer> inputs) {
        SimpleUnorderedArrayList<Integer> list = new SimpleUnorderedArrayList<Integer>();

        inputs.stream().forEach(i -> list.add(i));

        assert (list.size() == inputs.size());
    }

    private void doTestAddAll(SimpleUnorderedArrayList<Integer> inputs, int startingCapacity) {
        SimpleConcurrentList<Integer> list = new SimpleConcurrentList<>(Integer.class, startingCapacity);

        list.addAll(inputs);

        assert (list.size() == inputs.size());
    }
    
   /** borrowed from Fermion */
   private static class MicroTimer {
       private final AtomicInteger hits = new AtomicInteger();
       private final AtomicLong elapsed = new AtomicLong();
       private final int sampleSize;
       private final String label;
       private final ThreadLocal<AtomicLong> started = new ThreadLocal<AtomicLong>() {
           @Override
           protected AtomicLong initialValue() {
               return new AtomicLong();
           }
       };

       public MicroTimer(String label, int sampleSize) {
           this.label = label;
           this.sampleSize = sampleSize;
       }

       public void start() {
           AtomicLong started = this.started.get();
           started.set(System.nanoTime());
       }

       /**
        * Returns true if timer output stats this sample. For use if want to output
        * supplementary information at same time.
        */
       public boolean stop() {
           long end = System.nanoTime();
           long e = this.elapsed.addAndGet(end - this.started.get().get());
           long h = this.hits.incrementAndGet();
           if (h == this.sampleSize) {
               doReportAndClear(e, h);
               return true;
           } else
               return false;
       }

       private void doReportAndClear(long e, long h) {
           this.hits.set(0);
           this.elapsed.set(0);
           Sc.LOG.info("Avg %s duration = %d ns, total duration = %d, total runs = %d", label, e / h,
                   e / 1000000, h);
       }
   }

}