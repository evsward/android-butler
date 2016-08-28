package com.evsward.butler.activity;

import java.io.IOException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.evsward.butler.activity.base.BaseActivity;
import com.evsward.butler.util.CommonUtil;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nxp.nfclib.classic.IMFClassicEV1;
import com.nxp.nfclib.exceptions.CloneDetectedException;
import com.nxp.nfcliblite.NxpNfcLibLite;
import com.nxp.nfcliblite.Nxpnfcliblitecallback;
import com.nxp.nfcliblite.cards.DESFireEV1;
import com.nxp.nfcliblite.cards.IDESFireEV1;
import com.evsward.butler.R;

/**
 * 登陆
 * 
 * @Date Mar 5, 2015
 * @author liuwb.edward
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	private TextView showMsg;// 首页显示条
	private NxpNfcLibLite libInstance = null;
	private DESFireEV1 mDESFire;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		libInstance = NxpNfcLibLite.getInstance();
		libInstance.registerActivity(this);
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
		libInstance.stopForeGroundDispatch();
	}

	@Override
	protected void onResume() {
		super.onResume();
		libInstance.startForeGroundDispatch();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		showMsg.setText("---请刷卡---");
	}

	protected void onNewIntent(final Intent intent) {
		showMsg.setText("---正在登陆---");
		try {
			libInstance.filterIntent(intent, new Nxpnfcliblitecallback() {
				@Override
				public void onDESFireCardDetected(final IDESFireEV1 objDESFire) {
					mDESFire = (DESFireEV1) objDESFire;
					Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
					byte[] bytesid = tag.getId();
					String strdecimalNFCID = CommonUtil.bytesToHexString(bytesid);

					// 后面用
					SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
					editor.putString("decimalNFCID", strdecimalNFCID);
					editor.commit();

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

				@Override
				public void onClassicEV1CardDetected(IMFClassicEV1 paramIMFClassicEV1) {
				}
			});
		} catch (CloneDetectedException e) {
			e.printStackTrace();
		}
	}

}
