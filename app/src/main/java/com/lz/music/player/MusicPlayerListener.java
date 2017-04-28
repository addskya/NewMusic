package com.lz.music.player;

import com.cmsc.cmmusic.common.data.MusicInfo;

public interface MusicPlayerListener {
	public void onPlayChanged(MusicInfo music);

	public void onPlayerProgress(int current);

	public void onPlayerCompleted();

	public void onPlayerPlay();

	public void onPlayerPause();

	public void onPlayerError();
}