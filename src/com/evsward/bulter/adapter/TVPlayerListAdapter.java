package com.evsward.bulter.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evsward.butler.entities.ScreenCompPlayerInfo;
import com.evsward.butler.util.Const;
import com.evsward.butler.R;

public class TVPlayerListAdapter extends TVListBaseAdapter<ScreenCompPlayerInfo> {

	public TVPlayerListAdapter(Context context, ArrayList<ScreenCompPlayerInfo> tvBaseList) {
		super(context, tvBaseList);
	}

	public int getCount() {
		if (tvBaseList.size() < Const.minPlayerEntered) {
			return tvBaseList.size();
		} else {
			return Integer.MAX_VALUE;
		}
	}

	@Override
	public View getView(int index, View convertView, ViewGroup arg2) {
		int pos = 0;
		ScreenCompPlayerInfo screenCompPlayerInfo;
		if (tvBaseList.size() < Const.minPlayerEntered) {
			pos = index;
		} else {
			pos = index % tvBaseList.size();
		}
		screenCompPlayerInfo = (ScreenCompPlayerInfo) tvBaseList.get(pos);

		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = inflater.inflate(R.layout.tvscreen_one_match_seat_item, arg2, false);
			viewHolder = new ViewHolder();
			viewHolder.tvSerial = (TextView) view.findViewById(R.id.tvSerial);
			viewHolder.tvCardNo = (TextView) view.findViewById(R.id.tvCardNo);
			viewHolder.tvPlayerName = (TextView) view.findViewById(R.id.tvPlayerName);
			viewHolder.tvTableSeatNo = (TextView) view.findViewById(R.id.tvTableSeatNo);
			viewHolder.tvChipsNum = (TextView) view.findViewById(R.id.tvChipsNum);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.tvSerial.setText(Integer.toString(screenCompPlayerInfo.getSerialNO()));
		viewHolder.tvCardNo.setText(screenCompPlayerInfo.getCardNO());
		viewHolder.tvPlayerName.setText(screenCompPlayerInfo.getMemName());
		viewHolder.tvTableSeatNo.setText(screenCompPlayerInfo.getTableNO() + "/" + screenCompPlayerInfo.getSeatNO());
		viewHolder.tvChipsNum.setText(Integer.toString(screenCompPlayerInfo.getChip()));

		return view;
	}

	class ViewHolder {
		TextView tvSerial;
		TextView tvCardNo;
		TextView tvPlayerName;
		TextView tvTableSeatNo;
		TextView tvChipsNum;
	}
}
