package grondag.fermion.tests;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.netty.util.internal.ThreadLocalRandom;
import org.junit.Test;

import grondag.fermion.sc.cache.KeyInterningCache;
import grondag.fermion.sc.cache.LongAtomicLoadingCache;
import grondag.fermion.sc.cache.LongSimpleCacheLoader;
import grondag.fermion.sc.cache.LongSimpleLoadingCache;
import grondag.fermion.sc.cache.ObjectSimpleCacheLoader;
import grondag.fermion.sc.cache.ObjectSimpleLoadingCache;
import grondag.fermion.sc.cache.WideSimpleCacheLoader;
import grondag.fermion.sc.cache.WideSimpleLoadingCache;

public class SimpleLoadingCacheTest {
    /** added to key to produce result */
    private static final long MAGIC_NUMBER = 42L;
    private static final int STEP_COUNT = 10000000;
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int LOAD_COST = 10;
    private static final AtomicLong twiddler = new AtomicLong(0);

    private static class Loader extends CacheLoader<Long, Long>
            implements LongSimpleCacheLoader<Long>, ObjectSimpleCacheLoader<Long, Long>, WideSimpleCacheLoader<Long> {
        @Override
        public Loader createNew() {
            return new Loader();
        }

        @Override
        public Long load(long key) {
            if (LOAD_COST > 0) {
                for (int i = 0; i < LOAD_COST; i++) {
                    twiddler.incrementAndGet();
                }
            }
            return new Long(key + MAGIC_NUMBER);
        }

        public Long loadInterned(long key) {
            if (LOAD_COST > 0) {
                for (int i = 0; i < LOAD_COST; i++) {
                    twiddler.incrementAndGet();
                }
            }
            return key;
        }

        @Override
        public Long load(Long key) {
            if (LOAD_COST > 0) {
                for (int i = 0; i < LOAD_COST; i++) {
                    twiddler.incrementAndGet();
                }
            }
            return load(key == null ? 0 : key.longValue());
        }

        @Override
        public Long load(long key1, long key2) {
            if (LOAD_COST > 0) {
                for (int i = 0; i < LOAD_COST; i++) {
                    twiddler.incrementAndGet();
                }
            }
            return load(key1);
        }
    }

    private interface CacheAdapter {
        long get(long key);

        CacheAdapter newInstance(int maxSize);
    }

    private abstract class Runner implements Callable<Void> {

        private final CacheAdapter subject;
        private final long magic;

        private Runner(CacheAdapter subject,long magic) {
            this.subject = subject;
            this.magic = magic;
        }

