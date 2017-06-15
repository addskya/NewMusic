
package com.lz.music.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.MusicListRsp;
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
    private MusicOption mOption;

    private int mPage = 1;
    private boolean mHasNotMoreData;

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
        mOption = new MusicOption(getActivity(), false);
        getListView().setOnScrollListener(mOnScrollListener);
        init();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //TODO Play
        MusicPlayer.getInstance().setChartMusicList(getString(R.string.text_recommend), mAdapter.getMusicList());
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
                List<MusicInfo> list = getMusicList("117");
                if (list != null && list.size() > 0) {
                    Message msg = mHandler.obtainMessage(MESSAGE_GET_CHART);
                    msg.obj = list;
                    msg.sendToTarget();
                }
            }
        }).start();
    }

    private MusicListAdapter mAdapter;

    private void showChartList(List<MusicInfo> list) {
        if (mHasNotMoreData) {
            getListView().removeFooterView(mFooter);
        }
        if (list != null && list.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new MusicListAdapter(getActivity());
                mAdapter.setOnMusicOptionSelectListener(new MusicItem.OnMusicOptionSelectListener() {
                    @Override
                    public void onMusicOptionSelect(MusicInfo music, int position) {
                        mOption.setMusicInfo(music);
                        mOption.doOption(position);
                    }
                });
                initFooterView();
                setListAdapter(mAdapter);
            }
            mAdapter.addMusicList(list);
            mInfo.setVisibility(View.GONE);

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
                    showChartList((List<MusicInfo>) msg.obj);
                    break;
            }
        }
    };

    private List<MusicInfo> getMusicList(String chartCode) {
        MusicListRsp response = MusicQueryInterface.getMusicsByChartId(getActivity(), chartCode, mPage, 30);
        if (response != null) {
            List<MusicInfo> list = new ArrayList<MusicInfo>();
            // Log.d(TAG, "code = " + list.getResCode() + "    message = " + list.getResMsg());
            if (response.getResCode().equals("300002")) {
                mHasNotMoreData = true;
                rollbackPage();

            } else {
                List<MusicInfo> result = response.getMusics();
                if (result == null || result.size() == 0) {
                    rollbackPage();
                } else {
                    list.addAll(result);
                }
            }
            return list;
        } else {
            rollbackPage();
        }
        return null;
    }

    private void rollbackPage() {
        if (--mPage < 0) {
            mPage = 0;
        }
    }

    private LinearLayout mFooter;

    @SuppressLint("InflateParams")
    private void initFooterView() {
        mFooter = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.footer_view, null);
        getListView().addFooterView(mFooter);
    }

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
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

}