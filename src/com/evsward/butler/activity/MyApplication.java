package com.evsward.butler.activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用于全局获取Context
 * 
 * @Date Mar 24, 2015
 * @author liuwb.edward
 */
public class MyApplication extends Application {
	private static SharedPreferences pref;
	private static Context context;

	@Override
	public void onCreate() {
		context = getApplicationContext();
		pref = context.getSharedPreferences("data", MyApplication.MODE_PRIVATE);
	}

	public static SharedPreferences getSharedPreferences() {
		return pref;
	}

	public static Context getContext() {
		return context;
	}

}