        @Override
        public Void call() {
            try {
                final Random random = ThreadLocalRandom.current();

                for (int i = 0; i < STEP_COUNT; i++) {
                    final long key = getKey(i, random.nextLong());
                    final Long result = subject.get(key);
                    assert (result.longValue() == key + magic);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public abstract long getKey(int step, long randomLong);
    }

    private class UniformRunner extends Runner {
        private final long keyMask;

        private UniformRunner(CacheAdapter subject, long keyMask, long magic) {
            super(subject, magic);
            this.keyMask = keyMask;
        }

        @Override
        public long getKey(int step, long randomLong) {
            return randomLong & keyMask;
        }
    }

    /** shifts from one set of uniform demand to another and then back again */
    private class ShiftRunner extends Runner {
        private final static int FIRST_MILESTONE = STEP_COUNT / 3;
        private final static int SECOND_MILESTONE = FIRST_MILESTONE * 2;

        private final long keyMask;

        private ShiftRunner(CacheAdapter subject, long keyMask, long magic) {
            super(subject, magic);
            this.keyMask = keyMask;
        }

        @Override
        public long getKey(int step, long randomLong) {
            // return odd values in 1st and 3rd phase, even in middle phase
            if (step < FIRST_MILESTONE || step > SECOND_MILESTONE) {
                if ((randomLong & 1L) == 0)
                    randomLong++;
            } else {
                if ((randomLong & 1L) == 1)
                    randomLong++;
            }
            return randomLong & keyMask;
        }
    }

    private class GoogleAdapter implements CacheAdapter {
        private LoadingCache<Long, Long> cache;

        @Override
        public long get(long key) {
            final long startTime = System.nanoTime();
            final long result = cache.getUnchecked(key);
            nanoCount.addAndGet(System.nanoTime() - startTime);
            return result;
        }

        @Override
        public CacheAdapter newInstance(int maxSize) {
            final GoogleAdapter result = new GoogleAdapter();

            result.cache = CacheBuilder.newBuilder().concurrencyLevel(THREAD_COUNT).initialCapacity(maxSize)
                    .maximumSize(maxSize).build(new Loader());

            return result;
        }
    }

    private class LongAtomicAdapter implements CacheAdapter {
        private LongAtomicLoadingCache<Long> cache;

        @Override
        public long get(long key) {
            final long startTime = System.nanoTime();
            final long result = cache.get(key);
            nanoCount.addAndGet(System.nanoTime() - startTime);
            return result;
        }

        @Override
        public CacheAdapter newInstance(int maxSize) {
            final LongAtomicAdapter result = new LongAtomicAdapter();
            result.cache = new LongAtomicLoadingCache<>(new Loader(), maxSize);
            return result;
        }
    }

    private class LongSimpleAdapter implements CacheAdapter {
        private LongSimpleLoadingCache<Long> cache;

        @Override
        public long get(long key) {
            final long startTime = System.nanoTime();
            final long result = cache.get(key);
            nanoCount.addAndGet(System.nanoTime() - startTime);
            return result;
        }

        @Override
        public CacheAdapter newInstance(int maxSize) {
            final LongSimpleAdapter result = new LongSimpleAdapter();
            result.cache = new LongSimpleLoadingCache<>(new Loader(), maxSize);
            return result;
        }
    }

    private class ObjectSimpleAdapter implements CacheAdapter {
        private ObjectSimpleLoadingCache<Long, Long> cache;

        @Override
        public long get(long key) {
            final long startTime = System.nanoTime();
            final long result = cache.get(key);
            nanoCount.addAndGet(System.nanoTime() - startTime);
            return result;
        }

        @Override
        public CacheAdapter newInstance(int maxSize) {
            final ObjectSimpleAdapter result = new ObjectSimpleAdapter();
            result.cache = new ObjectSimpleLoadingCache<>(new Loader(), maxSize);
            return result;
        }
    }

    private class KeyInterningAdapter implements CacheAdapter {
        private KeyInterningCache<Long> cache;
        final Loader loader = new Loader();

        @Override
        public long get(long key) {
            final long startTime = System.nanoTime();
            final long result = cache.get(key);
            nanoCount.addAndGet(System.nanoTime() - startTime);
            return result;
        }

        @Override
        public CacheAdapter newInstance(int maxSize) {
            final KeyInterningAdapter result = new KeyInterningAdapter();
            result.cache = new KeyInterningCache<>(loader::loadInterned, maxSize);
            return result;
        }
    }

    private class WideSimpleAdapter implements CacheAdapter {
        private WideSimpleLoadingCache<Long> cache;

        @Override
        public long get(long key) {
            final long startTime = System.nanoTime();
            final long result = cache.get(key, key * 31);
            nanoCount.addAndGet(System.nanoTime() - startTime);
            return result;
        }

        @Override
        public CacheAdapter newInstance(int maxSize) {
            final WideSimpleAdapter result = new WideSimpleAdapter();
            result.cache = new WideSimpleLoadingCache<>(new Loader(), maxSize);
            return result;
        }
    }

    AtomicLong nanoCount = new AtomicLong(0);

    private void doTestInner(ExecutorService executor, CacheAdapter subject, long magic) {
        final ArrayList<Runner> runs = new ArrayList<>();

        System.out.println("Practical best case: key space == max capacity - uniform random demand");
        runs.clear();
        nanoCount.set(0);
        subject = subject.newInstance(0xFFFFF);
        for (int i = 0; i < THREAD_COUNT; i++) {
            runs.add(new UniformRunner(subject, 0xFFFFF, magic));
        }
        try {
            executor.invokeAll(runs);
            System.out.println("Mean get() time = " + (nanoCount.get() / (STEP_COUNT * THREAD_COUNT)));
        } catch (final Exception e) {
            e.printStackTrace();
        }

        System.out.println("Suboptimal case: moderately constrained memory test - uniform random demand");
        runs.clear();
        nanoCount.set(0);
        subject = subject.newInstance(0xCCCCC);
        for (int i = 0; i < THREAD_COUNT; i++) {
            runs.add(new UniformRunner(subject, 0xFFFFF, magic));
        }
        try {
            executor.invokeAll(runs);
            System.out.println("Mean get() time = " + (nanoCount.get() / (STEP_COUNT * THREAD_COUNT)));
        } catch (final Exception e) {
            e.printStackTrace();
        }

        System.out.println("Worst case: Severely constrained memory test - uniform random demand");
        runs.clear();
        nanoCount.set(0);
        subject = subject.newInstance(0x2FFFF);
        for (int i = 0; i < THREAD_COUNT; i++) {
            runs.add(new UniformRunner(subject, 0xFFFFF, magic));
        }
        try {
            executor.invokeAll(runs);
            System.out.println("Mean get() time = " + (nanoCount.get() / (STEP_COUNT * THREAD_COUNT)));
        } catch (final Exception e) {
            e.printStackTrace();
        }

        System.out.println("Nominal case: moderately constrained memory test - shifting random demand");
        runs.clear();
        nanoCount.set(0);
        subject = subject.newInstance(0x7FFFF);
        for (int i = 0; i < THREAD_COUNT; i++) {
            runs.add(new ShiftRunner(subject, 0xFFFFF, magic));
        }
        try {
            executor.invokeAll(runs);
            System.out.println("Mean get() time = " + (nanoCount.get() / (STEP_COUNT * THREAD_COUNT)));
        } catch (final Exception e) {
            e.printStackTrace();
        }

        System.out.println("Nominal case / single thread: moderately constrained memory test - shifting random demand");
        runs.clear();
        nanoCount.set(0);
        subject = subject.newInstance(0x7FFFF);
        for (int i = 0; i < THREAD_COUNT; i++) {
            new ShiftRunner(subject, 0xFFFFF, magic).call();
        }
        System.out.println("Mean get() time = " + (nanoCount.get() / (STEP_COUNT * THREAD_COUNT)));

        System.out.println("");
    }

    public void doTestOuter(ExecutorService executor) {
    	System.out.println("Running key interning object cache test");
    	doTestInner(executor, new KeyInterningAdapter(), 0);

    	System.out.println("Running google cache test");
        doTestInner(executor, new GoogleAdapter(), MAGIC_NUMBER);

        System.out.println("Running simple long cache test");
        doTestInner(executor, new LongSimpleAdapter(), MAGIC_NUMBER);

        System.out.println("Running atomic long cache test");
        doTestInner(executor, new LongAtomicAdapter(), MAGIC_NUMBER);

        System.out.println("Running wide key cache test");
        doTestInner(executor, new WideSimpleAdapter(), MAGIC_NUMBER);

        System.out.println("Running simple object cache test");
        doTestInner(executor, new ObjectSimpleAdapter(), MAGIC_NUMBER);
    }

    @Test
    public void test() {

        // not really a unit test, so disable unless actually want to run

        ExecutorService SIMULATION_POOL;
        SIMULATION_POOL = Executors.newFixedThreadPool(THREAD_COUNT);

        System.out.println("WARM UP RUN");
        doTestOuter(SIMULATION_POOL);

        System.out.println("TEST RUN");
        doTestOuter(SIMULATION_POOL);
    }
}