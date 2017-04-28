
package com.lz.music;

import java.io.File;

import android.app.Application;
import android.os.Environment;

import com.lz.music.player.MusicPlayer;

public class MusicApp extends Application {
    public static final String APP_NAME = "kuyuehui";
    public static final String APP_DIR = Environment.getExternalStorageDirectory() + File.separator + APP_NAME + File.separator;

    public void onCreate() {
        super.onCreate();
        try {
            System.loadLibrary("mg20pbase");
            MusicPlayer.init(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}