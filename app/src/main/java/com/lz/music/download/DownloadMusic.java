
package com.lz.music.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.widget.Toast;

import com.lz.music.MusicApp;
import com.lz.music.util.MusicUtil;

public class DownloadMusic {
    private Context mContext;
    private DownloadManager mManager;

    private String mVibrateRingPath;

    public DownloadMusic(Context context) {
        mContext = context;
        mManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void download(String name, String url, int type) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setDestinationInExternalPublicDir(MusicApp.APP_NAME, name + ".mp3");
        request.setVisibleInDownloadsUi(true);
        // 振铃
        if (type == 0) {
            mVibrateRingPath = MusicApp.APP_DIR + name + ".mp3";
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            mContext.registerReceiver(mReceiver, filter);
        }
        mManager.enqueue(request);
        Toast.makeText(mContext, "文件已保存至" + MusicApp.APP_DIR, Toast.LENGTH_LONG).show();
    }

    private void setVibrateRing() {
        MusicUtil.setVibrateRing(mContext, mVibrateRingPath);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                setVibrateRing();
                mContext.unregisterReceiver(mReceiver);
            }
        }
    };
}
