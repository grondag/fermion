package grondag.fermion.concurrency;

import java.util.ArrayList;
import java.util.Comparator;

import javax.annotation.Nullable;

import grondag.exotic_matter.ExoticMatter;


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
            public int compare(@Nullable PerformanceCounter o1, @Nullable PerformanceCounter o2)
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
        
        ExoticMatter.INSTANCE.info("======================================================================================");
        ExoticMatter.INSTANCE.info("Performance Measurement for " + this.title);
        ExoticMatter.INSTANCE.info("--------------------------------------------------------------------------------------");
        for(PerformanceCounter counter : this.counters)
        {
            if(counter.runTime() > 0) ExoticMatter.INSTANCE.info((counter.runTime() * 100 / total) + "% " + counter.stats());
        }
        ExoticMatter.INSTANCE.info("--------------------------------------------------------------------------------------");
        ExoticMatter.INSTANCE.info(String.format("TOTAL TIME = %1$.3fs (%2$,dns)", (double)total/1000000000L, total));
    }
    
    public void clearStats()
    {
        for(PerformanceCounter counter : this.counters)
        {
            counter.clearStats();
        }
    }
    
}
