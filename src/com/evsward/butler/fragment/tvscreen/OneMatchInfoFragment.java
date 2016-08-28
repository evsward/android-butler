package com.evsward.butler.fragment.tvscreen;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evsward.bulter.adapter.TVPrizeListAdapter;
import com.evsward.bulter.adapter.TimerTaskForListViewRolling;
import com.evsward.butler.activity.base.TVBaseFragment;
import com.evsward.butler.entities.Competition;
import com.evsward.butler.entities.ScreenCompTimeInfo;
import com.evsward.butler.entities.ScreenPrizeInfo;
import com.evsward.butler.util.CommonUtil;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.evsward.butler.R;

public class OneMatchInfoFragment extends TVBaseFragment {

	private TextView tvCompNameShow, tvCompSubNameShow, tvCompCurrentLevel, tvCompForceScore, tvCompSmallBilndScore, tvCompBigBilndScore,
			tvCompNextLevel, tvCompNextForceScore, tvCompNextBilndScore, tvOneCompStateShow, tvOneCompCountDownShow, tvOneCompTipShow,
			tvOneCompPlayersLeft, tvOneCompAverageChips, tvOneCompTotalPlayers, tvOneCompTotalSumChips, tvCompTimeShow, prizePoolInfoViewContent;
	private ListView lvPrizePoolInfoList;
	private LinearLayout prizePoolInfoView;
	private ImageView TVAD;
	private static MyCountDown myCountDown;
	private Timer prizeRoll;
	// AD
	protected Uri imageUri;
	protected Bitmap adBitmap;
	protected static String adImagePath = CommonUtil.createFolder(Const.AD_IMAGE_FOLDER);

	public class MyCountDown extends CountDownTimer {
		public MyCountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			tvOneCompCountDownShow.setText(CommonUtil.convertTime(millisUntilFinished));
		}

