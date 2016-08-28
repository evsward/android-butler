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

public class PlayerListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<PlayerInfo> playerListData;

	public PlayerListAdapter(Context context, List<PlayerInfo> playerListData) {
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
			view = mInflater.inflate(R.layout.player_one_cell_oflist, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.playerNO = (TextView) view.findViewById(R.id.playerNO);
			viewHolder.playerCardNO = (TextView) view.findViewById(R.id.playerCardNO);
			viewHolder.playerName = (TextView) view.findViewById(R.id.playerName);
			viewHolder.playerTableSeat = (TextView) view.findViewById(R.id.playerTableSeat);
			viewHolder.playerChip = (TextView) view.findViewById(R.id.playerChip);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.playerNO.setText(String.valueOf(position+1));
		viewHolder.playerCardNO.setText(player.getCardNO());
		viewHolder.playerName.setText(player.getMemName());
		viewHolder.playerTableSeat.setText(player.getSeatNO() + "/" + player.getTableNO());
		viewHolder.playerChip.setText(String.valueOf(player.getChip()));
		return view;
	}

	class ViewHolder {
		TextView playerNO;
		TextView playerCardNO;
		TextView playerName;
		TextView playerTableSeat;
		TextView playerChip;
	}
}
