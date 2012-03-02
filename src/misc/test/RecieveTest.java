package misc.test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.HexDump;

import ssrv.flv.FLVException;
import ssrv.flv.FLVReader;
import ssrv.flv.FLVTag;

public class RecieveTest
{
	public static void main(String _args[]) throws MalformedURLException, IOException, FLVException
	{
		HttpURLConnection con = (HttpURLConnection)(new URL("http://localhost:8881/servlet/movie.flv?id=0")).openConnection();
		
		byte[] buf = new byte[1000];
		DataInputStream in = new DataInputStream(con.getInputStream());
		in.readFully(buf);
		HexDump.dump(buf,buf.length,System.out,0);
		
		/*
		FLVReader reader = new FLVReader(con.getInputStream());
		
		FLVTag tag = null;
		while((tag = reader.getNextTag()) != FLVReader.FLV_EOF){
			System.out.println(String.format("%d:%d",tag.getTagType(),tag.getDataSize()));
		}
		*/
	}
}
