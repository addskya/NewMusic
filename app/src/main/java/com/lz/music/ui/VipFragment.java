
package com.lz.music.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.CPManagerInterface;
import com.cmsc.cmmusic.common.data.OrderResult;
import com.cmsc.cmmusic.common.data.QueryResult;
import com.lz.music.kuyuehui.R;

import org.w3c.dom.Text;

public class VipFragment extends Fragment {

    private static final int MSG_VIP_USER = 0;
    private static final int MSG_NORMAL = 1;
    private static final int MSG_ORDER_VIP_SUCCESS = 30;
    private static final int MSG_ORDER_VIP_FAILURE = 31;

    private TextView mTipsView;
    private View mOrderView;

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
                buyCPMonth();
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkVipRights();
    }

    private void checkVipRights() {
        final Context context = getActivity();
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

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_VIP_USER: {
                    mOrderView.setVisibility(View.GONE);
                    mTipsView.setText(R.string.text_vip_rights);
                    break;
                }
                case MSG_NORMAL: {
                    mOrderView.setVisibility(View.VISIBLE);
                    mTipsView.setText(R.string.text_not_vip);
                    break;
                }
                case MSG_ORDER_VIP_SUCCESS: {
                    mTipsView.setText((String) msg.obj);
                    mOrderView.setVisibility(View.GONE);
                }
                case MSG_ORDER_VIP_FAILURE: {
                    mTipsView.setText((String) msg.obj);
                    mOrderView.setVisibility(View.VISIBLE);
                }
                default: {
                    //Nothing todo.
                }
            }
        }
    };

    private void buyCPMonth() {
        final Context context = getActivity();
        new Thread(new Runnable() {
            @Override
            public void run() {
                CPManagerInterface.openCPMonth(context,
                        context.getString(R.string.service_id), null, new CMMusicCallback<OrderResult>() {
                            @Override
                            public void operationResult(OrderResult result) {
                                if (result != null) {
                                    Message message = Message.obtain();
                                    if (result.getResCode() != null && result.getResCode().equals("000000")) {
                                        Message msg = mHandler.obtainMessage(MSG_ORDER_VIP_SUCCESS);
                                        msg.obj = getString(R.string.text_not_vip);
                                        msg.sendToTarget();
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

    public static VipFragment newInstance() {
        return new VipFragment();
    }
}