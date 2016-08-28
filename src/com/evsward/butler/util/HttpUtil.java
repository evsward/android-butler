package com.evsward.butler.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Http工具
 * 
 * @Date Mar 24, 2015
 * @author liuwb.edward
 */
public class HttpUtil {
	private final static String TAG = "HttpUtil";
	private static AsyncHttpClient client = new AsyncHttpClient();
	static {
		client.setTimeout(8000);
	}

	// get
	public static void get(String urlString, AsyncHttpResponseHandler res) {
		LogUtil.i(TAG + " get", urlString);
		client.get(urlString, res);
	}

	// get with parameters
	public static void get(String urlString, RequestParams params, AsyncHttpResponseHandler res) {
		LogUtil.i(TAG + " get wp", urlString);
		client.get(urlString, params, res);
	}

	// get json
	public static void get(String urlString, JsonHttpResponseHandler res) {
		String paraFlag = "&";
		if (urlString.indexOf("?") == -1)
			paraFlag = "?";
		urlString += paraFlag + "sysType=" + Const.HI;// 增加赛制标识
		LogUtil.i(TAG + " get json", urlString);
		client.get(urlString, res);
	}

	// download
	public static void get(String urlString, BinaryHttpResponseHandler bHandler) {
		String paraFlag = "&";
		if (urlString.indexOf("?") == -1)
			paraFlag = "?";
		urlString += paraFlag + "sysType=" + Const.HI;// 增加赛制标识
		LogUtil.i(TAG + " get download", urlString);
		client.get(urlString, bHandler);
	}

	// post
	public static void post(String urlString, RequestParams params, AsyncHttpResponseHandler res) {
		LogUtil.i(TAG + " post", urlString);
		client.post(urlString, params, res);
	}

	// post json
	public static void post(String urlString, RequestParams params, JsonHttpResponseHandler res) {
		params.put("sysType", Const.HI);// 增加赛制标识
		LogUtil.i(TAG + " post json", urlString);
		client.post(urlString, params, res);
	}

	// Binary
	public static void post(String urlString, RequestParams params, BinaryHttpResponseHandler bHandler) {
		LogUtil.i(TAG + " post Binary", urlString);
		client.post(urlString, params, bHandler);
	}

	public static AsyncHttpClient getClient() {
		return client;
	}
}
