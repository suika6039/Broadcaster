package ssrv.resource;

public interface ResourceProvider
{
	public Resource resolve(String _id);
	public String getName();
	public Directory getRootDirectory();
	public String toString();
}
