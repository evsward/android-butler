package com.evsward.butler.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.evsward.butler.activity.MyApplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;

/**
 * 常用工具类
 * 
 * @Date Apr 2, 2015
 * @author liuwb.edward
 */
public class CommonUtil {
	private static final String TAG = "CommonUtil";

	/**
	 * 判断一个字符串数组strs中是否含有字符串s
	 * 
	 * @param strs
	 * @param s
	 * @return
	 */
	public static boolean isHave(String[] strs, String s) {
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].indexOf(s) > -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将字符串格式的时间转为整型用于装配ID
	 * 
	 * @param date
	 *            格式为：2015-04-30 星期四
	 * @return 20150430
	 */
	public static int date2Int(String date) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		try {
			return Integer.parseInt(sdf2.format(sdf1.parse(date.split(" ")[0])));
		} catch (ParseException e) {
			LogUtil.se(TAG, e);
		}
		return 0;
	}

	/**
	 * 将nfc读取的byte[]转成16进制的字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 格式化16进制的nfctagID成10进制数字
	 * 
	 * @param nfctagIDHex
	 * @return null：格式化错误
	 */
	public static Long convertHex2Long(String nfctagIDHex) {
		Long nfctagIDDecimal = null;
		try {
			nfctagIDDecimal = Long.parseLong(nfctagIDHex, 16);
		} catch (NumberFormatException e) {
			LogUtil.se(TAG, e);
			return nfctagIDDecimal;
		}
		return nfctagIDDecimal;
	}

	/**
	 * 将map转换成url
	 * 
	 * @param paramMap
	 * @return
	 */
	public static String map2Str(Map<String, Object> paramMap) {
		StringBuilder sb = new StringBuilder();
		Set<Entry<String, Object>> set = paramMap.entrySet();
		Iterator<Entry<String, Object>> it = set.iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append("&");
		}
		String urlParam = sb.toString();
		if (urlParam.endsWith("&")) {
			urlParam = StringUtils.substringBeforeLast(urlParam, "&");
		}
		return urlParam;
	}

	/**
	 * 判断手机网络是否连接
	 * 
	 * @return
	 */
	public static void isNetWorkAvailable() {
		Context mContext = MyApplication.getContext();
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo == null) {
			Toast.makeText(mContext, "pad未连接网络", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 还剩{param}毫秒，转化为还剩 00:59:56的【倒计时格式】
	 * 
	 * @param millisUntilFinished
	 * @return
	 */
	public static CharSequence convertTime(long millisUntilfinished) {
		long secondsUntilfinished = millisUntilfinished / 1000;
		String showHour = StringUtils.leftPad(String.valueOf(secondsUntilfinished / 3600), 2, "0");
		String showMinute = StringUtils.leftPad(String.valueOf(secondsUntilfinished % 3600 / 60), 2, "0");
		String showSecond = StringUtils.leftPad(String.valueOf(secondsUntilfinished % 60), 2, "0");
		return showHour + ":" + showMinute + ":" + showSecond;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param strFolderName
	 * @return
	 */
	public static String createFolder(String strFolderName) {
		String status = Environment.getExternalStorageState();
		String path = "";
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath() + strFolderName;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		return path;
	}
}
