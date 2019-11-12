package grondag.fermion.sc.concurrency;

public class PerformanceCounter
{
	public static PerformanceCounter create(boolean enablePerformanceCounting, String title, PerformanceCollector collector)
	{
		return enablePerformanceCounting ? new RealPerformanceCounter(title, collector) : new PerformanceCounter();
	}
	private PerformanceCounter() {}

	public void clearStats() {}

	public void startRun() {}

	public void endRun() {}

	public void addCount(int howMuch) {}

	public int runCount() { return 0; }

	public long runTime() { return 0; }

	public long timePerRun() { return 0; }

	public String stats() { return "Performance counting disabled"; }

	private static class RealPerformanceCounter extends PerformanceCounter
	{
		long runTime = 0;
		int runCount = 0;
		long minTime = Long.MAX_VALUE;
		long maxTime = 0;
		final String title;

		long startTime;

		public RealPerformanceCounter(String title, PerformanceCollector collector)
		{
			this.title = title;
			if(collector != null)
			{
				collector.register(this);
			}
		}

		@Override
		public void clearStats()
		{
			runCount = 0;
			runTime = 0;
			minTime = Long.MAX_VALUE;
			maxTime = 0;
		}

		@Override
		public void startRun()
		{
			startTime = System.nanoTime();
		}

		@Override
		public void endRun()
		{
			final long time = System.nanoTime() - startTime;
			if(time > maxTime) {
				maxTime = time;
			}
			if(time < minTime) {
				minTime = time;
			}
			runTime += time;
		}

		@Override
		public void addCount(int howMuch)
		{
			runCount += howMuch;
		}

		@Override
		public int runCount()
		{ return runCount; }

		@Override
		public long runTime()
		{ return runTime; }

		@Override
		public long timePerRun()
		{ return runCount == 0 ? 0 : runTime / runCount; }

		@Override
		public String stats()
		{ return title + String.format(": %1$.3fs for %2$,d items @ %3$,dns each. Min = %4$,dns Max = %5$,dns"
			, ((double)runTime() / 1000000000), runCount(),  timePerRun(), minTime, maxTime); }

	}

}