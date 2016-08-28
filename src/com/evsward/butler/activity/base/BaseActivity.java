package com.evsward.butler.activity.base;

import com.evsward.butler.util.CommonUtil;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.LogUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * 活动基类
 * 
 * @Date Mar 24, 2015
 * @author liuwb.edward
 */
@SuppressLint("Registered")
public class BaseActivity extends Activity {
	public static String TAG,URL_SERVER_ADDRESS, printIP, serverIP, empUuid;
	public static Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		TAG = this.getClass().getSimpleName();
		SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
		serverIP = pref.getString("ServerIP", "");
		printIP = pref.getString("PrinterIP", "");
		empUuid = pref.getString("empUuid", "");
		URL_SERVER_ADDRESS = Const.HTTP + serverIP + Const.SERVER_PORT;
		CommonUtil.isNetWorkAvailable();
		LogUtil.i(TAG, getClass().getSimpleName());
	}

}
