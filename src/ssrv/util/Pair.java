package ssrv.util;

public class Pair<L,R>
{
	public L left;
	public R right;
	
	public Pair()
	{
		left = null;
		right = null;
	}
	
	public Pair(L _left,R _right)
	{
		left = _left;
		right = _right;
	}
}
