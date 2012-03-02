package misc.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import ssrv.io.FileCachedInputStream;
import ssrv.io.PreloadingInputStream;

public class FileChannelTest
{
	public static void main(String _args[]) throws IOException
	{
		FileInputStream in = new FileInputStream("./flv/defefe.flv");
		InputStream cache = new PreloadingInputStream(in,new File("./cache"),20*1024*1024);
		FileOutputStream out = new FileOutputStream("./flv/detetefd.flv");
		
		byte[] buf = new byte[1024];
		int total = 0;
		while(true){
			int read = cache.read(buf);
			total += read;
			if(read == -1){
				break;
			}
			out.write(buf,0,read);
		}
		System.out.println(total);
	}
}
