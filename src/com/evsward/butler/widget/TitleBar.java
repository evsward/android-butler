package com.evsward.butler.widget;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.evsward.butler.R;

public class TitleBar {
	private static Activity mActivity;

	// 标题栏默认样式
	public static void getTitleBar(Activity activity, String title) {
		mActivity = activity;
		activity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		activity.setContentView(R.layout.custom_title);
		activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		TextView textView = (TextView) activity.findViewById(R.id.title_text);
		textView.setText(title);
		Button titleBackBtn = (Button) activity.findViewById(R.id.title_back);
		titleBackBtn.setPadding(0, 0, 40, 0);
		titleBackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mActivity.finish();
			}
		});
	}

	// 标题栏自定义按钮
	public static void getTitleBar(Activity activity, String title, String buttonName, OnClickListener onClickListener) {
		mActivity = activity;
		activity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		activity.setContentView(R.layout.custom_title);
		activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		TextView textView = (TextView) activity.findViewById(R.id.title_text);
		textView.setText(title);
		Button titleBackBtn = (Button) activity.findViewById(R.id.title_back);
		if (buttonName != null && onClickListener != null) {
			titleBackBtn.setText(buttonName);
			titleBackBtn.setOnClickListener(onClickListener);
		} else {
			titleBackBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mActivity.finish();
				}
			});
		}
	}
}
