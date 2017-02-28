package com.evsward.bulter.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evsward.butler.entities.Competition;
import com.evsward.butler.entities.ScreenCompInfo;
import com.evsward.butler.util.Const;
import com.evsward.butler.R;

public class TVMatchListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public ArrayList<ScreenCompInfo> screenCompInfoList;

	public TVMatchListAdapter(Context context, ArrayList<ScreenCompInfo> screenCompInfoList) {
		super();
		this.screenCompInfoList = screenCompInfoList;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return screenCompInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ScreenCompInfo screenCompInfo = screenCompInfoList.get(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.tvscreen_matches_list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.tvMatchName = (TextView) view.findViewById(R.id.tvMatchName);
			viewHolder.tvMatchDate = (TextView) view.findViewById(R.id.tvMatchDate);
			viewHolder.tvMatchTime = (TextView) view.findViewById(R.id.tvMatchTime);
			viewHolder.tvCurBlindLvl = (TextView) view.findViewById(R.id.tvCurBlindLvl);
			viewHolder.tvPlayerNum = (TextView) view.findViewById(R.id.tvPlayerNum);
			viewHolder.tvInitChips = (TextView) view.findViewById(R.id.tvInitChips);
			viewHolder.tvMatchStateDesc = (TextView) view.findViewById(R.id.tvMatchStateDesc);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tvMatchName.setText(screenCompInfo.getCompName());
		viewHolder.tvMatchDate.setText(screenCompInfo.getCompStartDateStr());
		viewHolder.tvMatchTime.setText(screenCompInfo.getCompStartTime());
		viewHolder.tvCurBlindLvl.setText(Integer.toString(screenCompInfo.getCurRoundRank()));
		viewHolder.tvPlayerNum.setText(Integer.toString(screenCompInfo.getTotalRegedPlayerCount()));
		switch (screenCompInfo.getCompState()) {
		case Competition.COMPSTATE.ENDSIGNNOTSTART:
			viewHolder.tvMatchStateDesc.setText(R.string.tv_compinfo_comp_state_endsignnotstart);
			break;
		case Competition.COMPSTATE.ENDSIGNSTART:
			viewHolder.tvMatchStateDesc.setText(R.string.tv_compinfo_comp_state_endsignstart);
			break;
		case Competition.COMPSTATE.CLOSE:
			viewHolder.tvMatchStateDesc.setText(R.string.tv_compinfo_comp_state_close);
			break;
		case Competition.COMPSTATE.SIGNNOTSTART:
			viewHolder.tvMatchStateDesc.setText(R.string.tv_compinfo_comp_state_signnotstart);
			break;
		case Competition.COMPSTATE.SIGNSTART:
			viewHolder.tvMatchStateDesc.setText(R.string.tv_compinfo_comp_state_signstart);
			break;
		}
		if(screenCompInfo.getCurRoundRank()==0&&screenCompInfo.getBeforeChip()==Const.countdownBeforeChip){
			viewHolder.tvInitChips.setText(Integer.toString(0));
			viewHolder.tvMatchStateDesc.setText(R.string.tv_compinfo_comp_state_countdown);
		}else{
			viewHolder.tvInitChips.setText(Integer.toString(screenCompInfo.getBeginChip()));
		}
		return view;
	}

	class ViewHolder {
		TextView tvMatchName;
		TextView tvMatchDate;
		TextView tvMatchTime;
		TextView tvCurBlindLvl;
		TextView tvPlayerNum;
		TextView tvInitChips;
		TextView tvMatchStateDesc;
	}
}