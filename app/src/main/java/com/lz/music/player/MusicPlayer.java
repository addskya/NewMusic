
package com.lz.music.player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.text.TextUtils;

import com.cmsc.cmmusic.common.OnlineListenerMusicInterface;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.StreamRsp;
import com.lz.music.MusicApp;

public class MusicPlayer implements MusicPlayerEngine {
    private static MusicPlayer sInstance;

    private Context mContext;
    private Handler mHandler;
    private MediaPlayer mMediaPlayer;
    private MusicInfo mCurrentMusic;

    private int mPosition;
    private int mDuration;
    private String mChartCode;
    private List<MusicInfo> mPlayList;
    private List<MusicPlayerListener> mListeners;

    private MusicPlayer(Context c) {
        mContext = c;
        mHandler = new Handler();
        mMediaPlayer = new MediaPlayer();
        mPlayList = new ArrayList<MusicInfo>();
        mListeners = new ArrayList<MusicPlayerListener>();
    }

    public static void init(Context c) {
        if (sInstance == null) {
            sInstance = new MusicPlayer(c);
        }
    }

    public static MusicPlayer getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("MusicPlayer Uninitialized.");
        }

        return sInstance;
    }

    private void killMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    notifyPlayerStop();
                }
            });
        }
    }

    private void play(String url) {
        try {
            killMediaPlayer();

            // mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

            mDuration = mMediaPlayer.getDuration() / 1000;
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.post(mUpdateTimeTask);

            notifyPlayChanged();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setChartMusicList(String chartCode, List<MusicInfo> list) {
        if (chartCode.equals(mChartCode)) {
            if (mPlayList.size() != list.size()) {
                mPlayList.clear();
                mPlayList.addAll(list);
            }
        } else {
            mChartCode = chartCode;
            mPlayList.clear();
            mPlayList.addAll(list);
        }
    }

    public void setMusicList(List<MusicInfo> list) {
        mPlayList.clear();
        mPlayList.addAll(list);
        mChartCode = null;
    }

    public void play(int position) {
        mPosition = position;
        mCurrentMusic = mPlayList.get(position);
        File file = new File(MusicApp.APP_DIR + mCurrentMusic.getSongName() + ".mp3");

        if (file.exists()) {
            play(file.getAbsolutePath());
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    StreamRsp stream = OnlineListenerMusicInterface.getStream(mContext, mCurrentMusic.getMusicId(), "0");
                    if (stream != null && !TextUtils.isEmpty(stream.getStreamUrl())) {
                        String url = stream.getStreamUrl();
                        if (url != null) {
                            play(url);
                        }
                    }
                }
            }).start();
        }
    }

    public MusicInfo getCurrentMusic() {
        return mCurrentMusic;
    }

    @Override
    public void play() {
        if (!isPlaying() && mPlayList.size() > 0) {
            mMediaPlayer.start();
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.post(mUpdateTimeTask);
            notifyPlayerPlay();
        }
    }

    @Override
    public List<MusicInfo> getPlaylist() {
        return mPlayList;
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
        notifyPlayerStop();
    }

    @Override
    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            notifyPlayerPause();
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void next() {
        mPosition++;
        if (mPosition >= mPlayList.size()) {
            mPosition = mPlayList.size() - 1;
        }

        playCurrentMusic();
    }

    @Override
    public void back() {
        mPosition--;
        if (mPosition < 0) {
            mPosition = 0;
        }

        playCurrentMusic();
    }

    @Override
    public void addMusicPlayerListener(MusicPlayerListener l) {
        mListeners.add(l);
    }

    @Override
    public void removeMusicPlayerListener(MusicPlayerListener l) {
        mListeners.remove(l);
    }

    public int getDuration() {
        return mDuration;
    }

    private void playCurrentMusic() {
        if (mPlayList.size() > 0) {
            play(mPosition);
        }
    }

    private void notifyPlayChanged() {
        for (MusicPlayerListener l : mListeners) {
            if (l != null) {
                l.onPlayChanged(getCurrentMusic());
            }
        }
    }

    private void notifyPlayerProgress() {
        for (MusicPlayerListener l : mListeners) {
            if (l != null) {
                l.onPlayerProgress(mMediaPlayer.getCurrentPosition() / 1000);
            }
        }
    }

    private void notifyPlayerStop() {
        for (MusicPlayerListener l : mListeners) {
            if (l != null) {
                l.onPlayerCompleted();
            }
        }
    }

    private void notifyPlayerPlay() {
        for (MusicPlayerListener l : mListeners) {
            if (l != null) {
                l.onPlayerPlay();
            }
        }
    }

    private void notifyPlayerPause() {
        for (MusicPlayerListener l : mListeners) {
            if (l != null) {
                l.onPlayerPause();
            }
        }
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            notifyPlayerProgress();
            mHandler.postDelayed(this, 1000);
        }
    };
}