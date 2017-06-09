
package com.lz.music.ui;

import java.util.ArrayList;
import java.util.List;

import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.MusicListRsp;
import com.lz.music.kuyuehui.R;
import com.lz.music.player.MusicPlayer;
import com.lz.music.player.PlayerResponser;
import com.lz.music.ui.MusicItem.OnMusicOptionSelectListener;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MusicListActivity extends ListActivity {
    public static final String KEY_CODE = "key_code";
    public static final String KEY_NAME = "key_name";

    private static final int MESSAGE_SHOW_MUSIC = 0;

    private MusicListAdapter mAdapter;
    private String mChartCode;
    private String mChartName;
    private List<MusicInfo> mMusicList;
    private int mPage = 1;
    private boolean mHasNotMoreData;

    private TextView mInfo;
    private TextView mTitle;
    private MusicPlayerPanel mPlayerPanel;

    private MusicOption mOption;
    private PlayerResponser mPlayerResponser;

    private LinearLayout mFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_list);

        mChartCode = getIntent().getStringExtra(KEY_CODE);
        mChartName = getIntent().getStringExtra(KEY_NAME);

        init();
    }

    private void init() {
        mInfo = (TextView) findViewById(R.id.empty_view);
        mTitle = (TextView) findViewById(R.id.tv_chart_name);
        mTitle.setText(mChartName);

        mPlayerPanel = (MusicPlayerPanel) findViewById(R.id.player_panel);
        mPlayerPanel.updatePlayer(MusicPlayer.getInstance().getCurrentMusic());

        mOption = new MusicOption(this);
        mPlayerResponser = new PlayerResponser(mPlayerPanel);

        getListView().setOnScrollListener(mOnScrollListener);

        MusicPlayer.getInstance().addMusicPlayerListener(mPlayerResponser);

        getMusicList();
    }

    @SuppressLint("InflateParams")
    private void initFooterView() {
        mFooter = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.footer_view, null);
        getListView().addFooterView(mFooter);
    }

    private void getMusicList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MusicListRsp list = MusicQueryInterface.getMusicsByChartId(MusicListActivity.this, mChartCode, mPage, 30);
                if (list != null) {
                    // Log.d(TAG, "code = " + list.getResCode() + "    message = " + list.getResMsg());
                    mMusicList = list.getMusics();
                    if (list.getResCode().equals("300002")) {
                        mHasNotMoreData = true;
                        rollbackPage();
                    } else {
                        if (mMusicList == null || mMusicList.size() == 0) {
                            rollbackPage();
                        }
                    }
                    mHandler.sendEmptyMessage(MESSAGE_SHOW_MUSIC);
                } else {
                    rollbackPage();
                }
            }
        }).start();
    }

    private void rollbackPage() {
        if (--mPage < 0) {
            mPage = 0;
        }
    }

    private void showMusicList() {
        if (mHasNotMoreData) {
            getListView().removeFooterView(mFooter);
        }

        if (mMusicList != null && mMusicList.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new MusicListAdapter(this);
                mAdapter.setOnMusicOptionSelectListener(mListener);
                initFooterView();
                setListAdapter(mAdapter);
            }
            mInfo.setVisibility(View.GONE);
            mAdapter.addMusicList(mMusicList);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MusicPlayer.getInstance().setChartMusicList(mChartCode, mAdapter.getMusicList());
        ((MusicItem) v).playMusic(position);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_MUSIC:
                    showMusicList();
                    break;
            }
        }
    };

    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (view.getLastVisiblePosition() == view.getCount() - 1 && !mHasNotMoreData) {
                mPage++;
                getMusicList();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mHasNotMoreData) {
                getListView().removeFooterView(mFooter);
            }
        }
    };

    private OnMusicOptionSelectListener mListener = new OnMusicOptionSelectListener() {
        @Override
        public void onMusicOptionSelect(MusicInfo music, int position) {
            mOption.setMusicInfo(music);
            mOption.doOption(position);
        }
    };

    public void onClickBack(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayer.getInstance().removeMusicPlayerListener(mPlayerResponser);
    }

}