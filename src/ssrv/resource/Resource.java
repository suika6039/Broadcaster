package ssrv.resource;

import java.io.IOException;
import java.io.InputStream;

public interface Resource
{
	public String getID();
	public String getName();
	public Directory getParent();
	public InputStream getInputStream() throws IOException;
}
