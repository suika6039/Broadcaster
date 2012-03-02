package ssrv.jetty.proto;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ssrv.player.FLVPlayer;
import ssrv.resource.Directory;
import ssrv.resource.Resource;
import ssrv.resource.ResourceProvider;

public class APIServlet extends HttpServlet
{
	private ServerContext context;
	private static final long serialVersionUID = 1L;

	public APIServlet(ServerContext _context)
	{
		context = _context;
	}
	
	@Override
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		PrintWriter pw = null;
		
		try{
			pw = new PrintWriter(_res.getOutputStream());
		}catch(IOException _e){
			_e.printStackTrace();
		}
		
		Map<String,FLVPlayer> players = context.getFLVPlayers();
		
		StringBuffer csv = new StringBuffer();
		for(String id : players.keySet()){
			FLVPlayer p = players.get(id);
			csv.append(String.format("%s:%s,",id,p.getName()));
		}
		
		csv.deleteCharAt(csv.length() - 1);
		
		pw.write(csv.toString());
		pw.flush();
	}
	
	public static class ProxyResourceProvider implements ResourceProvider
	{
		private String server;
		private String name;
		
		public ProxyResourceProvider(String _name,String _serverURL)
		{
			name = _name;
			server = _serverURL;
		}
		
		@Override
		public String getName()
		{
			return name;
		}
		
		@Override
		public Directory getRootDirectory()
		{
			return new DirectoryImpl(name,server);
		}
		
		private class DirectoryImpl implements Directory
		{
			private String name;
			private String serverURL;
			
			public DirectoryImpl(String _name,String _serverURL)
			{
				name = _name;
				serverURL = _serverURL;
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
				HttpURLConnection con = null;
				try{
					con = (HttpURLConnection)(new URL(serverURL+"/api")).openConnection();
					con.connect();
					
					StringBuffer buf = new StringBuffer();
					BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String line;
					while((line = br.readLine()) != null){
						buf.append(line);
					}
					
					StringTokenizer players = new StringTokenizer(buf.toString(),",");
					
					HashSet<Resource> resources = new HashSet<Resource>();
					while(players.hasMoreTokens()){
						String playerToken = players.nextToken();
						StringTokenizer idAndName = new StringTokenizer(playerToken,":");
						String id = idAndName.nextToken();
						String name = idAndName.nextToken();
						resources.add(new ResourceImpl(name,String.format("%s/movie.flv?id=%s",serverURL,id),this));
					}
					br.close();
					
					return resources;
				}catch(Exception _e){
					_e.printStackTrace();
					return Collections.emptySet();
				}
			}

			@Override
			public Set<Directory> getDirectories()
			{
				return Collections.emptySet();
			}
			
		}
		
		private class ResourceImpl implements Resource
		{
			private Directory parent;
			private String name;
			private String url;
			
			public ResourceImpl(String _name,String _url,Directory _parent)
			{
				parent = _parent;
				name = _name;
				url = _url;
			}
			
			@Override
			public String getName()
			{
				return name;
			}

			@Override
			public Directory getParent()
			{
				return parent;
			}

			@Override
			public InputStream getInputStream() throws IOException
			{
				HttpURLConnection con = (HttpURLConnection)(new URL(url)).openConnection();
				con.connect();
				return con.getInputStream();
			}

			@Override
			public String getID()
			{
				return null;
			}
			
		}

		@Override
		public Resource resolve(String _id)
		{
			return null;
		}
	}
}
