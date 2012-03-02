package ssrv.flv;

public class FLVException extends Exception
{
	private static final long serialVersionUID = 6115037160824039033L;

	public FLVException(String _message)
	{
		super(_message);
	}
	
	public FLVException(String _message,Throwable _cause)
	{
		super(_message,_cause);
	}
	
	public FLVException(Throwable _cause)
	{
		super(_cause);
	}
}
