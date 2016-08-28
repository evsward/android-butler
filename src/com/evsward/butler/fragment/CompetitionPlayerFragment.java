package com.evsward.butler.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evsward.bulter.adapter.PlayerListAdapter;
import com.evsward.butler.activity.base.CompetitionManageBaseFragment;
import com.evsward.butler.entities.PlayerInfo;
import com.evsward.butler.entities.PlayersTotalInfo;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class CompetitionPlayerFragment extends CompetitionManageBaseFragment {
	private TextView totalPeople, totalChips;
	private PlayerListAdapter playerListAdapter;
	private ListView playerListInfo;
	private List<PlayerInfo> playerListData = new ArrayList<PlayerInfo>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_competition_player, container, false);
		playerListInfo = (ListView) view.findViewById(R.id.playerListInfo);
		totalPeople = (TextView) view.findViewById(R.id.totalPeople);
		totalChips = (TextView) view.findViewById(R.id.totalChips);
		initPlayerList();
		return view;
	}
	
	// 发送HTTP请求，设置totalPeople,totalChips,获得playerList
	public void initPlayerList() {
		HttpUtil.get(URL_SERVER_ADDRESS + String.format(Const.METHOD_MATCH_MANAGE_PLAYERS, empUuid, compID), new JsonBaseHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					if (response.getInt("rspCode") == Const.RspCode_Success) {
						Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
						PlayersTotalInfo pti = gson.fromJson(response.getString("playersTotalInfo"), new TypeToken<PlayersTotalInfo>() {
						}.getType());
						totalPeople.setText(pti.getTotalRegedPlayerEdit() + "/" + pti.getTotalRegedPlayer());
						totalChips.setText(pti.getTotalChipEdit() + "/" + pti.getTotalChip());
						playerListData = pti.getPlayerInfos();
						playerListAdapter = new PlayerListAdapter(getSherlockActivity(), playerListData);
						playerListInfo.setAdapter(playerListAdapter);
					} else {
						Toast.makeText(mContext, "获取玩家列表:" + response.getString("msg"), Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, String.format("获取玩家:%s", response.getString("msg")));
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}

		});
	}
}
