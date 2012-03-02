package ssrv.resource;

import java.util.Set;

public interface Directory
{
	public String getName();
	public Directory getParent();
	public Set<Resource> getResources();
	public Set<Directory> getDirectories();
}
