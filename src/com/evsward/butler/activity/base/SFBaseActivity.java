package com.evsward.butler.activity.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.evsward.butler.util.CommonUtil;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.R;

/**
 * sherlock活动基类
 * @Date Jun 3, 2015
 * @author liuwb.edward
 */
public class SFBaseActivity extends SherlockFragmentActivity {
	public static String TAG,URL_SERVER_ADDRESS, printIP, serverIP, empUuid,strDecimalNFCID;
	public static Context mContext;

	/**
	 * actionbarsherlock Menu 替换titlebar
	 */
	protected void useActionBarMenu(String title) {
		setTheme(R.style.MyTheme); // Used for theme switching in samples
		getSupportActionBar().setIcon(android.R.color.transparent);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_blue_gradual_login));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + title + "</font>"));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		TAG = this.getClass().getSimpleName();
		SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
		serverIP = pref.getString("ServerIP", "");
		printIP = pref.getString("PrinterIP", "");
		empUuid = pref.getString("empUuid", "");
		strDecimalNFCID = pref.getString("decimalNFCID", "");
		URL_SERVER_ADDRESS = Const.HTTP + serverIP + Const.SERVER_PORT;
		CommonUtil.isNetWorkAvailable();
		LogUtil.d(TAG, getClass().getSimpleName());
	}

}