		@Override
		public void onFinish() {
			tvOneCompCountDownShow.setText("00:00:00");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tvscreen_one_match_info, container, false);
		prizePoolInfoView = (LinearLayout) view.findViewById(R.id.prizePoolInfoView);
		tvCompNameShow = (TextView) view.findViewById(R.id.TVCompNameShow);
		tvCompSubNameShow = (TextView) view.findViewById(R.id.TVCompSubNameShow);
		tvCompCurrentLevel = (TextView) view.findViewById(R.id.TVCompCurrentLevel);
		tvCompForceScore = (TextView) view.findViewById(R.id.TVCompForceScore);
		tvCompSmallBilndScore = (TextView) view.findViewById(R.id.TVCompSmallBilndScore);
		tvCompBigBilndScore = (TextView) view.findViewById(R.id.TVCompBigBilndScore);
		tvCompNextLevel = (TextView) view.findViewById(R.id.TVCompNextLevel);
		tvCompNextForceScore = (TextView) view.findViewById(R.id.TVCompNextForceScore);
		tvCompNextBilndScore = (TextView) view.findViewById(R.id.TVCompNextBilndScore);
		tvOneCompStateShow = (TextView) view.findViewById(R.id.TVOneCompStateShow);
		tvOneCompCountDownShow = (TextView) view.findViewById(R.id.TVOneCompCountDownShow);
		tvOneCompTipShow = (TextView) view.findViewById(R.id.TVOneCompTipShow);
		tvOneCompPlayersLeft = (TextView) view.findViewById(R.id.TVOneCompPlayersLeft);
		tvOneCompAverageChips = (TextView) view.findViewById(R.id.TVOneCompAverageChips);
		tvOneCompTotalPlayers = (TextView) view.findViewById(R.id.TVOneCompTotalPlayers);
		tvOneCompTotalSumChips = (TextView) view.findViewById(R.id.TVOneCompTotalSumChips);
		tvCompTimeShow = (TextView) view.findViewById(R.id.TVCompTimeShow);
		prizePoolInfoViewContent = (TextView) view.findViewById(R.id.prizePoolInfoViewContent);
		lvPrizePoolInfoList = (ListView) view.findViewById(R.id.prizePoolInfoList);
		TVAD = (ImageView) view.findViewById(R.id.TVAD);
		Bundle bundle = getArguments();
		String strCompTimeInfo = bundle.getString("jsonMsg");
		updateUIContent(strCompTimeInfo);
		getPrizeList(strCompTimeInfo);
		return view;
	}

	public void updateUIContent(String strCompTimeInfo) {
		LogUtil.d(TAG, strCompTimeInfo);
		getPrizeList(strCompTimeInfo);
		try {
			tvCompTimeShow.setText(DateFormat.format(Const.TIME_PATTERN, new Date()));
			Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
			ScreenCompTimeInfo tvCompTimeInfo = gson.fromJson(strCompTimeInfo, new TypeToken<ScreenCompTimeInfo>() {
			}.getType());
			tvCompNameShow.setText(tvCompTimeInfo.getTopic());
			if (myCountDown != null) {
				myCountDown.cancel();
				myCountDown = null;
			}
			if (tvCompTimeInfo.getCompPause() == Competition.COMPPAUSE.PAUSE) {
				tvOneCompStateShow.setText(R.string.tv_compinfo_comp_state_pause);
			} else if (tvCompTimeInfo.getCurRound() != null && tvCompTimeInfo.getCurRound().getCurType() == 0) {
				tvOneCompStateShow.setText(R.string.tv_compinfo_comp_state_rest);
				myCountDown = new MyCountDown(tvCompTimeInfo.getCurRound().getRestTime() * 1000, 1000);
				myCountDown.start();
			} else {
				switch (tvCompTimeInfo.getCompState()) {
				case Competition.COMPSTATE.ENDSIGNNOTSTART:
					tvOneCompStateShow.setText(R.string.tv_compinfo_comp_state_endsignnotstart);
					break;
				case Competition.COMPSTATE.ENDSIGNSTART:
					tvOneCompStateShow.setText(R.string.tv_compinfo_comp_state_endsignstart);
					break;
				case Competition.COMPSTATE.CLOSE:
					tvOneCompStateShow.setText(R.string.tv_compinfo_comp_state_close);
					break;
				case Competition.COMPSTATE.SIGNNOTSTART:
					tvOneCompStateShow.setText(R.string.tv_compinfo_comp_state_signnotstart);
					break;
				case Competition.COMPSTATE.SIGNSTART:
					tvOneCompStateShow.setText(R.string.tv_compinfo_comp_state_signstart);
					break;
				}

				myCountDown = new MyCountDown(tvCompTimeInfo.getCurRound().getRestTime() * 1000, 1000);
				myCountDown.start();
			}
			tvCompSubNameShow.setText(tvCompTimeInfo.getCompName());
			prizePoolInfoViewContent.setText(tvCompTimeInfo.getCompName());
			tvOneCompPlayersLeft.setText(NumberFormat.getInstance().format(tvCompTimeInfo.getLivedPlayerCount()));
			tvOneCompAverageChips.setText(NumberFormat.getInstance().format(tvCompTimeInfo.getLivedAvgChipCount()));
			tvOneCompTotalPlayers.setText(NumberFormat.getInstance().format(tvCompTimeInfo.getTotalRegedPlayerCount()));
			tvOneCompTotalSumChips.setText(NumberFormat.getInstance().format(tvCompTimeInfo.getTotalChipCount()));
			if (tvCompTimeInfo.getCompAdvertInfo() != null) {
				reqADImageInfo(tvCompTimeInfo.getCompAdvertInfo().getPath(), TVAD);
			} else {
				TVAD.setImageBitmap(null);
				TVAD.setBackgroundResource(R.drawable.tvadvertisement);
			}
			if (tvCompTimeInfo.getCompState() != 2 && tvCompTimeInfo.getCompState() != 4) {
				Toast.makeText(mContext, "比赛未开始，暂无进程信息", Toast.LENGTH_SHORT).show();
				return;
			}
			tvCompCurrentLevel.setText(NumberFormat.getInstance().format(tvCompTimeInfo.getCurRound().getCurRank()));
			tvCompForceScore.setText(Integer.toString(tvCompTimeInfo.getCurRound().getCurBeforeChip()));
			tvCompSmallBilndScore.setText(Integer.toString(tvCompTimeInfo.getCurRound().getCurSmallBlind()));
			tvCompBigBilndScore.setText(Integer.toString(tvCompTimeInfo.getCurRound().getCurBigBlind()));
			if (tvCompTimeInfo.getNextRound() != null) {
				tvCompNextLevel.setText(Integer.toString(tvCompTimeInfo.getNextRound().getRoundrank()));
				tvCompNextForceScore.setText(Integer.toString(tvCompTimeInfo.getNextRound().getBeforeChip()));
				tvCompNextBilndScore.setText(Integer.toString(tvCompTimeInfo.getNextRound().getSmallBlind()) + "-"
						+ Integer.toString(tvCompTimeInfo.getNextRound().getBigBlind()));
			}
			if (tvCompTimeInfo.getNextBreakRound() != null) {
				if (tvScreenActivity.ifEnglish) {
					tvOneCompTipShow.setText("BREAK " + tvCompTimeInfo.getNextBreakRound().getStepLen() / 60 + " MIN AFTER LEVEL "
							+ tvCompTimeInfo.getNextBreakRound().getRoundrank());
				} else {
					tvOneCompTipShow.setText("第" + tvCompTimeInfo.getNextBreakRound().getRoundrank() + "级别结束后  休息"
							+ tvCompTimeInfo.getNextBreakRound().getStepLen() / 60 + "分钟");
				}
			}
		} catch (Exception e) {
			LogUtil.se(TAG, e);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (prizeRoll != null) {
			prizeRoll.cancel();
			prizeRoll = null;
		}
		if (myCountDown != null) {
			myCountDown.cancel();
			myCountDown = null;
		}
	}

	private void getPrizeList(String strCompTimeInfo) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(strCompTimeInfo);
			if (jsonObject.getInt("aword") == 0 || jsonObject.getInt("compState") < Competition.COMPSTATE.ENDSIGNNOTSTART) {
				lvPrizePoolInfoList.setAdapter(null);
				prizePoolInfoView.setVisibility(View.VISIBLE);
				lvPrizePoolInfoList.setVisibility(View.GONE);
				return;
			}
		} catch (JSONException e) {
			LogUtil.se(TAG, e);
		}
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_TV_PRIZE_INFO, tvScreenActivity.compID, Const.ANDROID_ID),
				new JsonBaseHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							if (response.getInt("rspCode") != Const.RspCode_Success) {
								Toast.makeText(mContext, response.getString("msg"), Toast.LENGTH_SHORT).show();
								lvPrizePoolInfoList.setAdapter(null);
								prizePoolInfoView.setVisibility(View.VISIBLE);
								lvPrizePoolInfoList.setVisibility(View.GONE);
								return;
							}
							prizePoolInfoView.setVisibility(View.GONE);
							lvPrizePoolInfoList.setVisibility(View.VISIBLE);
							Gson gson = new Gson();
							ArrayList<ScreenPrizeInfo> currentScreenPrizeList = gson.fromJson(response.getString("screenCompPrizeArea"),
									new TypeToken<ArrayList<ScreenPrizeInfo>>() {
									}.getType());
							TVPrizeListAdapter tvPrizeListAdapter = new TVPrizeListAdapter(tvScreenActivity, currentScreenPrizeList,
									tvScreenActivity.ifEnglish);
							if (currentScreenPrizeList.size() < Const.minPrizeListNum) {
								lvPrizePoolInfoList.setAdapter(tvPrizeListAdapter);
							} else {
								prizeRoll = new Timer();
								prizeRoll.schedule(new TimerTaskForListViewRolling(tvPrizeListAdapter, lvPrizePoolInfoList), 100, 100);
							}
						} catch (JSONException e) {
							LogUtil.se(TAG, e);
						}
					}
				});
	}

	private void reqADImageInfo(String strImagePath, final ImageView imgView) {
		String strHttpUtil = String.format(URL_SERVER_ADDRESS + Const.METHOD_DOWNLOAD_AD_IMAGE, strImagePath);
		final File file = new File(adImagePath, strImagePath);
		HttpUtil.get(strHttpUtil, new BinaryHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					FileOutputStream oStream = new FileOutputStream(file);
					oStream.write(arg2);
					oStream.flush();
					oStream.close();
					imageUri = Uri.fromFile(file);
					adBitmap = BitmapFactory.decodeFile(imageUri.getPath());
					imgView.setImageBitmap(adBitmap);
				} catch (Exception e) {
					LogUtil.se(TAG, e);
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(mContext, "广告图下载失败...", Toast.LENGTH_SHORT).show();
				if (adBitmap != null && !adBitmap.isRecycled()) {
					adBitmap.recycle();
					adBitmap = null;
				}
			}
		});
	}
}
