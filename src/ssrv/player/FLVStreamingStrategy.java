package ssrv.player;

import ssrv.flv.FLVHeader;
import ssrv.flv.FLVMetadata;
import ssrv.flv.FLVTag;

public interface FLVStreamingStrategy
{
	public void onInitialize(FLVHeader _header,FLVMetadata _metadata);
	public void onFLVTag(FLVTag _tag);
	public void onFinish();
}
