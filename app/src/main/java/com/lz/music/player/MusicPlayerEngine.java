package com.lz.music.player;

import java.util.List;

import com.cmsc.cmmusic.common.data.MusicInfo;

public interface MusicPlayerEngine {

	public List<MusicInfo> getPlaylist();

	public void play();

	public boolean isPlaying();

	public void stop();

	public void pause();

	public void next();

	public void back();

	public void addMusicPlayerListener(MusicPlayerListener l);

	public void removeMusicPlayerListener(MusicPlayerListener l);
}