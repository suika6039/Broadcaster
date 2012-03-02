package ssrv.streamer;

public interface FLVStreamingScheduler
{
	public void sleep() throws InterruptedException;
	public FLVStreamingClientScheduler newClientScheduler();
}
