
package com.lz.music.ui;

import com.lz.music.kuyuehui.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OptionAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mItems;
    private int[] mIds;

    public OptionAdapter(Context context, int type) {
        mContext = context;
        if (type == MusicItem.TYPE_FAVORITES_LIST) {
            mItems = mContext.getResources().getStringArray(R.array.music_options_2);
            mIds = new int[] { R.drawable.zhenling, R.drawable.ring, R.drawable.ring, R.drawable.ring, R.drawable.download };
        } else if (type == MusicItem.TYPE_MUSIC_LIST) {
            mItems = mContext.getResources().getStringArray(R.array.music_options);
            mIds = new int[] { R.drawable.shoucang, R.drawable.zhenling, R.drawable.ring, R.drawable.ring, R.drawable.ring, R.drawable.download };
        }
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_option_item, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.option_item_icon);
        icon.setImageResource(mIds[position]);

        TextView text = (TextView) convertView.findViewById(R.id.option_item_text);
        text.setText(mItems[position]);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mItems[position];
    }

    @Override
    public int getCount() {
        return mItems.length;
    }
}