package ssrv.player;

public class FLVChunkPack
{
	private final FLVChunk prevChunk;
	private final FLVChunk currentChunk;
	private long lastModified;
	
	public FLVChunkPack(FLVChunk _prev,FLVChunk _current)
	{
		prevChunk = _prev;
		currentChunk = _current;
		
		lastModified = System.currentTimeMillis();
	}
	
	public FLVChunk getPrevChunk()
	{
		return prevChunk;
	}
	
	public FLVChunk getCurrentChunk()
	{
		return currentChunk;
	}
	
	public long getLastModified()
	{
		return lastModified;
	}
}
