package ssrv.jetty.proto;

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

public class FLVStreamServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private ServerContext context;

	public FLVStreamServlet(ServerContext _context)
	{
		context = _context;
	}
	
	@Override
	public void doGet(HttpServletRequest _req,HttpServletResponse _res)
	{
		String id = _req.getParameter("id");
		OutputStream out = null;
		try{
			out = _res.getOutputStream();
		}catch(IOException _e){
			_e.printStackTrace();
		}
		
		FLVPlayer p = context.getFLVPlayers().get(id);
		
		FLVPlayerClient client = p.newClient();
		FLVHeader header = client.getHeader();
		FLVMetadata metadata = client.getMetadata();
		try {
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
			
			out.flush();
		}catch(IOException _e){
			_e.printStackTrace();
		}
	}
}
