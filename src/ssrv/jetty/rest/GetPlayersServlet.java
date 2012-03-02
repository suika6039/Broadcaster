package ssrv.jetty.rest;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import ssrv.player.FLVPlayer;

public class GetPlayersServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private ServerContext context;
	
	public GetPlayersServlet(ServerContext _context)
	{
		context = _context;
	}
	
	/* generate player list as JSON
	 * ex:
	 * {
	 *  "id1","playername1"
	 *  "id2","playername2"
	 * }
	 */
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		Map<String,FLVPlayer> players = context.getFLVPlayers();
		
		JSONObject res = new JSONObject();
		for(String key: players.keySet()){
			FLVPlayer player = players.get(key);
			res.put(key,player.getName());
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
