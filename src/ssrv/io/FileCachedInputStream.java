package ssrv.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.ReentrantLock;

public class FileCachedInputStream extends InputStream
{
	private final Preloader loader;
	private final File cache;
	private final MappedByteBuffer buffer;
	private volatile int position;
	
	public FileCachedInputStream(final InputStream _in,File _dir,int _cacheSize) throws IOException
	{
		if(_dir.isDirectory()){
			cache = File.createTempFile("tmp",".dat",_dir);
			cache.deleteOnExit();
			RandomAccessFile file = new RandomAccessFile(cache,"rw");
			file.setLength(_cacheSize);
			buffer = file.getChannel().map(FileChannel.MapMode.READ_WRITE,0,file.length());
			loader = new Preloader(_in,buffer);
			loader.start();
		}else{
			throw new IllegalArgumentException("the _dir was not directory");
		}
	}
	
	public static final class Preloader extends Thread
	{
		private static final int BUF_SIZE = 1024*8;
		private final InputStream in;
		private final byte[] buffer;
		private final ReentrantLock fairLock;
		private final MappedByteBuffer cache;
		
		public Preloader(final InputStream _in,MappedByteBuffer _cache) throws FileNotFoundException
		{
			in = _in;
			cache = _cache;
			buffer = new byte[BUF_SIZE];
			fairLock = new ReentrantLock(true);
		}
		
		@Override
		public void run()
		{
			while(!Thread.interrupted()){
				try {
					if(getAndPut() != -1){
						continue;
					}
					return;
				}catch(IOException _e){
					_e.printStackTrace();
				}
			}
		}
		
		public int getAndPut() throws IOException
		{
			try{
				fairLock.lock();
				int read = in.read(buffer);
				if(read != -1){
					cache.put(buffer,0,read);
				}
				fairLock.unlock();
				return read;
			}catch(IOException _e){
				fairLock.unlock();
				throw _e;
			}
		}
	}
	
	@Override
	public int read() throws IOException
	{
		while(true){
			try{
				if(position >= buffer.position()){
					int ret = loader.getAndPut();
					if(ret == -1){
						return -1;
					}
					continue;
				}	
				
				byte data = buffer.get(position);
				position++;
				return data & 0xFF;
			}catch(IndexOutOfBoundsException _e){
				_e.printStackTrace();
				return -1;
			}
		}
	}
	
	@Override
	public void close() throws IOException
	{
		super.close();
		loader.interrupt();
		try {
			loader.join();
			cache.delete();
		}catch(InterruptedException _e){
			Thread.currentThread().interrupt();
		}
	}
}
