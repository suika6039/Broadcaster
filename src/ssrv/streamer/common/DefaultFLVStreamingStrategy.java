package ssrv.streamer.common;

import ssrv.flv.FLVException;
import ssrv.flv.FLVHeader;
import ssrv.flv.FLVMetadata;
import ssrv.flv.FLVReader;
import ssrv.flv.FLVTag;
import ssrv.streamer.FLVHeaders;
import ssrv.streamer.FLVPack;
import ssrv.streamer.FLVStreamingStrategy;

public class DefaultFLVStreamingStrategy implements FLVStreamingStrategy
{
	private final FLVReader reader;
	private final FLVHeader header;
	private final FLVMetadata metadata;
	private final FLVHeaders headers;
	private final long interval;
	private long nextTimeStamp;
	
	public DefaultFLVStreamingStrategy(FLVReader _reader,long _interval)
	{
		reader = _reader;
		header = _reader.getHeader();
		metadata = _reader.getMetadata();
		headers = new DefaultFLVHeaders(header,metadata,FLVPack.NULL_PACK);
		interval = _interval;
		nextTimeStamp = interval;
	}

	@Override
	public FLVHeaders getHeaders()
	{
		return headers;
	}

	@Override
	public FLVPack getNextFLVPack() throws FLVException
	{
		DefaultFLVPack pack = new DefaultFLVPack();
		FLVTag tag = null;
		
		while((tag = reader.getNextTag()) != FLVReader.FLV_EOF){
			long timeStamp = tag.getTimeStamp();
			if(timeStamp <= nextTimeStamp){
				pack.addFLVTag(tag);
			}else{
				pack.addFLVTag(tag);
				nextTimeStamp += interval;
				break;
			}
		}
		
		if(tag == FLVReader.FLV_EOF){
			pack.addFLVTag(tag);
		}
		
		return pack;
	}
}
