package ssrv.jetty.rest;

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
	private Map<String,FLVPlayer> players;
	private Map<String,String> parameters;
	private boolean debug;
	
	public static final String REFRESH_INTERVAL = "REFRESH_INTERVAL";
	
	public ServerContext()
	{
		providerID = new AtomicLong();
		playerID = new AtomicLong();
		providers = new ConcurrentHashMap<String,ResourceProvider>();
		players = new ConcurrentHashMap<String,FLVPlayer>();
		parameters = new ConcurrentHashMap<String,String>();
		debug = false;
	}
	
	public void setDebugMode(boolean _debug)
	{
		debug = _debug;
	}
	
	public boolean getDebugMode()
	{
		return debug;
	}
	
	public void setParameter(String _key,String _value)
	{
		parameters.put(_key,_value);
	}
	
	public String getParameter(String _key)
	{
		return parameters.get(_key);
	}
	
	public Map<String,ResourceProvider> getResourceProviders()
	{
		return Collections.unmodifiableMap(providers);
	}
	
	public Map<String,FLVPlayer> getFLVPlayers()
	{
		return Collections.unmodifiableMap(players);
	}
	
	public void debug(String _str)
	{
		if(debug){
			System.out.println(_str);
		}
	}
	
	public long addResourceProvider(ResourceProvider _provider)
	{
		long newID = providerID.getAndIncrement();
		providers.put(Long.toString(newID),_provider);
		return newID;
	}
	
	public long addFLVPlayer(FLVPlayer _player)
	{
		long newID = playerID.getAndIncrement();
		players.put(Long.toString(newID),_player);
		return newID;
	}
	
	public ResourceProvider removeResourceProvider(String _pid)
	{
		ResourceProvider target = providers.remove(_pid);
		return target;
	}
	
	public FLVPlayer removeFLVPlayer(String _rid)
	{
		FLVPlayer target = players.remove(_rid);
		return target;
	}
}
