package grondag.fermion.concurrency;

import java.util.ArrayList;
import java.util.Comparator;

import grondag.fermion.Fermion;

public class PerformanceCollector
{
    private ArrayList<PerformanceCounter> counters = new ArrayList<PerformanceCounter>();
    
    private final String title;
    
    public PerformanceCollector(String title)
    {
        this.title = title;
    };
    
    public void register(PerformanceCounter counter)
    {
        this.counters.add(counter);
    }
    
    public void clear()
    {
        this.counters.clear();
    }
    
    public void outputStats()
    {
        this.counters.sort(new Comparator<PerformanceCounter> () 
        {

            @Override
            public int compare(PerformanceCounter o1, PerformanceCounter o2)
            {
                return Long.compare(o2.runTime(), o1.runTime());
            }
        });
        
        long total = 0;
        for(PerformanceCounter counter : this.counters)
        {
            total += counter.runTime();
        }
        if(total == 0) total = 1;  // prevent div by zero below
        
        Fermion.LOG.info("======================================================================================");
        Fermion.LOG.info("Performance Measurement for " + this.title);
        Fermion.LOG.info("--------------------------------------------------------------------------------------");
        for(PerformanceCounter counter : this.counters)
        {
            if(counter.runTime() > 0) Fermion.LOG.info((counter.runTime() * 100 / total) + "% " + counter.stats());
        }
        Fermion.LOG.info("--------------------------------------------------------------------------------------");
        Fermion.LOG.info(String.format("TOTAL TIME = %1$.3fs (%2$,dns)", (double)total/1000000000L, total));
    }
    
    public void clearStats()
    {
        for(PerformanceCounter counter : this.counters)
        {
            counter.clearStats();
        }
    }
    
}
