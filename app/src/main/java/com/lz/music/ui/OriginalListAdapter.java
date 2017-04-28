
package com.lz.music.ui;

import java.util.List;

import com.cmsc.cmmusic.common.data.ChartInfo;
import com.lz.music.kuyuehui.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OriginalListAdapter extends BaseAdapter {
    private Context mContext;
    private List<ChartInfo> mChartList;

    private int[] mIds = {
            R.drawable.tu01,
            R.drawable.tu02,
            R.drawable.tu03,
            R.drawable.tu04,
            R.drawable.tu05,
            R.drawable.tu06,
            R.drawable.tu07,
            R.drawable.tu08,
            R.drawable.tu09,
            R.drawable.tu10
    };

    public OriginalListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mChartList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.music_original_item, null);
            holder = new ViewHolder();
            holder.mChartIcon = (ImageView) convertView.findViewById(R.id.chart_icon);
            holder.mChartName = (TextView) convertView.findViewById(R.id.chart_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mChartIcon.setImageResource(mIds[position % mIds.length]);
        holder.mChartName.setText(mChartList.get(position).getChartName());

        return convertView;
    }

    public void setChartList(List<ChartInfo> list) {
        mChartList = list;
    }

    private static class ViewHolder {
        ImageView mChartIcon;
        TextView mChartName;
    }
}