package com.evsward.butler.fragment.tvscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evsward.butler.activity.base.TVBaseFragment;
import com.evsward.butler.entities.ScreenWelcome;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class EnterFieldFragment extends TVBaseFragment {
	private TextView tvMemName;
	private TextView tvCompInfoName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tvscreen_enter_field, container, false);
		tvMemName = (TextView) view.findViewById(R.id.TVMemName);
		tvCompInfoName = (TextView) view.findViewById(R.id.tvCompInfoName);
		Bundle bundle = getArguments();
		String strEntranceCheckInfo = bundle.getString("jsonMsg");
		updateUIContent(strEntranceCheckInfo);
		LogUtil.i(TAG, "盒子显示比赛名称：" + tvCompInfoName);
		return view;
	}

	public void updateUIContent(String strEntranceCheckInfo) {
		try {
			Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
			ScreenWelcome tvWelcomeObj = gson.fromJson(strEntranceCheckInfo, new TypeToken<ScreenWelcome>() {
			}.getType());
			// tvCompInfoName.setText("欢迎参加" + tvWelcomeObj.getMemNickName());
			if (tvWelcomeObj.getMemID() != 0) {
				tvMemName.setText(tvWelcomeObj.getMemName());
			} else {// TODO 显示广告

			}
		} catch (Exception e) {
			LogUtil.se(TAG, e);
		}
	}
}
