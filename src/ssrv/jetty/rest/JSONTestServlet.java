package ssrv.jetty.rest;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public class JSONTestServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		JSONObject res = new JSONObject();
		String p = _req.getParameter("p");
		if(p != null){
			System.out.println("hogefe= "+p);
			res.put("p",p);
		}
		
		res.put("hogefuga","fugafuga");
		res.put("aaaaaa",new String[]{"aaa","bbb","ccc","ddd"});
		res.put("fafefefe","feeeee");
		
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(_res.getOutputStream());
		}catch(Exception _e){
			_res.setStatus(500);
			return;
		}
		
		res.write(pw);
		pw.flush();
	}
	
	@Override
	public void doPost(HttpServletRequest _req,HttpServletResponse _res)
	{
		
	}
}
