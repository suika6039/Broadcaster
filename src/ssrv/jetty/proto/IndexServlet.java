package ssrv.jetty.proto;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ssrv.player.FLVPlayer;
import ssrv.resource.ResourceProvider;

public class IndexServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private ServerContext context;
	
	public IndexServlet(ServerContext _context)
	{
		context = _context;
	}
	
	@Override
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		Map<String,ResourceProvider> providers = context.getResourceProviders();
		Map<String,FLVPlayer> players = context.getFLVPlayers();
		
		try {
			PrintWriter pw = new PrintWriter(_res.getOutputStream());
			
			pw.write("<html><head><title>test server.</title></head><body>\n");
			//print providers
			pw.write("<h2>ResourceProviders</h2>\n");
			for(String providerID : providers.keySet()){
				ResourceProvider provider = providers.get(providerID);
				String name = provider.getRootDirectory().getName();
				pw.write(String.format("<a href='./provider?id=%s'>%s</a><br/>",providerID,name));
			}
			pw.write("<h2>FLVPlayers</h2>");
			for(String playerID : players.keySet()){
				FLVPlayer player = players.get(playerID);
				String name = player.getName();
				pw.write(String.format("<a href='./player?id=%s'>%s:%s</a><br/>",playerID,playerID,name));
			}
			pw.write("</body></html>");
			pw.flush();
			System.out.println("logggg");
		}catch(IOException _e){
			_e.printStackTrace();
		}
	}
}
