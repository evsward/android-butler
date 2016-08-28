package com.evsward.butler.fragment.tvscreen;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.evsward.bulter.adapter.TVMatchListAdapter;
import com.evsward.butler.activity.base.TVBaseFragment;
import com.evsward.butler.entities.ScreenCompInfo;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class MatchListFragment extends TVBaseFragment {

	private TVMatchListAdapter tvMatchListAdapter;
	private ListView lvMatchList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tvscreen_matches_list, container, false);
		lvMatchList = (ListView) view.findViewById(R.id.matchPlayersListInfo);
		Bundle bundle = getArguments();
		String strTVMatchList = bundle.getString("jsonMsg");
		updateUIContent(strTVMatchList);
		return view;
	}

	public void updateUIContent(String strTVMatchList) {
		try {
			Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
			ArrayList<ScreenCompInfo> screenCompInfoListObj = gson.fromJson(strTVMatchList, new TypeToken<ArrayList<ScreenCompInfo>>() {
			}.getType());
			tvMatchListAdapter = new TVMatchListAdapter(mContext, screenCompInfoListObj);
			lvMatchList.setAdapter(tvMatchListAdapter);
		} catch (Exception e) {
			LogUtil.se(TAG, e);
		}
	}

}
