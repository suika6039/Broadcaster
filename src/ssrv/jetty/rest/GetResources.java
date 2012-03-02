package ssrv.jetty.rest;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import ssrv.resource.Directory;
import ssrv.resource.Resource;
import ssrv.resource.ResourceProvider;

import net.sf.json.JSONObject;

public class GetResources extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private ServerContext context;
	
	public GetResources(ServerContext _context)
	{
		context = _context;
	}
	
	/*
	 * list all resource and return as a JSON
	 * {
	 *  provider1:{
	 *   resource1:id_hogehogehoge,
	 *   resource2:id_fugafugafuga, ...
	 *  },
	 *  provider2:{
	 *   resource1:id_hogehogehoge,
	 *   resource2:id_fugafugafuga
	 *  }
	 * }
	 */
	@Override
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		JSONObject json = new JSONObject();
		
		Map<String,ResourceProvider> providers = context.getResourceProviders();
		for(String pid : providers.keySet()){
			HashMap<String,String> resources = new HashMap<String,String>();
			ResourceProvider provider = providers.get(pid);
			Directory pwd = provider.getRootDirectory();
			LinkedList<String> path = new LinkedList<String>();
			path.add(pwd.getName());
			listResourceRecursive(pwd,resources,path);
			json.put(pid,resources);
		}
		
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(_res.getOutputStream());
		}catch(Exception _e){
			_res.setStatus(500);
			_e.printStackTrace();
		}
		
		json.write(pw);
		pw.flush();
	}
	
	public void listResourceRecursive(Directory _parent,Map<String,String> _map,LinkedList<String> _path)
	{
		//list resources
		String pathString = StringUtils.join(_path,'/');
		for(Resource res : _parent.getResources()){
			String name = pathString.concat("/").concat(res.getName());
			_map.put(name,res.getID());
		}
		
		//list directories
		for(Directory dir : _parent.getDirectories()){
			_path.add(dir.getName());
			listResourceRecursive(dir,_map,_path);
			_path.remove();
		}
	}
}
