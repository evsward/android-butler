package com.evsward.butler.activity.member;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import com.evsward.butler.util.Const;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.widget.TitleBar;
import com.loopj.android.http.RequestParams;
import com.evsward.butler.R;

/**
 * 新建会员活动
 * 
 * @Date Apr 5, 2015
 * @author liuwb.edward
 */
public class RegisterActivity extends RegisterBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.getTitleBar(this, "新建会员");
		setContentView(R.layout.activity_register);
		initUI();
	}

	@Override
	protected RequestParams postSaveOrUpdateParam() {
		// entity to parameters
		RequestParams params = new RequestParams();
		params.put("nfcID", decimalNFCID);
		params.put("cardNO", nfcCardMemberNo.getText().toString());
		params.put("memName", memberName.getText().toString());
		params.put("mobile", memberMobile.getText().toString());
		params.put("memSex", gender);
		params.put("identno", memberID.getText().toString());
		params.put("empUuid", empUuid);// 操作员工编号
		// Upload a File
		try {
			params.put("image", new File(imageUri.getPath()));
		} catch (FileNotFoundException e) {
			LogUtil.se(TAG, e);
		}
		return params;
	}

	@Override
	protected void callBackHandler() {
		LogUtil.i(TAG, "------------新建会员---打印小条------------");
		registPrintTags();
		initRegister();
		closeKeyBoard();
	}

	@Override
	protected String postSaveOrUpdateUrl() {
		return URL_SERVER_ADDRESS + Const.METHOD_REGISTER;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			initRegister();
			decimalNFCID = processIntent(intent);
			getcardno("新建会员");
		}
	}

}
