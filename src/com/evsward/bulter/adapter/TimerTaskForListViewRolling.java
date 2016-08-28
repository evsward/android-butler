package com.evsward.bulter.adapter;

import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

public class TimerTaskForListViewRolling extends TimerTask {
	private static ListView listView;
	private static int smoothBy = 1;

	private static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			listView.smoothScrollBy(smoothBy, 0);
		};
	};

	public TimerTaskForListViewRolling(TVListBaseAdapter<?> TVListAdpter, ListView listView) {
		TimerTaskForListViewRolling.listView = listView;
		listView.setAdapter(TVListAdpter);
	}

	@Override
	public void run() {
		Message msg = handler.obtainMessage();
		handler.sendMessage(msg);
	}

}