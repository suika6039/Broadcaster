package ssrv.streamer;

import ssrv.flv.FLVException;

public interface FLVStreamingStrategy
{
	public FLVHeaders getHeaders();
	public FLVPack getNextFLVPack() throws FLVException;
}
