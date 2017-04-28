
package com.lz.music.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.CPManagerInterface;
import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.ChartInfo;
import com.cmsc.cmmusic.common.data.ChartListRsp;
import com.cmsc.cmmusic.common.data.OrderResult;
import com.cmsc.cmmusic.init.InitCmmInterface;
import com.lz.music.kuyuehui.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class OriginalListFragment extends ListFragment {
    private static final String TAG = "OriginalListFragment";

    private static final int MESSAGE_INIT = 0;
    private static final int MESSAGE_GET_CHART = 1;

    private TextView mInfo;
    private List<ChartInfo> mChartList;

    public static Fragment newInstance() {
        return new OriginalListFragment();
    }

    @Override
    @SuppressLint("InflateParams")
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_original, null);
        mInfo = (TextView) view.findViewById(R.id.empty_view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(getActivity(), MusicListActivity.class);
        ChartInfo chart = mChartList.get(position);
        if (chart.getChartCode().equalsIgnoreCase(getString(R.string.original_header_chart_code_cp))) {
            openCpMonth();
            return;
        }

        i.putExtra(MusicListActivity.KEY_CODE, chart.getChartCode());
        i.putExtra(MusicListActivity.KEY_NAME, chart.getChartName());
        startActivity(i);
    }

    private void init() {
        if (!InitCmmInterface.initCheck(getActivity()).equalsIgnoreCase("0")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Hashtable<String, String> result = InitCmmInterface.initCmmEnv(getActivity());
                    Message message = Message.obtain();
                    message.arg1 = Integer.parseInt(result.get("code"));
                    message.what = MESSAGE_INIT;
                    mHandler.sendMessage(message);
                }
            }).start();
        } else {
            Log.d(TAG, "Has been initialized.");
            getChartList();
        }
        // mChartList = MockData.getChartList();
        // showChartList();
    }

    private void getChartIfNeed(int code) {
        if (code == 0) {
            Log.d(TAG, "Initialization is successful.");
            getChartList();
        } else {
            showCodeMeaning(code);
        }
    }

    private void getChartList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChartListRsp chart = MusicQueryInterface.getChartInfo(getActivity(), 1, 30);
                if (chart != null) {
                    Log.d(TAG, "getChartInfo response code = " + chart.getResCode() + "    message = " + chart.getResMsg());
                    mChartList = chart.getChartInfos();
                    mHandler.sendEmptyMessage(MESSAGE_GET_CHART);
                }
            }
        }).start();
    }

    private void showChartList() {
        if (mChartList != null && mChartList.size() > 0) {
            OriginalListAdapter adapter = new OriginalListAdapter(getActivity());
            List<ChartInfo> headers = getHeaderChartInfo();
            if (headers != null && headers.size() > 0) {
                mChartList.addAll(0, headers);
            }
            adapter.setChartList(mChartList);
            mInfo.setVisibility(View.GONE);
            setListAdapter(adapter);
        }
    }

    private void showCodeMeaning(int code) {
        switch (code) {
            case 1:
                mInfo.setText("初始化失败");
                break;
            case 2:
                mInfo.setText("请检查网络连接");
                break;
            case 3:
                mInfo.setText("请使用中国移动SIM卡");
                break;
            case 4:
                mInfo.setText("无SIM卡");
                break;
            case -1:
                mInfo.setText("未知错误");
                break;
            case -2:
                mInfo.setText("connection timeout and so on");
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_INIT:
                    getChartIfNeed(msg.arg1);
                    break;
                case MESSAGE_GET_CHART:
                    showChartList();
                    break;
            }
        }
    };

    /**
     * Get the Original Music Header List
     *
     * @return the header list
     */
    private List<ChartInfo> getHeaderChartInfo() {
        List<ChartInfo> list = new ArrayList<ChartInfo>(1);
        final Resources res = getResources();
        String[] chartCodes = res.getStringArray(R.array.original_header_code);
        String[] chartNames = res.getStringArray(R.array.original_header_name);
        int length = Math.min(chartCodes.length, chartNames.length);
        for (int i = 0; i < length; i++) {
            ChartInfo chartInfo = new ChartInfo();
            chartInfo.setChartCode(chartCodes[i]);
            chartInfo.setChartName(chartNames[i]);
            list.add(chartInfo);
        }
        return list;
    }

    private void openCpMonth() {
        CPManagerInterface.openCPMonth(getActivity(),
                getString(R.string.service_id),
                null,
                new CMMusicCallback<OrderResult>() {
                    @Override
                    public void operationResult(OrderResult orderResult) {

                    }
                });
    }
}