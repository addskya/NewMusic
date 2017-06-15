
package com.lz.music.ui;

import java.io.File;

import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.CPManagerInterface;
import com.cmsc.cmmusic.common.FullSongManagerInterface;
import com.cmsc.cmmusic.common.RingbackManagerInterface;
import com.cmsc.cmmusic.common.VibrateRingManagerInterface;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.OrderResult;
import com.cmsc.cmmusic.common.data.QueryResult;
import com.lz.music.MusicApp;
import com.lz.music.database.MusicContent.Music;
import com.lz.music.download.DownloadMusic;
import com.lz.music.kuyuehui.R;
import com.lz.music.util.GetPublicKey;
import com.lz.music.util.MusicUtil;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class MusicOption {

    private static final String TAG = "MusicOption";

    private static final int MESSAGE_DOWNLOAD_VIBRATERING = 0;
    private static final int MESSAGE_DOWNLOAD_RINGBACK = 1;
    private static final int MESSAGE_DOWNLOAD_MUSIC = 2;
    private static final int MESSAGE_DOWNLOAD_RING_MONTH = 3;
    private static final int MESSAGE_DOWNLOAD_CP_MONTH = 4;

    private Context mContext;
    private MusicInfo mMusic;

    private String mVibrateRingDownloadUrl;
    private String mMusicDownloadUrl;

    private boolean mCpStore;

    public MusicOption(Context c, boolean cpStore) {
        mContext = c;
        mCpStore = cpStore;
    }

    public void setMusicInfo(MusicInfo music) {
        mMusic = music;
    }

    public void doOption(int position) {
        switch (position) {
            case 0:
                favorites();
                break;
            //case 1:
            //    setVibrateRing();
            //    break;
            case 1:
                setRingBack();
                break;
            // case 3:
            // buyRingMonth();
            // break;
            // case 4:
            // buyCPMonth();
            // break;
            case 2:
                if (mCpStore) {
                    downloadCpMusic();
                } else {
                    downloadMusic();
                }
                break;
        }
    }

    private void favorites() {
        ContentValues values = new ContentValues();
        values.put(Music.ID, mMusic.getMusicId());
        values.put(Music.NAME, mMusic.getSongName());
        values.put(Music.SINGER, mMusic.getSingerName());
        Uri uri = mContext.getContentResolver().insert(Music.CONTENT_URI, values);
        if (uri != null) {
            Toast.makeText(mContext, "收藏 " + mMusic.getSongName() + " 成功", Toast.LENGTH_LONG).show();
        }
    }

    private void setVibrateRing() {
        File file = new File(MusicApp.APP_DIR + mMusic.getSongName() + ".mp3");
        if (file.exists()) {
            MusicUtil.setVibrateRing(mContext, file.getAbsolutePath());
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    VibrateRingManagerInterface.queryVibrateRingDownloadUrl(mContext, mMusic.getMusicId(), new CMMusicCallback<OrderResult>() {
                        public void operationResult(OrderResult result) {
                            if (result != null) {
                                Log.d("lz", "VibrateRing url = " + result.getDownUrl());
                                mVibrateRingDownloadUrl = result.getDownUrl();
                                mHandler.sendEmptyMessage(MESSAGE_DOWNLOAD_VIBRATERING);
                            }
                        }
                    });
                }
            }).start();
        }
    }

    private void setRingBack() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RingbackManagerInterface.buyRingBack(mContext, mMusic.getMusicId(), new CMMusicCallback<OrderResult>() {
                    @Override
                    public void operationResult(OrderResult result) {
                        Log.i(TAG, "OrderResult:" + result);
                        if (result != null) {
                            Message message = Message.obtain();
                            message.what = MESSAGE_DOWNLOAD_RINGBACK;
                            if (result.getResCode() != null && result.getResCode().equals("000000")) {
                                message.arg1 = 0;
                            } else {
                                message.arg1 = 1;
                                message.obj = TextUtils.isEmpty(result.getResMsg()) ? "设置彩铃失败" : result.getResMsg();
                            }
                            mHandler.sendMessage(message);
                        }
                    }
                });
            }
        }).start();
    }

    // private void buyRingMonth() {
    // new Thread(new Runnable() {
    // @Override
    // public void run() {
    // RingbackManagerInterface.OwnRingMonth(mContext, new
    // CMMusicCallback<Result>() {
    // @Override
    // public void operationResult(Result result) {
    // if (result != null) {
    // Message message = Message.obtain();
    // message.what = MESSAGE_DOWNLOAD_RING_MONTH;
    // if (result.getResCode().equals("000000")) {
    // message.arg1 = 0;
    // } else {
    // message.arg1 = 1;
    // message.obj = result.getResMsg();
    // }
    // mHandler.sendMessage(message);
    // }
    // }
    // });
    // }
    // }).start();
    // }
    //

    private void downloadMusic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("lz", "downloadMusic id = " + mMusic.getMusicId());
                FullSongManagerInterface.getFullSongDownloadUrl(mContext, mMusic.getMusicId(),
                        new CMMusicCallback<OrderResult>() {
                            @Override
                            public void operationResult(OrderResult result) {
                                Log.i(TAG, "OrderResult:" + result);
                                if (result != null) {
                                    Log.d(TAG, "Full song url = " + result.getDownUrl());
                                    mMusicDownloadUrl = result.getDownUrl();
                                    Message msg = mHandler.obtainMessage(MESSAGE_DOWNLOAD_MUSIC);
                                    if (result.getResCode() != null && result.getResCode().equals("000000")) {
                                        msg.arg1 = 0;
                                    } else {
                                        msg.arg1 = 1;
                                        msg.obj = TextUtils.isEmpty(result.getResMsg()) ? "操作失败" : result.getResMsg();
                                    }
                                    msg.sendToTarget();
                                }
                            }
                        });
            }
        }).start();
    }

    private void downloadCpMusic() {
        new Thread() {
            @Override
            public void run() {
                Log.d("lz", "downloadCpMusic id = " + mMusic.getMusicId());
                String serviceId = mContext.getResources().getString(R.string.service_id);
                CPManagerInterface.queryCPFullSongDownloadUrl(mContext, serviceId,
                        mMusic.getMusicId(), "", new CMMusicCallback<OrderResult>() {
                            @Override
                            public void operationResult(OrderResult result) {
                                Log.i(TAG, "OrderResult:" + result);
                                Log.d(TAG, "OrderResult:" + result);
                                if (result != null) {
                                    Log.d(TAG, "Full song url = " + result.getDownUrl());
                                    mMusicDownloadUrl = result.getDownUrl();
                                    Message msg = mHandler.obtainMessage(MESSAGE_DOWNLOAD_MUSIC);
                                    if (result.getResCode() != null && result.getResCode().equals("000000")) {
                                        msg.arg1 = 0;
                                    } else {
                                        msg.arg1 = 1;
                                        msg.obj = TextUtils.isEmpty(result.getResMsg()) ? "操作失败" : result.getResMsg();
                                    }
                                    msg.sendToTarget();
                                }
                            }
                        });
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_DOWNLOAD_VIBRATERING:
                    downloadVibrateRing();
                    break;
                case MESSAGE_DOWNLOAD_RINGBACK:
                    showSetRingBackResult(msg.arg1, msg.obj);
                    break;
                case MESSAGE_DOWNLOAD_MUSIC:
                    downloadFullSong();
                    showDownloadResult(msg.arg1, msg.obj);
                    break;
                case MESSAGE_DOWNLOAD_RING_MONTH:
                    showRingMonthResult(msg.arg1, msg.obj);
                    break;
                case MESSAGE_DOWNLOAD_CP_MONTH:
                    showCPMonthResult(msg.arg1, msg.obj);
                    break;
            }
        }
    };

    private void downloadVibrateRing() {
        if (mVibrateRingDownloadUrl != null) {
            new DownloadMusic(mContext).download(mMusic.getSongName(), mVibrateRingDownloadUrl, 0);
            mVibrateRingDownloadUrl = null;
        }
    }

    private void showSetRingBackResult(int result, Object obj) {
        if (result == 0) {
            Toast.makeText(mContext, "设置彩铃成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, obj.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void showRingMonthResult(int result, Object obj) {
        if (result == 0) {
            Toast.makeText(mContext, "彩铃包月成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, obj.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void showCPMonthResult(int result, Object obj) {
        if (result == 0) {
            Toast.makeText(mContext, "CP包月成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, obj.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void showDownloadResult(int result, Object obj) {
        if (result == 0) {
            Toast.makeText(mContext, "开始下载...", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, obj.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void downloadFullSong() {
        if (mMusicDownloadUrl != null) {
            new DownloadMusic(mContext).download(mMusic.getSongName(), mMusicDownloadUrl, 1);
            mMusicDownloadUrl = null;
        }
    }
}