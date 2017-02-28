package com.evsward.bulter.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evsward.butler.entities.CompetitionInfoDayList.CompetitionInfoDay.CompetitionInfo;
import com.evsward.butler.util.Const;
import com.evsward.butler.R;

public class CompetitionAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;

	List<CompetitionInfo> compListInfo;
	int nSelectedMatchID = -1;

	public CompetitionAdapter(Context context, List<CompetitionInfo> compListInfo, int nSelectedMatchID) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.compListInfo = compListInfo;
		this.nSelectedMatchID = nSelectedMatchID;
	}

	@Override
	public int getCount() {
		return compListInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return compListInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		CompetitionInfo competitionInfo = compListInfo.get(position);
		View view;
		ViewHolder viewHolder;
		// 优化出Bug，暂时屏蔽，待重新优化
		// if (convertView == null) {
		view = mInflater.inflate(R.layout.match_item, parent, false);
		viewHolder = new ViewHolder();
		viewHolder.tvMatchName = (TextView) view.findViewById(R.id.tvMatchName);
		viewHolder.tvMatchTime = (TextView) view.findViewById(R.id.tvMatchTime);
		viewHolder.tvMatchIsOpen = (TextView) view.findViewById(R.id.tvMatchIsOpen);
		viewHolder.tvMatchChongJin = (TextView) view.findViewById(R.id.tvMatchChongJin);
		viewHolder.tvMatchCanSaiFei = (TextView) view.findViewById(R.id.tvMatchCanSaiFei);
		viewHolder.tvMatchGuanLiFei = (TextView) view.findViewById(R.id.tvMatchGuanLiFei);
		viewHolder.tvMatchChuShiChouMa = (TextView) view.findViewById(R.id.tvMatchChuShiChouMa);
		viewHolder.tvMatchCurMangZhulvl = (TextView) view.findViewById(R.id.tvMatchCurMangZhulvl);
		view.setTag(viewHolder);
		// } else {
		// view = convertView;
		// viewHolder = (ViewHolder) view.getTag();
		// }

		viewHolder.tvMatchName.setText(competitionInfo.getCompName());
		viewHolder.tvMatchTime.setText(competitionInfo.getTime());
		if(competitionInfo.getCurRank()==0&&competitionInfo.getBeforeChip()==Const.countdownBeforeChip){
			viewHolder.tvMatchChuShiChouMa.setText(String.valueOf(0));
			viewHolder.tvMatchIsOpen.setText(R.string.tv_compinfo_comp_state_countdown);
		}else{
			viewHolder.tvMatchChuShiChouMa.setText(String.valueOf(competitionInfo.getBeginChip()));
			viewHolder.tvMatchIsOpen.setText(competitionInfo.getCompStateShow());
		}
		String strReEntry = "否";
		if (competitionInfo.getReEntry() == 1) {
			strReEntry = "是";
		}
		viewHolder.tvMatchChongJin.setText(strReEntry);
		viewHolder.tvMatchCanSaiFei.setText(String.valueOf(competitionInfo.getRegFee()));
		viewHolder.tvMatchGuanLiFei.setText(String.valueOf(competitionInfo.getServiceFee()));
		viewHolder.tvMatchCurMangZhulvl.setText(String.valueOf(competitionInfo.getCurRank()));

		if (nSelectedMatchID == competitionInfo.getCompID()) {
			view.setBackgroundResource(R.drawable.shape_corner_white_no_border_selected);
		}
		return view;
	}

	class ViewHolder {
		TextView tvMatchName;
		TextView tvMatchTime;
		TextView tvMatchIsOpen;
		TextView tvMatchChongJin;
		TextView tvMatchCanSaiFei;
		TextView tvMatchGuanLiFei;
		TextView tvMatchChuShiChouMa;
		TextView tvMatchCurMangZhulvl;
	}
}
