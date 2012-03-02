package ssrv.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PreloadingInputStream extends InputStream
{
	private final BlockingQueue<ByteBuffer> theBuffers;
	private volatile ByteBuffer buffer;
	private final Preloader loader;
	private int position;
	
	public PreloadingInputStream(InputStream _in,File _cacheDir,int _size)
	{
		if(!_cacheDir.isDirectory()){
			throw new IllegalArgumentException("_cacheDir was not directory.");
		}
		
		theBuffers = new LinkedBlockingQueue<ByteBuffer>();
		loader = new Preloader(_in,_cacheDir,_size,theBuffers);
		loader.start();
		
		position = 1;
		buffer = ByteBuffer.allocate(position);
	}

	@Override
	public int read() throws IOException
	{
		if(buffer.capacity() == position){
			try{
				//change to next buffer , take it from blocking queue.
				buffer = theBuffers.take();
				position = 0; //reset the position
				System.out.println("Changing to new buffer");
			}catch(InterruptedException _e) {
				// return -1 when the thread is interrupt during blocking.
				Thread.currentThread().interrupt();
				return -1;
			}
		}
		
		while(buffer.position() <= position){
			int stat = loader.preload();
			switch(stat){
				case Preloader.CREATE_BUFFER:
				case Preloader.PRELOAD_OK:
					continue;
				case Preloader.END_OF_STREAM:
					return -1;
			}
		}
		
		byte b = buffer.get(position);
		position ++;
		
		// translate to byte to unsigned
		return b & 0xFF; 
	}
	
	@Override
	public void close()
	{
		loader.cleanUp();
	}

	private static class Preloader extends Thread
	{
		private final InputStream in;
		private final File cacheDir;
		private final int cacheSize;
		private final BlockingQueue<ByteBuffer> theBuffers;
		private volatile ByteBuffer buffer;
		
		private final byte[] array = new byte[1024*8];
		
		public Preloader(InputStream _in,File _cacheDir,int _size,BlockingQueue<ByteBuffer> _theBuffers)
		{
			in = _in;
			cacheDir = _cacheDir;
			cacheSize = (_size / array.length)*array.length;
			theBuffers = _theBuffers;
		}
		
		private static MappedByteBuffer createBuffer(File _cacheDir,int _size) throws IOException
		{
			File cache = File.createTempFile("tmp",".dat",_cacheDir);
			cache.deleteOnExit();
			RandomAccessFile randomAccessFile = new RandomAccessFile(cache,"rw");
			randomAccessFile.setLength(_size);
			FileChannel channel = randomAccessFile.getChannel();
			MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE,0,randomAccessFile.length());
			return buffer;
		}
		
		public void cleanUp()
		{
			interrupt();
			try{
				join();
			}catch(InterruptedException _e){
				Thread.currentThread().interrupt();
			}
			
			try{
				in.close();
			}catch(IOException _e){
				_e.printStackTrace();
			}
		}
		
		public static final int CREATE_BUFFER = 0;
		public static final int PRELOAD_OK = 1;
		public static final int END_OF_STREAM = -1;
		
		private synchronized int preload()
		{
			try{
				if(buffer.remaining() == 0){
					return CREATE_BUFFER;
				}
				
				int read = in.read(array);
				
				if(read != -1){
					buffer.put(array,0,read);
					return PRELOAD_OK;
				}
			}catch(IOException _e){
				_e.printStackTrace();
			}
			
			return END_OF_STREAM;
		}
		
		@Override
		public void run()
		{
			while(!Thread.interrupted()){
				try{
					buffer = createBuffer(cacheDir,cacheSize);
					theBuffers.add(buffer);
					
					while(true){
						switch(preload()){
							case CREATE_BUFFER:
								break;
							case PRELOAD_OK:
								continue;
							case END_OF_STREAM:
								return;
						}
						break;
					}
				}catch(IOException _e){
					_e.printStackTrace();
					break;
				}
			}
		}
	}
}
