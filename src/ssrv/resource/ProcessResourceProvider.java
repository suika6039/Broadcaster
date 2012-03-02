package ssrv.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ProcessResourceProvider implements ResourceProvider
{
	public ProcessResourceProvider()
	{
		
	}
	
	@Override
	public Resource resolve(String _id)
	{
		return null;
	}
	
	@Override
	public String getName()
	{
		return null;
	}

	@Override
	public Directory getRootDirectory()
	{
		return null;
	}
	
	public Directory createDirectory(String _name)
	{
		return null;
	}
	
	public static class DirectoryImpl implements Directory
	{

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Directory getParent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<Resource> getResources() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<Directory> getDirectories() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static class ResourceImpl implements Resource
	{
		@Override
		public String getID()
		{
			return null;
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Directory getParent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
