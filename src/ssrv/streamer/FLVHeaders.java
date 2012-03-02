package ssrv.streamer;

import ssrv.flv.FLVHeader;
import ssrv.flv.FLVMetadata;

public interface FLVHeaders
{
	public FLVHeader getHeader();
	public FLVMetadata getMetadata();
	public FLVPack getOtherHeaders();
}
