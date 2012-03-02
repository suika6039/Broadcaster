package ssrv.streamer.common;

import ssrv.streamer.FLVStreamingClientScheduler;
import ssrv.streamer.FLVStreamingScheduler;

public class DefaultFLVStreamingScheduler implements FLVStreamingScheduler
{
	private long interval;
	
	public DefaultFLVStreamingScheduler(long _interval)
	{
		interval = _interval;
	}

	@Override
	public void sleep() throws InterruptedException
	{
		Thread.sleep(interval);
	}

	@Override
	public FLVStreamingClientScheduler newClientScheduler()
	{
		return new FLVStreamingClientSchedulerImpl(interval/2);
	}
	
	private static class FLVStreamingClientSchedulerImpl implements FLVStreamingClientScheduler
	{
		private long interval;
		
		public FLVStreamingClientSchedulerImpl(long _interval)
		{
			interval = _interval;
		}
		
		@Override
		public void sleep() throws InterruptedException
		{
			Thread.sleep(interval);
		}
	}
}
