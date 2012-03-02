package ssrv.jetty.proto;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FLVPlayerServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	public FLVPlayerServlet(ServerContext _context)
	{
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
		
		String id = _req.getParameter("id");
		
		pw.println("<html><head><title>player</title></head><body>");
		pw.println("<div id='mediaplayer'>player</div>");
		pw.println("<script type='text/javascript' src='../files/jwplayer.js'></script>");
		pw.println("<script type='text/javascript'>");
		pw.println("jwplayer('mediaplayer').setup({flashplayer: '../files/player.swf',file: './movie.flv?id="+id+"'});");
		pw.println("</script>");
		
		pw.flush();
	}
}
