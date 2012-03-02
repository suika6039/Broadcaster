package ssrv.tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

import ssrv.flv.FLVReader;
import ssrv.flv.FLVTag;
import ssrv.streamer.FLVHeaders;
import ssrv.streamer.FLVStreamer;
import ssrv.streamer.FLVStreamerClient;
import ssrv.streamer.avc.AVCFLVStreaming;
import ssrv.streamer.common.DefaultFLVStreamingScheduler;
import ssrv.streamer.common.DefaultFLVStreamingStrategy;

public class StreamerTestJetty
{
	public static void main(String _args[]) throws Exception
	{
		Server srv = new Server(8882);
		ContextHandler resourceContext = new ContextHandler("/");
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase("./jetty");
		resourceContext.addHandler(resourceHandler);
		
		FLVReader reader = new FLVReader(new FileInputStream("./flv/grisaia2.flv"));
		AVCFLVStreaming avc = new AVCFLVStreaming(reader,5000);
		FLVStreamer streamer = new FLVStreamer(avc,avc);
		
		ContextHandler servletContext = new ContextHandler("/api");
		ServletHandler servletHandler = new ServletHandler();
		servletHandler.addServletWithMapping(new ServletHolder(new StreamerServlet(streamer)),"/movie.flv");
		servletContext.addHandler(servletHandler);
		
		HandlerList list = new HandlerList();
		list.setHandlers(new Handler[]{resourceContext,servletContext});
		
		srv.setHandler(list);
		srv.start();
		srv.join();
	}
}

class StreamerServlet extends HttpServlet
{
	private FLVStreamer streamer;
	
	public StreamerServlet(FLVStreamer _streamer)
	{
		streamer = _streamer;
	}
	
	@Override
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		System.out.println("access!");
		OutputStream out = null;
		try{
			out = _res.getOutputStream();
		}catch(IOException _e){
			_e.printStackTrace();
		}
		
		if(out == null){
			return;
		}
		
		streamer.start();
		
		_res.setContentType("video/x-flv");
		
		FLVStreamerClient client = streamer.newClient();
		
		FLVHeaders headers = client.getHeaders();
		try{
			out.write(headers.getHeader().serialize());
			out.write(headers.getMetadata().serialize());
			for(FLVTag tag : headers.getOtherHeaders()){
				out.write(tag.serialize());
			}
			
			FLVTag tag = null;
			while((tag = client.getNextTag()) != FLVReader.FLV_EOF){
				out.write(tag.serialize());
				System.out.println(Thread.currentThread().getName()+":"+tag.toString());
			}
			
			System.out.println("end of stream.");
		}catch(IOException _e){
			_e.printStackTrace();
		}catch(InterruptedException _e){
			_e.printStackTrace();
		}
	}
}
