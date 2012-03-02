package ssrv.player.event;

import ssrv.player.FLVPlayer;
import ssrv.player.FLVPlayerClient;

public interface PlayerEventListener
{
	public void onPlay(FLVPlayer _player);
	public void onPause(FLVPlayer _player);
	public void onFinish(FLVPlayer _player);
	public void onRefreshChunk(FLVPlayer _player);
	public void onClientAdded(FLVPlayer _player,FLVPlayerClient _client);
}
