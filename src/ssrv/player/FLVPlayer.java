package ssrv.player;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import ssrv.flv.FLVException;
import ssrv.flv.FLVHeader;
import ssrv.flv.FLVMetadata;
import ssrv.flv.FLVReader;
import ssrv.flv.FLVTag;
import ssrv.player.event.PlayerEventListener;

public class FLVPlayer implements Runnable
{
	private final FLVReader reader;
	private final int refreshInterval;
	private final ConcurrentHashMap<String,String> parameters;
	
	private String name;
	private volatile FLVChunk current;
	private volatile Thread thread;
	private final CountDownLatch latch;
	
	private CopyOnWriteArrayList<PlayerEventListener> listeners;
	private FLVStreamingStrategy strategy = null;
	
	public FLVPlayer(FLVReader _reader,int _refreshInterval,String _name)
	{
		this(_reader,_refreshInterval);
		name = _name;
	}
	
	public FLVPlayer(FLVReader _reader,int _refreshInterval)
	{
		name = "noname";
		reader = _reader;
		refreshInterval = _refreshInterval;
		current = null;
		latch = new CountDownLatch(1);
		thread = new Thread(this);
		parameters = new ConcurrentHashMap<String,String>();
		listeners = new CopyOnWriteArrayList<PlayerEventListener>();
	}
	
	public void addPlayerEventListener(PlayerEventListener _listener)
	{
		listeners.add(_listener);
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getParameter(String _key)
	{
		return parameters.get(_key);
	}
	
	public void setParameter(String _key,String _value)
	{
		parameters.put(_key,_value);
	}
	
	public String removeParameter(String _key)
	{
		return parameters.remove(_key);
	}
	
	public FLVPlayerClient newClient()
	{
		play();
		
		FLVHeader header = reader.getHeader();
		FLVMetadata meta = reader.getMetadata();
		FLVPlayerClient client = new FLVPlayerClient(header,meta,refreshInterval,
			new FLVChunkHolder(){
				@Override
				public FLVChunk getNextChunk()
				{
					try{
						latch.await();
					}catch(InterruptedException _e){
						_e.printStackTrace();
						Thread.currentThread().interrupt();
						return null;
					}
					return current;
				}
			});
		
		for(PlayerEventListener listener : listeners){
			listener.onClientAdded(this,client);
		}
		
		return client;
	}
	
	public synchronized void play()
	{
		if(thread.isAlive()){
			return;
		}
		thread.start();
		
		for(PlayerEventListener listener: listeners){
			listener.onPlay(this);
		}
	}
	
	public synchronized void pause()
	{
		thread.interrupt();
	}
	
	@Override
	public void run()
	{
		while(!Thread.interrupted()){
			try{
				FLVTag tag;
				LinkedList<FLVTag> list = new LinkedList<FLVTag>();
				
				long refreshStart = System.currentTimeMillis();
				
				tag = reader.getNextTag();
				int ts = 0;
				if(tag != FLVReader.FLV_EOF){
					ts = tag.getTimeStamp();
					list.add(tag);
				}else{
					current = null;
					for(PlayerEventListener listener : listeners){
						listener.onFinish(this);
					}
					break;
				}
				
				while((tag = reader.getNextTag()) != FLVReader.FLV_EOF){
					int diff = tag.getTimeStamp() - ts;
					if(diff > refreshInterval){
						list.add(tag);
						break;
					}
					list.add(tag);
				}
				
				FLVChunk chunk = new FLVChunk(list);
				current = chunk;
				latch.countDown();
				for(PlayerEventListener listener : listeners){
					listener.onRefreshChunk(this);
				}
				
				try{
					Thread.sleep(refreshInterval - (refreshStart - System.currentTimeMillis()));
				}catch(InterruptedException _e){
					_e.printStackTrace();
					break;
				}
			}catch(FLVException _e){
				_e.printStackTrace();
				break;
			}
		}
		
		if(Thread.interrupted()){
			for(PlayerEventListener listener: listeners){
				listener.onPause(this);
			}
		}
	}
}
