package ssrv.player;

import java.util.Iterator;
import ssrv.flv.FLVHeader;
import ssrv.flv.FLVMetadata;
import ssrv.flv.FLVReader;
import ssrv.flv.FLVTag;

public class FLVPlayerClient
{
	private final FLVHeader header;
	private final FLVMetadata metadata;
	private final FLVChunkHolder holder;
	private final int refreshInterval;
	
	private FLVChunk chunk;
	private Iterator<FLVTag> tagIterator;
	private int startTimeStamp;
	
	public FLVPlayerClient(FLVHeader _header,FLVMetadata _metadata,int _refreshInterval,FLVChunkHolder _holder)
	{
		header = _header.copy();
		metadata = _metadata.copy();
		holder = _holder;
		refreshInterval = _refreshInterval;
		
		metadata.setProperty(FLVMetadata.PropertyName.duration,(Double)0.0);
		metadata.setProperty(FLVMetadata.PropertyName.filesize,(Double)0.0);
		
		chunk = holder.getNextChunk();
		tagIterator = chunk.iterator();
		startTimeStamp = chunk.getFirstTimeStamp();
	}
	
	public int getRefreshInterval()
	{
		return refreshInterval;
	}
	
	public FLVHeader getHeader()
	{
		return header;
	}
	
	public FLVMetadata getMetadata()
	{
		return metadata;
	}
	
	public FLVTag next()
	{
		FLVTag theNext;
		while(true){
			if(tagIterator != null && tagIterator.hasNext()){
				//Copy the FLVTag
				theNext = tagIterator.next().copy();
				break;
			}else{
				FLVChunk newChunk = holder.getNextChunk();
				if(newChunk == null){
					return FLVReader.FLV_EOF;
				}
				
				if(newChunk == chunk){
					try{
						System.out.println(String.format("%s : chunk is not available",Thread.currentThread().getName()));
						Thread.sleep(refreshInterval/2);
						continue;
					}catch(InterruptedException _e){
						_e.printStackTrace();
						Thread.currentThread().interrupt();
						return FLVReader.FLV_EOF;
					}
				}
				
				System.out.println(String.format("%s : newChunk = %d",Thread.currentThread().getName(),newChunk.getFirstTimeStamp()));
				chunk = newChunk;
				tagIterator = chunk.iterator();
			}
		}
		
		int timeStamp = theNext.getTimeStamp();
		theNext.setTimeStamp(timeStamp - startTimeStamp);
		return theNext;
	}
}
