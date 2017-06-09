package com.lz.music.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.lz.music.database.MusicContent.Music;

public class MusicProvider extends ContentProvider {
    private static final int MUSIC = 0;
    private static final int MUSIC_ID = 1;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private SQLiteDatabase mDatabase;

    static {
        sURIMatcher.addURI(MusicContent.AUTHORITY, "music", MUSIC);
        sURIMatcher.addURI(MusicContent.AUTHORITY, "music/#", MUSIC_ID);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = getDatabase();
        Cursor c = null;

        switch (sURIMatcher.match(uri)) {
            case MUSIC:
            case MUSIC_ID:
                c = db.query(Music.TABLE_NAME, Music.CONTENT_PROJECTION, selection, selectionArgs, null, null, sortOrder);
                break;
        }

        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case MUSIC:
                return "vnd.android-dir/music";
            case MUSIC_ID:
                return "vnd.android/music";
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = getDatabase();
        Uri result = null;

        switch (sURIMatcher.match(uri)) {
            case MUSIC:
            case MUSIC_ID:
                long id = db.insert(Music.TABLE_NAME, "foo", values);
                result = ContentUris.withAppendedId(uri, id);
                break;
        }

        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private synchronized SQLiteDatabase getDatabase() {
        if (mDatabase != null) {
            return mDatabase;
        }

        DatabaseHelper helper = new DatabaseHelper(getContext());
        mDatabase = helper.getWritableDatabase();

        return mDatabase;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "music.db";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createMusicTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        private void createMusicTable(SQLiteDatabase db) {
            db.execSQL("create table " + Music.TABLE_NAME + "("
                    + Music._ID + " integer primary key autoincrement, "
                    + Music.ID + " text, "
                    + Music.NAME + " text, "
                    + Music.SINGER + " text);");
        }
    }
}
