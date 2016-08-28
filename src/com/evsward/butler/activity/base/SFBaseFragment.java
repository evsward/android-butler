package com.evsward.butler.activity.base;

import android.content.Context;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.evsward.butler.activity.CompetitionManageActivity;
import com.evsward.butler.activity.MyApplication;

/**
 * sherlock fragment基类
 * 
 * 应用场景：比赛管理，左右滑动UI
 * 
 * @Date Jun 3, 2015
 * @author liuwb.edward
 */
public class SFBaseFragment extends SherlockFragment {
	public static CompetitionManageActivity competitionManageActivity;
	public static String TAG, URL_SERVER_ADDRESS, printIP, serverIP, empUuid;
	public static Context mContext;
	public static int compID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		competitionManageActivity = (CompetitionManageActivity) getSherlockActivity();
		compID = competitionManageActivity.compID;
		URL_SERVER_ADDRESS = SFBaseActivity.URL_SERVER_ADDRESS;
		empUuid = SFBaseActivity.empUuid;
		serverIP = SFBaseActivity.serverIP;
		printIP = SFBaseActivity.printIP;
		mContext = MyApplication.getContext();
		TAG = this.getClass().getSimpleName();
	}

}
