
package com.lz.music.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.lz.music.kuyuehui.R;

public class RecommendMusicItem extends MusicItem {
    public RecommendMusicItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void showOptionDialog(int type) {
        super.showOptionDialog(TYPE_MUSIC_RECOMMEND);
    }

    protected void setMusicIndex(int position) {
        mIndex.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.shoucang1), null, null, null);
    }
}