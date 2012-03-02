package ssrv.jetty.rest;

import java.util.concurrent.atomic.AtomicLong;

import ssrv.player.FLVPlayer;
import ssrv.player.FLVPlayerClient;
import ssrv.player.event.PlayerEventAdapter;

public class FLVPlayerContext
{
	private FLVPlayer player;
	private PlayerEventAdapterImpl eventAdapter;
	
	public FLVPlayerContext(FLVPlayer _player)
	{
		player = _player;
		eventAdapter = new PlayerEventAdapterImpl();
		player.addPlayerEventListener(eventAdapter);
	}
	
	public long getActiveClientsCount()
	{
		return 0;
	}
	
	public long getElapsedTime()
	{
		return 0;
	}
	
	public FLVPlayer getPlayer()
	{
		return player;
	}
	
	public long getNumberOfClientsCreated()
	{
		return eventAdapter.getNumberOfClientsCreated();
	}
	
	private static class PlayerEventAdapterImpl extends PlayerEventAdapter
	{
		private AtomicLong numClientCreate;
		
		public PlayerEventAdapterImpl()
		{
			numClientCreate = new AtomicLong();
		}
		
		@Override
		public void onClientAdded(FLVPlayer _player,FLVPlayerClient _client)
		{
			numClientCreate.incrementAndGet();
		}
		
		public long getNumberOfClientsCreated()
		{
			return numClientCreate.get();
		}
	}
}
