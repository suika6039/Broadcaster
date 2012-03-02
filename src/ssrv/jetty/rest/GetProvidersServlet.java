package ssrv.jetty.rest;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import ssrv.resource.ResourceProvider;

public class GetProvidersServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private ServerContext context;
	
	public GetProvidersServlet(ServerContext _context)
	{
		context = _context;
	}
	
	@Override
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		Map<String,ResourceProvider> providers = context.getResourceProviders();
		
		JSONObject res = new JSONObject();
		for(String key: providers.keySet()){
			ResourceProvider provider = providers.get(key);
			res.put(key,provider.getName());
		}
		
		
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(_res.getOutputStream());
		}catch(Exception _e){
			_res.setStatus(500);
			return;
		}
		
		_res.setContentType("application/json; charset=utf-8");
		res.write(pw);
		pw.flush();
	}
}
