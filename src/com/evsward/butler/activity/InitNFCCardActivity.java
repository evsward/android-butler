package com.evsward.butler.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.evsward.butler.activity.base.BaseActivity;
import com.evsward.butler.util.CommonUtil;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.widget.TitleBar;
import com.evsward.butler.R;

public class InitNFCCardActivity extends BaseActivity {
	private final int UploadNormalCard_Value = 1;
	private final int UploadPrivilegeCard_Value = 2;
	int mUploadType = UploadNormalCard_Value;

	// nfc
	protected NfcAdapter nfcAdapter;
	protected PendingIntent pendingIntent;
	protected IntentFilter[] mFilters;
	protected String[][] mTechLists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.getTitleBar(this, "初始化NFC");
		setContentView(R.layout.activity_init_nfc_card);

		// nfc
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		ndef.addCategory("*/*");
		mFilters = new IntentFilter[] { ndef };// 过滤器
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() }, new String[] { NfcA.class.getName() } };// 允许扫描的标签类型

		RadioGroup radiogroup = (RadioGroup) findViewById(R.id.rbUploadCard);
		radiogroup.setOnCheckedChangeListener(listen);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, mFilters, mTechLists);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	private OnCheckedChangeListener listen = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			int id = group.getCheckedRadioButtonId();
			switch (id) {
			case R.id.rbUploadNormalCard:
				mUploadType = UploadNormalCard_Value;
				break;
			case R.id.rbUploadPrivilegeCard:
				mUploadType = UploadPrivilegeCard_Value;
				break;
			default:
				break;
			}
		}
	};

	protected void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			byte[] bytesid = tag.getId();

			if (mUploadType == UploadNormalCard_Value) {
				long decimalNFCID = CommonUtil.convertHex2Long(CommonUtil.bytesToHexString(bytesid));
				HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_NEW_MEMBER_NFC, decimalNFCID), new JsonBaseHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							if (response.getInt("rspCode") == Const.RspCode_Success) {
								Toast.makeText(mContext, "---添加 选手NFC卡 成功---", Toast.LENGTH_SHORT).show();
								LogUtil.i(TAG, "---添加 选手NFC卡 成功---");
							} else {
								Toast.makeText(mContext, String.format("添加 选手NFC卡 失败:%s", response.getString("msg").toString()), Toast.LENGTH_SHORT)
										.show();
								LogUtil.i(TAG, String.format("添加 选手NFC卡 失败:%s", response.getString("msg").toString()));
							}
						} catch (JSONException e) {
							LogUtil.e(TAG, "服务器通讯错误！");
						}
					}
				});
			} else if (mUploadType == UploadPrivilegeCard_Value) {
				String strsid = CommonUtil.bytesToHexString(bytesid);
				HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_NEW_MANAGER_NFC, strsid), new JsonBaseHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							if (response.getInt("rspCode") == Const.RspCode_Success) {
								Toast.makeText(mContext, "---添加 权限NFC卡 成功---", Toast.LENGTH_SHORT).show();
								LogUtil.i(TAG, "---添加 权限NFC卡 成功---");
							} else {
								Toast.makeText(mContext, String.format("添加 权限NFC卡 失败:%s", response.getString("msg").toString()), Toast.LENGTH_SHORT)
										.show();
								LogUtil.i(TAG, String.format("添加 权限NFC卡 失败:%s", response.getString("msg").toString()));
							}
						} catch (JSONException e) {
							LogUtil.e(TAG, "服务器通讯错误！");
						}
					}
				});
			}
		}
	}
}
