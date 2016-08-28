package com.evsward.butler.activity.member;

import android.os.Bundle;

import com.evsward.butler.util.Const;
import com.evsward.butler.widget.TitleBar;
import com.evsward.butler.R;

/**
 * 会员报名
 * 
 * @Date May 5, 2015
 * @author liuwb.edward
 */
public class EnterActivity extends EnterBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.getTitleBar(this, "会员报名");
		setContentView(R.layout.activity_enter);
		initUI();
		searchBar();
		unEditable();
	}

	@Override
	protected String regUrl(int compId) {
		return String.format(URL_SERVER_ADDRESS + Const.METHOD_MEM_REG_MATCH, empUuid, memID, compId);
	}
}
