package com.evsward.butler.activity;

import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Window;
import android.widget.Toast;

import com.evsward.butler.activity.base.BaseActivity;
import com.evsward.butler.activity.base.TVBaseFragment;
import com.evsward.butler.entities.Screen;
import com.evsward.butler.fragment.tvscreen.EmptyFragment;
import com.evsward.butler.fragment.tvscreen.EnterFieldFragment;
import com.evsward.butler.fragment.tvscreen.MatchListFragment;
import com.evsward.butler.fragment.tvscreen.OneMatchInfoFragment;
import com.evsward.butler.fragment.tvscreen.OneMatchSeatFragment;
import com.evsward.butler.service.NettySocketService;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class TVScreenActivity extends BaseActivity {
	// broadcast
	private MessageBackReciver mReciver;
	private LocalBroadcastManager mLocalBroadcastManager;
	// Service
	private ServiceConnection mConn;

	private NettySocketService.NettyBinder nettyBinder;
	private TVBaseFragment tvBaseFragment;
	public int compID;

	private int lastLanguage = -1;
	private boolean ifChangeLanguage = false;
	public boolean ifEnglish = false;

	private int lastCompID = -1;
	private boolean ifChangeComp = false;

	// 广播
	private class MessageBackReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context mContext, Intent intent) {
			String action = intent.getAction();
			if (Const.Ack_XLarge_TV_101_ACTION.equals(action)) {
				String strTVScreen = intent.getStringExtra(Const.Ack_XLarge_TV_101_ACTION);
				LogUtil.d(TAG, strTVScreen);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(strTVScreen);
					if (jsonObject.getInt("rspCode") != Const.RspCode_Success) {
						Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
						return;
					}
					Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
					Screen currentScreen = gson.fromJson(jsonObject.getString("screenDevInfo"), new TypeToken<Screen>() {
					}.getType());
					compID = currentScreen.getCompID();
					if (lastCompID == compID) {
						ifChangeComp = false;
					} else {
						ifChangeComp = true;
						lastCompID = compID;
					}
					if (lastLanguage == currentScreen.getLanguage()) {
						ifChangeLanguage = false;
					} else {
						ifChangeLanguage = true;
						lastLanguage = currentScreen.getLanguage();
					}

					Locale locale = null;
					if (currentScreen.getLanguage() == 1) {
						locale = new Locale(Locale.ENGLISH.toString());
						ifEnglish = true;
					} else {
						locale = new Locale(Locale.CHINESE.toString());
						ifEnglish = false;
					}
					Locale.setDefault(locale);
					Configuration config = new Configuration();
					config.locale = locale;
					mContext.getApplicationContext().getResources().updateConfiguration(config, null);

					android.app.FragmentManager fragmentManager = getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					Bundle bundle = new Bundle();
					switch (currentScreen.getPushType()) {
					case 0:
						if (!(tvBaseFragment instanceof EmptyFragment) || ifChangeLanguage) {
							tvBaseFragment = new EmptyFragment();
						}
						break;
					case 1:
						if (!(tvBaseFragment instanceof MatchListFragment) || ifChangeLanguage) {
							tvBaseFragment = new MatchListFragment();
							bundle.putString("jsonMsg", jsonObject.getString("screenCompList"));
							tvBaseFragment.setArguments(bundle);
						} else {
							((MatchListFragment) tvBaseFragment).updateUIContent(jsonObject.getString("screenCompList"));
						}
						break;
					case 2:
						if (!(tvBaseFragment instanceof OneMatchInfoFragment) || ifChangeLanguage || ifChangeComp) {
							tvBaseFragment = new OneMatchInfoFragment();
							bundle.putString("jsonMsg", jsonObject.getString("compTimeInfo"));
							tvBaseFragment.setArguments(bundle);
						} else {
							((OneMatchInfoFragment) tvBaseFragment).updateUIContent(jsonObject.getString("compTimeInfo"));
						}
						break;
					case 3:
						if (!(tvBaseFragment instanceof OneMatchSeatFragment) || ifChangeLanguage || ifChangeComp) {
							tvBaseFragment = new OneMatchSeatFragment();
						}
						break;
					case 4:
						if (!(tvBaseFragment instanceof EnterFieldFragment) || ifChangeLanguage) {
							tvBaseFragment = new EnterFieldFragment();
							bundle.putString("jsonMsg", jsonObject.getString("welcome"));
							tvBaseFragment.setArguments(bundle);
						} else {
							((EnterFieldFragment) tvBaseFragment).updateUIContent(jsonObject.getString("welcome"));
						}
						break;
					default:
						break;
					}
					fragmentTransaction.replace(R.id.showPosition, tvBaseFragment).commit();
				} catch (JSONException e) {
					LogUtil.se(TAG, e);
				}
			}
		};
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_main);
		registBoxToServer();
		IntentFilter mIntentFilter = new IntentFilter(Const.Ack_XLarge_TV_101_ACTION);
		mReciver = new MessageBackReciver();
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
		mConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder paramIbinder) {
				nettyBinder = (NettySocketService.NettyBinder) paramIbinder;
				String messageJson = "{\"IMEI\":\"" + Const.ANDROID_ID + "\",\"sysType\":" + Const.HI + "}";
				nettyBinder.sendMsg(Const.Req_XLarge_TV_101, messageJson);
			}
		};
	}

	private void registBoxToServer() {
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_REGIST_TV_BOX, Const.ANDROID_ID), new JsonBaseHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Toast.makeText(mContext, "盒子注册成功！", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				finish();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				finish();
			}

		});
	}

	@Override
	public void onStart() {
		super.onStart();
		bindService(new Intent(this, NettySocketService.class), mConn, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		super.onStop();
		unbindService(mConn);
		LogUtil.i(TAG, "大屏幕服务解绑");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocalBroadcastManager.unregisterReceiver(mReciver);
		LogUtil.i(TAG, "大屏幕断开");
	}
}
