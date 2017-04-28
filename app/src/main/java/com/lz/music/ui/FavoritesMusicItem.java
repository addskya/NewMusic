
package com.lz.music.ui;

import com.lz.music.kuyuehui.R;

import android.content.Context;
import android.util.AttributeSet;

public class FavoritesMusicItem extends MusicItem {
    public FavoritesMusicItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void showOptionDialog(int type) {
        super.showOptionDialog(TYPE_FAVORITES_LIST);
    }

    protected void setMusicIndex(int position) {
        mIndex.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.shoucang1), null, null, null);
    }
}