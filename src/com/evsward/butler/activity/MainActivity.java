package com.evsward.butler.activity;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import com.evsward.butler.activity.base.BaseActivity;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.R;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_main);
		int screenSize = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
		LogUtil.d(TAG, "screenSize=" + screenSize);
		switch (screenSize) {
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			Toast.makeText(mContext, "access mobile ", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(mContext, LoginActivity.class));
			finish();
			break;
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			Toast.makeText(mContext, "access pad ", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(mContext, LoginActivity.class));
			finish();
			break;
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
			final String serverIP = pref.getString("ServerIP", "");
			if (StringUtils.isBlank(serverIP)) {
				goToSetIP();
			} else {
				// progress dialog
				final ProgressDialog progressDialog = new ProgressDialog(mContext);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setTitle("正在测试连接服务器，请稍后...");
				progressDialog.setCancelable(false);
				progressDialog.show();
				HttpUtil.get(URL_SERVER_ADDRESS + Const.CHECK_SERVER_ADDRESS, new JsonBaseHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						progressDialog.dismiss();
						Toast.makeText(mContext, "access tv box " + "测试连接服务器:" + serverIP + " 成功！", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(mContext, TVScreenActivity.class);
						startActivity(intent);
						finish();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						super.onFailure(statusCode, headers, responseString, throwable);
						progressDialog.dismiss();
						goToSetIP();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable, errorResponse);
						progressDialog.dismiss();
						goToSetIP();
					}

				});
			}
			break;
		default:
			break;
		}
	}

	private void goToSetIP() {
		Toast.makeText(mContext, "请设置盒子的服务器IP ", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent("com.evsward.butler.SET_SERVER_PRINTER");
		intent.putExtra("source", "tv");
		startActivity(intent);
		finish();
	}
}
