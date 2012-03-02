package ssrv.jetty.proto;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.Handler;

public class FLVProxyServer
{
	public static void main(String _args[]) throws Exception
	{
		Server srv = new Server(8883);
		ServerContext context = new ServerContext();
		context.addResouceProvider(new APIServlet.ProxyResourceProvider("proxy","http://localhost:8881/servlet"));
		IndexServlet index = new IndexServlet(context);
		ResourceProviderServlet provider = new ResourceProviderServlet(context);
		CreatePlayerServlet create = new CreatePlayerServlet(context);
		FLVPlayerServlet player = new FLVPlayerServlet(context);
		FLVStreamServlet stream = new FLVStreamServlet(context);
		APIServlet api = new APIServlet(context);
		
		ContextHandler resourceContext = new ContextHandler("/files");
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase("./jetty");
		resourceContext.addHandler(resourceHandler);
		
		ContextHandler servletContext = new ContextHandler("/servlet");
		ServletHandler servletHandler = new ServletHandler();
		servletHandler.addServletWithMapping(new ServletHolder(index),"/");
		servletHandler.addServletWithMapping(new ServletHolder(provider),"/provider");
		servletHandler.addServletWithMapping(new ServletHolder(create),"/create");
		servletHandler.addServletWithMapping(new ServletHolder(player),"/player");
		servletHandler.addServletWithMapping(new ServletHolder(stream),"/movie.flv");
		servletHandler.addServletWithMapping(new ServletHolder(api),"/api");
		servletContext.addHandler(servletHandler);
		
		HandlerList list = new HandlerList();
		list.setHandlers(new Handler[]{resourceContext,servletContext});
		
		srv.setHandler(list);
		srv.start();
		srv.join();
	}
}
