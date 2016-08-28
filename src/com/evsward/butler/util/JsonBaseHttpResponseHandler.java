package com.evsward.butler.util;

import org.apache.http.Header;
import org.json.JSONObject;

import com.evsward.butler.activity.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Context;
import android.widget.Toast;

public class JsonBaseHttpResponseHandler extends JsonHttpResponseHandler {
	private String TAG = "JsonBaseHttpResponseHandler";
	private static Context mContext = MyApplication.getContext();

	@Override
	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		Toast.makeText(mContext, "服务器连接异常！", Toast.LENGTH_SHORT).show();
		LogUtil.se(TAG, throwable);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
		Toast.makeText(mContext, "获取Json数据失败：系统错误", Toast.LENGTH_SHORT).show();
		LogUtil.se(TAG, throwable);
	}

}
