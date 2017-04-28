
package com.lz.music.ui;

import java.util.ArrayList;
import java.util.List;

import com.cmsc.cmmusic.common.data.MusicInfo;
import com.lz.music.database.MusicContent.Music;
import com.lz.music.kuyuehui.R;
import com.lz.music.player.MusicPlayer;
import com.lz.music.ui.MusicItem.OnMusicOptionSelectListener;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FavoritesFragment extends ListFragment {
    private MusicQueryHandler mQueryHandler;
    private FavoritesAdapter mAdapter;
    private MusicOption mOption;

    private List<MusicInfo> mMusicList;

    @Override
    @SuppressLint("InflateParams")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.music_favorites, null);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        MusicPlayer.getInstance().setMusicList(mMusicList);
        ((MusicItem) v).playMusic(position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMusicList = new ArrayList<MusicInfo>();
        mOption = new MusicOption(getActivity());
        mAdapter = new FavoritesAdapter(getActivity());
        mAdapter.setOnMusicOptionSelectListener(mListener);
        setListAdapter(mAdapter);
        mQueryHandler = new MusicQueryHandler(getActivity().getContentResolver());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            refreshMusicList();
        }
    }

    private void refreshMusicList() {
        mQueryHandler.startQuery(0, null, Music.CONTENT_URI, Music.CONTENT_PROJECTION, null, null, null);
    }

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    private class MusicQueryHandler extends AsyncQueryHandler {
        public MusicQueryHandler(ContentResolver contentResolver) {
            super(contentResolver);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    MusicInfo music = new MusicInfo();
                    music.setMusicId(cursor.getString(Music.ID_COLUMN));
                    music.setSongName(cursor.getString(Music.NAME_COLUMN));
                    music.setSingerName(cursor.getString(Music.SINGER_COLUMN));
                    mMusicList.add(music);
                }
                mAdapter.changeCursor(cursor);
            }
        }
    }

    private OnMusicOptionSelectListener mListener = new OnMusicOptionSelectListener() {
        @Override
        public void onMusicOptionSelect(MusicInfo music, int position) {
            mOption.setMusicInfo(music);
            mOption.doOption(position);
        }
    };
}