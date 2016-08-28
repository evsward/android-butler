package com.evsward.bulter.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import com.evsward.butler.entities.Advert;
import com.evsward.butler.R;

public class ADListAdapter extends BaseAdapter {
	private Context mContext;
	private List<Advert> adListData;
	private LayoutInflater mInflater;

	public int selectedAdID = -1;

	public ADListAdapter(Context context, List<Advert> adListData) {
		super();
		this.mContext = context;
		this.adListData = adListData;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return adListData.size();
	}

	@Override
	public Object getItem(int position) {
		return adListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setSelectedAdvertID(int index) {
		selectedAdID = index;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Advert ad = adListData.get(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.ad_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.adItemRadioButton = (RadioButton) view.findViewById(R.id.adItemRadioButton);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.adItemRadioButton.setText(ad.getAdvertName());
		if (ad.getAdvertID() == selectedAdID) {
			viewHolder.adItemRadioButton.setChecked(true);
		} else {
			viewHolder.adItemRadioButton.setChecked(false);
		}
		return view;
	}

	class ViewHolder {
		RadioButton adItemRadioButton;
	}
}
