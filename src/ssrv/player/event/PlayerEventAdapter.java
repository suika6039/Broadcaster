package ssrv.player.event;

import ssrv.player.FLVPlayer;
import ssrv.player.FLVPlayerClient;

public abstract class PlayerEventAdapter implements PlayerEventListener
{

	@Override
	public void onPlay(FLVPlayer _player)
	{
	}

	@Override
	public void onPause(FLVPlayer _player)
	{
	}

	@Override
	public void onFinish(FLVPlayer _player)
	{
	}
	
	@Override
	public void onRefreshChunk(FLVPlayer _player)
	{
	}

	@Override
	public void onClientAdded(FLVPlayer _player, FLVPlayerClient _client)
	{
	}
}
