package com.evsward.bulter.adapter;

import java.util.ArrayList;
import java.util.Formatter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evsward.butler.entities.ScreenPrizeInfo;
import com.evsward.butler.util.Const;
import com.evsward.butler.R;

public class TVPrizeListAdapter extends TVListBaseAdapter<ScreenPrizeInfo> {

	private boolean mIfEnglish;

	public TVPrizeListAdapter(Context context, ArrayList<ScreenPrizeInfo> screenPrizeList, boolean ifEnglish) {
		super(context, screenPrizeList);
		this.mIfEnglish = ifEnglish;
	}

	public int getCount() {
		if (tvBaseList.size() < Const.minPrizeListNum) {
			return tvBaseList.size();
		} else {
			return Integer.MAX_VALUE;
		}
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		int position = 0;
		ScreenPrizeInfo currentPrize;
		if (tvBaseList.size() < Const.minPrizeListNum) {
			position = index;
		} else {
			position = index % tvBaseList.size();
		}
		currentPrize = tvBaseList.get(position);

		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = inflater.inflate(R.layout.tvscreen_one_match_prize_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.tvMatchPrize = (TextView) view.findViewById(R.id.tvMatchPrize);
			viewHolder.tvMatchRanking = (TextView) view.findViewById(R.id.tvMatchRanking);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		if (mIfEnglish) {
			viewHolder.tvMatchRanking.setText("#" + Integer.toString(currentPrize.getRanking()));
			viewHolder.tvMatchPrize.setText(new Formatter().format("%.2f", (double) currentPrize.getAwordMoneyInt() / 1000) + "K RMB");
		} else {
			viewHolder.tvMatchRanking.setText("第" + Integer.toString(currentPrize.getRanking()) + "名");
			viewHolder.tvMatchPrize.setText(new Formatter().format("%.2f", (double) currentPrize.getAwordMoneyInt() / 10000) + "万元");
		}
		return view;
	}

	class ViewHolder {
		TextView tvMatchRanking, tvMatchPrize;
	}
}
