package com.evsward.butler.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.evsward.bulter.adapter.ChipsListAdapter;
import com.evsward.butler.activity.base.CompetitionManageBaseFragment;
import com.evsward.butler.entities.PlayerInfo;
import com.evsward.butler.fragment.dialog.ChipsModifyChipsDialogFragment;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class CompetitionChipsFragment extends CompetitionManageBaseFragment {
	private EditText keyWord;
	private Button searchPlayerChips;
	private ListView chipsListInfo;
	// ----
	private ChipsListAdapter chipsListAdapter;
	private List<PlayerInfo> playerListData = new ArrayList<PlayerInfo>();
	private FragmentManager mFragmentManager;
	private ChipsModifyChipsDialogFragment mChipsModifyDialogFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		TAG = "chips fragment";
		mChipsModifyDialogFragment = new ChipsModifyChipsDialogFragment("修改筹码量");
		View view = inflater.inflate(R.layout.fragment_competition_chips, container, false);
		keyWord = (EditText) view.findViewById(R.id.keyWord);
		searchPlayerChips = (Button) view.findViewById(R.id.searchPlayerChips);
		searchPlayerChips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String strKeyWord = keyWord.getText().toString().trim();
				if (!StringUtils.isBlank(strKeyWord)) {
					getPlayerChips(null, strKeyWord);// 1、点击搜索按钮打开一个玩家筹码量
				} else {
					Toast.makeText(mContext, "请输入卡号！", Toast.LENGTH_SHORT).show();
				}
			}
		});
		chipsListInfo = (ListView) view.findViewById(R.id.chipsListInfo);
		initChipsList();
		mFragmentManager = getFragmentManager();
		return view;
	}

	private void initChipsList() {
		HttpUtil.get(URL_SERVER_ADDRESS + String.format(Const.METHOD_MATCH_MANAGE_CHIPS, empUuid, compID), new JsonBaseHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					if (response.getInt("rspCode") == Const.RspCode_Success) {
						Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
						playerListData = gson.fromJson(response.getString("playersChip"), new TypeToken<ArrayList<PlayerInfo>>() {
						}.getType());
						chipsListAdapter = new ChipsListAdapter(competitionManageActivity, playerListData);
						chipsListInfo.setAdapter(chipsListAdapter);
						chipsListInfo.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								PlayerInfo player = playerListData.get(position);
								getPlayerChips(null, player.getCardNO());// 2、点击列表中一项修改筹码量
							}
						});
					} else {
						Toast.makeText(mContext, "获取筹码列表:" + response.getString("msg"), Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, String.format("获取筹码:%s", response.getString("msg")));
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}

		});
	}

	// 3、刷卡修改筹码量
	public void getChipsByNFCPlayerID(long decimalNFCID) {
		getPlayerChips(decimalNFCID, null);
	}

	/**
	 * 获取玩家的筹码量
	 * 
	 * @param decimalNFCID
	 *            NFC卡号，若为空，则通过会员卡号查询
	 * @param cardNO
	 *            会员卡号，若为空，则通过NFC卡号查询
	 */
	public void getPlayerChips(Long decimalNFCID, String cardNO) {
		String getChipsURL = "";
		if (cardNO == null) {
			getChipsURL = URL_SERVER_ADDRESS + String.format(Const.METHOD_GET_PLAYER_CHIPS, empUuid, compID, decimalNFCID, "");
		} else {
			getChipsURL = URL_SERVER_ADDRESS + String.format(Const.METHOD_GET_PLAYER_CHIPS, empUuid, compID, 0, cardNO);
		}
		// 利用decimalNFCID请求到筹码筹码（只有1个筹码，不存在重复。系统默认会取筹码最多的那个。）
		HttpUtil.get(getChipsURL, new JsonBaseHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					if (response.getInt("rspCode") == Const.RspCode_Success) {
						Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
						PlayerInfo player = gson.fromJson(response.getString("playerChip"), new TypeToken<PlayerInfo>() {
						}.getType());
						showDialog(player);
					} else {
						Toast.makeText(mContext, "获取筹码列表:" + response.getString("msg"), Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, String.format("获取筹码:%s", response.getString("msg")));
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}

		});

	}

	public static final int CHIP_DIALOG = 1;

	private void showDialog(PlayerInfo player) {
		mChipsModifyDialogFragment.setPlayer(player);
		mChipsModifyDialogFragment.setTargetFragment(this, CHIP_DIALOG);
		mChipsModifyDialogFragment.show(mFragmentManager, "ChipsModifyChips");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CHIP_DIALOG:
			if (resultCode == Activity.RESULT_OK) {
				initChipsList();// 修改后同步刷新 “筹码”，“玩家”两列数据
				((CompetitionPlayerFragment) competitionManageActivity.competitionPlayerFragment).initPlayerList();
				LogUtil.i(TAG, "---------------------- 刷新比赛管理（筹码）列表 ----------------------");
			}
			break;
		}
	}

}
