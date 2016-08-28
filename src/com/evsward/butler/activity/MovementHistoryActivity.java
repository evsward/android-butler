package com.evsward.butler.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.evsward.bulter.adapter.BalanceMoveLogAdapter;
import com.evsward.bulter.adapter.BurstMoveLogAdapter;
import com.evsward.butler.activity.base.SFBaseActivity;
import com.evsward.butler.entities.AckNewSeatInfoList;
import com.evsward.butler.entities.CompetitionHistoryLog;
import com.evsward.butler.entities.ReqSeatMovedLog;
import com.evsward.butler.entities.AckNewSeatInfoList.NewSeatInfo;
import com.evsward.butler.entities.ReqSeatMovedLog.BurstMovedLog;
import com.evsward.butler.service.PrintUtil;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class MovementHistoryActivity extends SFBaseActivity {

	private BurstMoveLogAdapter burstMoveLogAdapter;
	private BalanceMoveLogAdapter balanceMoveLogAdapter;

	private ListView burstOptRecordList, balanceOptRecordList;
	private ReqSeatMovedLog seatLogAll = null;
	private int compID = -1;// 当前比赛ID
	private String strCompName;
	private String strCompTime;

	public static BurstMovedLog burstMovedLogChoosed;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		useActionBarMenu("选手座位移动情况");
		setContentView(R.layout.activity_movement_history);
		initUI();
	}

	private void initUI() {
		Bundle bundle = this.getIntent().getExtras();
		compID = bundle.getInt("compID");
		strCompName = bundle.getString("compName");
		strCompTime = bundle.getString("compTime");
		burstOptRecordList = (ListView) findViewById(R.id.burstOptRecordList);
		balanceOptRecordList = (ListView) findViewById(R.id.balanceOptRecordList);
		reqMovementLogData(compID);

		balanceOptRecordList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		balanceOptRecordList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				balanceMoveLogAdapter.selectedLog = seatLogAll.getBalanceLogList().get(position);
				balanceMoveLogAdapter.notifyDataSetChanged();

				burstMoveLogAdapter.selectedLog = null;
				burstMoveLogAdapter.notifyDataSetChanged();
			}
		});
	}

	private void reqMovementLogData(int compID) {
		HttpUtil.get(SFBaseActivity.URL_SERVER_ADDRESS + String.format(Const.METHOD_SEAT_MOVEMENT_HISTORY, compID, SFBaseActivity.empUuid),
				new JsonBaseHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
						seatLogAll = gson.fromJson(response.toString(), new TypeToken<ReqSeatMovedLog>() {
						}.getType());
						if (seatLogAll.getRspCode() == Const.RspCode_Success) {
							balanceMoveLogAdapter = new BalanceMoveLogAdapter(mContext, seatLogAll.getBalanceLogList());
							balanceOptRecordList.setAdapter(balanceMoveLogAdapter);
							LogUtil.i(TAG, "平衡总条数：" + seatLogAll.getBalanceLogList().size());
							final ArrayList<BurstMovedLog> burstLogList = seatLogAll.getBurstLogList();
							burstMoveLogAdapter = new BurstMoveLogAdapter(mContext, burstLogList,balanceMoveLogAdapter);
							burstOptRecordList.setAdapter(burstMoveLogAdapter);
						} else {
							Toast.makeText(mContext, seatLogAll.getMsg(), Toast.LENGTH_SHORT).show();
							LogUtil.w(TAG, seatLogAll.getMsg());
						}
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.movement_history, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.movement_history_print:
			printMovementHistory();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void printMovementHistory() {
		CompetitionHistoryLog selectedLog = null;
		if (balanceMoveLogAdapter.selectedLog != null) {
			Toast.makeText(mContext, "正在打印平衡信息...", Toast.LENGTH_SHORT).show();
			selectedLog = balanceMoveLogAdapter.selectedLog;
			PrintUtil.printBalancePlayer(strCompName, strCompTime, selectedLog.getMemName(), selectedLog.getCardNO(), selectedLog.getNewTableNO(),
					selectedLog.getNewSeatNO(), selectedLog.getMemSex() == 1 ? true : false);
		} else if (burstMovedLogChoosed != null) {
			Toast.makeText(mContext, "正在打印爆桌信息...", Toast.LENGTH_SHORT).show();
			List<CompetitionHistoryLog> logListLog = burstMovedLogChoosed.getLogList();
			List<NewSeatInfo> burstSeatInfoList = new ArrayList<NewSeatInfo>();
			for (CompetitionHistoryLog shl : logListLog) {
				AckNewSeatInfoList ackNewSeatInfoList = new AckNewSeatInfoList();
				NewSeatInfo newSeatInfo = ackNewSeatInfoList.new NewSeatInfo(shl.getMemID(), shl.getCardNO(), shl.getMemName(), shl.getNewTableNO(),
						shl.getNewSeatNO(), shl.getMemSex());
				burstSeatInfoList.add(newSeatInfo);
			}
			PrintUtil.printBurstTable(strCompName, strCompTime, burstSeatInfoList);
		}else{
			Toast.makeText(mContext, "未选中", Toast.LENGTH_SHORT).show();
		}
	}
}
