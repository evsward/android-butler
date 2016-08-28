package com.evsward.bulter.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.evsward.butler.entities.CompetitionInfoDayList.CompetitionInfoDay.CompetitionInfo;
import com.evsward.butler.R;

@SuppressLint("UseSparseArrays")
public class CompsOriginAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<CompetitionInfo> origList;

	@SuppressLint("UseSparseArrays")
	public HashMap<Integer, CompetitionInfo> compInfoMap = new HashMap<Integer, CompetitionInfo>();
	public HashMap<Integer, Boolean> compCheckedMap = new HashMap<Integer, Boolean>();

	public CompsOriginAdapter(Context context, ArrayList<CompetitionInfo> origList) {
		this.mContext = context;
		this.origList = origList;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return origList.size();
	}

	@Override
	public Object getItem(int position) {
		return origList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final CompetitionInfo competitionInfo = origList.get(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.export_match_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.checkBox = (CheckBox) view.findViewById(R.id.idCheckBoxMatchItem);
			viewHolder.finishedCompName = (TextView) view.findViewById(R.id.finishedMatchName);

			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					compInfoMap.put(position, competitionInfo);
					compCheckedMap.put(position, isChecked);
				} else {
					compInfoMap.remove(position);
					compCheckedMap.remove(position);
				}
			}
		});

		viewHolder.finishedCompName.setText(competitionInfo.getCompName());
		viewHolder.checkBox.setChecked((compCheckedMap.get(position) == null ? false : true));

		return view;
	}

	class ViewHolder {
		CheckBox checkBox;
		TextView finishedCompName;
	}
}
