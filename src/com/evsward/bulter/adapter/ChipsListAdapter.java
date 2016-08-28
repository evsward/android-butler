package com.evsward.bulter.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evsward.butler.entities.PlayerInfo;
import com.evsward.butler.R;

public class ChipsListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<PlayerInfo> playerListData;

	public ChipsListAdapter(Context context, List<PlayerInfo> playerListData) {
		this.mContext = context;
		this.playerListData = playerListData;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return playerListData.size();
	}

	@Override
	public Object getItem(int position) {
		return playerListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PlayerInfo player = playerListData.get(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.chips_one_cell_oflist, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.playerCardNO = (TextView) view.findViewById(R.id.playerCardNO);
			viewHolder.playerName = (TextView) view.findViewById(R.id.playerName);
			viewHolder.playerMobileNO = (TextView) view.findViewById(R.id.playerMobileNO);
			viewHolder.playerChip = (TextView) view.findViewById(R.id.playerChip);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.playerCardNO.setText(player.getCardNO());
		viewHolder.playerName.setText(player.getMemName());
		viewHolder.playerMobileNO.setText(player.getMemMobile());
		viewHolder.playerChip.setText(String.valueOf(player.getChip()));
		return view;
	}

	class ViewHolder {
		TextView playerCardNO;
		TextView playerName;
		TextView playerMobileNO;
		TextView playerChip;
	}
}
