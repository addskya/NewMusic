package com.lz.music.player;

import android.os.Handler;
import android.os.Message;

import com.cmsc.cmmusic.common.data.MusicInfo;
import com.lz.music.ui.MusicPlayerPanel;

public class PlayerResponser implements MusicPlayerListener {
	private static final int MESSAGE_PLAYER_COMPLETED = 0;
	private static final int MESSAGE_PLAYER_PROGRESS = 1;
	private static final int MESSAGE_PLAYER_PLAY = 2;
	private static final int MESSAGE_PLAYER_PAUSE = 3;
	private static final int MESSAGE_PLAYER_ERROR = 4;
	private static final int MESSAGE_PLAYER_CHANGED = 5;

	private MusicPlayerPanel mPanel;

	public PlayerResponser(MusicPlayerPanel panel) {
		mPanel = panel;
	}

	@Override
	public void onPlayerCompleted() {
		mHandler.sendEmptyMessage(MESSAGE_PLAYER_COMPLETED);
	}

	@Override
	public void onPlayerProgress(int current) {
		Message message = Message.obtain();
		message.arg1 = current;
		message.what = MESSAGE_PLAYER_PROGRESS;
		mHandler.sendMessage(message);
	}

	@Override
	public void onPlayerPlay() {
		mHandler.sendEmptyMessage(MESSAGE_PLAYER_PLAY);
	}

	@Override
	public void onPlayerPause() {
		mHandler.sendEmptyMessage(MESSAGE_PLAYER_PAUSE);
	}

	@Override
	public void onPlayerError() {
		mHandler.sendEmptyMessage(MESSAGE_PLAYER_ERROR);
	}

	@Override
	public void onPlayChanged(MusicInfo music) {
		Message message = Message.obtain();
		message.what = MESSAGE_PLAYER_CHANGED;
		message.obj = music;
		mHandler.sendMessage(message);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_PLAYER_COMPLETED:
				mPanel.updateCompleted();
				break;
			case MESSAGE_PLAYER_PROGRESS:
				mPanel.updateTime(msg.arg1);
				break;
			case MESSAGE_PLAYER_PLAY:
				mPanel.updatePlayer(MusicPlayer.getInstance().getCurrentMusic());
				break;
			case MESSAGE_PLAYER_PAUSE:
				mPanel.updatePlayButton(false);
				break;
			case MESSAGE_PLAYER_ERROR:
				mPanel.updatePlayButton(false);
				break;
			case MESSAGE_PLAYER_CHANGED:
				MusicInfo music = (MusicInfo) msg.obj;
				mPanel.updatePlayer(music);
				break;
			}
		}
	};
}
