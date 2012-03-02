package ssrv.jetty.proto;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import ssrv.player.FLVPlayer;
import ssrv.resource.ResourceProvider;

public class ServerContext
{
	private AtomicLong providerID;
	private AtomicLong playerID;
	private Map<String,ResourceProvider> providers;
	private Map<String,FLVPlayer> player;
	
	public ServerContext()
	{
		providerID = new AtomicLong();
		playerID = new AtomicLong();
		providers = new ConcurrentHashMap<String,ResourceProvider>();
		player = new ConcurrentHashMap<String,FLVPlayer>();
	}
	
	public Map<String,ResourceProvider> getResourceProviders()
	{
		return Collections.unmodifiableMap(providers);
	}
	
	public Map<String,FLVPlayer> getFLVPlayers()
	{
		return Collections.unmodifiableMap(player);
	}
	
	public void addResouceProvider(ResourceProvider _provider)
	{
		long newID = providerID.getAndIncrement();
		providers.put(Long.toString(newID),_provider);
	}
	
	public void addFLVPlayer(FLVPlayer _player)
	{
		long newID = playerID.getAndIncrement();
		player.put(Long.toString(newID),_player);
	}
}
