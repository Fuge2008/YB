/***
This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

For more information, please refer to <http://unlicense.org/>
 */

package com.android.futures.util;

import android.util.Log;

/**
 * @date 17.03.2015
 * @ChineseName Lin_Huaming
 * @EnglishName Andy
 * 
 *              更好的显示Log.
 * */

public class LogUtils
{

	static String className;
	static String methodName;
	static int lineNumber;

	private LogUtils()
	{
		// 保护。不允许外面直接new
		return;
	}

	public static boolean isDebuggable()
	{
		return com.femto.post.BuildConfig.DEBUG;
	}

	private static String createLog(String log)
	{

		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(methodName);
		buffer.append(":");
		buffer.append(lineNumber);
		buffer.append("]------>");
		buffer.append(log);

		return buffer.toString();
	}

	private static void getMethodNames(StackTraceElement[] sElements)
	{
		className = "--->" + sElements[1].getFileName();
		methodName = sElements[1].getMethodName();
		lineNumber = sElements[1].getLineNumber();
	}

	public static void i(String message)
	{
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.i(className, createLog(message));
	}

	public static void i(int message)
	{
		i(message + "");
	}

	public static void e(String message)
	{
		if (!isDebuggable())
			return;

		// 这个实例之前不许先new 创建该方法Throwable()
		getMethodNames(new Throwable().getStackTrace());
		Log.e(className, createLog(message));
	}

	public static void e(int message)
	{
		e(message + "");
	}

	public static void d(String message)
	{
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.d(className, createLog(message));
	}

	public static void d(int message)
	{
		d(message + "");
	}

	public static void v(String message)
	{
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.v(className, createLog(message));
	}

	public static void v(int message)
	{
		v(message + "");
	}

	public static void w(String message)
	{
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.w(className, createLog(message));
	}

	public static void w(int message)
	{
		w(message + "");
	}

	public static void wtf(String message)
	{
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.wtf(className, createLog(message));
	}

	public static void wtf(int message)
	{
		wtf(message + "");
	}
}
