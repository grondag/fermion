package grondag.fermion.sc.concurrency;

import java.util.ArrayList;
import java.util.Comparator;

import grondag.fermion.sc.Sc;

public class PerformanceCollector
{
	private final ArrayList<PerformanceCounter> counters = new ArrayList<PerformanceCounter>();

	private final String title;

	public PerformanceCollector(String title)
	{
		this.title = title;
	}

	public void register(PerformanceCounter counter)
	{
		counters.add(counter);
	}

	public void clear()
	{
		counters.clear();
	}

	public void outputStats()
	{
		counters.sort(new Comparator<PerformanceCounter> ()
		{

			@Override
			public int compare(PerformanceCounter o1, PerformanceCounter o2)
			{
				return Long.compare(o2.runTime(), o1.runTime());
			}
		});

		long total = 0;
		for(final PerformanceCounter counter : counters)
		{
			total += counter.runTime();
		}
		if(total == 0)
		{
			total = 1;  // prevent div by zero below
		}

		Sc.LOG.info("======================================================================================");
		Sc.LOG.info("Performance Measurement for " + title);
		Sc.LOG.info("--------------------------------------------------------------------------------------");
		for(final PerformanceCounter counter : counters)
		{
			if(counter.runTime() > 0) {
				Sc.LOG.info((counter.runTime() * 100 / total) + "% " + counter.stats());
			}
		}
		Sc.LOG.info("--------------------------------------------------------------------------------------");
		Sc.LOG.info(String.format("TOTAL TIME = %1$.3fs (%2$,dns)", (double)total/1000000000L, total));
	}

	public void clearStats()
	{
		for(final PerformanceCounter counter : counters)
		{
			counter.clearStats();
		}
	}

}
