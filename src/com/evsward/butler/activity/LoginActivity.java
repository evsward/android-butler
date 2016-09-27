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
import com.nxp.nfclib.classic.IMFClassicEV1;
import com.nxp.nfclib.exceptions.CloneDetectedException;
import com.nxp.nfclib.exceptions.SmartCardException;
import com.nxp.nfclib.ntag.INTag213215216;
import com.nxp.nfclib.utils.NxpLogUtils;
import com.nxp.nfcliblite.NxpNfcLibLite;
import com.nxp.nfcliblite.Nxpnfcliblitecallback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 登陆
 * 
 * @Date Mar 5, 2015
 * @author liuwb.edward
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	private TextView showMsg;// 首页显示条
	private NxpNfcLibLite libInstance = null;
	private Tag tag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		libInstance = NxpNfcLibLite.getInstance();
		libInstance.registerActivity(this);
		// Registering the activity for the NFC tag detection mode.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// Registering the activity for the NFC tag detection mode.

			final Handler mLibhandler = new Handler(mLibhandlercb);

			try {
				libInstance.setNfcControllerMode(1000, new NfcAdapter.ReaderCallback() {

					@Override
					public void onTagDiscovered(final Tag tagObject) {
						NxpLogUtils.d(TAG, "TAG is Discovered from ReaderCallBack...");
						tag = tagObject;
						mLibhandler.sendEmptyMessage(0);

					}
				}, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_V | NfcAdapter.FLAG_READER_NFC_B
						| NfcAdapter.FLAG_READER_NFC_F);
			} catch (SmartCardException e) {

			}
		}
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
		libInstance.stopForeGroundDispatch();
		super.onPause();
	}

	@Override
	protected void onResume() {
		libInstance.startForeGroundDispatch();
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		showMsg.setText("---请刷卡---");
	}

	protected void onNewIntent(final Intent intent) {
		showMsg.setText("---正在登陆---");
		try {
			libInstance.filterIntent(intent, mCallback);
		} catch (CloneDetectedException e1) {
			e1.printStackTrace();
		}
	}

	/** Message Handler call back for enableReaderMode API. */
	private Handler.Callback mLibhandlercb = new Handler.Callback() {

		@Override
		public boolean handleMessage(final Message arg0) {

			try {
				libInstance.filterIntent(tag, mCallback);
			} catch (CloneDetectedException e) {
				NxpLogUtils.e(TAG, e.getMessage(), e);
			}
			return false;
		}

	};
	private boolean mIsPerformingCardOperations = false;

	private Nxpnfcliblitecallback mCallback = new Nxpnfcliblitecallback() {

		@Override
		public void onNTag213215216CardDetected(final INTag213215216 objnTag213215216) {

			if (mIsPerformingCardOperations) {
				Log.d(TAG, "----- same card, discard the callback:" + mIsPerformingCardOperations);
				return;
			}
			mIsPerformingCardOperations = true;
			try {
				objnTag213215216.getReader().connect();
				byte[] bytesid = objnTag213215216.getUID();
				String strdecimalNFCID = CommonUtil.bytesToHexString(bytesid);
				objnTag213215216.getReader().close();
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			mIsPerformingCardOperations = false;

		}

		@Override
		public void onClassicEV1CardDetected(IMFClassicEV1 arg0) {

		}

	};

}
