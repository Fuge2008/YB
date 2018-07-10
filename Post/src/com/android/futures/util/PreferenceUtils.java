package com.android.futures.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 持久化存储工具类
 * 
 * @author chaoyang
 * 
 */
public class PreferenceUtils {
	private final static String NAME = "LyLock";
	private static SharedPreferences mPreferences;

	/**
	 * 获得preference
	 * 
	 * @param context
	 * @return
	 */
	private static SharedPreferences getPreferences(Context context) {
		if (mPreferences == null) {
			mPreferences = context.getSharedPreferences(NAME,
					Context.MODE_PRIVATE);
		}
		return mPreferences;
	}

	/**
	 * 获得boolean类型的数�?如果没有返回false
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(Context context, String key) {
		return getBoolean(context, key, false);
	}

	/**
	 * 获得boolean类型的数�?
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static boolean getBoolean(Context context, String key,
			boolean defValue) {
		// 频繁的读文件
		SharedPreferences sp = getPreferences(context);
		return sp.getBoolean(key, defValue);
	}

	/**
	 * 存储boolean数据
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = getPreferences(context);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 获得String类型的数�?如果没有返回null
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getString(Context context, String key) {
		return getString(context, key, null);
	}

	/**
	 * 获得String类型的数�?
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context, String key, String defValue) {
		// 频繁的读文件
		SharedPreferences sp = getPreferences(context);
		return sp.getString(key, defValue);
	}

	/**
	 * 存储String数据
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putString(Context context, String key, String value) {
		SharedPreferences sp = getPreferences(context);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 获得int类型的数�?如果没有返回-1
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static int getInt(Context context, String key) {
		return getInt(context, key, -1);
	}

	/**
	 * 获得int类型的数�?
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static int getInt(Context context, String key, int defValue) {
		// 频繁的读文件
		SharedPreferences sp = getPreferences(context);
		return sp.getInt(key, defValue);
	}

	/**
	 * 存储int数据
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putInt(Context context, String key, int value) {
		SharedPreferences sp = getPreferences(context);
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 获得long类型的数�?如果没有返回-1
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static long getLong(Context context, String key) {
		return getLong(context, key, -1);
	}

	/**
	 * 获得long类型的数�?
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static long getLong(Context context, String key, long defValue) {
		// 频繁的读文件
		SharedPreferences sp = getPreferences(context);
		return sp.getLong(key, defValue);
	}

	/**
	 * 存储long数据
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putLong(Context context, String key, long value) {
		SharedPreferences sp = getPreferences(context);
		Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}
}
