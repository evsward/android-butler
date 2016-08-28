package com.evsward.bulter.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.evsward.butler.entities.CompetitionInfoDayList.CompetitionInfoDay;
import com.evsward.butler.entities.CompetitionInfoDayList.CompetitionInfoDay.CompetitionInfo;
import com.evsward.butler.R;

public class CompetitionListAdapter extends BaseAdapter {
	public static Menu menu;

	public CompetitionInfo selectedCompetition;

	private Context mContext;
	private LayoutInflater mInflater;
	private List<CompetitionInfoDay> padCompList;
	CompetitionAdapter CompListDayAdapter;

	public CompetitionListAdapter(Context context, Menu menu, List<CompetitionInfoDay> padCompList) {
		this.mContext = context;
		CompetitionListAdapter.menu = menu;
		this.padCompList = padCompList;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return padCompList.size();
	}

	@Override
	public Object getItem(int position) {
		return padCompList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int headLineHeight = 100;
		int perLineHeight = 290;
		String strCompDayWeek = (String) padCompList.get(position).getDay_week();
		final List<CompetitionInfo> dayCompList = padCompList.get(position).getCompListInfo();
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.compday_item, parent, false);
			view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, perLineHeight * dayCompList.size() + headLineHeight));
			viewHolder = new ViewHolder();
			viewHolder.tvTableNo = (TextView) view.findViewById(R.id.tvCompDay);
			viewHolder.lvCompDayList = (ListView) view.findViewById(R.id.lvCompDayList);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		CompListDayAdapter = new CompetitionAdapter(mContext, dayCompList, selectedCompetition == null ? -1 : selectedCompetition.getCompID());

		viewHolder.tvTableNo.setText(strCompDayWeek);
		viewHolder.lvCompDayList.setAdapter(CompListDayAdapter);
		viewHolder.lvCompDayList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int subposition, long id) {
				selectedCompetition = dayCompList.get(subposition);
				notifyDataSetChanged();
				MenuItem deleteMatch = menu.findItem(R.id.deleteMatch);
				MenuItem finishMatch = menu.findItem(R.id.finishMatch);
				MenuItem matchManage = menu.findItem(R.id.matchManage);
				matchManage.setVisible(true);
				switch (selectedCompetition.getCompState()) {
				case 0:
				case 1:
				case 2:
					deleteMatch.setVisible(true);
					finishMatch.setVisible(false);
					break;
				case 4:
					finishMatch.setVisible(true);
					deleteMatch.setVisible(false);
					break;
				case 3:
				case 5:
					deleteMatch.setVisible(false);
					finishMatch.setVisible(false);
					break;
				default:
					break;
				}
			}
		});
		return view;
	}

	class ViewHolder {
		TextView tvTableNo;
		ListView lvCompDayList;
	}
}
