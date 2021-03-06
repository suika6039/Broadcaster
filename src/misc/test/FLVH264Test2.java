package misc.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.io.HexDump;

import ssrv.flv.FLVException;
import ssrv.flv.FLVHeader;
import ssrv.flv.FLVMetadata;
import ssrv.flv.FLVReader;
import ssrv.flv.FLVTag;

public class FLVH264Test2
{
	public static void main(String _args[]) throws Exception
	{
		FLVReader flv = new FLVReader(new FileInputStream("./flv/hello.flv"));
		FLVHeader header = flv.getHeader();
		FLVMetadata metadata = flv.getMetadata();
		
		int timeStamp = 30000;
		OutputStream out = new FileOutputStream("./flv/h264.flv");
		double duration = (Double)metadata.getProperty(FLVMetadata.PropertyName.duration);
		metadata.setProperty(FLVMetadata.PropertyName.duration,duration - (double)(timeStamp/1000));
		out.write(header.serialize());
		out.write(metadata.serialize());
		
		FLVTag tag;
		FLVTag sequenceHeader = null;
		boolean flagWrite = false;
		FLVTag lastKeyFrame = null;
		//LinkedList<FLVTag> list;
		int cur = 0;
		while((tag = flv.getNextTag()) != FLVReader.FLV_EOF){
			System.in.read();
			System.out.println("tag.");
			int time = tag.getTimeStamp();
			if(flagWrite){
				out.write(tag.serialize());
			}
			if(tag.getTagType() == FLVTag.FLVTAG_VIDEODATA){
				byte[] body = tag.getBody();
				ByteBuffer buf = ByteBuffer.wrap(body);
				byte b = buf.get();
				int frame = (b & 0xF0) >> 4;
				if(frame == 1){
					System.out.println(tag.getTimeStamp() - cur);
					cur = tag.getTimeStamp();
					lastKeyFrame = tag;
				}
				if(time > timeStamp && sequenceHeader != null && !flagWrite){
					flagWrite = true;
					System.out.println("startwriting..");
					out.write(lastKeyFrame.serialize());
					out.write(tag.serialize());
				}
				int codecID = (b & 0x0F);
				//System.out.println(frame);
				if(codecID == 7){
					byte pt = buf.get();
					if(pt == 0){
						System.out.println("AVC sequence header");
						HexDump.dump(body, 0, System.out, 0);
						sequenceHeader = tag;
						out.write(tag.serialize());
					}
				}
			}
		}
	}
}
