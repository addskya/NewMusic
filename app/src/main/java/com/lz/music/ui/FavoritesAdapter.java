
package com.lz.music.ui;

import com.cmsc.cmmusic.common.data.MusicInfo;
import com.lz.music.database.MusicContent.Music;
import com.lz.music.kuyuehui.R;
import com.lz.music.ui.MusicItem.OnMusicOptionSelectListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class FavoritesAdapter extends CursorAdapter {
    private OnMusicOptionSelectListener mListener;

    @SuppressWarnings("deprecation")
    public FavoritesAdapter(Context context) {
        super(context, null);
    }

    @Override
    @SuppressLint("InflateParams")
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorites_music_item, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MusicInfo music = new MusicInfo();
        music.setMusicId(cursor.getString(Music.ID_COLUMN));
        music.setSongName(cursor.getString(Music.NAME_COLUMN));
        music.setSingerName(cursor.getString(Music.SINGER_COLUMN));

        ((FavoritesMusicItem) view).setMusicInfo(music, cursor.getPosition());
        ((FavoritesMusicItem) view).setOnMusicOptionSelectListener(mListener);
    }

    public void setOnMusicOptionSelectListener(OnMusicOptionSelectListener l) {
        mListener = l;
    }
}
