
package com.lz.music.ui;

import com.cmsc.cmmusic.common.data.MusicInfo;
import com.lz.music.kuyuehui.R;
import com.lz.music.player.MusicPlayer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MusicItem extends RelativeLayout {
    public static final int TYPE_MUSIC_LIST = 0;
    public static final int TYPE_FAVORITES_LIST = 1;

    protected Context mContext;
    private TextView mName;
    private TextView mSinger;
    protected TextView mIndex;
    protected View mMore;

    protected MusicInfo mMusic;
    protected OnMusicOptionSelectListener mListener;
    private AlertDialog mDialog;

    public MusicItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflateView();
    }

    private void setMoreOnClickListener() {
        mMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog(TYPE_MUSIC_LIST);
            }
        });
    }

    protected void setMusicIndex(int position) {
        mIndex.setText(String.valueOf(position + 1));
    }

    private void inflateView() {
        mName = (TextView) findViewById(R.id.tv_name);
        mSinger = (TextView) findViewById(R.id.tv_singer);
        mIndex = (TextView) findViewById(R.id.tv_index);
        mMore = findViewById(R.id.btn_more);
        setMoreOnClickListener();
    }

    @SuppressLint("InflateParams")
    protected void showOptionDialog(final int type) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_option, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        String name = mMusic == null ? "未知" : mMusic.getSongName();
        title.setText(name);
        ListView list = (ListView) view.findViewById(android.R.id.list);
        list.setAdapter(new OptionAdapter(mContext, type));
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    if (type == TYPE_MUSIC_LIST) {
                        mListener.onMusicOptionSelect(mMusic, position);
                    } else if (type == TYPE_FAVORITES_LIST) {
                        mListener.onMusicOptionSelect(mMusic, position + 1);
                    }
                }
            }
        });

        Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mDialog = null;
            }
        });
        mDialog = builder.show();
    }

    public void setMusicInfo(MusicInfo music, int position) {
        mMusic = music;
        String name = music.getSongName();
        String singer = music.getSingerName();
        mName.setText(TextUtils.isEmpty(name) ? "未知" : name);
        mSinger.setText(TextUtils.isEmpty(singer) ? "未知" : singer);
        setMusicIndex(position);
    }

    public void playMusic(int position) {
        MusicPlayer.getInstance().play(position);
    }

    public void setOnMusicOptionSelectListener(OnMusicOptionSelectListener l) {
        mListener = l;
    }

    public static interface OnMusicOptionSelectListener {
        public void onMusicOptionSelect(MusicInfo music, int position);
    }
}
