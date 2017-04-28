package com.lz.music.util;

import java.io.File;

import com.lz.music.kuyuehui.R;

import android.content.ContentValues;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class MusicUtil {
	public static final int SIM_FOR_UNKNOWN = -1; // Unknown
	public static final int SIM_FOR_CHINA_MOBILE = 0; // 中国移动
	public static final int SIM_FOR_CHINA_UNICOM = 1; // 中国联通
	public static final int SIM_FOR_CHINA_TELECOM = 2; // 中国电信

	public static String getLocalPhoneNumber(Context context) {
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String number = manager.getLine1Number();

		return number;
	}

	public static String getLocalPhoneIMSI(Context context) {
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = manager.getSubscriberId();

		return imsi;
	}

	public static boolean isSimReady(Context context) {
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (manager.getSimState() == TelephonyManager.SIM_STATE_READY) {
			return true;
		} else {
			return false;
		}
	}

	public static int getPhoneOperator(Context context) {
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (manager.getSimState() == TelephonyManager.SIM_STATE_READY) {
			String operator = manager.getSimOperator();
			if (operator != null) {
				if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
					return SIM_FOR_CHINA_MOBILE;
				} else if (operator.equals("46001")) {
					return SIM_FOR_CHINA_UNICOM;
				} else if (operator.equals("46003")) {
					return SIM_FOR_CHINA_TELECOM;
				} else {
					return SIM_FOR_UNKNOWN;
				}
			} else {
				return SIM_FOR_UNKNOWN;
			}
		} else {
			return SIM_FOR_UNKNOWN;
		}
	}

	public static boolean isChinaMobileSimCard(Context c) {
		if (MusicUtil.getPhoneOperator(c) == MusicUtil.SIM_FOR_CHINA_MOBILE) {
			return true;
		} else {
			return false;
		}
	}

	public static void setVibrateRing(Context c, String path) {
		if (path != null) {
			File file = new File(path);
			ContentValues values = new ContentValues();
			values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
			values.put(MediaStore.MediaColumns.TITLE, file.getName());
			values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
			values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
			values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
			values.put(MediaStore.Audio.Media.IS_ALARM, false);
			values.put(MediaStore.Audio.Media.IS_MUSIC, false);

			Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
			Uri newUri = c.getContentResolver().insert(uri, values);
			RingtoneManager.setActualDefaultRingtoneUri(c, RingtoneManager.TYPE_RINGTONE, newUri);
			if (newUri != null) {
				Toast.makeText(c, c.getString(R.string.set_vibrate_ring_success), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(c, c.getString(R.string.set_vibrate_ring_failed), Toast.LENGTH_LONG).show();
			}
		}
	}
}