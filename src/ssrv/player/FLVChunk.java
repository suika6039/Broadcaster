package ssrv.player;

import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import ssrv.flv.FLVTag;

public class FLVChunk
{
	private final Deque<FLVTag> deque;
	private final long lastModified;
	
	public FLVChunk(Deque<FLVTag> _deque)
	{
		deque = _deque;
		lastModified = System.currentTimeMillis();
	}
	
	public long getLastModified()
	{
		return lastModified;
	}
	
	public Iterator<FLVTag> iterator()
	{
		return Collections.unmodifiableCollection(deque).iterator();
	}
	
	public int getFirstTimeStamp()
	{
		FLVTag tag = deque.getFirst();
		return tag.getTimeStamp();
	}
	
	public int getLastTimeStamp()
	{
		FLVTag tag = deque.getLast();
		return tag.getTimeStamp();
	}
	
	public int flvChunkTagSize()
	{
		int size = deque.size();
		return size;
	}
	
	public int flvChunkTimeLength()
	{
		int diff = getLastTimeStamp() - getFirstTimeStamp();
		return diff;
	}
}
