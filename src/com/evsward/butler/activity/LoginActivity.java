package com.evsward.butler.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.evsward.butler.R;
import com.evsward.butler.activity.base.BaseActivity;
import com.evsward.butler.util.CommonUtil;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 登陆
 * 
 * @Date Mar 5, 2015
 * @author liuwb.edward
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	private TextView showMsg;// 首页显示条
	// nfc
	protected NfcAdapter nfcAdapter;
	protected PendingIntent pendingIntent;
	protected IntentFilter[] mFilters;
	protected String[][] mTechLists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		// nfc
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		checkNFC();
		pendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		ndef.addCategory("*/*");
		mFilters = new IntentFilter[] { ndef };// 过滤器
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() },
				new String[] { NfcA.class.getName() } };// 允许扫描的标签类型
		showMsg = (TextView) findViewById(R.id.showMsg);
		View editText2 = (View) findViewById(R.id.editText2);
		ImageButton setServerPrint = (ImageButton) findViewById(R.id.setServerPrinter);
		setServerPrint.setOnClickListener(this);
		editText2.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setServerPrinter:
		case R.id.editText2:
			Intent intent = new Intent("com.evsward.butler.SET_SERVER_PRINTER");
			intent.putExtra("source", "pad");
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.d(TAG, "*************onResume*****************");
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, mFilters, mTechLists);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		showMsg.setText("---请刷卡---");
	}

	protected void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			showMsg.setText("---正在登陆---");
			String strdecimalNFCID = processIntent(intent);
			// 后面用
			SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
			editor.putString("decimalNFCID", strdecimalNFCID);
			editor.commit();
			Log.d(TAG, "NFCID:");
			HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_LOGIN, strdecimalNFCID),
					new JsonHttpResponseHandler() {

						@Override
						public void onFailure(int statusCode, Header[] headers, String responseString,
								Throwable throwable) {
							showMsg.setText("---请设置服务器和打印机---");
							LogUtil.se(TAG, throwable);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, Throwable throwable,
								JSONObject errorResponse) {
							showMsg.setText("--请设置服务器和打印机---");
							LogUtil.se(TAG, throwable);
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
							try {
								if (response.getInt("rspCode") == Const.RspCode_Success) {
									Intent intent = new Intent("com.evsward.butler.MAIN_UI");
									Bundle bundle = new Bundle();
									bundle.putString("employeeInfo", response.getString("employee"));
									intent.putExtras(bundle);
									startActivity(intent);
								} else {
									LogUtil.e(TAG, "登陆出错：" + response.getString("msg"));
									showMsg.setText("---" + response.getString("msg") + "---");
								}
							} catch (JSONException e) {
								showMsg.setText("---请联系IT人员---");
								LogUtil.e(TAG, "服务器通讯错误！");
							}
						}
					});
		}
	}

	/**
	 * 获取tab标签中的内容
	 * 
	 * @param intent
	 * @return 十进制NFC卡号
	 */
	protected String processIntent(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		byte[] bytesid = tag.getId();
		String result = CommonUtil.bytesToHexString(bytesid);
		Log.d(TAG, "bytesToHexString: " + result);
		Log.d(TAG, "convertHex2Long: " + CommonUtil.convertHex2Long(result));
		return result;
	}

	protected void checkNFC() {
		if (nfcAdapter == null) {
			Toast.makeText(this, "该设备不支持NFC！", Toast.LENGTH_SHORT).show();
			finish();
			return;
		} else if (!nfcAdapter.isEnabled()) {
			Toast.makeText(this, "您的NFC未开启", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}
}
