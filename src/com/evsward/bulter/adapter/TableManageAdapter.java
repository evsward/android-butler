package com.evsward.bulter.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evsward.butler.entities.ReqCardTableList;
import com.evsward.butler.entities.ReqCardTableList.CardTable;
import com.evsward.butler.R;

public class TableManageAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	public ArrayList<CardTable> cardTableList;

	public TableManageAdapter(Context context, ArrayList<CardTable> cardTableList) {
		super();
		this.cardTableList = cardTableList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return cardTableList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int index, View view, ViewGroup arg2) {
		CardTable cardTable = cardTableList.get(index);
		view = inflater.inflate(R.layout.table_manage_item, null);
		view.setBackgroundColor(Color.WHITE);
		TextView tvTableId = (TextView) view.findViewById(R.id.tvTableId);
		TextView tvTableStatus = (TextView) view.findViewById(R.id.tvTableStatus);
		tvTableId.setText(Integer.toString(cardTable.getTableNO()));
		String strTabStatus = "";
		switch (cardTable.getTableState()) {
		case ReqCardTableList.TABLESTATE.TABLEUSING:
			strTabStatus = cardTable.getCompName();// "被比赛占用";
			break;
		case ReqCardTableList.TABLESTATE.TABLEIDLE:
			strTabStatus = "空闲";
			break;
		case ReqCardTableList.TABLESTATE.TABLEDISABLED:
			strTabStatus = "不可用";
			break;
		}
		tvTableStatus.setText(strTabStatus);

		if (index % 2 != 0) {
			view.setBackgroundColor(Color.argb(250, 255, 255, 255));
		} else {
			view.setBackgroundColor(Color.argb(250, 224, 243, 250));
		}

		return view;
	}
}
