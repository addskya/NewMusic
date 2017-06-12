
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
            R.drawable.b0a,
            R.drawable.b0b,
            R.drawable.b0d,
            R.drawable.b0e,
            R.drawable.b0f,
            R.drawable.b0g,
            R.drawable.b0h,
            R.drawable.b0i,
            R.drawable.b1k,
            R.drawable.b1l,

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
            holder.mChartNameCeil = (TextView) convertView.findViewById(R.id.chart_name_ceil);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mChartIcon.setImageResource(mIds[position % mIds.length]);
        holder.mChartName.setText(mChartList.get(position).getChartName());
        holder.mChartNameCeil.setText(mChartList.get(position).getChartName());

        return convertView;
    }

    public void setChartList(List<ChartInfo> list) {
        mChartList = list;
    }

    private static class ViewHolder {
        ImageView mChartIcon;
        TextView mChartName;
        TextView mChartNameCeil;
    }
}