package com.evsward.butler.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evsward.butler.activity.base.BaseActivity;
import com.evsward.butler.entities.Screen;
import com.evsward.butler.entities.TVList;
import com.evsward.butler.service.NettySocketService;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.widget.TitleBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

@SuppressLint("InflateParams")
public class TVManageActivity extends BaseActivity {
	// 盒子的4项信息
	private TextView boxName;
	private TextView boxIP;
	private TextView boxState;
	private TextView boxContent;
	// broadcast
	private MessageBackReciver mReciver;
	private LocalBroadcastManager mLocalBroadcastManager;
	// Service
	private ServiceConnection mConn;
	private NettySocketService.NettyBinder nettyBinder;

	// 广播
	private class MessageBackReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Const.ACK_Manage_TV_501_ACTION.equals(action)) {
				Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
				TVList tvListObj = gson.fromJson(intent.getStringExtra(Const.ACK_Manage_TV_501_ACTION), new TypeToken<TVList>() {
				}.getType());
				if (tvListObj.getRspCode() != Const.RspCode_Success) {
					Toast.makeText(context, "TV管理socket错误：" + tvListObj.getMsg(), Toast.LENGTH_SHORT).show();
					return;
				}
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout parent = (LinearLayout) findViewById(R.id.tvmanager);
				parent.removeAllViews();// 每次进来先清空当前内容
				for (int i = 0; i < tvListObj.getScreens().length; i++) {
					Screen screen = tvListObj.getScreens()[i];
					final String strDevImei = screen.getDevImei();
					int nDevImei = Integer.parseInt(strDevImei.length() > 7 ? strDevImei.substring(strDevImei.length() - 8, strDevImei.length() - 1)
							: strDevImei, 16);
					LogUtil.d(TAG, "盒子唯一ID变量为：" + nDevImei);
					boxName = null;
					boxIP = null;
					boxState = null;
					boxContent = null;

					View tvBox = (View) parent.findViewById(nDevImei);
					if (tvBox != null) {
						boxName = (TextView) tvBox.findViewById(nDevImei + 1);
						boxIP = (TextView) tvBox.findViewById(nDevImei + 2);
						boxState = (TextView) tvBox.findViewById(nDevImei + 3);
						boxContent = (TextView) tvBox.findViewById(nDevImei + 4);
					}

					if (boxName != null && boxIP != null && boxState != null && boxContent != null) {// 更新盒子状态
						// 设置4个元素的显示值
						boxName.setText(screen.getDevName());
						boxIP.setText(screen.getIpStr());
						boxState.setText(String.valueOf(screen.getDevStateShow()));
						boxContent.setText(String.valueOf(screen.getPushTypeShow()));
					} else {
						tvBox = inflater.inflate(R.layout.tvbox, null);
						tvBox.setId(nDevImei);

						// 4元素初始化
						boxName = (TextView) tvBox.findViewById(R.id.tvBoxName);
						boxIP = (TextView) tvBox.findViewById(R.id.tvBoxIP);
						boxState = (TextView) tvBox.findViewById(R.id.tvBoxState);
						boxContent = (TextView) tvBox.findViewById(R.id.tvBoxContent);

						// 设置4个元素的显示值
						boxName.setText(screen.getDevName());
						boxIP.setText(screen.getIpStr());
						boxState.setText(String.valueOf(screen.getDevStateShow()));
						boxContent.setText(String.valueOf(screen.getPushTypeShow()));

						// 设置4个元素的ID
						boxName.setId(nDevImei + 1);
						boxIP.setId(nDevImei + 2);
						boxState.setId(nDevImei + 3);
						boxContent.setId(nDevImei + 4);

						tvBox.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_FIND_SCREEN, strDevImei),
										new JsonBaseHttpResponseHandler() {

											@Override
											public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
												try {
													if (response.getInt("rspCode") == Const.RspCode_Success) {
														Intent intent = new Intent(TVManageActivity.this, TVInfoActivity.class);
														Bundle bundle = new Bundle();
														bundle.putString("screen", response.getString("screen"));
														bundle.putString("noEndCompsOnday", response.getString("noEndCompsOnday"));
														intent.putExtras(bundle);
														startActivity(intent);
														finish();
													} else {
														LogUtil.e(TAG, String.format("查询Screen:%s", response.getString("msg").toString()));
													}
												} catch (JSONException e) {
													LogUtil.e(TAG, "服务器通讯错误！");
												}
											}

										});
							}
						});
						parent.addView(tvBox);
					}
				}
			}
		};
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.getTitleBar(this, "大屏幕管理");
		setContentView(R.layout.activity_tvmanage);
		mConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder paramIbinder) {
				nettyBinder = (NettySocketService.NettyBinder) paramIbinder;
				String messageJson = "{\"IMEI\":\"" + Const.ANDROID_ID + "\",\"sysType\":" + Const.HI + "}";
				nettyBinder.sendMsg(Const.Req_Manage_TV_501, messageJson);
			}
		};
	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Const.ACK_Manage_TV_501_ACTION);
		mReciver = new MessageBackReciver();
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
		bindService(new Intent(this, NettySocketService.class), mConn, BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindService(mConn);
		mLocalBroadcastManager.unregisterReceiver(mReciver);
	}
}
