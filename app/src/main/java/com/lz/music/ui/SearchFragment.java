
package com.lz.music.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.MusicListRsp;
import com.lz.music.kuyuehui.R;
import com.lz.music.player.MusicPlayer;
import com.lz.music.ui.MusicItem.OnMusicOptionSelectListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchFragment extends ListFragment {
    private static final int MESSAGE_SHOW_SEARCH_RESULT = 0;

    private View mSearch;
    private EditText mText;
    private TextView mInfo;

    private List<MusicInfo> mMusicList;
    private MusicOption mOption;

    @Override
    @SuppressLint("InflateParams")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.music_search, null);
        mSearch = view.findViewById(R.id.btn_search);
        mText = (EditText) view.findViewById(R.id.et_search);
        mInfo = (TextView) view.findViewById(R.id.tv_info);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        MusicPlayer.getInstance().setMusicList(mMusicList);
        ((MusicItem) v).playMusic(position);
    }

    private void searchMusic() {
        if (TextUtils.isEmpty(mText.getText())) {
            Toast.makeText(getActivity(), "关键字不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String key = URLEncoder.encode(mText.getText().toString(), "UTF-8");
                    MusicListRsp list = MusicQueryInterface.getMusicsByKey(getActivity(), key, "0", 1, 30);
                    if (list != null) {
                        mMusicList = list.getMusics();
                        mHandler.sendEmptyMessage(MESSAGE_SHOW_SEARCH_RESULT);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOption = new MusicOption(getActivity(),false);
        mSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMusic();
            }
        });
        mText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                    case EditorInfo.IME_ACTION_DONE: {
                        searchMusic();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void showSearchResult() {
        if (mMusicList != null && mMusicList.size() > 0) {
            MusicListAdapter adapter = new MusicListAdapter(getActivity());
            adapter.setOnMusicOptionSelectListener(mListener);
            adapter.addMusicList(mMusicList);
            setListAdapter(adapter);

            MusicPlayer.getInstance().setMusicList(mMusicList);
        } else {
            getListView().setVisibility(View.GONE);
            mInfo.setVisibility(View.VISIBLE);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_SEARCH_RESULT:
                    showSearchResult();
                    break;
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

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }
}