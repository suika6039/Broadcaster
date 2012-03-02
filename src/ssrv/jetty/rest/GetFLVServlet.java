package ssrv.jetty.rest;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ssrv.flv.FLVHeader;
import ssrv.flv.FLVMetadata;
import ssrv.flv.FLVReader;
import ssrv.flv.FLVTag;
import ssrv.player.FLVPlayer;
import ssrv.player.FLVPlayerClient;

public class GetFLVServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	public ServerContext context;
	
	public static final String PLAYER_ID = "pid";
	
	public GetFLVServlet(ServerContext _context)
	{
		context = _context;
	}

	@Override
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		String pid = _req.getParameter(PLAYER_ID);
		
		
		OutputStream out = null;
		try{
			out = _res.getOutputStream();
		}catch(IOException _e){
			_e.printStackTrace();
			_res.setStatus(500);
		}
		
		_res.setContentType("video/x-flv");
		
		FLVPlayer player = context.getFLVPlayers().get(pid);
		FLVPlayerClient client = player.newClient();
		FLVHeader header = client.getHeader();
		FLVMetadata metadata = client.getMetadata();
		
		StreamingStrategy strategy = new VP6StreamingStrategy();
		
		try{
			out.write(header.serialize());
			out.write(metadata.serialize());
			out.flush();
			
			FLVTag tag;
			int prevTagSize = 0;
			while((tag = client.next()) != FLVReader.FLV_EOF){
				out.write(tag.serialize());
				if(prevTagSize != tag.getPrevTagSize()){
					System.out.println("PrevTagSize missmatch! at = "+tag);
				}
				
				prevTagSize = tag.getDataSize() + 11;
			}
		}catch(Exception _e){
			_e.printStackTrace();
			_res.setStatus(500);
		}
	}
	
	public static interface StreamingStrategy
	{
		public void onInitialize(FLVHeader _header,FLVMetadata _metadata,OutputStream _out);
		public void onFLVUpdate(FLVTag _tag);
		public void onFinish();
	}
	
	public static class VP6StreamingStrategy implements StreamingStrategy
	{
		@Override
		public void onInitialize(FLVHeader _header,FLVMetadata _metadata,OutputStream _out)
		{
		}

		@Override
		public void onFLVUpdate(FLVTag _tag)
		{
		}

		@Override
		public void onFinish()
		{
		}
	}

	public static class H264StreamingStrategy implements StreamingStrategy
	{
		@Override
		public void onInitialize(FLVHeader _header, FLVMetadata _metadata,OutputStream _out)
		{
		}

		@Override
		public void onFLVUpdate(FLVTag _tag)
		{
		}

		@Override
		public void onFinish()
		{
		}
	}
}
