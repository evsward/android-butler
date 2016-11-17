package com.evsward.butler.activity.member;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.evsward.butler.entities.MemberInfo;
import com.evsward.butler.service.PrintUtil;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.evsward.butler.R;

/**
 * 基类 查询会员基本信息
 * 
 * @Date May 5, 2015
 * @author liuwb.edward
 */
public abstract class SearcherBaseActivity extends RegisterBaseActivity {

	// search bar
	private EditText keywords;
	private ImageButton memberSearch;
	// photo lastModify
	protected long lastModity;
	// 会员主键
	protected long memID;
	// 当前的会员卡号
	private String currentCardID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	protected void searchBar() {
		// search bar
		keywords = (EditText) findViewById(R.id.keywords);
		SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
		keywords.setText(pref.getString("keyword", ""));// 设置上一次查询的对象
		memberSearch = (ImageButton) findViewById(R.id.memberSearch);
		memberSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String keyword = keywords.getText().toString();
				if (StringUtils.isBlank(keyword)) {
					new AlertDialog.Builder(mContext).setTitle("提示").setMessage("内容为空").show();
					return;
				}
				// 保存最后一次查询对象
				SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
				editor.putString("keyword", keyword);
				editor.commit();
				final ProgressDialog progressDialog = ProgressDialog.show(mContext, "请等待", "正在查询会员信息...");
				HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_SEARCH_MEMBER_BY_INPUT, keyword), new JsonHttpResponseHandler() {

					@Override
					public void onFailure(int i, Header[] aheader, Throwable throwable, JSONObject jsonobject) {
						progressDialog.dismiss();
						Toast.makeText(mContext, "查询失败请重试", Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, String.format("查询失败:%s", throwable.toString()));
						initRegister();
					}

					@Override
					public void onFailure(int i, Header[] aheader, String s, Throwable throwable) {
						progressDialog.dismiss();
						Toast.makeText(mContext, "请检查网络连接！", Toast.LENGTH_SHORT).show();
						LogUtil.se(TAG, throwable);
						initRegister();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						progressDialog.dismiss();
						try {
							if (response.getInt("rspCode") == Const.RspCode_Success) {
								Gson gson = new Gson();
								final List<MemberInfo> memberList = gson.fromJson(response.getString("memberInfo"),
										new TypeToken<ArrayList<MemberInfo>>() {
										}.getType());
								if (memberList.size() == 1) {
									showMemInfo(memberList.get(0));
									askMatchInfo();
								} else {
									String itemsChoosed[] = new String[memberList.size()];
									for (int i = 0; i < itemsChoosed.length; i++) {
										itemsChoosed[i] = memberList.get(i).toString();
									}
									new AlertDialog.Builder(mContext).setTitle("请选择会员信息：")
											.setItems(itemsChoosed, new DialogInterface.OnClickListener() {

												@Override
												public void onClick(DialogInterface dialog, int which) {
													showMemInfo(memberList.get(which));
													askMatchInfo();
													dialog.dismiss();
												}
											}).show();
								}

							} else {
								initRegister();
								Toast.makeText(mContext, String.format("查询失败:%s", response.getString("msg").toString()), Toast.LENGTH_SHORT).show();
								LogUtil.e(TAG, String.format("查询失败:%s", response.getString("msg").toString()));
							}
						} catch (JSONException e) {
							Toast.makeText(mContext, "查询失败:请检查后台日志", Toast.LENGTH_SHORT).show();
							LogUtil.e(TAG, "服务器通讯错误！");
						}
					}

				});
			}
		});
	}

	@Override
	protected void callBackHandler() {
		LogUtil.i(TAG, "------------编辑会员---打印小条------------");
		registPrintTags();
		initRegister();
		closeKeyBoard();
	}

	@Override
	protected RequestParams postSaveOrUpdateParam() {
		boolean ifUpdateCard = false;
		File file = new File(avatarPath, "avatartemp.jpg");
		if (!currentCardID.equals(cardNO)) {// 补卡
			ifUpdateCard = true;
			currentCardID = cardNO;
			LogUtil.i(TAG, "补卡流程：" + currentCardID + " 可使用 ->" + cardNO + "替换");
		}
		// entity to parameters
		RequestParams params = new RequestParams();
		params.put("memID", memID);
		params.put("nfcID", decimalNFCID);
		params.put("cardNO", cardNO);
		params.put("memName", memberName.getText().toString());
		params.put("mobile", memberMobile.getText().toString());
		params.put("memSex", gender);
		params.put("identno", memberID.getText().toString());
		params.put("empUuid", empUuid);// 操作员工编号
		// 如果图片被改动，则上传服务器，如果未被改动，则不上传。
		if (file.lastModified() != lastModity || ifUpdateCard) {
			try {
				// Upload a File
				params.put("image", new File(imageUri.getPath()));
			} catch (FileNotFoundException e) {
				LogUtil.se(TAG, e);
			}
		}
		return params;
	}

	@Override
	protected String postSaveOrUpdateUrl() {
		return URL_SERVER_ADDRESS + Const.METHOD_UPDATE_MEMBER_INFO;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			decimalNFCID = processIntent(intent);
			final ProgressDialog progressDialog = ProgressDialog.show(mContext, "请等待", "正在查询NFC卡信息...");
			HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_SEARCH_MEMBER_BY_NFC, decimalNFCID), new JsonHttpResponseHandler() {

				@Override
				public void onFailure(int i, Header[] aheader, Throwable throwable, JSONObject jsonobject) {
					progressDialog.dismiss();
					nfcCardMemberNo.setText(null);
					nfcCardMemberNo.setHint("扫卡失败请重试");
					initRegister();
					LogUtil.e(TAG, String.format("扫卡失败:%s", throwable.toString()));
				}

				@Override
				public void onFailure(int i, Header[] aheader, String s, Throwable throwable) {
					progressDialog.dismiss();
					Toast.makeText(mContext, "请检查网络连接！", Toast.LENGTH_SHORT).show();
					LogUtil.se(TAG, throwable);
					initRegister();
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					progressDialog.dismiss();
					try {
						if (response.getInt("rspCode") == Const.RspCode_Success) {
							Gson gson = new Gson();
							MemberInfo memberInfo = gson.fromJson(response.getString("memberInfo"), MemberInfo.class);
							showMemInfo(memberInfo);
							NeedSuppyCard = true;
							askMatchInfo();
						} else if (response.getInt("rspCode") == 231 && memID != 0 && NeedSuppyCard) {
							// 1、有效卡 2、补卡 3、非报名UI
							Toast.makeText(mContext, "正在补卡...", Toast.LENGTH_LONG).show();
							getcardno("补卡");
						} else {
							initRegister();
							Toast.makeText(mContext, String.format("扫卡失败:%s", response.getString("msg").toString()), Toast.LENGTH_SHORT).show();
							LogUtil.e(TAG, String.format("扫卡失败:%s", response.getString("msg").toString()));
						}
					} catch (JSONException e) {
						Toast.makeText(mContext, String.format("扫卡失败:请检查后台日志-->%s", e.toString()), Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, "服务器通讯错误！");
					}
				}
			});
		}
	}

	/**
	 * 显示单个会员的信息。
	 * 
	 * @param memberInfo
	 */
	private void showMemInfo(MemberInfo memberInfo) {
		currentCardID = memberInfo.getCardNO();
		memID = memberInfo.getMemID();
		memberName.setText(memberInfo.getMemName());
		memberID.setText(memberInfo.getMemIdentNO());
		memberMobile.setText(memberInfo.getMemMobile());
		nfcCardMemberNo.setText(memberInfo.getCardNO());
		decimalNFCID = memberInfo.getUuidLong();
		if (memberInfo.getMemSex() == 0) {
			switchGender.setChecked(false);
			gender = "0";
		} else {
			switchGender.setChecked(true);
			gender = "1";
		}
		final String memImageName = memberInfo.getMemImage();
		final File file = new File(avatarPath, "avatartemp.jpg");
		// progress dialog
		final ProgressDialog progressDialog = new ProgressDialog(mContext);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle("请等待");
		progressDialog.setMessage("正在下载图片...");
		progressDialog.show();
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_DOWNLOAD_IMAGE_PATH, memImageName), new BinaryHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					progressDialog.dismiss();
					FileOutputStream oStream = new FileOutputStream(file);
					oStream.write(arg2);
					oStream.flush();
					oStream.close();
					imageUri = Uri.fromFile(file);
					avatarBitmap = BitmapFactory.decodeFile(imageUri.getPath());
					memberAvatar.setImageBitmap(avatarBitmap);
					lastModity = file.lastModified();
				} catch (Exception e) {
					LogUtil.se(TAG, e);
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(mContext, "图片下载失败...", Toast.LENGTH_SHORT).show();
				memberAvatar.setImageResource(R.drawable.big_avatar);
				if (avatarBitmap != null && !avatarBitmap.isRecycled()) {
					avatarBitmap.recycle();
					avatarBitmap = null;
				}
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				super.onProgress(bytesWritten, totalSize);
				progressDialog.setProgress((int) (totalSize <= 0 ? -1D : (((double) bytesWritten * 1.0D) / (double) totalSize) * 100D));
			}

		});
	}

	/**
	 * 会员报名，查询，打印比赛座位信息
	 * 
	 * @param matchName
	 * @param matchTime
	 * @param tableNO
	 * @param seatNO
	 */
	protected void signUpPrint(final String matchName, final String matchTime, final int tableNO, final int seatNO) {
		PrintUtil.printSeatInfo(matchName, matchTime, memName, cardNO, tableNO, seatNO, Integer.parseInt(gender) == 1 ? "M" : "F");
	}

	protected void unEditable() {
		// 不可编辑
		memberAvatar.setClickable(false);
		memberAvatar.setEnabled(false);
		saveAndPrintMember.setVisibility(View.GONE);
		memberName.setEnabled(false);
		switchGender.setEnabled(false);
		memberID.setEnabled(false);
		memberMobile.setEnabled(false);
	}

	protected void askMatchInfo() {
		LinearLayout root = (LinearLayout) findViewById(R.id.matchInfoLayout);
		root.removeAllViews();// 每次进来先清空上一次查询的结果
		cardNO = nfcCardMemberNo.getText().toString();
		memName = memberName.getText().toString();
	}
}
