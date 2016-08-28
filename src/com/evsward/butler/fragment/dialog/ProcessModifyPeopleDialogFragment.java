package com.evsward.butler.fragment.dialog;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.evsward.butler.activity.CompetitionManageActivity;
import com.evsward.butler.activity.MyApplication;
import com.evsward.butler.activity.base.SFBaseActivity;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.R;

public class ProcessModifyPeopleDialogFragment extends SherlockDialogFragment {
	private CompetitionManageActivity competitionManageActivity;
	private String TAG;
	private EditText reducePeopleNum;
	private String reducedPeople;
	private static String title;

	DialogInterface.OnClickListener cancelClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			ProcessModifyPeopleDialogFragment.this.dismiss();
		}
	};
	DialogInterface.OnClickListener comfirmClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			ProcessModifyPeopleDialogFragment.this.postUpdatePeopleChanged();
		}
	};

	public static ProcessModifyPeopleDialogFragment newInstance(String mTitle) {
		ProcessModifyPeopleDialogFragment frag = new ProcessModifyPeopleDialogFragment();
		title = mTitle;
		return frag;
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(title).setCancelable(false).setPositiveButton("确定", comfirmClickListener)
				.setNegativeButton("取消", cancelClickListener);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.process_modify_people_dialog, null);
		reducePeopleNum = (EditText) view.findViewById(R.id.reducePeopleNum);
		reducePeopleNum.requestFocus();
		builder.setView(view);
		return builder.create();
	}

	protected void postUpdatePeopleChanged() {
		reducedPeople = reducePeopleNum.getText().toString();
		if (!TextUtils.isEmpty(reducedPeople)) {
			getLivedPlayersChipInfo(Integer.parseInt(reducedPeople));
		} else {
			LogUtil.d(TAG, "Dialog修改人数为空！");
			Toast.makeText(ProcessModifyPeopleDialogFragment.this.getActivity(), "reducedPeople.isEmpty为空", Toast.LENGTH_SHORT).show();
		}
		this.dismiss();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		competitionManageActivity = (CompetitionManageActivity) getSherlockActivity();
		TAG = "process dialog";
	}

	// 发送http请求，修改选手人数
	private void getLivedPlayersChipInfo(final int reducedPeople) {
		HttpUtil.get(
				SFBaseActivity.URL_SERVER_ADDRESS
						+ String.format(Const.METHOD_MATCH_PROCESS_MODIFY_PEOPLE_NUM, SFBaseActivity.empUuid, competitionManageActivity.compID,
								reducedPeople), new JsonBaseHttpResponseHandler() {
					@Override
					public void onSuccess(int i, Header[] aheader, JSONObject jsonobject) {
						try {
							if (jsonobject.getInt("rspCode") == Const.RspCode_Success) {
								Toast.makeText(MyApplication.getContext(), "减去总数" + reducedPeople + "人", Toast.LENGTH_SHORT).show();
								LogUtil.d(TAG, "减去总数" + reducedPeople + "人");
							}
						} catch (JSONException e) {
							LogUtil.e(TAG, "服务器通讯错误！");
						}
					}

				});
	}
}
