package grondag.fermion.sc.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import grondag.fermion.sc.Sc;

public class SimpleConcurrentCounter
{
    private AtomicLong runTime = new AtomicLong();
    private AtomicInteger runCount = new AtomicInteger(0);
    private final int sampleSize;
    private final String name;
    
    private static class LongHolder
    {
        long start;
    }
    
    private ThreadLocal<LongHolder> start = new ThreadLocal<LongHolder>()
    {
        @Override
        protected LongHolder initialValue()
        {
            return new LongHolder();
        }
    };
    
    public SimpleConcurrentCounter(String name, int sampleSize)
    {
        this.name = name;
        this.sampleSize = sampleSize;
    }
    
    public void startRun()
    {
        start.get().start = System.nanoTime();
    }
    
    public void endRun()
    {
        long nanos = runTime.addAndGet(System.nanoTime() - start.get().start);
        int samples = runCount.incrementAndGet();
        if(samples == sampleSize)
        {
            runCount.addAndGet(-samples);
            runTime.addAndGet(-nanos);
            Sc.LOG.info("Timer %1s sample result: %2$.3fs for %3$,d items @ %4$dns each.",
                    name, ((double)nanos / 1000000000), samples,  nanos / samples);
        }
    }
}
