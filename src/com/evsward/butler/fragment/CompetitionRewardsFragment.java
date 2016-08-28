package com.evsward.butler.fragment;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.evsward.bulter.adapter.PrizeListAdapter;
import com.evsward.butler.activity.base.CompetitionManageBaseFragment;
import com.evsward.butler.activity.base.SFBaseActivity;
import com.evsward.butler.entities.prize.CompManPrizeInfo;
import com.evsward.butler.fragment.dialog.PrizeModifyPeopleDialogFragment;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class CompetitionRewardsFragment extends CompetitionManageBaseFragment {
	private TextView playersNum, totalBonus, awardPeopleNum;
	private ListView prizeListInfo;
	private ArrayList<CompManPrizeInfo> prizeListData;
	private PrizeListAdapter prizeListAdapter;
	private CompManPrizeInfo onePrizeChoosed;
	private int totalPrize;
	public static int nSelectedItemRanking = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_competition_rewards, container, false);
		playersNum = (TextView) view.findViewById(R.id.playersNum);
		totalBonus = (TextView) view.findViewById(R.id.totalBonus);
		awardPeopleNum = (TextView) view.findViewById(R.id.awardNum);
		prizeListInfo = (ListView) view.findViewById(R.id.prizeListInfo);
		initRewardUI();
		return view;
	}

	@Override
	public void onStop() {
		super.onStop();
		nSelectedItemRanking = -1;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.competition_manage_reward, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (onePrizeChoosed == null) {
			new AlertDialog.Builder(SFBaseActivity.mContext).setTitle("提示").setMessage("请选择一条玩家的奖励项").show();
			return false;
		}
		switch (item.getItemId()) {
		case R.id.changeBonus:
			showDialog(onePrizeChoosed);
			break;
		case R.id.printBonus:
			Toast.makeText(mContext, "打印中...", Toast.LENGTH_SHORT).show();
			printPlayerPrize();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void printPlayerPrize() {
		competitionManageActivity.printRewards(competitionManageActivity.strCompName, competitionManageActivity.strCompTime,
				onePrizeChoosed.getMemName(), Integer.toString(onePrizeChoosed.getMemID()), onePrizeChoosed.getTableNO(),
				onePrizeChoosed.getSeatNO(), onePrizeChoosed.getMemSex() == 1 ? true : false, onePrizeChoosed.getMemIdentNO(),
				(int) ((float) totalPrize * onePrizeChoosed.getPercent()), onePrizeChoosed.getRanking());
	}

	public static final int PRIZE_DIALOG = 1;

	private void showDialog(CompManPrizeInfo playerPrize) {
		PrizeModifyPeopleDialogFragment newFragment = new PrizeModifyPeopleDialogFragment("修改奖金金额", playerPrize);
		newFragment.setTargetFragment(this, PRIZE_DIALOG);
		newFragment.show(getFragmentManager(), "PrizeModifyPrize");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PRIZE_DIALOG:
			if (resultCode == Activity.RESULT_OK) {
				initRewardUI();// 修改后刷新
				LogUtil.i(TAG, "---------------------- 刷新比赛管理（奖池）列表 ----------------------");
			}
			break;
		}
	}

	private void initRewardUI() {

		HttpUtil.get(URL_SERVER_ADDRESS + String.format(Const.METHOD_REWARD_LIST, empUuid, compID), new JsonBaseHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					if (response.getInt("rspCode") == Const.RspCode_Success) {
						Gson gson = new Gson();
						prizeListData = gson.fromJson(response.getString("compManPrizeInfoList"), new TypeToken<ArrayList<CompManPrizeInfo>>() {
						}.getType());
						playersNum.setText(response.getString("totalRegedPlayerEdit") + "/" + response.getString("totalRegedPlayer"));
						totalBonus.setText(response.getString("totalPrizeEdit") + "/" + response.getString("totalPrize"));
						totalPrize = Integer.parseInt(response.getString("totalPrize"));
						awardPeopleNum.setText(response.getString("prizeTotalPlayer"));
						prizeListAdapter = new PrizeListAdapter(getSherlockActivity(), prizeListData);
						prizeListInfo.setAdapter(prizeListAdapter);
						prizeListInfo.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								onePrizeChoosed = prizeListData.get(position);
								nSelectedItemRanking = onePrizeChoosed.getRanking();
								prizeListAdapter.notifyDataSetChanged();
							}
						});
					} else {
						Toast.makeText(mContext, "获取奖池列表:" + response.getString("msg"), Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, String.format("获取奖池列表:%s", response.getString("msg")));
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}

		});

	}
}
