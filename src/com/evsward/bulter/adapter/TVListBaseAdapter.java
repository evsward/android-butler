package com.evsward.bulter.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TVListBaseAdapter<T> extends BaseAdapter {
	protected LayoutInflater inflater;
	protected ArrayList<T> tvBaseList;

	public TVListBaseAdapter(Context context, ArrayList<T> tvBaseList) {
		super();
		this.tvBaseList = (ArrayList<T>) tvBaseList;
		this.inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return Integer.MAX_VALUE;
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int index, View view, ViewGroup arg2) {
		return null;
	}
}
