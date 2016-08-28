package com.evsward.butler.fragment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.evsward.bulter.adapter.SeatAdapter;
import com.evsward.butler.activity.CompetitionListActivity;
import com.evsward.butler.activity.MovementHistoryActivity;
import com.evsward.butler.activity.base.CompetitionManageBaseFragment;
import com.evsward.butler.activity.base.SFBaseActivity;
import com.evsward.butler.entities.CompManTableInfoList;
import com.evsward.butler.entities.CompManTableInfoList.CompManTableInfo;
import com.evsward.butler.service.NettySocketService;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class CompetitionSeatFragment extends CompetitionManageBaseFragment {
	// broadcast
	private MessageBackReciver mReciver;
	private LocalBroadcastManager mLocalBroadcastManager;
	// Service
	private ServiceConnection mConn;
	private NettySocketService.NettyBinder nettyBinder;
	private SeatAdapter MySeatAdapter;
	private ListView lvTableSeat;
	private CompManTableInfoList compManTableInfoListObj;
	private Menu mMenu;

	// 广播
	private class MessageBackReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context mContext, Intent intent) {
			String action = intent.getAction();
			if (Const.ACK_Table_Seat_List_503_ACTION.equals(action)) {
				String strTableInfoList = intent.getStringExtra(Const.ACK_Table_Seat_List_503_ACTION);
				Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
				compManTableInfoListObj = gson.fromJson(strTableInfoList, new TypeToken<CompManTableInfoList>() {
				}.getType());

				if (compManTableInfoListObj.getRspCode() != Const.RspCode_Success) {
					Toast.makeText(mContext, "比赛列表 socket错误：" + compManTableInfoListObj.getMsg(), Toast.LENGTH_SHORT).show();
					return;
				}

				int compID = compManTableInfoListObj.getCompID();
				if (compID != competitionManageActivity.compID)
					return;

				MySeatAdapter = new SeatAdapter(competitionManageActivity, compManTableInfoListObj.getCompTableInfos());
				lvTableSeat.setAdapter(MySeatAdapter);
			}
		};
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
		IntentFilter mIntentFilter = new IntentFilter(Const.ACK_Table_Seat_List_503_ACTION);
		mReciver = new MessageBackReciver();
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(competitionManageActivity);
		mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
		mConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder paramIbinder) {
				nettyBinder = (NettySocketService.NettyBinder) paramIbinder;
				String messageJson = "{\"IMEI\":\"" + Const.ANDROID_ID + "\",\"sysType\":" + Const.HI + ",\"compID\":" + compID + "}";
				nettyBinder.sendMsg(Const.Req_Table_Seat_List_503, messageJson);
			}
		};
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocalBroadcastManager.unregisterReceiver(mReciver);
	}

	@Override
	public void onStart() {
		super.onStart();
		competitionManageActivity.bindService(new Intent(competitionManageActivity, NettySocketService.class), mConn, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		super.onStop();
		competitionManageActivity.unbindService(mConn);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		this.mMenu = menu;
		super.onCreateOptionsMenu(menu, inflater);
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.competition_manage_seat, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.backtolast:
			getSherlockActivity().finish();
			startActivity(new Intent(mContext, CompetitionListActivity.class));
			break;
		case R.id.chooseAll:
			chooseAll();
			break;
		case R.id.undochooseAll:
			undochooseAll();
			break;
		case R.id.openTable:
			showComfirmOpenTable();
			break;
		case R.id.movementHistory:
			Intent intent = new Intent(mContext, MovementHistoryActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("compID", compID);
			bundle.putString("compName", competitionManageActivity.strCompName);
			bundle.putString("compTime", competitionManageActivity.strCompTime);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		default:
			Toast.makeText(competitionManageActivity, "default", Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = (View) inflater.inflate(R.layout.fragment_competition_seat, container, false);
		lvTableSeat = (ListView) rootView.findViewById(R.id.lvTableSeat);
		return rootView;
	}

	private void chooseAll() {
		MenuItem chooseAll = mMenu.findItem(R.id.chooseAll);
		MenuItem undochooseAll = mMenu.findItem(R.id.undochooseAll);
		chooseAll.setVisible(false);
		undochooseAll.setVisible(true);
		MySeatAdapter.checked.clear();
		MySeatAdapter.tableID.clear();
		int i = 0;
		for (CompManTableInfo data : compManTableInfoListObj.getCompTableInfos()) {
			if (data.getTableState() == 0) {
				MySeatAdapter.checked.put(i, true);
				MySeatAdapter.tableID.add(Integer.toString(data.getTableNO()));
				i++;
			}
		}
		MySeatAdapter.notifyDataSetChanged();
	}

	private void undochooseAll() {
		MenuItem chooseAll = mMenu.findItem(R.id.chooseAll);
		MenuItem undochooseAll = mMenu.findItem(R.id.undochooseAll);
		chooseAll.setVisible(true);
		undochooseAll.setVisible(false);
		MySeatAdapter.checked.clear();
		MySeatAdapter.tableID.clear();
		MySeatAdapter.notifyDataSetChanged();
	}

	DialogInterface.OnClickListener comfirmOpenTableListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int i) {
			dialog.dismiss();
			openTable();
		}
	};

	DialogInterface.OnClickListener cancelComfirmOpenTableListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int i) {
			dialog.dismiss();
		}
	};

	private void showComfirmOpenTable() {
		if (MySeatAdapter.tableID.isEmpty()) {
			new AlertDialog.Builder(SFBaseActivity.mContext).setTitle("提示").setMessage("请先选中牌桌").show();
			return;
		}
		AlertDialog.Builder builder = new Builder(competitionManageActivity);
		builder.setMessage("是否确定开启牌桌？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", comfirmOpenTableListener);
		builder.setNegativeButton("取消", cancelComfirmOpenTableListener);
		builder.create().show();
	}

	private void openTable() {
		String openTabNo = "";
		Set<String> set = new HashSet<String>();
		set.addAll(MySeatAdapter.tableID);
		for (Iterator<String> it = set.iterator(); it.hasNext();) {
			openTabNo += it.next().toString() + ",";
		}

		if (openTabNo.length() > 0) {
			openTabNo = openTabNo.substring(0, openTabNo.lastIndexOf(","));
		}

		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_OPEN_TABLE, empUuid, compID, openTabNo), new JsonBaseHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					if (response.getInt("rspCode") == Const.RspCode_Success) {
						Toast.makeText(mContext, "开启牌桌成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mContext, "开启牌桌失败：" + response.getString("msg"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}
		});
	}
}
