package com.evsward.butler.activity.base;

import com.evsward.butler.activity.MyApplication;
import com.evsward.butler.activity.TVScreenActivity;
import com.evsward.butler.util.CommonUtil;
import com.evsward.butler.util.Const;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * fragment基类
 * 
 * 应用场景：大屏幕传输
 * 
 * @Date Jun 25, 2015
 * @author liuwb.edward
 */
public class TVBaseFragment extends Fragment {
	public static TVScreenActivity tvScreenActivity;
	public static String TAG, URL_SERVER_ADDRESS, printIP, serverIP, empUuid;
	public static Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvScreenActivity = (TVScreenActivity) getActivity();
		mContext = MyApplication.getContext();
		TAG = this.getClass().getSimpleName();
		SharedPreferences pref = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
		serverIP = pref.getString("ServerIP", "");
		printIP = pref.getString("PrinterIP", "");
		URL_SERVER_ADDRESS = Const.HTTP + serverIP + Const.SERVER_PORT;
		CommonUtil.isNetWorkAvailable();
	}

}
