package ssrv.jetty.rest;

import java.io.File;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

import ssrv.jetty.rest.config.ConfigurationLoader;
import ssrv.resource.FileResourceProvider;

public class APIProxyServer
{
	public static void main(String _args[]) throws Exception
	{
		
		APIProxyServer srv = new APIProxyServer(null);
		srv.start();
		srv.join();
	}
	
	private Server srv;
	private ServerContext context;
	
	public APIProxyServer(ConfigurationLoader _loader)
	{
		srv = new Server(8886);
		context = new ServerContext();
		context.setDebugMode(true);
		context.setParameter(ServerContext.REFRESH_INTERVAL,"5000");
		context.addResourceProvider(new APIProxyResourceProvider("http://localhost:8881","localhost"));
		
		//initialize servlets.
		GetProvidersServlet providers = new GetProvidersServlet(context);
		GetPlayersServlet players = new GetPlayersServlet(context);
		ResourceProviderBrowser browse = new ResourceProviderBrowser(context);
		CreatePlayerServlet create = new CreatePlayerServlet(context);
		GetFLVServlet flv = new GetFLVServlet(context);
		GetResources resources = new GetResources(context);
	
		//test api
		
		ContextHandler resourceContext = new ContextHandler("/");
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase("./rest");
		resourceContext.addHandler(resourceHandler);
		ContextHandler servletContext = new ContextHandler("/api");
		ServletHandler servletHandler = new ServletHandler();
		servletHandler.addServletWithMapping(new ServletHolder(providers),"/providers");
		servletHandler.addServletWithMapping(new ServletHolder(players),"/players");
		servletHandler.addServletWithMapping(new ServletHolder(browse),"/browse");
		servletHandler.addServletWithMapping(new ServletHolder(create),"/create");
		servletHandler.addServletWithMapping(new ServletHolder(flv),"/flv");
		servletHandler.addServletWithMapping(new ServletHolder(resources),"/resources");
		servletContext.addHandler(servletHandler);
		
		HandlerList list = new HandlerList();
		list.setHandlers(new Handler[]{resourceContext,servletContext});
		
		srv.setHandler(list);
	}
	
	public void start() throws Exception
	{
		srv.start();
	}
	
	public void join() throws InterruptedException
	{
		srv.join();
	}
}
