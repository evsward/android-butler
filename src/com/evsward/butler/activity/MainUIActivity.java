package com.evsward.butler.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TextView;

import com.evsward.butler.activity.base.BaseActivity;
import com.evsward.butler.activity.member.EnterActivity;
import com.evsward.butler.activity.member.EntranceCheckActivity;
import com.evsward.butler.activity.member.RegisterActivity;
import com.evsward.butler.activity.member.SearcherActivity;
import com.evsward.butler.activity.member.VIPEnterActivity;
import com.evsward.butler.entities.Employee;
import com.evsward.butler.entities.Privilege;
import com.evsward.butler.entities.Role;
import com.evsward.butler.util.Const;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

/**
 * 权限主界面
 * 
 * @Date Mar 5, 2015
 * @author liuwb.edward
 */
public class MainUIActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		findViewById(R.id.privilegeAddMember).setOnClickListener(this);
		findViewById(R.id.privilegeSearchMember).setOnClickListener(this);
		findViewById(R.id.privilegeTVManager).setOnClickListener(this);
		findViewById(R.id.privilegeInitNFCCard).setOnClickListener(this);
		findViewById(R.id.privilegeMemEnter).setOnClickListener(this);
		findViewById(R.id.privilegeVIPEnter).setOnClickListener(this);
		findViewById(R.id.privilegeEntranceCheck).setOnClickListener(this);
		findViewById(R.id.privilegeCompetitionList).setOnClickListener(this);
		findViewById(R.id.privilegeCompetitionAdvancedManage).setOnClickListener(this);
		findViewById(R.id.privilegeTableResManage).setOnClickListener(this);

		Bundle bundle = this.getIntent().getExtras();// 获取传递过来的封装了数据的Bundle
		String employeeInfo = bundle.getString("employeeInfo");
		Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
		Employee employee = gson.fromJson(employeeInfo, new TypeToken<Employee>() {
		}.getType());
		// 存入当前员工ID
		SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
		editor.putString("empUuid", employee.getEmpUUID());
		editor.commit();
		TextView employeeName = (TextView) findViewById(R.id.employeeName);
		TextView employeeRole = (TextView) findViewById(R.id.employeeRole);
		employeeName.setText(employee.getEmpName());
		String memRoles = "";
		Role[] roles = employee.getRoles();
		if (roles.length > 0) {
			for (int i = 0; i < roles.length; i++) {
				Privilege[] privileges = roles[i].getPrivileges();
				if (privileges.length > 0) {
					for (int j = 0; j < privileges.length; j++) {
						String privilegeID = privileges[j].getPrivName();
						Resources res = getResources();
						TableLayout tb = (TableLayout) findViewById(res.getIdentifier(privilegeID, "id", "com.evsward.butler"));
						tb.setVisibility(View.VISIBLE);
					}
				}
				memRoles += roles[i].getRoleNameShow() + ",";
			}
			memRoles = memRoles.substring(0, memRoles.length() - 1);
			employeeRole.setText(memRoles);
		} else {
			employeeRole.setText("无任何权限");
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.privilegeAddMember:
			intent = new Intent(mContext, RegisterActivity.class);
			break;
		case R.id.privilegeSearchMember:
			intent = new Intent(mContext, SearcherActivity.class);
			break;
		case R.id.privilegeTVManager:
			intent = new Intent(mContext, TVManageActivity.class);
			break;
		case R.id.privilegeMemEnter:
			intent = new Intent(mContext, EnterActivity.class);
			break;
		case R.id.privilegeVIPEnter:
			intent = new Intent(mContext, VIPEnterActivity.class);
			break;
		case R.id.privilegeEntranceCheck:
			intent = new Intent(mContext, EntranceCheckActivity.class);
			break;
		case R.id.privilegeInitNFCCard:
			intent = new Intent(mContext, InitNFCCardActivity.class);
			break;
		case R.id.privilegeCompetitionList:
			intent = new Intent(mContext, CompetitionListActivity.class);
			break;
		case R.id.privilegeCompetitionAdvancedManage:
			intent = new Intent(mContext, CompetitionAdvancedManageActivity.class);
			break;
		case R.id.privilegeTableResManage:
			intent = new Intent(mContext, TableManageActivity.class);
			break;
		default:
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}
}
