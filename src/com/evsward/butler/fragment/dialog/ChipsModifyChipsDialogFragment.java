package com.evsward.butler.fragment.dialog;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.evsward.butler.activity.CompetitionManageActivity;
import com.evsward.butler.activity.MyApplication;
import com.evsward.butler.activity.base.SFBaseActivity;
import com.evsward.butler.entities.PlayerInfo;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.R;

public class ChipsModifyChipsDialogFragment extends SherlockDialogFragment {
	private CompetitionManageActivity competitionManageActivity;
	private String TAG;
	private TextView playerCardNO, playerName, playerMobileNO, warnMsgInfo, playerSTNO;
	private EditText modifiedChipsNum;
	private int chipsModified;

	private String empUuid;
	private String mTitle;
	private PlayerInfo mPlayer;
	private static Context mContext = MyApplication.getContext();

	DialogInterface.OnClickListener cancelClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			ChipsModifyChipsDialogFragment.this.dismiss();
		}
	};
	
	DialogInterface.OnClickListener outMemClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {

			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setMessage("是否确认淘汰（" + mPlayer.getTableNO() + "）号桌（" + mPlayer.getSeatNO() + "）号选手【" + mPlayer.getMemName() + "】？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, int i) {
					HttpUtil.get(String.format(SFBaseActivity.URL_SERVER_ADDRESS + Const.METHOD_ELIMINATE_PLAYER, empUuid,
							competitionManageActivity.compID, mPlayer.getTableNO(), mPlayer.getSeatNO(), mPlayer.getMemID()), new JsonBaseHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
							try {
								if (response.getInt("rspCode") == Const.RspCode_Success) {
									Toast.makeText(mContext, "淘汰选手成功", Toast.LENGTH_SHORT).show();
									dialog.dismiss();
								} else {
									Toast.makeText(mContext, response.getString("msg"), Toast.LENGTH_SHORT).show();
									LogUtil.e(TAG, response.getString("msg"));
								}
							} catch (JSONException e) {
								LogUtil.e(TAG, "服务器通讯错误！");
							}
						}
					});
				}
			});
			builder.setNegativeButton("取消", cancelClickListener);
			builder.create().show();
		}
	};

	public ChipsModifyChipsDialogFragment(String title) {
		this.mTitle = title;
	}

	public void setPlayer(PlayerInfo player) {
		this.mPlayer = player;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		competitionManageActivity = (CompetitionManageActivity) getSherlockActivity();
		TAG = "chips dialog";
		SharedPreferences pref = competitionManageActivity.getSharedPreferences("data", SFBaseActivity.MODE_PRIVATE);
		empUuid = pref.getString("empUuid", "");
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(mTitle).setCancelable(false)
				.setPositiveButton("确定", null).setNeutralButton("淘汰", outMemClickListener).setNegativeButton("取消", cancelClickListener);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.chips_modify_chips_dialog, null);
		playerCardNO = (TextView) view.findViewById(R.id.playerCardNO);
		playerName = (TextView) view.findViewById(R.id.playerName);
		playerMobileNO = (TextView) view.findViewById(R.id.playerMobileNO);
		playerSTNO = (TextView) view.findViewById(R.id.playerSTNO);
		warnMsgInfo = (TextView) view.findViewById(R.id.warnMsgInfo);
		modifiedChipsNum = (EditText) view.findViewById(R.id.modifiedChipsNum);
		playerCardNO.setText(mPlayer.getCardNO());
		playerName.setText(mPlayer.getMemName());
		playerMobileNO.setText(mPlayer.getMemMobile());
		playerSTNO.setText(mPlayer.getTableNO() + "/" + mPlayer.getSeatNO());
		modifiedChipsNum.setText(String.valueOf(mPlayer.getChip()));
		warnMsgInfo.setText(null);
		playerSTNO.requestFocus();
		builder.setView(view);
		return builder.create();
	}

	protected void postUpdateChipsChanged() {
		if (!StringUtils.isBlank(modifiedChipsNum.getText().toString())) {
			chipsModified = Integer.parseInt(modifiedChipsNum.getText().toString());
			httpPostUpdateChips(chipsModified);
			this.dismiss();
		} else {
			LogUtil.d(TAG, "Dialog修改筹码数量为空！");
			warnMsgInfo.setText(" * 不可为空！");
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
//		// 关闭键盘
//		InputMethodManager imm = (InputMethodManager) competitionManageActivity
//				.getSystemService(Activity.INPUT_METHOD_SERVICE);
//		if (imm.isActive()) {
//			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//		}
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
					ChipsModifyChipsDialogFragment.this.postUpdateChipsChanged();
				}
			});
		}
	}

	/**
	 * post请求修改筹码量
	 * 
	 * @param chipsModified
	 */
	private void httpPostUpdateChips(final int chipsModified) {
		String modifyURL = SFBaseActivity.URL_SERVER_ADDRESS
				+ String.format(Const.METHOD_MODIFY_PLAYER_CHIPS, SFBaseActivity.empUuid,
						competitionManageActivity.compID, mPlayer.getId(), mPlayer.getMemID(), chipsModified);
		HttpUtil.get(modifyURL, new JsonBaseHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					if (response.getInt("rspCode") == Const.RspCode_Success) {
						Toast.makeText(mContext, "筹码量成功修改为" + chipsModified, Toast.LENGTH_SHORT).show();
						getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
					} else {
						Toast.makeText(mContext, "修改筹码:" + response.getString("msg"), Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, String.format("修改筹码:%s", response.getString("msg")));
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}

		});
	}

}
