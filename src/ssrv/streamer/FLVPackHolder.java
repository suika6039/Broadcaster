package ssrv.streamer;


public class FLVPackHolder
{
	private final FLVPack current;
	private final FLVPack previous;
	
	public static final FLVPackHolder CONSTRUCTING = new FLVPackHolder(FLVPack.NULL_PACK,FLVPack.NULL_PACK);
	public static final FLVPackHolder FINISH = new FLVPackHolder(FLVPack.NULL_PACK,FLVPack.NULL_PACK);
	public static final FLVPackHolder NULL_HOLDER = new FLVPackHolder(FLVPack.NULL_PACK,FLVPack.NULL_PACK);
	
	public FLVPackHolder(FLVPack _cur,FLVPack _prev)
	{
		current = _cur;
		previous = _prev;
	}
	
	public FLVPack getCurrentPack()
	{
		return current;
	}
	
	public FLVPack getPreviousPack()
	{
		return previous;
	}
	
}
