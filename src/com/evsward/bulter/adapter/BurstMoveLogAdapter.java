package com.evsward.bulter.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.evsward.butler.activity.MovementHistoryActivity;
import com.evsward.butler.entities.CompetitionHistoryLog;
import com.evsward.butler.entities.ReqSeatMovedLog.BurstMovedLog;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.R;

public class BurstMoveLogAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private BurstMoveLogChildAdapter movementHistoryPlayerTableAdapter;
	private ArrayList<BurstMovedLog> mBurstLogList;
	private BalanceMoveLogAdapter balanceMoveLogAdapter;
	public static int burstMovedtableNOChoosed;
	public CompetitionHistoryLog selectedLog = null;

	public BurstMoveLogAdapter(Context context, ArrayList<BurstMovedLog> burstLogList, BalanceMoveLogAdapter balanceMoveLogAdapter) {
		this.mContext = context;
		this.mBurstLogList = burstLogList;
		this.mInflater = LayoutInflater.from(mContext);
		this.balanceMoveLogAdapter = balanceMoveLogAdapter;
	}

	@Override
	public int getCount() {
		return mBurstLogList.size();
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
		int tableNo = mBurstLogList.get(position).getTableNO();
		final List<CompetitionHistoryLog> logList = mBurstLogList.get(position).getLogList();
		LogUtil.i("BurstMoveLogAdapter", tableNo + "桌： 爆桌数据共" + logList.size() + "条。共" + mBurstLogList.size() + "桌");
		View view;
		final ViewHolder viewHolder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.movement_history_table_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.tvTableNo = (TextView) view.findViewById(R.id.tvTableNo);
			viewHolder.lvMovementTableList = (ListView) view.findViewById(R.id.lvMovementTableList);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.lvMovementTableList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 150 * logList.size()));
		movementHistoryPlayerTableAdapter = new BurstMoveLogChildAdapter(mContext, logList);

		viewHolder.tvTableNo.setText(StringUtils.leftPad(String.valueOf(tableNo), 3, "0") + "桌");
		viewHolder.lvMovementTableList.setAdapter(movementHistoryPlayerTableAdapter);
		viewHolder.lvMovementTableList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int subposition, long id) {
				MovementHistoryActivity.burstMovedLogChoosed = mBurstLogList.get(position);
				BurstMoveLogAdapter.burstMovedtableNOChoosed = logList.get(subposition).getLogID();
				notifyDataSetChanged();
				balanceMoveLogAdapter.selectedLog = null;
				balanceMoveLogAdapter.notifyDataSetChanged();
			}
		});
		return view;
	}

	class ViewHolder {
		TextView tvTableNo;
		ListView lvMovementTableList;
	}
}
