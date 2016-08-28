package com.evsward.butler.fragment.tvscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evsward.butler.activity.base.TVBaseFragment;
import com.evsward.butler.R;

public class EmptyFragment extends TVBaseFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tvscreen_empty, container, false);

		return view;
	}

}
