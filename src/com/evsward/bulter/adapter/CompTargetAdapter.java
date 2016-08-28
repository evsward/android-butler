package com.evsward.bulter.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.evsward.butler.entities.CompetitionInfoDayList.CompetitionInfoDay.CompetitionInfo;
import com.evsward.butler.R;

public class CompTargetAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<CompetitionInfo> destList;

	public CompetitionInfo matchChecked;
	public int pos = -1;

	// 构造函数
	public CompTargetAdapter(Context context, ArrayList<CompetitionInfo> destList) {
		this.mContext = context;
		this.destList = destList;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return destList.size();
	}

	@Override
	public Object getItem(int position) {
		return destList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final CompetitionInfo competitionInfo = destList.get(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.import_match_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.rbImportMatchItem = (RadioButton) view.findViewById(R.id.rbImportMatchItem);
			viewHolder.tvImportMatchName = (TextView) view.findViewById(R.id.tvImportMatchName);
			viewHolder.rbImportMatchItem.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						pos = position;
						matchChecked = competitionInfo;
						notifyDataSetChanged();
					}
				}
			});
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tvImportMatchName.setText(competitionInfo.getCompName());
		viewHolder.rbImportMatchItem.setChecked(position == pos ? true : false);
		return view;
	}

	class ViewHolder {
		RadioButton rbImportMatchItem;
		TextView tvImportMatchName;
	}
}
