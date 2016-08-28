package com.evsward.butler.fragment.tvscreen;

import java.util.ArrayList;
import java.util.Timer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.evsward.bulter.adapter.TVPlayerListAdapter;
import com.evsward.bulter.adapter.TimerTaskForListViewRolling;
import com.evsward.butler.activity.base.TVBaseFragment;
import com.evsward.butler.entities.ScreenCompPlayerInfo;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class OneMatchSeatFragment extends TVBaseFragment {
	private ListView lvPlayerList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tvscreen_one_match_seat, container, false);
		lvPlayerList = (ListView) view.findViewById(R.id.matchSeatListInfo);
		reqTVPlayerList();
		return view;
	}

	private void reqTVPlayerList() {
		HttpUtil.get(URL_SERVER_ADDRESS + String.format(Const.METHOD_TV_PLAYERLIST, tvScreenActivity.compID, Const.ANDROID_ID),
				new JsonBaseHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							if (response.getInt("rspCode") != Const.RspCode_Success) {
								Toast.makeText(mContext, response.getString("msg"), Toast.LENGTH_SHORT).show();
								return;
							}
							Gson gson = new Gson();
							ArrayList<ScreenCompPlayerInfo> screenCompPlayerInfoListObj = gson.fromJson(response.getString("playerInfoList"),
									new TypeToken<ArrayList<ScreenCompPlayerInfo>>() {
									}.getType());
							if (screenCompPlayerInfoListObj.size() == 0) {
								Toast.makeText(mContext, "暂时没有玩家报名参赛！", Toast.LENGTH_SHORT).show();
								return;
							}
							TVPlayerListAdapter tvPlayerListAdapter = new TVPlayerListAdapter(mContext, screenCompPlayerInfoListObj);
							if (screenCompPlayerInfoListObj.size() < Const.minPlayerEntered) {// 报名人数少则不滚动
								lvPlayerList.setAdapter(tvPlayerListAdapter);
							} else {
								new Timer().schedule(new TimerTaskForListViewRolling(tvPlayerListAdapter, lvPlayerList), 100, 100);
							}
						} catch (JSONException e) {
							LogUtil.se(TAG, e);
						}
					}
				});
	}
}