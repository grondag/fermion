package grondag.fermion.varia;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import grondag.fermion.Fermion;

/**
 * For crude but simple microbenchmarks - for small scope, in-game situations
 * where JMH would be more than I want
 */
public class MicroTimer {
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
        Fermion.INSTANCE.info("Avg %s duration = %d ns, total duration = %d, total runs = %d", label, e / h,
                e / 1000000, h);
    }

    public void reportAndClear() {
        this.doReportAndClear(this.elapsed.get(), this.hits.get());
    }
}
