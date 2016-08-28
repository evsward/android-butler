package com.evsward.bulter.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evsward.butler.entities.CompetitionHistoryLog;
import com.evsward.butler.R;

public class BalanceMoveLogAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<CompetitionHistoryLog> logList;
	public CompetitionHistoryLog selectedLog = null;

	public BalanceMoveLogAdapter(Context context, List<CompetitionHistoryLog> logList) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.logList = logList;
	}

	@Override
	public int getCount() {
		return logList.size();
	}

	@Override
	public Object getItem(int position) {
		return logList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		CompetitionHistoryLog logInfo = logList.get(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.movement_history_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.tvPlayerName = (TextView) view.findViewById(R.id.tvPlayerName);
			viewHolder.tvCardNo = (TextView) view.findViewById(R.id.tvCardNo);
			viewHolder.tvSrcTableSeatNo = (TextView) view.findViewById(R.id.tvSrcTableSeatNo);
			viewHolder.tvDesTableSeatNo = (TextView) view.findViewById(R.id.tvDesTableSeatNo);

			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		String strSrcTableSeatNo = Integer.toString(logInfo.getOldTableNO()) + "/" + Integer.toString(logInfo.getOldSeatNO());
		String strDesTableSeatNo = Integer.toString(logInfo.getNewTableNO()) + "/" + Integer.toString(logInfo.getNewSeatNO());
		
		viewHolder.tvPlayerName.setText(logInfo.getMemName());
		viewHolder.tvCardNo.setText(logInfo.getCardNO());
		viewHolder.tvSrcTableSeatNo.setText(strSrcTableSeatNo);
		viewHolder.tvDesTableSeatNo.setText(strDesTableSeatNo);
		if (selectedLog != null && selectedLog.getLogID() == logInfo.getLogID()) {
			viewHolder.tvPlayerName.setBackgroundResource(R.drawable.shape_white_border_selected_frame_line_without_top);
			viewHolder.tvCardNo.setBackgroundResource(R.drawable.shape_white_border_selected_frame_line_without_left_top);
			viewHolder.tvSrcTableSeatNo.setBackgroundResource(R.drawable.shape_white_border_selected_frame_line_without_left_top);
			viewHolder.tvDesTableSeatNo.setBackgroundResource(R.drawable.shape_white_border_selected_frame_line_without_left_top);
		} else {
			viewHolder.tvPlayerName.setBackgroundResource(R.drawable.shape_white_border_without_top);
			viewHolder.tvCardNo.setBackgroundResource(R.drawable.shape_white_border_without_left_top);
			viewHolder.tvSrcTableSeatNo.setBackgroundResource(R.drawable.shape_white_border_without_left_top);
			viewHolder.tvDesTableSeatNo.setBackgroundResource(R.drawable.shape_white_border_without_left_top);
		}
		return view;
	}

	class ViewHolder {
		TextView tvPlayerName;
		TextView tvCardNo;
		TextView tvSrcTableSeatNo;
		TextView tvDesTableSeatNo;
	}
}
