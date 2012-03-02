package ssrv.streamer;

import java.util.Collections;
import java.util.Iterator;

import ssrv.flv.FLVTag;

public interface FLVPack extends Iterable<FLVTag>
{
	public static final FLVPack NULL_PACK = new FLVPack(){
		@Override
		public Iterator<FLVTag> iterator()
		{
			Iterable<FLVTag> itrble = Collections.emptyList();
			return itrble.iterator();
		}
	};
}
