
package com.lz.music.ui;

import com.cmsc.cmmusic.common.data.MusicInfo;
import com.lz.music.kuyuehui.R;
import com.lz.music.player.MusicPlayer;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicPlayerPanel extends RelativeLayout {
    private TextView mNameAndSinger;
    private TextView mTime;

    private View mPlay;

    private ProgressBar mProgress;

    public MusicPlayerPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.music_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoMigu();
            }
        });
        mNameAndSinger = (TextView) findViewById(R.id.tv_name_singer);
        mTime = (TextView) findViewById(R.id.tv_time);
        mProgress = (ProgressBar) findViewById(R.id.progress_time);

        mPlay = findViewById(R.id.btn_play);
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

        findViewById(R.id.btn_next)
                .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().next();
            }
        });

        findViewById(R.id.btn_previous)
                .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().back();
            }
        });
    }

    private void gotoMigu(){
        Intent intent = new Intent();
        final Context context = getContext();
        String pkgName = context.getString(R.string.migu_pkg_name);
        String clsName = context.getString(R.string.migu_cls_name);
        ComponentName cn = new ComponentName(pkgName, clsName);
        intent.setComponent(cn);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            String miguUrl = context.getString(R.string.url_migu_index);
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setData(Uri.parse(miguUrl));
            context.startActivity(viewIntent);
        }
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
            mPlay.setBackgroundResource(R.drawable.bg_pause);
        } else {
            mPlay.setBackgroundResource(R.drawable.bg_play);
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