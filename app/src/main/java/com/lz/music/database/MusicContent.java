package com.lz.music.database;

import android.net.Uri;
import android.provider.BaseColumns;

import com.lz.music.kuyuehui.BuildConfig;

public class MusicContent {
    public static final String AUTHORITY = BuildConfig.AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private MusicContent() {
    }

    public interface MusicColumns extends BaseColumns {
        String ID = "music_id";
        String NAME = "music_name";
        String SINGER = "music_singer";
    }

    public static final class Music implements MusicColumns {
        public static final String TABLE_NAME = "music";
        public static final Uri CONTENT_URI = Uri.parse(MusicContent.CONTENT_URI + "/music");

        public static final int BASE_ID_COLUMN = 0;
        public static final int ID_COLUMN = 1;
        public static final int NAME_COLUMN = 2;
        public static final int SINGER_COLUMN = 3;

        public static final String[] CONTENT_PROJECTION = new String[]{
                MusicColumns._ID,
                MusicColumns.ID,
                MusicColumns.NAME,
                MusicColumns.SINGER
        };
    }
}
