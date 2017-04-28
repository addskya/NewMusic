
package com.lz.music.ui;

import java.util.ArrayList;
import java.util.List;

import com.cmsc.cmmusic.common.data.MusicInfo;
import com.lz.music.kuyuehui.R;
import com.lz.music.ui.MusicItem.OnMusicOptionSelectListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MusicListAdapter extends BaseAdapter {
    private Context mContext;
    private List<MusicInfo> mMusicList;
    private OnMusicOptionSelectListener mListener;

    public MusicListAdapter(Context c) {
        mContext = c;
        mMusicList = new ArrayList<MusicInfo>();
    }

    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.music_item, null);
        }

        ((MusicItem) convertView).setMusicInfo(mMusicList.get(position), position);
        ((MusicItem) convertView).setOnMusicOptionSelectListener(mListener);

        return convertView;
    }

    public void addMusicList(List<MusicInfo> list) {
        if (list != null && list.size() > 0) {
            mMusicList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public List<MusicInfo> getMusicList() {
        return mMusicList;
    }

    public void setOnMusicOptionSelectListener(OnMusicOptionSelectListener l) {
        mListener = l;
    }
}