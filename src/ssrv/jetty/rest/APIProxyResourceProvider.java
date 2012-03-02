package ssrv.jetty.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sf.json.JSONObject;

import ssrv.resource.Directory;
import ssrv.resource.Resource;
import ssrv.resource.ResourceProvider;

public class APIProxyResourceProvider implements ResourceProvider
{
	private String baseURL;
	private String name;
	
	public APIProxyResourceProvider(String _baseURL,String _name)
	{
		baseURL = _baseURL;
		name = _name;
	}

	@Override
	public Resource resolve(String _id)
	{
		Directory dir = getRootDirectory();
		Set<Resource> resources = dir.getResources();
		for(Resource res : resources){
			if(res.getID().equals(_id)){
				return res;
			}
		}
		return null;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public Directory getRootDirectory()
	{
		return new DirectoryImpl(baseURL,name);
	}

	private class DirectoryImpl implements Directory
	{
		private String baseURL;
		private String name;
		
		public DirectoryImpl(String _baseURL,String _name)
		{
			baseURL = _baseURL;
			name = _name;
		}
		
		@Override
		public String getName()
		{
			return name;
		}

		@Override
		public Directory getParent()
		{
			return null;
		}

		@Override
		public Set<Resource> getResources()
		{
			JSONObject json = null;
			StringBuffer buf = new StringBuffer();
			HttpURLConnection con = null;
			Set<Resource> resources = null;
			try{
				con = (HttpURLConnection)(new URL(baseURL+"/api/players")).openConnection();
				con.connect();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line;
				while((line = br.readLine()) != null){
					buf.append(line);
				}
				
				json = JSONObject.fromObject(buf.toString());
				
				resources = new HashSet<Resource>();
				for(Object key : json.keySet()){
					String pid = key.toString();
					String name = json.getString(key.toString());
					resources.add(new ResourceImpl(baseURL,this,name,pid));
				}
				
			}catch(Exception _e){
				_e.printStackTrace();
				resources = Collections.emptySet();
			}
			
			return resources;
		}

		@Override
		public Set<Directory> getDirectories()
		{
			return Collections.emptySet();
		}
		
	}
	
	private static class ResourceImpl implements Resource
	{
		private String baseURL;
		private String pid;
		private String name;
		private Directory directory;
		
		public ResourceImpl(String _baseURL,Directory _dir,String _name,String _pid)
		{
			baseURL = _baseURL;
			name = _name;
			pid = _pid;
			directory = _dir;
		}
		
		@Override
		public String getID()
		{
			return pid;
		}

		@Override
		public String getName()
		{
			return name;
		}

		@Override
		public Directory getParent()
		{
			return directory;
		}

		@Override
		public InputStream getInputStream() throws IOException
		{
			String url = String.format("%s/api/flv?pid=%s",baseURL,pid);
			HttpURLConnection con = (HttpURLConnection)(new URL(url)).openConnection();
			return con.getInputStream();
		}
		
	}
}
