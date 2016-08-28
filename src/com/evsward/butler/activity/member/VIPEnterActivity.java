package com.evsward.butler.activity.member;

import org.apache.commons.lang.StringUtils;

import android.os.Bundle;
import android.widget.EditText;

import com.evsward.butler.util.Const;
import com.evsward.butler.widget.TitleBar;
import com.evsward.butler.R;

/**
 * VIP会员报名
 * 
 * @Date May 7, 2015
 * @author liuwb.edward
 */
public class VIPEnterActivity extends EnterBaseActivity {
	private EditText vipTableNO;
	private EditText vipSeatNO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.getTitleBar(this, "VIP会员报名");
		setContentView(R.layout.activity_vipenter);
		initUI();
		searchBar();
		unEditable();
		vipTableNO = (EditText) findViewById(R.id.vipTableNO);
		vipSeatNO = (EditText) findViewById(R.id.vipSeatNO);
	}

	@Override
	protected String regUrl(int compId) {
		String tableNo = vipTableNO.getText().toString();
		String seatNo = vipSeatNO.getText().toString();
		if (StringUtils.isBlank(tableNo)) {
			return "warn:请填写桌号！";
		}
		if (StringUtils.isBlank(seatNo)) {
			return "warn:请填写座位号！";
		}
		return String.format(URL_SERVER_ADDRESS + Const.METHOD_VIP_REG_MATCH, empUuid, memID,compId,Integer.parseInt(tableNo),Integer.parseInt(seatNo));
	}
}