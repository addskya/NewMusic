
package com.lz.music.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.CPManagerInterface;
import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.ChartInfo;
import com.cmsc.cmmusic.common.data.ChartListRsp;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.OrderResult;
import com.cmsc.cmmusic.init.InitCmmInterface;
import com.lz.music.kuyuehui.R;
import com.lz.music.player.MusicPlayer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class RecommendFragment extends ListFragment {
    private static final String TAG = "OriginalListFragment";

    private static final int MESSAGE_INIT = 0;
    private static final int MESSAGE_GET_CHART = 1;

    private TextView mInfo;
    private List<MusicInfo> mMusicList;

    public static Fragment newInstance() {
        return new RecommendFragment();
    }

    @Override
    @SuppressLint("InflateParams")
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_original, null);
        mInfo = (TextView) view.findViewById(R.id.empty_view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //TODO Play
        MusicPlayer.getInstance().setChartMusicList(getString(R.string.text_recommend), mMusicList);
        ((MusicItem) v).playMusic(position);
    }

    private void init() {
        if (!InitCmmInterface.initCheck(getActivity()).equalsIgnoreCase("0")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Hashtable<String, String> result = InitCmmInterface.initCmmEnv(getActivity());
                    Message message = Message.obtain();
                    message.arg1 = Integer.parseInt(result.get("code"));
                    message.what = MESSAGE_INIT;
                    mHandler.sendMessage(message);
                }
            }).start();
        } else {
            Log.d(TAG, "Has been initialized.");
            getMusicList();
        }

    }

    private void getChartIfNeed(int code) {
        if (code == 0) {
            Log.d(TAG, "Initialization is successful.");
            getMusicList();
        } else {
            showCodeMeaning(code);
        }
    }

    private void getMusicList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMusicList = getLocalMusicList();
                mHandler.sendEmptyMessage(MESSAGE_GET_CHART);
            }
        }).start();
    }

    private void showChartList() {
        if (mMusicList != null && mMusicList.size() > 0) {
            MusicListAdapter adapter = new MusicListAdapter(getActivity());
            adapter.addMusicList(mMusicList);
            mInfo.setVisibility(View.GONE);
            setListAdapter(adapter);
        }
    }

    private void showCodeMeaning(int code) {
        switch (code) {
            case 1:
                mInfo.setText("初始化失败");
                break;
            case 2:
                mInfo.setText("请检查网络连接");
                break;
            case 3:
                mInfo.setText("请使用中国移动SIM卡");
                break;
            case 4:
                mInfo.setText("无SIM卡");
                break;
            case -1:
                mInfo.setText("未知错误");
                break;
            case -2:
                mInfo.setText("connection timeout and so on");
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_INIT:
                    getChartIfNeed(msg.arg1);
                    break;
                case MESSAGE_GET_CHART:
                    showChartList();
                    break;
            }
        }
    };


    /**
     * Get the Music List at local
     *
     * @return music list
     */
    private List<MusicInfo> getLocalMusicList() {
        List<MusicInfo> list = new ArrayList<MusicInfo>(1);
        Resources res = getResources();
        String[] musicIds = res.getStringArray(R.array.music_id);
        String[] musicNames = res.getStringArray(R.array.music_name);
        String[] musicAuthor = res.getStringArray(R.array.music_author);
        int musicLength = Math.min(musicIds.length, Math.min(musicNames.length, musicAuthor.length));
        for (int i = 0; i < musicLength; i++) {
            MusicInfo music = new MusicInfo();
            music.setMusicId(musicIds[i]);
            music.setSongName(musicNames[i]);
            music.setSingerName(musicAuthor[i]);
            list.add(music);
        }
        musicIds = null;
        musicNames = null;
        musicAuthor = null;
        return list;
    }
}