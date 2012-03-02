package ssrv.streamer;

import java.util.concurrent.atomic.AtomicReference;

import ssrv.flv.FLVException;

public class FLVStreamer implements Runnable
{
	private FLVStreamingStrategy strategy;
	private FLVStreamingScheduler scheduler;
	
	private AtomicReference<Thread> referenceThread;
	private AtomicReference<FLVPackHolder> referenceHolder;
	
	/**
	 * constructor.
	 * @param _strategy
	 * @param _scheduler
	 */
	public FLVStreamer(FLVStreamingStrategy _strategy,FLVStreamingScheduler _scheduler)
	{
		strategy = _strategy;
		scheduler = _scheduler;
		
		referenceHolder = new AtomicReference<FLVPackHolder>(FLVPackHolder.NULL_HOLDER);
		referenceThread = new AtomicReference<Thread>(null);
	}
	
	/**
	 * create new streamer client
	 * @return new FLVStreamerClient
	 */
	public FLVStreamerClient newClient()
	{
		FLVStreamerClient client = new FLVStreamerClient(referenceHolder,strategy.getHeaders(),scheduler.newClientScheduler());
		return client;
	}
	
	/**
	 * starts streaming.
	 * @return if succeeded true otherwise false
	 */
	public boolean start()
	{
		//check current thread is alive or not.
		Thread prevThread = referenceThread.get();
		if(prevThread != null){
			if(prevThread.isAlive()){
				return false;
			}
		}
		
		//create new thread object and try to CAS.
		Thread newThread = new Thread(this);
		if(referenceThread.compareAndSet(prevThread,newThread)){
			//if succeeded then runs thread.
			newThread.start();
			return true;
		}
		
		return false;
	}
	
	/**
	 * pause streaming.
	 * @return if succeeded true , otherwise false
	 */
	public boolean pause()
	{
		Thread currentThread = referenceThread.get();
		if(currentThread.isAlive()){
			currentThread.interrupt();
			return true;
		}
		
		return false;
	}

	/**
	 * streaming loop.
	 * basic strategy of streaming FLVs
	 */
	@Override
	public void run()
	{
		while(!Thread.interrupted()){
			FLVPackHolder holder = referenceHolder.get();
			try{
				FLVPack previous = holder.getCurrentPack();
				//get new FLVPack from strategy
				FLVPack current = null;
				try{
					current = strategy.getNextFLVPack();
				}catch(FLVException _e){
					_e.printStackTrace();
				}
				
				if(current == FLVPack.NULL_PACK || current == null){
					//end of stream
					break;
				}
				
				FLVPackHolder newHolder = new FLVPackHolder(current,previous);
				referenceHolder.set(newHolder);
				
				//sleep until next update time.
				scheduler.sleep();
				
			}catch(InterruptedException _e){
				_e.printStackTrace();
			}
		}
	}
}
