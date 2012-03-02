package ssrv.jetty.proto;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ssrv.flv.FLVReader;
import ssrv.player.FLVPlayer;
import ssrv.resource.Directory;
import ssrv.resource.Resource;
import ssrv.resource.ResourceProvider;

public class CreatePlayerServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private ServerContext context;
	public CreatePlayerServlet(ServerContext _context)
	{
		context = _context;
	}
	
	@Override
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(_res.getOutputStream());
			System.out.println("create.");
		}catch(IOException _e){
			_e.printStackTrace();
			return;
		}
		System.out.println("create2.");
		
		pw.write("<html><head><title>create</title></head><body>");
		
		String param = _req.getParameter("p");
		Location loc = Location.decodeFromBase64URLSafe(param);
		
		ResourceProvider provider = context.getResourceProviders().get(loc.getID());
		System.out.println("create3.");
		if(provider == null){
			pw.write("unknown ResourceProvider ID => "+loc.getID());
			System.out.println("create4.");
		}else{
			Resource res = null;
			Directory current = provider.getRootDirectory();
			for(String name : loc.getList()){
				boolean find = false;
				for(Directory dir : current.getDirectories()){
					if(dir.getName().equals(name)){
						find = true;
						break;
					}
				}
				
				if(!find){
					for(Resource r : current.getResources()){
						if(r.getName().equals(name)){
							res = r;
							break;
						}
					}
				}
			}
			
			if(res == null){
				pw.write("unknown ResourceID => "+loc.toString());
			}else{
				FLVReader reader;
				try{
					reader = new FLVReader(res.getInputStream());
					FLVPlayer p = new FLVPlayer(reader,5000,res.getName());
					System.out.println("player created");
					pw.println("<p>player successfully created. => "+p.getName()+"</p>");
					pw.println("<a href='./'>goto index</a>");
					context.addFLVPlayer(p);
				}catch(Exception _e){
					pw.write("error "+_e.toString());
					_e.printStackTrace();
				}
			}
		}
		
		System.out.println("create5.");
		pw.write("</body></html>");
		pw.flush();
	}
}
