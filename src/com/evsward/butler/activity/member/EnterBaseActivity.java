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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evsward.butler.entities.Competition;
import com.evsward.butler.entities.DayCompetitionList;
import com.evsward.butler.entities.RegCompetitionInfo;
import com.evsward.butler.entities.Competition.RunCompSeatInfoList;
import com.evsward.butler.util.CommonUtil;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.evsward.butler.R;

/**
 * 基类 会员报名
 * 
 * @Date May 5, 2015
 * @author liuwb.edward
 */
public abstract class EnterBaseActivity extends SearcherBaseActivity {
	protected abstract String regUrl(int compId);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NeedSuppyCard = false;// 不需要补卡
	}

	// 获取所有比赛的报名状态
	@Override
	protected void askMatchInfo() {
		super.askMatchInfo();
		NeedSuppyCard = false;// 不需要补卡
		final ProgressDialog progressDialog = ProgressDialog.show(mContext, "会员报名", "正在查询比赛信息...");
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_MEM_ENTER_MATCH, empUuid, memID), new JsonHttpResponseHandler() {

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
						List<DayCompetitionList> compInfoList = gson.fromJson(response.getString("compInfos"),
								new TypeToken<List<DayCompetitionList>>() {
								}.getType());
						LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						LinearLayout root = (LinearLayout) findViewById(R.id.matchInfoLayout);
						root.removeAllViews();// 每次进来先清空上一次查询的结果
						int nDevImei = Integer.parseInt(Const.MATCH_ID_KEY, 16) * 10;
						for (DayCompetitionList oneCompetition : compInfoList) {
							View customDate = (View) inflater.inflate(R.layout.enter_date, root, false);
							customDate.setId(nDevImei + CommonUtil.date2Int(oneCompetition.getDay_week()));
							TextView matchDate = (TextView) customDate.findViewById(R.id.matchDate);
							matchDate.setText(oneCompetition.getDay_week());
							for (Competition dm : oneCompetition.getList()) {
								View customMatch = (View) inflater.inflate(R.layout.enter_match, root, false);
								final int compId = dm.getCompID();
								final String compName = dm.getCompName();
								final String compTime = dm.getTime();
								final String buyTimes = dm.getRegCount();
								customMatch.setId(nDevImei + compId);
								TextView matchTime = (TextView) customMatch.findViewById(R.id.matchTime);
								TextView matchName = (TextView) customMatch.findViewById(R.id.matchName);
								TextView matchState = (TextView) customMatch.findViewById(R.id.matchState);
								final TextView memState = (TextView) customMatch.findViewById(R.id.memState);
								final TextView memBuyTimes = (TextView) customMatch.findViewById(R.id.memBuyTimes);
								final TextView memPosition = (TextView) customMatch.findViewById(R.id.memPosition);
								matchTime.setText(compTime);
								matchName.setText(compName);
								matchState.setText(dm.getCompStateShow());
								memState.setText(dm.getMcStateShow());
								memBuyTimes.setText(buyTimes);
								// 获得比赛座位信息字符串
								StringBuilder sb = new StringBuilder();
								for (RunCompSeatInfoList rc : dm.getRunCompSeatInfoList()) {
									sb.append(rc);
									sb.append(",");
								}
								String result = sb.toString();
								if (!StringUtils.isBlank(result))
									result = result.substring(0, result.length() - 1);
								memPosition.setText(result);
								if (Integer.parseInt(dm.getRegBut()) == 1) {
									memState.setText(dm.getMcStateShow() + "(点击报名)");
									memState.setTextColor(Color.RED);
									customMatch.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											final String regUrl = regUrl(compId);
											if (regUrl.indexOf("warn") > -1) {
												new AlertDialog.Builder(mContext).setTitle("提示").setMessage(regUrl).show();
												return;
											}
											LogUtil.d(TAG, "comID:" + compId);
											AlertDialog dialog = new AlertDialog.Builder(mContext).create();
											dialog.setTitle("会员报名");
											dialog.setMessage("您确定报名比赛【" + compName + "】吗？");
											dialog.setCancelable(false);
											dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int buttonId) {

													final ProgressDialog progressDialog = ProgressDialog.show(mContext, "请稍候", "正在报名...");
													HttpUtil.get(regUrl, new JsonHttpResponseHandler() {
														@Override
														public void onFailure(int i, Header[] aheader, Throwable throwable, JSONObject jsonobject) {
															progressDialog.dismiss();
															LogUtil.e(TAG, String.format("报名比赛异常:%s", throwable.toString()));
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
																	RegCompetitionInfo regComp = gson.fromJson(response.getString("regCompInfo"),
																			RegCompetitionInfo.class);
																	memState.setText("已报名");
																	memPosition.setText(regComp.getTableNO() + "/" + regComp.getSeatNO());
																	memBuyTimes.setText(String.valueOf(Integer.parseInt(buyTimes) + 1));// 购买手数+1
																	Toast.makeText(mContext, "报名成功!正在打印小条...", Toast.LENGTH_SHORT).show();
																	LogUtil.i(TAG, "报名【" + compName + "】成功!");
																	signUpPrint(compName, compTime, regComp.getTableNO(), regComp.getSeatNO());// 调用打印桌号服务
																} else {
																	Toast.makeText(mContext,
																			String.format("报名比赛:%s", response.getString("msg").toString()),
																			Toast.LENGTH_SHORT).show();
																	LogUtil.e(TAG, String.format("报名比赛:%s", response.getString("msg").toString()));
																}
															} catch (JSONException e) {
																LogUtil.e(TAG, "服务器通讯错误！");
															}
														}
													});

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
								((ViewGroup) customDate).addView(customMatch);
							}
							root.addView(customDate);
						}
					} else {
						Toast.makeText(mContext, String.format("查询比赛信息:%s", response.getString("msg").toString()), Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, String.format("查询比赛信息:%s", response.getString("msg").toString()));
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}
		});
	}
}
