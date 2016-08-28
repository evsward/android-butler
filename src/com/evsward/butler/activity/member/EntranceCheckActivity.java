package com.evsward.butler.activity.member;

import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evsward.butler.entities.Competition;
import com.evsward.butler.entities.Competition.RunCompSeatInfoList;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.widget.TitleBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.evsward.butler.R;

public class EntranceCheckActivity extends SearcherBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.getTitleBar(this, "入场安检");
		setContentView(R.layout.activity_entrance_check);
		initUI();
//		searchBar();
		unEditable();
		NeedSuppyCard = false;// 不需要补卡
		LinearLayout IDArea = (LinearLayout) memberID.getParent();
		LinearLayout MobileArea = (LinearLayout) memberMobile.getParent();
		IDArea.setVisibility(View.GONE);
		MobileArea.setVisibility(View.GONE);
	}

	@Override
	protected void askMatchInfo() {
		super.askMatchInfo();
		NeedSuppyCard = false;// 不需要补卡
		final ProgressDialog progressDialog = ProgressDialog.show(mContext, "会员查询", "正在查询比赛信息...");
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_MEM_ENTRANCE_CHECK, empUuid, memID), new JsonHttpResponseHandler() {

			@Override
			public void onFailure(int i, Header[] aheader, Throwable throwable, JSONObject jsonobject) {
				progressDialog.dismiss();
				LogUtil.e(TAG, String.format("查询比赛信息异常:%s", throwable.toString()));
			}

			@Override
			public void onFailure(int i, Header[] aheader, String s, Throwable throwable) {
				progressDialog.dismiss();
				Toast.makeText(mContext, "请检查网络连接！", Toast.LENGTH_SHORT).show();
				LogUtil.se(TAG, throwable);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				progressDialog.dismiss();
				try {
					if (response.getInt("rspCode") == Const.RspCode_Success) {
						Gson gson = new Gson();
						List<Competition> compList = gson.fromJson(response.getString("compInfos"), new TypeToken<List<Competition>>() {
						}.getType());
						LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						LinearLayout root = (LinearLayout) findViewById(R.id.matchInfoLayout);
						root.removeAllViews();// 每次进来先清空上一次查询的结果
						int nDevImei = Integer.parseInt(Const.MATCH_ID_KEY, 16) * 10;
						for (Competition comp : compList) {
							View customMatch = (View) inflater.inflate(R.layout.competition, root, false);
							int compId = comp.getCompID();
							final String compTime = comp.getTime();
							customMatch.setId(nDevImei + compId);
							TextView compDate = (TextView) customMatch.findViewById(R.id.compDate);
							TextView matchTime = (TextView) customMatch.findViewById(R.id.compTime);
							TextView matchName = (TextView) customMatch.findViewById(R.id.matchName);
							TextView memPosition = (TextView) customMatch.findViewById(R.id.memPosition);
							TextView regCount = (TextView) customMatch.findViewById(R.id.regCount);
							TextView matchState = (TextView) customMatch.findViewById(R.id.matchState);
							TextView memState = (TextView) customMatch.findViewById(R.id.memState);
							compDate.setText(comp.getDateNoTime());
							matchTime.setText(compTime);
							matchName.setText(comp.getCompName());
							regCount.setText(comp.getRegCount());
							matchState.setText(comp.getCompStateShow());
							memState.setText(comp.getMcStateShow());
							// 获得比赛座位信息字符串
							StringBuilder sb = new StringBuilder();
							for (RunCompSeatInfoList rc : comp.getRunCompSeatInfoList()) {
								sb.append(rc);
								sb.append(",");
							}
							memPosition.setText(sb.toString());
							memPosition.setTextColor(Color.RED);
							root.addView(customMatch);
						}
					} else {
						Toast.makeText(mContext, String.format("查询比赛信息:%s", response.getString("msg").toString()), Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, String.format("查询比赛信息%s", response.getString("msg").toString()));
					}
				} catch (JSONException e) {
					Toast.makeText(mContext, String.format("查询比赛信息失败:请检查后台日志-->%s", e.toString()), Toast.LENGTH_SHORT).show();
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}
		});

	}
}
