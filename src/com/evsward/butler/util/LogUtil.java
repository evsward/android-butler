package com.evsward.butler.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.text.format.DateFormat;
import android.util.Log;

/**
 * 日志工具
 * 
 * 设置日志级别
 * 
 * @Date Mar 24, 2015
 * @author liuwb.edward
 */
@SuppressWarnings("unused")
public class LogUtil {
	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;
	public static final int NOTHING = 6;
	public static final int LEVEL = DEBUG;

	public static void v(String tag, String msg) {
		if (LEVEL <= VERBOSE) {
			Log.v(tag, msg);
			save2File(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (LEVEL <= DEBUG) {
			Log.d(tag, msg);
			save2File(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (LEVEL <= INFO) {
			Log.i(tag, msg);
			save2File(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (LEVEL <= WARN) {
			Log.w(tag, msg);
			save2File(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (LEVEL <= ERROR) {
			Log.e(tag, msg);
			save2File(tag, msg);
		}
	}

	public static void se(String tag, Throwable e) {
		if (LEVEL <= ERROR) {
			Log.e(tag, e.toString());
			save2File(tag, e.toString());
			Log.e(tag, "Exception: " + Log.getStackTraceString(e));
			save2File(tag, "Exception: " + Log.getStackTraceString(e));
		}
	}

	private static void save2File(String tag, String msg) {
		String logFilePath = CommonUtil.createFolder(Const.LOCAL_LOG_FOLDER);
		File logFile = new File(logFilePath, DateFormat.format(Const.DATE_PATTERN1, new Date()) + ".txt");
		if (logFile.exists()) {
			String logItem = DateFormat.format(Const.DATE_PATTERN, new Date()) + "：" + tag + "=====" + msg + "\r\n";
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(logFile.getAbsoluteFile(), true));
				out.write(logItem);
				out.newLine();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
