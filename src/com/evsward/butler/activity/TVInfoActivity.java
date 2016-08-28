package com.evsward.butler.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.evsward.butler.activity.base.BaseActivity;
import com.evsward.butler.entities.Competition;
import com.evsward.butler.entities.Screen;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.widget.TitleBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class TVInfoActivity extends BaseActivity implements OnItemSelectedListener {
	private final String compShow[] = { "现场比赛信息", "选手座位表" };
	private EditText updateBoxName;
	private Switch switchLanguage;
	private Screen oneTVScreen;
	private boolean screenCompNameFlag;// radiobutton 是否选中赛事名称
	private int pushType = 0;// 默认不推送
	private int language = 0;// 默认为中文
	private int compID = 0;

	private RadioButton screenBlank, screenEntranceCheck, screenCompList, screenCompName;

	private ArrayList<Competition> compsList;
	private ArrayAdapter<Competition> compNameArrayAdapter;
	private Spinner spinnerCompName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.getTitleBar(this, "大屏幕信息", "保存", saveOnclickListener);
		setContentView(R.layout.activity_tvinfo);

		Bundle bundle = this.getIntent().getExtras();
		Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
		oneTVScreen = gson.fromJson(bundle.getString("screen"), new TypeToken<Screen>() {
		}.getType());
		compsList = gson.fromJson(bundle.getString("noEndCompsOnday"), new TypeToken<List<Competition>>() {
		}.getType());

		updateBoxName = (EditText) findViewById(R.id.updateBoxName);
		updateBoxName.setText(oneTVScreen.getDevName());// 显示当前盒子的名字

		// spinner
		spinnerCompName = (Spinner) findViewById(R.id.spinnerCompName);
		Spinner spinnerScreenShow = (Spinner) findViewById(R.id.spinnerScreenShow);
		compNameArrayAdapter = new ArrayAdapter<Competition>(this, android.R.layout.simple_spinner_item, compsList);
		ArrayAdapter<String> compShowAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, compShow);
		compNameArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		compShowAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spinnerCompName.setAdapter(compNameArrayAdapter);
		spinnerScreenShow.setAdapter(compShowAdapter);
		spinnerCompName.setOnItemSelectedListener(this);
		spinnerScreenShow.setOnItemSelectedListener(this);

		// radio button
		RadioGroup radioShowContentGroup = (RadioGroup) findViewById(R.id.radioShowContentGroup);
		screenBlank = (RadioButton) findViewById(R.id.screenBlank);
		screenEntranceCheck = (RadioButton) findViewById(R.id.screenEntranceCheck);
		screenCompList = (RadioButton) findViewById(R.id.screenCompList);
		screenCompName = (RadioButton) findViewById(R.id.screenCompName);
		pushType = oneTVScreen.getPushType();// 盒子当前推送内容
		compID = oneTVScreen.getCompID();// 盒子当前的比赛ID
		switch (pushType) {// case 2,3 盒子不自动显示当前比赛那条信息
		case 0:
			screenBlank.setChecked(true);
			compID = 0;
			break;
		case 1:
			screenCompList.setChecked(true);
			compID = 0;
			break;
		case 2:
			screenCompNameFlag = true;
			screenCompName.setChecked(true);
			spinnerScreenShow.setSelection(0);
			compID = oneTVScreen.getCompID();
			spinnerCompName.setSelection(compNameArrayAdapter.getPosition(getCompetitionByID(compID)));
			break;
		case 3:
			screenCompNameFlag = true;
			screenCompName.setChecked(true);
			spinnerScreenShow.setSelection(1);
			compID = oneTVScreen.getCompID();
			spinnerCompName.setSelection(compNameArrayAdapter.getPosition(getCompetitionByID(compID)));
			break;
		case 4:
			screenEntranceCheck.setChecked(true);
			compID = 0;
			break;
		default:
			break;
		}
		radioShowContentGroup.setOnCheckedChangeListener(radioChangeListener);
		// 语言
		switchLanguage = (Switch) findViewById(R.id.switchLanguage);
		language = oneTVScreen.getLanguage();// 显示当前盒子的语言
		switch (language) {
		case 0:
			switchLanguage.setText("中文");
			switchLanguage.setChecked(false);
			break;
		case 1:
			switchLanguage.setText("English");
			switchLanguage.setChecked(true);
			break;
		default:
			break;
		}
		switchLanguage.setOnCheckedChangeListener(switchLanguageListener);
	}

	private Competition getCompetitionByID(int compID) {
		LogUtil.d(TAG, "查询的比赛ID是：" + compID);
		for (Competition com : compsList) {
			if (com.getCompID() == compID) {
				return com;
			}
		}
		return null;
	}

	private CompoundButton.OnCheckedChangeListener switchLanguageListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				switchLanguage.setText("English");
				language = 1;
			} else {
				switchLanguage.setText("中文");
				language = 0;
			}
		}
	};
	private RadioGroup.OnCheckedChangeListener radioChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.screenBlank:
				pushType = 0;
				compID = 0;
				break;
			case R.id.screenCompList:
				pushType = 1;
				compID = 0;
				break;
			case R.id.screenCompName:
				Competition compSelected = (Competition) spinnerCompName.getSelectedItem();
				if (compSelected != null) {
					compID = compSelected.getCompID();
					screenCompNameFlag = true;
					pushType = 2;
				} else {
					Toast.makeText(mContext, "当前没有可选择的比赛！", Toast.LENGTH_SHORT).show();
					screenBlank.setChecked(true);
				}
				break;
			case R.id.screenEntranceCheck:
				pushType = 4;
				compID = 0;
				break;
			default:
				break;
			}
		}
	};

	private View.OnClickListener saveOnclickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			LogUtil.d(TAG, "pushType = " + pushType);
			String modifiedBoxName = updateBoxName.getText().toString();
			if (StringUtils.isBlank(modifiedBoxName)) {
				new AlertDialog.Builder(mContext).setTitle("警告！").setMessage("请输入盒子的名称以供区分！").show();
				return;
			}
			HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_UPDATE_SCREEN, oneTVScreen.getDevImei(), modifiedBoxName, pushType, compID,
					language), new JsonBaseHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					try {
						if (response.getInt("rspCode") == Const.RspCode_Success) {
							Toast.makeText(mContext, "盒子信息更新成功", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(TVInfoActivity.this, TVManageActivity.class);
							startActivity(intent);
							finish();
						} else {
							Toast.makeText(mContext, "盒子信息更新失败：" + response.getString("msg"), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						LogUtil.se(TAG, e);
					}
				}
			});
		}
	};

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.spinnerCompName: // 获取用户选择的比赛
			if (screenCompNameFlag) {
				Competition selected = (Competition) parent.getItemAtPosition(position);
				compID = selected.getCompID();
				LogUtil.d(TAG, "选中一个比赛 " + selected.getCompName() + " ID:" + selected.getCompID());
			}
			break;
		case R.id.spinnerScreenShow: // 获取用户选择的显示内容
			if (screenCompNameFlag)
				pushType = position == 0 ? 2 : 3;
			LogUtil.d(TAG, "选中比赛显示内容" + pushType);
			break;
		default:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}