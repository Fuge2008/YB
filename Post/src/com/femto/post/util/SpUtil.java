package com.femto.post.util;

import java.util.ArrayList;
import java.util.Arrays;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {

	private static final String SP_NAME = "config.xml";

	public static void writeSp(Context context, String key, boolean value) {
		getSp(context).edit().putBoolean(key, value).commit();
	}

	public static boolean readSp(Context context, String key, boolean defValue) {
		return getSp(context).getBoolean(key, defValue);
	}

	public static void writeSp(Context context, String key,
			ArrayList<Integer> ints) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ints.size(); i++) {
			sb.append(ints.get(i) + ":");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		getSp(context).edit().putString(key, sb.toString()).commit();
	}

	public static ArrayList<Integer> readSp(Context context, String key) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		String str = getSp(context).getString(key, "");
		if(str.length()>0){
			String[] split = str.split(":");

			
			for (String string : split) {
				result.add(Integer.valueOf(string));
			}
		}
		return result;
	}

	private static SharedPreferences getSp(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
		return sp;
	}

	public static void writeSp(Context context, String key, String value) {
		getSp(context).edit().putString(key, value).commit();
	}

	public static String readSp(Context context, String key, String defValue) {
		return getSp(context).getString(key, defValue);
	}
	
	public static void writeSP(Context context, String key,Integer value) {
		getSp(context).edit().putInt(key, value).commit();
	}
	
	public static int  readSP(Context context, String key, Integer defvalue) {
		return getSp(context).getInt(key, defvalue);
	}
}
