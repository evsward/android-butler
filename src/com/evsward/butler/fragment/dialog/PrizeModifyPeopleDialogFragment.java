package com.evsward.butler.fragment.dialog;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.evsward.butler.activity.CompetitionManageActivity;
import com.evsward.butler.activity.MyApplication;
import com.evsward.butler.activity.base.SFBaseActivity;
import com.evsward.butler.entities.prize.CompManPrizeInfo;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.R;

public class PrizeModifyPeopleDialogFragment extends SherlockDialogFragment {
	private CompetitionManageActivity competitionManageActivity;
	private String TAG;
	private TextView playerName, playerRanking, warnMsgInfo;
	private EditText playerPrizeNum;

	private int prizeNumModified;
	private String mTitle;
	private CompManPrizeInfo playerPrize;
	private static Context mContext = MyApplication.getContext();

	DialogInterface.OnClickListener cancelClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			PrizeModifyPeopleDialogFragment.this.dismiss();
		}
	};
	DialogInterface.OnClickListener comfirmClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			PrizeModifyPeopleDialogFragment.this.postUpdatePrizeChanged();
		}
	};

	public PrizeModifyPeopleDialogFragment(String title, CompManPrizeInfo playerPrize) {
		this.mTitle = title;
		this.playerPrize = playerPrize;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		competitionManageActivity = (CompetitionManageActivity) getSherlockActivity();
		TAG = "prize pool dialog";
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(mTitle).setCancelable(false).setPositiveButton("确定", null)
				.setNegativeButton("取消", cancelClickListener);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.prize_modify_people_dialog, null);
		playerName = (TextView) view.findViewById(R.id.playerName);
		playerRanking = (TextView) view.findViewById(R.id.playerRanking);
		playerPrizeNum = (EditText) view.findViewById(R.id.playerPrizeNum);
		warnMsgInfo = (TextView) view.findViewById(R.id.warnMsgInfo);
		playerName.setText(playerPrize.getMemName());
		playerRanking.setText(String.valueOf(playerPrize.getRanking()));
		playerPrizeNum.setText(String.valueOf(playerPrize.getAmountInt()));
		playerPrizeNum.requestFocus();
		warnMsgInfo.setText(null);
		builder.setView(view);
		return builder.create();
	}

	protected void postUpdatePrizeChanged() {
		if (!TextUtils.isEmpty(playerPrizeNum.getText().toString())) {
			prizeNumModified = Integer.parseInt(playerPrizeNum.getText().toString());
			httpPostUpdatePrize(prizeNumModified);
			this.dismiss();
		} else {
			LogUtil.d(TAG, "Dialog修改奖金金额为空！");
			warnMsgInfo.setText(" * 不可为空！");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		final AlertDialog d = (AlertDialog) getDialog();
		if (d != null) {
			Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					PrizeModifyPeopleDialogFragment.this.postUpdatePrizeChanged();
				}
			});
		}
	}

	/**
	 * HTTP提交修改奖金金额
	 * 
	 * @param prizeNumModified
	 */
	private void httpPostUpdatePrize(final int prizeNumModified) {
		String modifyURL = SFBaseActivity.URL_SERVER_ADDRESS
				+ String.format(Const.METHOD_MODIFY_ONE_PLAYER_REWARD, SFBaseActivity.empUuid, competitionManageActivity.compID,
						playerPrize.getRanking(), playerPrize.getMemID(), prizeNumModified);
		HttpUtil.get(modifyURL, new JsonBaseHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					if (response.getInt("rspCode") == Const.RspCode_Success) {
						Toast.makeText(mContext, "成功：" + playerPrize.getMemName() + "的奖金修改为" + prizeNumModified, Toast.LENGTH_SHORT).show();
						getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
					} else {
						Toast.makeText(mContext, "修改奖金:" + response.getString("msg"), Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, String.format("修改奖金:%s", response.getString("msg")));
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}

		});
	}

}
