
package com.lz.music.ui;

import com.cmsc.cmmusic.common.data.MusicInfo;
import com.lz.music.kuyuehui.R;
import com.lz.music.player.MusicPlayer;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicPlayerPanel extends RelativeLayout {
    private TextView mNameAndSinger;
    private TextView mTime;

    private Button mPlay;
    private Button mPlayNext;
    private Button mPlayBack;

    private SeekBar mProgress;

    public MusicPlayerPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNameAndSinger = (TextView) findViewById(R.id.tv_name_singer);
        mTime = (TextView) findViewById(R.id.tv_time);

        mPlay = (Button) findViewById(R.id.btn_play);
        mPlayNext = (Button) findViewById(R.id.btn_next);
        mPlayBack = (Button) findViewById(R.id.btn_previous);
        mProgress = (SeekBar) findViewById(R.id.progress_time);

        mPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicPlayer.getInstance().isPlaying()) {
                    MusicPlayer.getInstance().pause();
                } else {
                    MusicPlayer.getInstance().play();
                }
            }
        });

        mPlayNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().next();
            }
        });

        mPlayBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().back();
            }
        });
    }

    public void updatePlayer(MusicInfo music) {
        if (music != null) {
            String name = music.getSongName();
            String singer = music.getSingerName();
            String nameAndSinger = (TextUtils.isEmpty(name) ? "未知" : name) + "-" + (TextUtils.isEmpty(singer) ? "未知" : singer);
            mNameAndSinger.setText(nameAndSinger);
            updatePlayButton(MusicPlayer.getInstance().isPlaying());
        }
    }

    public void updatePlayButton(boolean isPlaying) {
        if (isPlaying) {
            mPlay.setBackgroundResource(R.drawable.zanting);
        } else {
            mPlay.setBackgroundResource(R.drawable.play);
        }
    }

    public void updateCompleted() {
        updatePlayButton(false);
        mProgress.setProgress(0);
        mProgress.setSecondaryProgress(0);
        mTime.setText("00:00 | 00:00");
    }

    public void updateTime(int current) {
        int total = MusicPlayer.getInstance().getDuration();
        String totalTime = formatTime(total);
        String currentTime = formatTime(current);

        mProgress.setMax(total);
        mProgress.setProgress(current);
        mProgress.setSecondaryProgress(current);
        mTime.setText(currentTime + " | " + totalTime);
    }

    private String formatTime(int time) {
        int minute1 = time / 60 / 10;
        int minute2 = time / 60 % 10;
        int second1 = time / 10 % 6;
        int second2 = time % 10;

        return String.valueOf(minute1) + String.valueOf(minute2) + ":" + String.valueOf(second1) + String.valueOf(second2);
    }
}