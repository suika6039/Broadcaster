package ssrv.streamer.common;

import ssrv.flv.FLVHeader;
import ssrv.flv.FLVMetadata;
import ssrv.streamer.FLVHeaders;
import ssrv.streamer.FLVPack;

public class DefaultFLVHeaders implements FLVHeaders
{
	private FLVHeader header;
	private FLVMetadata metadata;
	private FLVPack otherHeaders;
	
	public DefaultFLVHeaders(FLVHeader _header,FLVMetadata _metadata,FLVPack _otherHeaders)
	{
		header = _header;
		metadata = _metadata;
		otherHeaders = _otherHeaders;
	}

	@Override
	public FLVHeader getHeader()
	{
		return header;
	}

	@Override
	public FLVMetadata getMetadata()
	{
		return metadata;
	}

	@Override
	public FLVPack getOtherHeaders()
	{
		return otherHeaders;
	}

}
