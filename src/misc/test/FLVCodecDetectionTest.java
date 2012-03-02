package misc.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

import ssrv.flv.FLVException;
import ssrv.flv.FLVMetadata;
import ssrv.flv.FLVReader;
import ssrv.flv.FLVMetadata.PropertyName;
import ssrv.flv.FLVTag;

public class FLVCodecDetectionTest
{
	public static void main(String _args[]) throws FileNotFoundException, FLVException
	{
		FLVReader reader = new FLVReader(new FileInputStream("./flv/hello.flv"));
		FLVMetadata metadata = reader.getMetadata();
		
		double aCodec = (Double)metadata.getProperty(PropertyName.audiocodecid);
		System.out.println(aCodec);
		if(aCodec == 10){
			System.out.println("Audio AAC");
		}
		if(aCodec == 2){
			System.out.println("Audio MP3");
		}
		
		double vCodec = (Double)metadata.getProperty(PropertyName.videocodecid);
		System.out.println(vCodec);
		if(vCodec == 7){
			System.out.println("Video AVC");
		}
		
		FLVTag tag = null;
		while((tag = reader.getNextTag()) != FLVReader.FLV_EOF){
			switch(tag.getTagType()){
			case FLVTag.FLVTAG_AUDIODATA:
				ByteBuffer buf = ByteBuffer.wrap(tag.getBody());
				byte b = buf.get();
				int format = (b & 0xF0) >> 4;
				byte b2 = buf.get();
				if(format == 10 && b2 == 0){
					System.out.println("AAC Sequence Header");
				}
				return;
			case FLVTag.FLVTAG_SCRIPTDATA:
				break;
			case FLVTag.FLVTAG_VIDEODATA:
				break;
			default:
				System.out.println("unknown tag");
				break;
			}
		}
	}
}
