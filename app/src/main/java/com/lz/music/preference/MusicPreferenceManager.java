package com.lz.music.preference;

import com.lz.music.util.MusicUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class MusicPreferenceManager {
    private static final String KEY_IMSI = "key_imsi";
    private static final String KEY_PHONE_NUMBER = "key_phone_number";

    private Context mContext;
    private SharedPreferences mPreferences;

    public void savePhoneNumberAndIMSI(String number) {
        Editor editor = mPreferences.edit();
        editor.putString(KEY_PHONE_NUMBER, number);
        editor.putString(KEY_IMSI, MusicUtil.getLocalPhoneIMSI(mContext));
        editor.commit();
    }

    public String getSavedIMSI() {
        return mPreferences.getString("KEY_IMSI", "");
    }

    public MusicPreferenceManager(Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
