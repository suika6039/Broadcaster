package ssrv.streamer.common;

import java.util.Iterator;
import java.util.LinkedList;

import ssrv.flv.FLVTag;
import ssrv.streamer.FLVPack;

public class DefaultFLVPack implements FLVPack
{
	private LinkedList<FLVTag> list;
	
	public DefaultFLVPack()
	{
		list = new LinkedList<FLVTag>();
	}
	
	public void addFLVTag(FLVTag _tag)
	{
		list.add(_tag);
	}

	@Override
	public Iterator<FLVTag> iterator()
	{
		return list.iterator();
	}

}
