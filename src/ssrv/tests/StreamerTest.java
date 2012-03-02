package ssrv.tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ssrv.flv.FLVException;
import ssrv.flv.FLVReader;
import ssrv.flv.FLVTag;
import ssrv.streamer.FLVHeaders;
import ssrv.streamer.FLVPack;
import ssrv.streamer.FLVStreamer;
import ssrv.streamer.FLVStreamerClient;
import ssrv.streamer.FLVStreamingScheduler;
import ssrv.streamer.FLVStreamingStrategy;
import ssrv.streamer.avc.AVCFLVStreaming;
import ssrv.streamer.common.DefaultFLVStreamingScheduler;
import ssrv.streamer.common.DefaultFLVStreamingStrategy;

public class StreamerTest
{
	public static void main(String _args[]) throws FLVException, IOException, InterruptedException
	{
		long interval = 5000;
		FLVReader reader = new FLVReader(new FileInputStream("./flv/hello.flv"));
		AVCFLVStreaming avc = new AVCFLVStreaming(reader,interval);
		FLVStreamingStrategy strategy = avc;
		FLVStreamingScheduler scheduler = avc;
		
		FLVStreamer streamer = new FLVStreamer(strategy,scheduler);
		streamer.start();
		
		FLVStreamerClient client = streamer.newClient();
		
		FileOutputStream out = new FileOutputStream("./flv/test3.flv");
		
		// write headers.
		FLVHeaders headers = client.getHeaders();
		out.write(headers.getHeader().serialize());
		out.write(headers.getMetadata().serialize());
		FLVPack otherHeaders = headers.getOtherHeaders();
		for(FLVTag tag : otherHeaders){
			out.write(tag.serialize());
		}
		
		System.in.read();
		
		// write body
		FLVTag tag = null;
		while((tag = client.getNextTag()) != FLVReader.FLV_EOF){
			System.out.println("tag = "+tag.toString());
			out.write(tag.serialize());
		}
		System.out.println("end of flv.");
		
		out.close();
	}
}
