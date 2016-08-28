package com.evsward.butler.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.evsward.bulter.adapter.CompTargetAdapter;
import com.evsward.bulter.adapter.CompsOriginAdapter;
import com.evsward.butler.activity.base.BaseActivity;
import com.evsward.butler.entities.CompAdvanManage;
import com.evsward.butler.entities.CompetitionInfoDayList.CompetitionInfoDay.CompetitionInfo;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.widget.TitleBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class CompetitionAdvancedManageActivity extends BaseActivity implements OnClickListener {
	// Base UI
	private Button btnImportFinishComp, btnImportCancel;
	private ListView lvFinishCompList, lvImportComp;
	private RadioGroup rgImportCondition;
	// Adapter
	private CompsOriginAdapter compsOriginAdapter;
	private CompTargetAdapter compTargetAdapter;
	// params
	private String originCompIDs;
	private int destCompID;
	private int condition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.getTitleBar(this, "比赛进阶管理");
		setContentView(R.layout.activity_competition_advanced_manage);
		btnImportFinishComp = (Button) findViewById(R.id.btnImportFinishComp);
		btnImportCancel = (Button) findViewById(R.id.btnImportCancel);
		lvFinishCompList = (ListView) findViewById(R.id.lvFinishCompList);
		lvImportComp = (ListView) findViewById(R.id.lvImportComp);
		rgImportCondition = (RadioGroup) findViewById(R.id.rgImportCondition);
		btnImportFinishComp.setOnClickListener(this);
		btnImportCancel.setOnClickListener(this);
		rgImportCondition.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rbImportConditionRandom:
					condition = 0;
					break;
				case R.id.rbImportConditionNonRandom:
					condition = 1;
					break;
				default:
					break;
				}
			}
		});
		reqCompAdvancedManage();
	}

	private void reqCompAdvancedManage() {
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_MATCH_ADVANCED_COMP_LIST, empUuid), new JsonBaseHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					if (response.getInt("rspCode") == Const.RspCode_Success) {
						Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
						CompAdvanManage compAdvanManage = gson.fromJson(response.toString(), new TypeToken<CompAdvanManage>() {
						}.getType());
						compsOriginAdapter = new CompsOriginAdapter(mContext, compAdvanManage.getOrigList());
						compTargetAdapter = new CompTargetAdapter(mContext, compAdvanManage.getDestList());
						lvFinishCompList.setAdapter(compsOriginAdapter);
						lvImportComp.setAdapter(compTargetAdapter);
					} else {
						Toast.makeText(mContext, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, response.getString("msg").toString());
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnImportFinishComp:
			HashMap<Integer, CompetitionInfo> compsOriginCheckedMap = compsOriginAdapter.compInfoMap;
			CompetitionInfo compTargetChecked = compTargetAdapter.matchChecked;
			if (compsOriginCheckedMap == null || compsOriginCheckedMap.size() < 1) {
				new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请选择“已结束的比赛”！").show();
				return;
			}
			if (compTargetChecked == null) {
				new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请选择“需要导入的比赛名称”！").show();
				return;
			}
			StringBuilder sbMsg = new StringBuilder();
			StringBuilder sbCompIDs = new StringBuilder();
			sbMsg.append("是否确认将选中的比赛:").append("\n\n");

			@SuppressWarnings("rawtypes")
			Iterator iter = compsOriginCheckedMap.entrySet().iterator();
			while (iter.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) iter.next();
				@SuppressWarnings("unused")
				Object key = entry.getKey();
				Object val = entry.getValue();

				CompetitionInfo compInfo = (CompetitionInfo) val;
				sbMsg.append("“" + compInfo.getCompName() + "”").append("\n");
				sbCompIDs.append(compInfo.getCompID()).append(",");
			}
			sbMsg.append("导入到比赛“").append(compTargetChecked.getCompName()).append("”中？");
			String showReminderMsg = sbMsg.toString();
			String result = sbCompIDs.toString();
			originCompIDs = result.substring(0, result.length() - 1);
			destCompID = compTargetChecked.getCompID();

			AlertDialog.Builder builder = new Builder(mContext);
			builder.setMessage(showReminderMsg);
			builder.setTitle("提示");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					doImport();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			break;
		case R.id.btnImportCancel:
			finish();
			break;
		default:
			break;
		}
	}

	protected void doImport() {
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_MATCH_ADVANCED, empUuid, originCompIDs, destCompID, condition),
				new JsonBaseHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							if (response.getInt("rspCode") == Const.RspCode_Success) {
								Toast.makeText(mContext, "导入成功!", Toast.LENGTH_SHORT).show();
								finish();
							} else {
								Toast.makeText(mContext, response.getString("msg"), Toast.LENGTH_SHORT).show();
								LogUtil.w(TAG, response.getString("msg"));
							}
						} catch (JSONException e) {
							LogUtil.e(TAG, "服务器通讯错误！");
						}
					}
				});
	}

}
