package com.evsward.butler.fragment;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.evsward.butler.activity.base.CompetitionManageBaseFragment;
import com.evsward.butler.entities.CompetitionProgress;
import com.evsward.butler.fragment.dialog.ProcessModifyPeopleDialogFragment;
import com.evsward.butler.service.NettySocketService;
import com.evsward.butler.util.CommonUtil;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class CompetitionProcessFragment extends CompetitionManageBaseFragment implements View.OnClickListener {
	// 盲注信息
	private TextView currentBlindLevel, blindLevelCountDown, smallBigBlindLevel, frontStake, totalPlayers, averageChips;
	private Button upOneBlindLevel, backOneBlindLevel, pauseMatch, startMatch, changePeopleNum;
	private SeekBar blindTimeSeekBar;
	private static MyCountDown myCountDown;
	private long duration;// 常量 接收到的规定的一个盲注的持续时间
	private long millisLeft;
	private int curRank;
	// broadcast
	private MessageBackReciver mReciver;
	private LocalBroadcastManager mLocalBroadcastManager;
	// Service
	private ServiceConnection mConn;
	private NettySocketService.NettyBinder nettyBinder;

	// 广播
	private class MessageBackReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.d(TAG, "--------------------------进程onReceive--------------------------");
			String action = intent.getAction();
			if (Const.ACK_Manage_Process_504_ACTION.equals(action)) {
				JSONObject jsonObject;
				try {
					String strCompetition = intent.getStringExtra(Const.ACK_Manage_Process_504_ACTION);
					jsonObject = new JSONObject(strCompetition);
					if (jsonObject.getInt("rspCode") != Const.RspCode_Success) {
						Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
						return;
					}
					Gson gson = new Gson();
					CompetitionProgress cprocess = gson.fromJson(jsonObject.getString("compProgessInfo"), new TypeToken<CompetitionProgress>() {
					}.getType());
					if (cprocess.getCompID() == compID) {
						curRank = cprocess.getCurRank();
						currentBlindLevel.setText(String.valueOf(curRank) + (cprocess.getRoundType() == 0 ? "（休息）" : ""));
						blindLevelCountDown.setText(CommonUtil.convertTime(cprocess.getRestTime() * 1000));
						smallBigBlindLevel.setText(cprocess.getSmallBlind() + "/" + cprocess.getBigBlind());
						totalPlayers.setText(cprocess.getTotalRegedPlayerEdit() + "/" + cprocess.getTotalRegedPlayer());
						averageChips.setText(String.valueOf(cprocess.getAverChip()));
						duration = cprocess.getInitedStepLen() * 1000;// 这里接收一个值初始化
						if (myCountDown != null) {
							myCountDown.cancel();
							myCountDown = null;
						}
						myCountDown = new MyCountDown(cprocess.getRestTime() * 1000, 1000);
						myCountDown.start();

						blindTimeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
							int beforeTouch = 0;

							@Override
							public void onStopTrackingTouch(SeekBar seekBar) {
								int timeOffset = seekBar.getProgress() - beforeTouch;
								long millisOffset = (long) ((double) duration * ((double) timeOffset / 100D));// 滑动后时间偏移量
								myCountDown = new MyCountDown(millisLeft - millisOffset, 1000);// 持续时间变化后,重启一个倒计时
								myCountDown.start();
								editRunningTime(curRank, millisOffset / 1000);
							}

							@Override
							public void onStartTrackingTouch(SeekBar seekBar) {
								myCountDown.cancel();// 倒计时取消
								beforeTouch = seekBar.getProgress();
							}

							@Override
							public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
								// 拖动进度条时，同步改变倒计时TextView的值
								blindLevelCountDown.setText(CommonUtil.convertTime((long) ((double) duration * (1 - (double) progresValue / 100D))));
							}
						});
						if(curRank==0&&cprocess.getBeforeChip()==Const.countdownBeforeChip){
							frontStake.setText(String.valueOf(0));
							upOneBlindLevel.setEnabled(false);
							backOneBlindLevel.setEnabled(false);
							pauseMatch.setEnabled(false);
							startMatch.setEnabled(false);
							changePeopleNum.setEnabled(false);
							blindTimeSeekBar.setEnabled(false);
						}else{
							if (cprocess.getCompPause() == 2) {
								compPauseOps();
							} else {
								compStartOps();
							}
							frontStake.setText(String.valueOf(cprocess.getBeforeChip()));
						}
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Const.ACK_Manage_Process_504_ACTION);
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
				nettyBinder.sendMsg(Const.Req_Manage_Process_504, messageJson);
			}
		};
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
	public void onDestroy() {
		super.onDestroy();
		mLocalBroadcastManager.unregisterReceiver(mReciver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_competition_process, container, false);
		upOneBlindLevel = (Button) view.findViewById(R.id.upOneBlindLevel);
		backOneBlindLevel = (Button) view.findViewById(R.id.backOneBlindLevel);
		pauseMatch = (Button) view.findViewById(R.id.pauseMatch);
		startMatch = (Button) view.findViewById(R.id.startMatch);
		changePeopleNum = (Button) view.findViewById(R.id.changePeopleNum);

		currentBlindLevel = (TextView) view.findViewById(R.id.currentBlindLevel);
		blindLevelCountDown = (TextView) view.findViewById(R.id.blindLevelCountDown);
		smallBigBlindLevel = (TextView) view.findViewById(R.id.smallBigBlindLevel);
		frontStake = (TextView) view.findViewById(R.id.frontStake);
		totalPlayers = (TextView) view.findViewById(R.id.totalPlayers);
		averageChips = (TextView) view.findViewById(R.id.averageChips);

		upOneBlindLevel.setOnClickListener(this);
		backOneBlindLevel.setOnClickListener(this);
		pauseMatch.setOnClickListener(this);
		startMatch.setOnClickListener(this);
		changePeopleNum.setOnClickListener(this);

		blindTimeSeekBar = (SeekBar) view.findViewById(R.id.changeBlindTime);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.changePeopleNum:
			showDialog();
			break;
		case R.id.upOneBlindLevel:
			goForward(curRank);
			break;
		case R.id.backOneBlindLevel:
			goBack(curRank);
			break;
		case R.id.pauseMatch:
		case R.id.startMatch:
			operateCompetition();
			break;
		default:
			break;
		}
	}

	private void compStartOps() {
		blindTimeSeekBar.setEnabled(true);
		upOneBlindLevel.setEnabled(true);
		backOneBlindLevel.setEnabled(true);
		blindTimeSeekBar.setEnabled(true);
		pauseMatch.setVisibility(View.VISIBLE);
		startMatch.setVisibility(View.GONE);
//		LogUtil.i(TAG, "开始比赛：" + strCompName);
//		Toast.makeText(CompetitionProcessFragment.this.getActivity(), "比赛[" + strCompName + "]：进行中...", Toast.LENGTH_SHORT).show();
	}

	private void compPauseOps() {
		if (myCountDown != null) {
			myCountDown.cancel();
			myCountDown = null;
		}
		blindTimeSeekBar.setEnabled(false);
		upOneBlindLevel.setEnabled(false);
		backOneBlindLevel.setEnabled(false);
		blindTimeSeekBar.setEnabled(false);
		startMatch.setVisibility(View.VISIBLE);
		pauseMatch.setVisibility(View.GONE);
		LogUtil.i(TAG, "比赛暂停：" + strCompName);
		Toast.makeText(CompetitionProcessFragment.this.getActivity(), "比赛[" + strCompName + "]：暂停-", Toast.LENGTH_SHORT).show();
	}

	public class MyCountDown extends CountDownTimer {

		public MyCountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			blindLevelCountDown.setText(CommonUtil.convertTime(millisUntilFinished));
			blindTimeSeekBar
					.setProgress((int) (duration <= 0 ? -1D : (((double) (duration - millisUntilFinished) * 1.0D) / (double) duration) * 100D));// 进度条随着倒计时联动
			millisLeft = millisUntilFinished;
		}

		@Override
		public void onFinish() {
			blindLevelCountDown.setText("00:00:00");
			blindTimeSeekBar.setProgress(100);
		}

	}

	private void showDialog() {
		ProcessModifyPeopleDialogFragment newFragment = ProcessModifyPeopleDialogFragment.newInstance("修改人数");
		newFragment.show(getFragmentManager(), "ProcessModifyPeople");
	}

	// 发送http请求，更改倒计时时间
	private void editRunningTime(int curRank, long secondsOffset) {
		HttpUtil.get(URL_SERVER_ADDRESS + String.format(Const.METHOD_MATCH_PROCESS_SET_COUNTDOWN, empUuid, compID, curRank, secondsOffset),
				new JsonBaseHttpResponseHandler() {

					@Override
					public void onSuccess(int i, Header[] aheader, JSONObject jsonobject) {
						try {
							if (jsonobject.getInt("rspCode") == Const.RspCode_Success) {
								Toast.makeText(mContext, "----更改时间成功----", Toast.LENGTH_SHORT).show();
								LogUtil.i(TAG, "----滑动更改时间成功----");
							}
						} catch (JSONException e) {
							LogUtil.e(TAG, "服务器通讯错误！");
						}
					}

				});
	}

	// 发送http请求，进一级
	private void goForward(int curRank) {
		// progress dialog
		final ProgressDialog progressDialog = new ProgressDialog(competitionManageActivity);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle("请等待");
		progressDialog.setMessage("正在请求进一级...");
		progressDialog.show();
		HttpUtil.get(URL_SERVER_ADDRESS + String.format(Const.METHOD_MATCH_PROCESS_FORWARD_ONE_LEVEL, empUuid, compID, curRank),
				new JsonBaseHttpResponseHandler() {

					@Override
					public void onSuccess(int i, Header[] aheader, JSONObject jsonobject) {
						try {
							if (jsonobject.getInt("rspCode") == Const.RspCode_Success) {
								progressDialog.dismiss();
								Toast.makeText(mContext, "比赛：[" + strCompName + "]----进一级成功----", Toast.LENGTH_SHORT).show();
								LogUtil.i(TAG, "比赛：[" + strCompName + "]----进一级成功----");
							}
						} catch (JSONException e) {
							LogUtil.e(TAG, "服务器通讯错误！");
						}
					}

				});
	}

	// 发送http请求，退一级
	private void goBack(int curRank) {
		if(curRank<=1){
			Toast.makeText(mContext, "比赛：[" + strCompName + "]----当前已经是第一级，无法退一级----", Toast.LENGTH_SHORT).show();
			LogUtil.i(TAG, "比赛：[" + strCompName + "]----当前已经是第一级，无法退一级----");
			return;
		}
		// progress dialog
		final ProgressDialog progressDialog = new ProgressDialog(competitionManageActivity);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle("请等待");
		progressDialog.setMessage("正在请求退一级...");
		progressDialog.show();
		HttpUtil.get(URL_SERVER_ADDRESS + String.format(Const.METHOD_MATCH_PROCESS_BACK_ONE_LEVEL, empUuid, compID, curRank),
				new JsonBaseHttpResponseHandler() {

					@Override
					public void onSuccess(int i, Header[] aheader, JSONObject jsonobject) {
						try {
							if (jsonobject.getInt("rspCode") == Const.RspCode_Success) {
								progressDialog.dismiss();
								Toast.makeText(mContext, "比赛：[" + strCompName + "]----退一级成功----", Toast.LENGTH_SHORT).show();
								LogUtil.i(TAG, "比赛：[" + strCompName + "]----退一级成功----");
							}
						} catch (JSONException e) {
							LogUtil.e(TAG, "服务器通讯错误！");
						}
					}

				});
	}

	// 发送http请求，切换比赛暂停&开始状态
	private void operateCompetition() {
		// progress dialog
		final ProgressDialog progressDialog = new ProgressDialog(competitionManageActivity);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle("请等待");
		progressDialog.setMessage("正在处理请求，请稍候...");
		progressDialog.show();
		HttpUtil.get(URL_SERVER_ADDRESS + String.format(Const.METHOD_MATCH_PROCESS_PAUSE_MATCH, empUuid, compID), new JsonBaseHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				progressDialog.dismiss();
			}
		});
	}
}
