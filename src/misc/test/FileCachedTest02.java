package misc.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileCachedTest02
{
	public static final byte[] DATA = new byte[]{0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F,0x0F};
	
	public static void main(String _args[]) throws IOException
	{
		File tmp = File.createTempFile("pett",".dat",new File("./cache"));
		RandomAccessFile file = new RandomAccessFile(tmp,"rw");
		file.setLength(1000000000);
		ByteBuffer buffer = file.getChannel().map(FileChannel.MapMode.READ_WRITE,0,Integer.MAX_VALUE);
		System.out.println(buffer.capacity());
		file.setLength(2000000000);
		System.out.println(file.length());
	}
}
