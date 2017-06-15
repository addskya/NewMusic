
package com.lz.music.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.CPManagerInterface;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.OrderResult;
import com.cmsc.cmmusic.common.data.QueryResult;
import com.cmsc.cmmusic.init.InitCmmInterface;
import com.lz.music.kuyuehui.R;
import com.lz.music.player.MusicPlayer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class VipFragment extends ListFragment {

    private static final String TAG = "VipFragment";

    private static final int MSG_VIP_USER = 0;
    private static final int MSG_NORMAL = 1;
    private static final int MSG_ORDER_VIP_SUCCESS = 30;
    private static final int MSG_ORDER_VIP_FAILURE = 31;

    private TextView mTipsView;
    private View mOrderView;

    private static final int MESSAGE_INIT = 10;
    private static final int MESSAGE_GET_CHART = 11;

    private TextView mInfo;
    private List<MusicInfo> mMusicList;
    private MusicOption mOption;

    private View mVipView;
    private View mMusicListView;

    public static VipFragment newInstance() {
        return new VipFragment();
    }

    @Override
    @SuppressLint("InflateParams")
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_vip, container, false);
        mTipsView = (TextView) root.findViewById(R.id.tips);
        mOrderView = root.findViewById(R.id.order);
        mOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBuyCpMonthDialog();
            }
        });
        mInfo = (TextView) root.findViewById(R.id.empty_view);
        mVipView = root.findViewById(R.id.vip);
        mMusicListView = root.findViewById(R.id.music_list);
        if (!hasVipRights()) {
            mVipView.setVisibility(View.GONE);
            mMusicListView.setVisibility(View.VISIBLE);
        }
        return root;
    }

    private boolean hasVipRights() {
        return getResources().getBoolean(R.bool.configHasVipRights);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOption = new MusicOption(getActivity(), hasVipRights());
        initUserInterface();
    }

    private void initUserInterface() {
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
            checkVipRights();
        }
    }


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_VIP_USER: {
                    mVipView.setVisibility(View.GONE);
                    mMusicListView.setVisibility(View.VISIBLE);
                    getMusicList();
                    break;
                }
                case MSG_NORMAL: {
                    mVipView.setVisibility(View.VISIBLE);
                    mMusicListView.setVisibility(View.GONE);
                    break;
                }
                case MSG_ORDER_VIP_SUCCESS: {
                    Toast.makeText(getActivity(), "Cp包月订购成功", Toast.LENGTH_SHORT).show();
                    mVipView.setVisibility(View.GONE);
                    mMusicListView.setVisibility(View.VISIBLE);
                    getMusicList();
                }
                case MSG_ORDER_VIP_FAILURE: {
                    Toast.makeText(getActivity(), String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
                    mVipView.setVisibility(View.VISIBLE);
                    mMusicListView.setVisibility(View.GONE);
                    mTipsView.setText((String) msg.obj);
                    mOrderView.setVisibility(View.VISIBLE);
                }
                case MESSAGE_INIT:
                    if (msg.arg1 == 0) {
                        Log.d(TAG, "Initialization is successful.");
                        checkVipRights();
                    } else {
                        Toast.makeText(getActivity(), getCodeMeaning(msg.arg1), Toast.LENGTH_SHORT).show();
                        ;
                    }

                    break;
                case MESSAGE_GET_CHART:
                    mVipView.setVisibility(View.GONE);
                    mMusicListView.setVisibility(View.VISIBLE);
                    showChartList();
                    break;
                default: {
                    //Nothing todo.
                }
            }
        }
    };

    private void checkVipRights() {
        final Context context = getActivity();

        if (!hasVipRights()) {
            getMusicList();
            return;
        }

        new Thread() {
            @Override
            public void run() {
                QueryResult result = CPManagerInterface.queryCPMonth(context,
                        context.getString(R.string.service_id));
                Log.i("OrangeDebug", "QueryResult:" + result);
                if (result.getResCode() != null && result.getResCode().equals("000000")) {
                    mHandler.sendEmptyMessage(MSG_VIP_USER);
                } else {
                    mHandler.sendEmptyMessage(MSG_NORMAL);
                }
            }
        }.start();
    }

    private Dialog mDialog;

    private void showBuyCpMonthDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.DialogTheme);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_buy_vip, null, false);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
                buyCpMonth();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            }
        });

        builder.setView(view);
        builder.setCancelable(true);
        mDialog = builder.create();
        mDialog.show();
        ;
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mDialog = null;
            }
        });
    }

    private void buyCpMonth() {
        final Context context = getActivity();
        new Thread(new Runnable() {
            @Override
            public void run() {
                CPManagerInterface.openCPMonth(context,
                        context.getString(R.string.service_id), null, new CMMusicCallback<OrderResult>() {
                            @Override
                            public void operationResult(OrderResult result) {
                                Log.i("OrangeDebug","orderResult:" + result);
                                if (result != null) {
                                    Message message = Message.obtain();
                                    if (result.getResCode() != null && result.getResCode().equals("000000")) {
                                        mHandler.sendEmptyMessage(MSG_ORDER_VIP_SUCCESS);
                                    } else {
                                        Message msg = mHandler.obtainMessage(MSG_ORDER_VIP_FAILURE);
                                        message.obj = result.getResMsg() == null ? "CP包月失败" : result.getResMsg();
                                        msg.sendToTarget();
                                    }
                                }
                            }
                        });
            }
        }).start();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //TODO Play
        MusicPlayer.getInstance().setChartMusicList(getString(R.string.text_recommend), mMusicList);
        ((MusicItem) v).playMusic(position);
    }


    private void getMusicList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMusicList = getLocalMusicList();
                mHandler.sendEmptyMessage(MESSAGE_GET_CHART);
            }
        }).start();
    }

    private void showChartList() {
        if (mMusicList != null && mMusicList.size() > 0) {
            MusicListAdapter adapter = new MusicListAdapter(getActivity());
            adapter.addMusicList(mMusicList);
            mInfo.setVisibility(View.GONE);
            setListAdapter(adapter);
            adapter.setOnMusicOptionSelectListener(new MusicItem.OnMusicOptionSelectListener() {
                @Override
                public void onMusicOptionSelect(MusicInfo music, int position) {
                    mOption.setMusicInfo(music);
                    mOption.doOption(position);
                }
            });
        }
    }

    private String getCodeMeaning(int code) {
        switch (code) {
            case 1:
                return ("初始化失败");
            case 2:
                return ("请检查网络连接");
            case 3:
                return ("请使用中国移动SIM卡");
            case 4:
                return ("无SIM卡");
            case -1:
                return ("未知错误");
            case -2:
            default:
                return ("connection timeout and so on");

        }
    }

    /**
     * Get the Music List at local
     *
     * @return music list
     */
    private List<MusicInfo> getLocalMusicList() {
        List<MusicInfo> list = new ArrayList<MusicInfo>(1);
        Resources res = getResources();
        String[] musicIds = res.getStringArray(R.array.music_id);
        String[] musicNames = res.getStringArray(R.array.music_name);
        String[] musicAuthor = res.getStringArray(R.array.music_author);
        int musicLength = Math.min(musicIds.length, Math.min(musicNames.length, musicAuthor.length));
        for (int i = 0; i < musicLength; i++) {
            MusicInfo music = new MusicInfo();
            music.setMusicId(musicIds[i]);
            music.setSongName(musicNames[i]);
            music.setSingerName(musicAuthor[i]);
            list.add(music);
        }
        musicIds = null;
        musicNames = null;
        musicAuthor = null;
        return list;
    }
}