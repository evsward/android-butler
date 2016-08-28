package com.evsward.bulter.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evsward.butler.entities.prize.CompManPrizeInfo;
import com.evsward.butler.fragment.CompetitionRewardsFragment;
import com.evsward.butler.R;

public class PrizeListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<CompManPrizeInfo> prizeListData;

	public PrizeListAdapter(Context context, List<CompManPrizeInfo> prizeListData) {
		this.mContext = context;
		this.prizeListData = prizeListData;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return prizeListData.size();
	}

	@Override
	public Object getItem(int position) {
		return prizeListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CompManPrizeInfo playerPrize = prizeListData.get(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.reward_one_cell_oflist, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.ranking = (TextView) view.findViewById(R.id.ranking);
			viewHolder.percentage = (TextView) view.findViewById(R.id.percentage);
			viewHolder.bonus = (TextView) view.findViewById(R.id.bonus);
			viewHolder.playerName = (TextView) view.findViewById(R.id.playerName);
			viewHolder.playerID = (TextView) view.findViewById(R.id.playerID);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		if (CompetitionRewardsFragment.nSelectedItemRanking == playerPrize.getRanking()) {
			viewHolder.ranking.setBackgroundResource(R.drawable.shape_white_border_selected_frame_line_without_top);
			viewHolder.percentage.setBackgroundResource(R.drawable.shape_white_border_selected_frame_line_without_left_top);
			viewHolder.bonus.setBackgroundResource(R.drawable.shape_white_border_selected_frame_line_without_left_top);
			viewHolder.playerName.setBackgroundResource(R.drawable.shape_white_border_selected_frame_line_without_left_top);
			viewHolder.playerID.setBackgroundResource(R.drawable.shape_white_border_selected_frame_line_without_left_top);
		}else{
			viewHolder.ranking.setBackgroundResource(R.drawable.shape_white_border_without_top);
			viewHolder.percentage.setBackgroundResource(R.drawable.shape_white_border_without_left_top);
			viewHolder.bonus.setBackgroundResource(R.drawable.shape_white_border_without_left_top);
			viewHolder.playerName.setBackgroundResource(R.drawable.shape_white_border_without_left_top);
			viewHolder.playerID.setBackgroundResource(R.drawable.shape_white_border_without_left_top);
		}
		viewHolder.ranking.setText(String.valueOf(playerPrize.getRanking()));
		viewHolder.percentage.setText(playerPrize.getPercent() + "%");
		viewHolder.bonus.setText(String.valueOf(playerPrize.getAmountInt()));
		viewHolder.playerName.setText(playerPrize.getMemName());
		viewHolder.playerID.setText(playerPrize.getMemIdentNO());
		return view;
	}

	class ViewHolder {
		TextView ranking;
		TextView percentage;
		TextView bonus;
		TextView playerName;
		TextView playerID;
	}
}
