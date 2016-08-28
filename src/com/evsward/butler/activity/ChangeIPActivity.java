package com.evsward.butler.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.evsward.butler.activity.base.BaseActivity;
import com.evsward.butler.util.Const;
import com.evsward.butler.widget.TitleBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.evsward.butler.R;

/**
 * 设置服务器和打印机IP的活动
 * 
 * @Date Apr 1, 2015
 * @author liuwb.edward
 */
public class ChangeIPActivity extends BaseActivity implements OnClickListener {
	private EditText updateServerIP;
	private EditText updatePrinterIP;
	private TextView showMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String source = getIntent().getStringExtra("source");
		if ("pad".equals(source)) {
			TitleBar.getTitleBar(this, "设置服务器和打印机");
		} else if ("tv".equals(source)) {
			TitleBar.getTitleBar(this, "设置服务器和打印机", "<<返回", tvBackListener);
		}
		setContentView(R.layout.activity_change_ip);
		Button saveServerIP = (Button) findViewById(R.id.saveServerIP);
		Button savePrinterIP = (Button) findViewById(R.id.savePrinterIP);
		showMessage = (TextView) findViewById(R.id.showMessage);
		updateServerIP = (EditText) findViewById(R.id.updateServerIP);
		updatePrinterIP = (EditText) findViewById(R.id.updatePrinterIP);
		updateServerIP.setText(serverIP);
		updatePrinterIP.setText(printIP);
		saveServerIP.setOnClickListener(this);
		savePrinterIP.setOnClickListener(this);
	}

	View.OnClickListener tvBackListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(mContext, MainActivity.class);
			startActivity(intent);
			finish();
		}
	};

	@Override
	public void onClick(View v) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(3000);
		switch (v.getId()) {
		case R.id.saveServerIP:
			final String newServerIP = ChangeIPActivity.this.updateServerIP.getText().toString();
			client.get(Const.HTTP + newServerIP + Const.SERVER_PORT + Const.CHECK_SERVER_ADDRESS, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
					showMessage.setText("测试连接服务器:" + newServerIP + " 成功！");
					serverIP = newServerIP;// 设置全局服务器IP
					URL_SERVER_ADDRESS = Const.HTTP + serverIP + Const.SERVER_PORT;
					SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
					editor.putString("ServerIP", newServerIP);
					editor.commit();
				}

				@Override
				public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, java.lang.Throwable error) {
					showMessage.setText("测试连接服务器:" + newServerIP + " 失败！");
				}
			});

			break;
		case R.id.savePrinterIP:
			final String newPrinterIP = ChangeIPActivity.this.updatePrinterIP.getText().toString();
			showMessage.setText("打印机IP:" + newPrinterIP + " 已保存！");
			printIP = newPrinterIP;// 设置全局打印机IP
			SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
			editor.putString("PrinterIP", newPrinterIP);
			editor.commit();
		default:
			break;
		}
	}
}
