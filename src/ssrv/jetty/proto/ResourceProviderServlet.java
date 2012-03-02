package ssrv.jetty.proto;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ssrv.resource.Directory;
import ssrv.resource.Resource;
import ssrv.resource.ResourceProvider;

public class ResourceProviderServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private ServerContext context;
	
	public ResourceProviderServlet(ServerContext _context)
	{
		context = _context;
	}
	
	@Override
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		String id = _req.getParameter("id");
		
		try {
			PrintWriter pw = new PrintWriter(_res.getOutputStream());
			pw.write("<html><head><title>Resouces.</title></head><body>");
			
			//list all resources recursively.
			ResourceProvider provider = context.getResourceProviders().get(id);
			Directory rootdir = provider.getRootDirectory();
			pw.write("<h2>List of resources.</h2>");
			printResource(pw,new Location(id),rootdir);
			
			pw.write("</body></html>");
			pw.flush();
		}catch(IOException _e){
			_e.printStackTrace();
		}
	}
	
	private static void printResource(PrintWriter _pw,Location _path,Directory _dir)
	{
		for(Resource res : _dir.getResources()){
			_pw.write(String.format("<a href='./create?p=%s'>%s</a><br/>",_path.add(res.getName()).encodeToBase64URLSafe(),res.getName()));
		}
		
		for(Directory dir : _dir.getDirectories()){
			printResource(_pw,_path.add(dir.getName()),dir);
		}
	}
	
}
