package ssrv.streamer.avc;

import java.nio.ByteBuffer;

import ssrv.flv.FLVException;
import ssrv.flv.FLVReader;
import ssrv.flv.FLVTag;
import ssrv.streamer.FLVHeaders;
import ssrv.streamer.FLVPack;
import ssrv.streamer.FLVStreamingClientScheduler;
import ssrv.streamer.FLVStreamingScheduler;
import ssrv.streamer.FLVStreamingStrategy;
import ssrv.streamer.common.DefaultFLVHeaders;
import ssrv.streamer.common.DefaultFLVPack;

public class AVCFLVStreaming implements FLVStreamingStrategy , FLVStreamingScheduler
{
	private FLVReader reader;
	private FLVHeaders headers;
	private FLVTag keyFrameReserved;
	private long interval;
	private long sleepTime;
	private long nextTimeStamp;
	
	public AVCFLVStreaming(FLVReader _reader,long _interval)
	{
		reader = _reader;
		headers = null;
		
		FLVPack pack = null;
		try{
			FLVTag avcSequenceHeader = reader.getNextTag();
			FLVTag aacSequenceHeader = reader.getNextTag();
			DefaultFLVPack tmp = new DefaultFLVPack();
			tmp.addFLVTag(avcSequenceHeader);
			tmp.addFLVTag(aacSequenceHeader);
			pack = tmp;
		}catch(FLVException _e){
			_e.printStackTrace();
			pack = FLVPack.NULL_PACK;
		}
		
		headers = new DefaultFLVHeaders(_reader.getHeader(),_reader.getMetadata(),pack);
		interval = _interval;
		sleepTime = _interval;
	}

	@Override
	public synchronized FLVHeaders getHeaders()
	{
		return headers;
	}

	@Override
	public FLVPack getNextFLVPack() throws FLVException
	{
		DefaultFLVPack pack = new DefaultFLVPack();
		FLVTag tag = null;
		
		if(keyFrameReserved != null){
			pack.addFLVTag(keyFrameReserved);
		}
		
		//need to think about key frame.
		long timeStampLast = 0;
		
		while((tag = reader.getNextTag()) != FLVReader.FLV_EOF){
			long timeStamp = tag.getTimeStamp();
			if(timeStamp <= nextTimeStamp){
				pack.addFLVTag(tag);
				timeStampLast = timeStamp;
			}else{
				//tag is KeyFrame or InnerFrame
				ByteBuffer buf = ByteBuffer.wrap(tag.getBody());
				byte b = buf.get();
				int frame = (b & 0xF0) >> 4;
				if(frame == 1){ // 1 means KeyFrame
					keyFrameReserved = tag;
					long exceed = timeStamp - timeStampLast;
					nextTimeStamp = timeStamp + interval;
					sleepTime = exceed + interval;
					break;
				}else{
					pack.addFLVTag(tag);
				}
			}
			
		}
		
		if(tag == FLVReader.FLV_EOF){
			pack.addFLVTag(tag);
		}
		
		return pack;
	}

	@Override
	public void sleep() throws InterruptedException
	{
		Thread.sleep(sleepTime);
	}

	@Override
	public FLVStreamingClientScheduler newClientScheduler()
	{
		return new FLVStreamingClientSchedulerImpl();
	}

	private class FLVStreamingClientSchedulerImpl implements FLVStreamingClientScheduler
	{
		@Override
		public void sleep() throws InterruptedException
		{
			Thread.sleep(sleepTime/2);
		}
	}
}
