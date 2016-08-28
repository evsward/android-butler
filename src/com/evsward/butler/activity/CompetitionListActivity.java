package com.evsward.butler.activity;

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
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.evsward.bulter.adapter.CompetitionListAdapter;
import com.evsward.butler.activity.base.SFBaseActivity;
import com.evsward.butler.entities.CompetitionInfoDayList;
import com.evsward.butler.entities.CompetitionInfoDayList.CompetitionInfoDay.CompetitionInfo;
import com.evsward.butler.service.NettySocketService;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class CompetitionListActivity extends SFBaseActivity {

	// broadcast
	private MessageBackReciver mReciver;
	private LocalBroadcastManager mLocalBroadcastManager;
	// Service
	private ServiceConnection mConn;
	private NettySocketService.NettyBinder nettyBinder;

	private CompetitionListAdapter CompListDayAdapter;
	private Menu mMenu;
	private CompetitionInfo compSelected;

	// 广播
	private class MessageBackReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Const.ACK_MatchList_502_ACTION.equals(action)) {
				Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
				String strMatchList = intent.getStringExtra(Const.ACK_MatchList_502_ACTION);
				CompetitionInfoDayList compInfoDayObj = gson.fromJson(strMatchList, new TypeToken<CompetitionInfoDayList>() {
				}.getType());

				if (compInfoDayObj.getRspCode() != Const.RspCode_Success) {
					Toast.makeText(context, compInfoDayObj.getMsg(), Toast.LENGTH_SHORT).show();
					return;
				}

				ListView lvCompDayList = (ListView) findViewById(R.id.lvCompetion_Day);
				CompListDayAdapter = new CompetitionListAdapter(mContext, mMenu, compInfoDayObj.getPadCompList());
				lvCompDayList.setAdapter(CompListDayAdapter);
			}
		};
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		useActionBarMenu("赛事列表");
		setContentView(R.layout.activity_competition_list);

		mConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder paramIbinder) {
				nettyBinder = (NettySocketService.NettyBinder) paramIbinder;
				String messageJson = "{\"IMEI\":\"" + Const.ANDROID_ID + "\",\"sysType\":" + Const.HI + "}";
				nettyBinder.sendMsg(Const.Req_MatchList_502, messageJson);
			}
		};
	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Const.ACK_MatchList_502_ACTION);
		mReciver = new MessageBackReciver();
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
		bindService(new Intent(this, NettySocketService.class), mConn, BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindService(mConn);
		mLocalBroadcastManager.unregisterReceiver(mReciver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		this.mMenu = menu;
		getSupportMenuInflater().inflate(R.menu.competition_list, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (CompListDayAdapter != null) {
			compSelected = CompListDayAdapter.selectedCompetition;
			if (compSelected == null && item.getItemId() != R.id.newMatch) {
				new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请选择一个比赛！").show();
				return false;
			}
		}
		switch (item.getItemId()) {
		case R.id.newMatch:
			reqMangzhuList();
			break;
		case R.id.deleteMatch:
			doDeleteComp();
			break;
		case R.id.finishMatch:
			AlertDialog.Builder builder = new Builder(CompetitionManageActivity.mContext);
			builder.setMessage("是否结束比赛“" + compSelected.getCompName() + "”?");
			builder.setTitle("提示");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					finishComp();
				}
			});
			builder.setNegativeButton("取消", comfirmCancelListener);
			builder.create().show();
			break;
		case R.id.matchManage:
			nettyBinder.closeLink();
			Intent intent = new Intent(mContext, CompetitionManageActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("compID", compSelected.getCompID());
			bundle.putString("compName", compSelected.getCompName());
			bundle.putString("compTime", compSelected.getTime());
			bundle.putInt("compAmountUnit", compSelected.getAmountUnit());
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void doDeleteComp() {
		if (compSelected.getCompState() == 1 || compSelected.getCompState() == 2 || compSelected.getCompState() == 0) {
			AlertDialog.Builder builder = new Builder(CompetitionManageActivity.mContext);
			builder.setMessage("是否删除比赛“" + compSelected.getCompName() + "”?");
			builder.setTitle("提示");
			builder.setPositiveButton("确认", comfirmDelCompListener);
			builder.setNegativeButton("取消", comfirmCancelListener);
			builder.create().show();
		} else {
			new AlertDialog.Builder(mContext).setTitle("提示").setMessage("“" + compSelected.getCompName() + "”不可被删除！").show();
			LogUtil.w(TAG, "“" + compSelected.getCompName() + "”不可被删除！");
		}
	}

	DialogInterface.OnClickListener comfirmDelCompListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int i) {
			deleteComp();
		}
	};

	DialogInterface.OnClickListener comfirmCancelListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int i) {
			dialog.dismiss();
		}
	};

	private void deleteComp() {
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_DELETE_MATCH, empUuid, compSelected.getCompID()),
				new JsonBaseHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							if (response.getInt("rspCode") == Const.RspCode_Success) {
								Toast.makeText(mContext, "--删除比赛成功--", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(mContext, response.getString("msg"), Toast.LENGTH_SHORT).show();
								LogUtil.e(TAG, "删除比赛出错：" + response.getString("msg"));
							}
						} catch (JSONException e) {
							LogUtil.e(TAG, "服务器通讯错误！");
						}
					}
				});
	}

	private void finishComp() {
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_FINISH_MATCH, empUuid, compSelected.getCompID()),
				new JsonBaseHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							if (response.getInt("rspCode") == Const.RspCode_Success) {
								Toast.makeText(mContext, "--结束比赛成功--", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(mContext, response.getString("msg"), Toast.LENGTH_SHORT).show();
								LogUtil.e(TAG, "结束比赛出错：" + response.getString("msg"));
							}
						} catch (JSONException e) {
							LogUtil.e(TAG, "服务器通讯错误！");
						}
					}
				});
	}

	private void reqMangzhuList() {
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_MANGZHU_LIST), new JsonBaseHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					if (response.getInt("rspCode") == Const.RspCode_Success) {
						Intent intent = new Intent("com.evsward.butler.NewMatch");
						Bundle bundle = new Bundle();
						bundle.putString("mangzhuList", response.toString());
						intent.putExtras(bundle);
						startActivity(intent);

						resetMenu();
					} else {
						Toast.makeText(mContext, response.getString("msg"), Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, "请求盲注列表出错：" + response.getString("msg"));
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}
		});
	}

	private void resetMenu() {
		MenuItem newMatch = mMenu.findItem(R.id.newMatch);
		MenuItem deleteMatch = mMenu.findItem(R.id.deleteMatch);
		MenuItem finishMatch = mMenu.findItem(R.id.finishMatch);
		MenuItem matchManage = mMenu.findItem(R.id.matchManage);

		newMatch.setVisible(true);
		deleteMatch.setVisible(false);
		finishMatch.setVisible(false);
		matchManage.setVisible(false);
	}
}
