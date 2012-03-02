package ssrv.jetty.rest;

import java.io.PrintWriter;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ssrv.flv.FLVReader;
import ssrv.player.FLVPlayer;
import ssrv.player.event.PlayerEventAdapter;
import ssrv.resource.Resource;
import ssrv.resource.ResourceProvider;
import net.sf.json.JSONObject;

public class CreatePlayerServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private ServerContext context;
	private AutomaticPlayerRemover remover;
	
	public static final String PROVIDER_ID = "pid";
	public static final String RESOURCE_ID = "rid";
	public static final String STATUS = "status";
	public static final String MESSAGE = "message";
	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_FAILED = "failed";
	
	public CreatePlayerServlet(ServerContext _context)
	{
		context = _context;
		remover = new AutomaticPlayerRemover(context);
	}
	
	@Override
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		JSONObject json = new JSONObject();
		
		boolean paramIsValid = true;
		
		String pid = _req.getParameter(PROVIDER_ID);
		if(pid == null){
			json.put(STATUS,STATUS_FAILED);
			json.put(MESSAGE,"parameter pid must not be null.");
			paramIsValid = false;
		}
		
		String rid = _req.getParameter(RESOURCE_ID);
		if(rid == null){
			json.put(STATUS,STATUS_FAILED);
			json.put(MESSAGE,"parameter pid must not be null.");
			paramIsValid = false;
		}
		
		if(paramIsValid){
			context.debug("paramIsValid");
			generateJSON(pid,rid,json);
		}
		
		
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(_res.getOutputStream());
		}catch(Exception _e){
			_res.setStatus(500);
		}
		
		context.debug(json.toString());
		json.write(pw);
		pw.flush();
	}
	
	public void generateJSON(String _pid,String _rid,JSONObject _json)
	{
		ResourceProvider provider = context.getResourceProviders().get(_pid);
		if(provider == null){
			_json.put(STATUS,STATUS_FAILED);
			_json.put(MESSAGE,"invalid pid.");
			return;
		}
		
		Resource res = provider.resolve(_rid);
		if(res == null){
			_json.put(STATUS,STATUS_FAILED);
			_json.put(MESSAGE,"invalid rid.");
			return;
		}
		
		FLVReader reader = null;
		try{
			reader = new FLVReader(res.getInputStream());
			int interval = Integer.parseInt(context.getParameter(ServerContext.REFRESH_INTERVAL));
			FLVPlayer player = new FLVPlayer(reader,interval,res.getName());
			
			long id = context.addFLVPlayer(player);
			remover.addPlayer(player,Long.toString(id));
			
			_json.put(STATUS,STATUS_SUCCESS);
			_json.put(MESSAGE,"the player was successfully created.");
			_json.put("id",Long.toString(id));
			
		}catch(Exception _e){
			String cause = _e.getMessage();
			_json.put(STATUS,STATUS_FAILED);
			_json.put(MESSAGE,cause);
		}
	}
	
	private static class AutomaticPlayerRemover extends PlayerEventAdapter
	{
		private ServerContext context;
		private ConcurrentHashMap<FLVPlayer,String> reverseIdTable;
		
		public AutomaticPlayerRemover(ServerContext _context)
		{
			if(_context == null){
				throw new NullPointerException("_context is null.");
			}
			
			context = _context;
			reverseIdTable = new ConcurrentHashMap<FLVPlayer,String>();
		}
		
		public void addPlayer(FLVPlayer _player,String _pid)
		{
			if(_player == null || _pid == null || _pid.length() == 0){
				throw new NullPointerException("_player == null or _pid == null or _pid.length() == 0");
			}
			
			reverseIdTable.put(_player,_pid);
			_player.addPlayerEventListener(this);
		}
		
		@Override
		public void onFinish(FLVPlayer _player)
		{
			String pid = reverseIdTable.get(_player);
			if(pid == null){
				return;
			}
			
			System.out.println("removing player pid="+pid);
			context.removeFLVPlayer(pid);
		}
	}
}
