package com.evsward.butler.activity.member;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

/**
 * 会员查询
 * 
 * @Date Apr 14, 2015
 * @author liuwb.edward
 */
public class SearcherActivity extends SearcherBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.getTitleBar(this, "会员查询");
		setContentView(R.layout.activity_searcher);
		initUI();
		searchBar();
	}

	// 获取跟该用户相关的比赛信息
	@Override
	protected void askMatchInfo() {
		super.askMatchInfo();
		final ProgressDialog progressDialog = ProgressDialog.show(mContext, "会员查询", "正在查询比赛信息...");
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_MEM_SEARCH_MATCH, empUuid, memID), new JsonHttpResponseHandler() {

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
							
							final String compName = comp.getCompName();
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
							int tableNOtemp = 0;
							int seatNOtemp = 0;
							for (RunCompSeatInfoList rc : comp.getRunCompSeatInfoList()) {
								tableNOtemp = rc.getTableNO();
								seatNOtemp = rc.getSeatNO();
								sb.append(rc);
								sb.append(",");
							}
							final int tableNO = tableNOtemp;
							final int seatNO = seatNOtemp;
							String result = sb.toString();

							if (!StringUtils.isBlank(result)) {// 有桌号
								result = result.substring(0, result.length() - 1) + "(点击打印桌号)";
								customMatch.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										AlertDialog dialog = new AlertDialog.Builder(mContext).create();
										dialog.setTitle("打印桌号");
										dialog.setMessage("您确定打印比赛【" + compName + "】的座位信息吗？");
										dialog.setCancelable(false);
										dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												// 调用打印桌号服务
												Toast.makeText(mContext, "正在打印...", Toast.LENGTH_SHORT).show();
												signUpPrint(compName, compTime, tableNO, seatNO);
											}
										});
										dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int buttonId) {
												dialog.dismiss();
											}
										});
										dialog.setIcon(android.R.drawable.ic_dialog_alert);
										dialog.show();
									}
								});
							}
							memPosition.setText(result);
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
