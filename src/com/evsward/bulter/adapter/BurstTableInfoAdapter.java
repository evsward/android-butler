package com.evsward.bulter.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evsward.butler.entities.AckNewSeatInfoList.NewSeatInfo;
import com.evsward.butler.R;

public class BurstTableInfoAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<NewSeatInfo> newSeatInfoList;

	public BurstTableInfoAdapter(Context context, ArrayList<NewSeatInfo> NewSeatInfoList) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.newSeatInfoList = NewSeatInfoList;
	}

	@Override
	public int getCount() {
		return newSeatInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return newSeatInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		NewSeatInfo newSeatInfo = newSeatInfoList.get(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.burst_table_dialog_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.tvMemName = (TextView) view.findViewById(R.id.tvMemName);
			viewHolder.tvNewTableNo = (TextView) view.findViewById(R.id.tvNewTableNo);
			viewHolder.tvNewSeatNo = (TextView) view.findViewById(R.id.tvNewSeatNo);

			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.tvMemName.setText(newSeatInfo.getMemName());
		viewHolder.tvNewTableNo.setText(Integer.toString(newSeatInfo.getTableNO()));
		viewHolder.tvNewSeatNo.setText(Integer.toString(newSeatInfo.getSeatNO()));

		return view;
	}

	class ViewHolder {
		TextView tvMemName;
		TextView tvNewTableNo;
		TextView tvNewSeatNo;
	}
}
