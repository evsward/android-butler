package com.evsward.bulter.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.droidparts.widget.ClearableEditText;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evsward.butler.activity.CompetitionManageActivity;
import com.evsward.butler.activity.base.SFBaseActivity;
import com.evsward.butler.entities.AckNewSeatInfoList;
import com.evsward.butler.entities.CompManSeatInfo;
import com.evsward.butler.entities.MemberInfo;
import com.evsward.butler.entities.CompManTableInfoList.CompManTableInfo;
import com.evsward.butler.entities.CompManTableInfoList.TABLEBUTOPER;
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

public class SeatAdapter extends BaseAdapter {
	public HashMap<Integer, Boolean> checked = new HashMap<Integer, Boolean>();
	public List<String> tableID = new ArrayList<String>();

	private CompetitionManageActivity competitionManageActivity;
	private final static String TAG = "SeatAdapter";
	// avatar
	private Uri imageUri;
	private Bitmap avatarBitmap;
	private static String seatavatarPath = CommonUtil.createFolder(Const.SEAT_AVATAR_FOLDER);
	private static String avatarPath = CommonUtil.createFolder(Const.HIGHLEVLE_AVATAR_FOLDER);
	// adapter init
	private Context mContext;
	private LayoutInflater mInflater;
	private List<CompManTableInfo> compTableInfos;
	private String empUuid;
	private AlertDialog myDialog;

	public SeatAdapter(Context context, List<CompManTableInfo> compTableInfos) {
		this.mContext = context;
		this.compTableInfos = compTableInfos;
		this.mInflater = LayoutInflater.from(mContext);
		competitionManageActivity = (CompetitionManageActivity) mContext;
		SharedPreferences pref = competitionManageActivity.getSharedPreferences("data", SFBaseActivity.MODE_PRIVATE);
		empUuid = pref.getString("empUuid", "");
	}

	@Override
	public int getCount() {
		double count = Math.ceil((double) compTableInfos.size() / (double) 2);
		return (int) count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		TableViewHolder viewHolder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.double_table, parent, false);
			viewHolder = new TableViewHolder();
			viewHolder.table1 = view.findViewById(R.id.table1);
			viewHolder.table2 = view.findViewById(R.id.table2);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (TableViewHolder) view.getTag();
		}
		final int table1Position = position * 2 + 0;
		final int table2Position = position * 2 + 1;
		final CompManTableInfo tableData1 = compTableInfos.get(table1Position);
		viewHolder.table1.setVisibility(View.VISIBLE);
		initTableUI(tableData1, viewHolder.table1, table1Position);
		if (table2Position < compTableInfos.size()) {
			final CompManTableInfo tableData2 = compTableInfos.get(table2Position);
			viewHolder.table2.setVisibility(View.VISIBLE);
			initTableUI(tableData2, viewHolder.table2, table2Position);
		} else {
			viewHolder.table2.setVisibility(View.INVISIBLE);
		}
		return view;
	}

	private void cleanViewContent(View view) {
		ImageButton chair1 = (ImageButton) view.findViewById(R.id.imgbtnchair1);
		ImageButton chair2 = (ImageButton) view.findViewById(R.id.imgbtnchair2);
		ImageButton chair3 = (ImageButton) view.findViewById(R.id.imgbtnchair3);
		ImageButton chair4 = (ImageButton) view.findViewById(R.id.imgbtnchair4);
		ImageButton chair5 = (ImageButton) view.findViewById(R.id.imgbtnchair5);
		ImageButton chair6 = (ImageButton) view.findViewById(R.id.imgbtnchair6);
		ImageButton chair7 = (ImageButton) view.findViewById(R.id.imgbtnchair7);
		ImageButton chair8 = (ImageButton) view.findViewById(R.id.imgbtnchair8);
		ImageButton chair9 = (ImageButton) view.findViewById(R.id.imgbtnchair9);
		ImageButton chair10 = (ImageButton) view.findViewById(R.id.imgbtnchair10);
		chair1.setImageResource(R.drawable.small_chair);
		chair2.setImageResource(R.drawable.small_chair);
		chair3.setImageResource(R.drawable.small_chair);
		chair4.setImageResource(R.drawable.small_chair);
		chair5.setImageResource(R.drawable.small_chair);
		chair6.setImageResource(R.drawable.small_chair);
		chair7.setImageResource(R.drawable.small_chair);
		chair8.setImageResource(R.drawable.small_chair);
		chair9.setImageResource(R.drawable.small_chair);
		chair10.setImageResource(R.drawable.small_chair);
		chair1.setClickable(false);
		chair2.setClickable(false);
		chair3.setClickable(false);
		chair4.setClickable(false);
		chair5.setClickable(false);
		chair6.setClickable(false);
		chair7.setClickable(false);
		chair8.setClickable(false);
		chair9.setClickable(false);
		chair10.setClickable(false);
	}

	private void showSeatModifyPlayerDialog(MemberInfo memberInfo, final CompManSeatInfo seatInfo, final CompManTableInfo tableData,
			ImageButton btnChair) {
		View layout = mInflater.inflate(R.layout.seat_modify_player_dialog,
				(ViewGroup) competitionManageActivity.findViewById(R.id.llSeatModifyPlayerDlg));

		TextView tvSeatPlayerName = (TextView) layout.findViewById(R.id.tvSeatPlayerName);
		TextView tvSeatPlayerCardID = (TextView) layout.findViewById(R.id.tvSeatPlayerCardID);
		ImageView imgPlayerImage = (ImageView) layout.findViewById(R.id.imgPlayerImage);

		tvSeatPlayerName.setText(memberInfo.getMemName());
		tvSeatPlayerCardID.setText(memberInfo.getCardNO());

		final String actorName = memberInfo.getMemName();
		final String actorCardID = memberInfo.getCardNO();
		final boolean actorMale = memberInfo.getMemSex() == 1 ? true : false;
		// 请求高清头像大图
		dialogReqImageInfo(memberInfo.getMemImage(), imgPlayerImage, btnChair);
		if (myDialog != null && myDialog.isShowing())
			return;
		myDialog = new AlertDialog.Builder(mContext).setView(layout).setPositiveButton("平衡", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				View llSeatBalancePlayerDlg = mInflater.inflate(R.layout.seat_balance_player_dialog,
						(ViewGroup) competitionManageActivity.findViewById(R.id.llSeatBalancePlayerDlg));

				final ClearableEditText edTableNo = (ClearableEditText) llSeatBalancePlayerDlg.findViewById(R.id.edTableNo);
				final ClearableEditText edSeatNo = (ClearableEditText) llSeatBalancePlayerDlg.findViewById(R.id.edSeatNo);

				new AlertDialog.Builder(mContext).setTitle("座位选项").setView(llSeatBalancePlayerDlg)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int i) {
								final String toTableNo = edTableNo.getText().toString().trim();
								final String toSeatNo = edSeatNo.getText().toString().trim();
								if (StringUtils.isBlank(toTableNo)) {
									new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先输入桌号！").show();
									return;
								}
								if (StringUtils.isBlank(toSeatNo)) {
									new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先输入座位号！").show();
									return;
								}
								AlertDialog.Builder builder = new Builder(mContext);
								builder.setMessage("是否确认将选手【" + actorName + "】分配到（" + toTableNo + "）桌 （" + toSeatNo + "）号？");
								builder.setTitle("提示");
								builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(final DialogInterface dialog, int i) {
										HttpUtil.get(String.format(SFBaseActivity.URL_SERVER_ADDRESS + Const.METHOD_BALANCE_PLAYER, empUuid,
												competitionManageActivity.compID, seatInfo.getId(), seatInfo.getMemID(), Integer.valueOf(toTableNo),
												Integer.valueOf(toSeatNo)), new JsonBaseHttpResponseHandler() {
											@Override
											public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
												try {
													if (response.getInt("rspCode") == Const.RspCode_Success) {
														dialog.dismiss();
														Toast.makeText(mContext, "平衡选手成功，正在打印小条...", Toast.LENGTH_SHORT).show();
														// 平衡成功后打印小条
														competitionManageActivity.printBalancePlayer(competitionManageActivity.strCompName,
																competitionManageActivity.strCompTime, actorName, actorCardID,
																Integer.valueOf(toTableNo), Integer.valueOf(toSeatNo), actorMale);
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
								builder.setNegativeButton("取消", cancelListener);
								builder.create().show();

							}
						}).setNegativeButton("取消", cancelListener).show();
			}
		}).setNegativeButton("淘汰", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				AlertDialog.Builder builder = new Builder(mContext);
				builder.setMessage("是否确认淘汰（" + tableData.getTableNO() + "）号桌（" + seatInfo.getSeatNO() + "）号选手【" + actorName + "】？");
				builder.setTitle("提示");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, int i) {
						HttpUtil.get(String.format(SFBaseActivity.URL_SERVER_ADDRESS + Const.METHOD_ELIMINATE_PLAYER, empUuid,
								competitionManageActivity.compID, seatInfo.getId(), seatInfo.getMemID()), new JsonBaseHttpResponseHandler() {
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
				builder.setNegativeButton("取消", cancelListener);
				builder.create().show();
			}
		}).setNeutralButton("关闭", cancelListener).create();
		myDialog.show();
	}

	DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int i) {
			dialog.dismiss();
		}
	};

	// 座位弹出框，显示单个用户的高清头像图片
	private void dialogReqImageInfo(final String strImageName, final ImageView imgView, final ImageButton btnChair) {
		String strHttpUtil = String.format(SFBaseActivity.URL_SERVER_ADDRESS + Const.METHOD_DOWNLOAD_IMAGE_PATH, strImageName);
		final File file = new File(avatarPath, "avatartemp.jpg");
		HttpUtil.get(strHttpUtil, new BinaryHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					FileOutputStream oStream = new FileOutputStream(file);
					oStream.write(arg2);
					oStream.flush();
					oStream.close();
					imageUri = Uri.fromFile(file);
					avatarBitmap = BitmapFactory.decodeFile(imageUri.getPath());
					imgView.setImageBitmap(avatarBitmap);
					final File smallAvatar = new File(seatavatarPath, strImageName);
					String strHttpUtil = String.format(SFBaseActivity.URL_SERVER_ADDRESS + Const.METHOD_DOWNLOAD_SEAT_IMAGE, strImageName);
					HttpUtil.get(strHttpUtil, new BinaryHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
							try {
								FileOutputStream oStream = new FileOutputStream(smallAvatar);
								oStream.write(arg2);
								oStream.flush();
								oStream.close();
								imageUri = Uri.fromFile(smallAvatar);
								avatarBitmap = BitmapFactory.decodeFile(imageUri.getPath());
								btnChair.setImageBitmap(avatarBitmap);
							} catch (Exception e) {
								LogUtil.se(SFBaseActivity.TAG, e);
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
							Toast.makeText(mContext, "小图加载失败...", Toast.LENGTH_SHORT).show();
							imgView.setImageResource(R.drawable.big_avatar);
							if (avatarBitmap != null && !avatarBitmap.isRecycled()) {
								avatarBitmap.recycle();
								avatarBitmap = null;
							}
						}
					});
				} catch (Exception e) {
					LogUtil.se(SFBaseActivity.TAG, e);
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(mContext, "头像加载失败...", Toast.LENGTH_SHORT).show();
				imgView.setImageResource(R.drawable.big_avatar);
				if (avatarBitmap != null && !avatarBitmap.isRecycled()) {
					avatarBitmap.recycle();
					avatarBitmap = null;
				}
			}
		});
	}

	// 获取座位小头像
	private void reqSeatImageInfo(String strImageName, final ImageView imgView) {
		final File file = new File(seatavatarPath, strImageName);
		if (file.exists()) {
			imageUri = Uri.fromFile(file);
			avatarBitmap = BitmapFactory.decodeFile(imageUri.getPath());
			imgView.setImageBitmap(avatarBitmap);
		} else {
			String strHttpUtil = String.format(SFBaseActivity.URL_SERVER_ADDRESS + Const.METHOD_DOWNLOAD_SEAT_IMAGE, strImageName);
			HttpUtil.get(strHttpUtil, new BinaryHttpResponseHandler() {
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					try {
						FileOutputStream oStream = new FileOutputStream(file);
						oStream.write(arg2);
						oStream.flush();
						oStream.close();
						imageUri = Uri.fromFile(file);
						avatarBitmap = BitmapFactory.decodeFile(imageUri.getPath());
						imgView.setImageBitmap(avatarBitmap);
					} catch (Exception e) {
						LogUtil.se(SFBaseActivity.TAG, e);
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					Toast.makeText(mContext, "小图下载失败...", Toast.LENGTH_SHORT).show();
					imgView.setImageResource(R.drawable.big_avatar);
					if (avatarBitmap != null && !avatarBitmap.isRecycled()) {
						avatarBitmap.recycle();
						avatarBitmap = null;
					}
				}
			});
		}
	}

	private void initTableUI(final CompManTableInfo tableData, View table, final int tablePosition) {
		CheckBox cbtableSelect = (CheckBox) table.findViewById(R.id.cbtableSelect);
		Button btntableBurst = (Button) table.findViewById(R.id.btntableBaoZhuo);
		Button btntableRelease = (Button) table.findViewById(R.id.btntableShiFang);
		ImageView imgLock = (ImageView) table.findViewById(R.id.imgLock);
		btntableBurst.setVisibility(View.GONE);
		btntableRelease.setVisibility(View.GONE);
		imgLock.setVisibility(View.GONE);
		cbtableSelect.setText(StringUtils.leftPad(String.valueOf(tableData.getTableNO()), 3, "0"));
		if (tableData.getTableState() == 1) {
			cbtableSelect.setChecked(false);
		}
		cbtableSelect.setClickable(false);
		switch (tableData.getButton()) {
		case TABLEBUTOPER.LOCK:
			cbtableSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						checked.put(tablePosition, isChecked);
						tableID.add(Integer.toString(tableData.getTableNO()));
					} else {
						checked.remove(tablePosition);
						tableID.remove(Integer.toString(tableData.getTableNO()));
					}
				}
			});
			cbtableSelect.setChecked((checked.containsKey(tablePosition)));
			cbtableSelect.setClickable(true);
			imgLock.setVisibility(View.VISIBLE);
			break;
		case TABLEBUTOPER.RELEASE:
			btntableRelease.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					final int releaseTableNo = tableData.getTableNO();
					AlertDialog.Builder builder = new Builder(mContext);
					builder.setMessage("是否确定释放【" + releaseTableNo + "】号桌？");
					builder.setTitle("提示");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int i) {
							doReleaseTable(releaseTableNo);
							dialog.dismiss();
						}
					});
					builder.setNegativeButton("取消", cancelListener);
					builder.create().show();
				}
			});
			btntableRelease.setVisibility(View.VISIBLE);
			break;
		case TABLEBUTOPER.BURSTTABLE:
			btntableBurst.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					final int burstTableNo = tableData.getTableNO();
					AlertDialog.Builder builder = new Builder(mContext);
					builder.setMessage("是否确定爆掉【" + burstTableNo + "】号桌？");
					builder.setTitle("提示");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int i) {
							doBurstTable(burstTableNo);
							dialog.dismiss();
						}
					});
					builder.setNegativeButton("取消", cancelListener);
					builder.create().show();
				}
			});
			btntableBurst.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		initSeatUI(tableData, table);
	}

	private void initSeatUI(final CompManTableInfo tableData, View table) {
		cleanViewContent(table);
		for (final CompManSeatInfo seatInfo : tableData.getMemSeatInfoList()) {
			int seatID = seatInfo.getSeatNO();
			Resources res = competitionManageActivity.getResources();
			LinearLayout llchair = (LinearLayout) table.findViewById(res.getIdentifier("llchair" + seatID, "id", "com.evsward.butler"));
			final ImageButton btnChair = (ImageButton) llchair.findViewById(res.getIdentifier("imgbtnchair" + seatID, "id", "com.evsward.butler"));
			// 座椅上面小照片请求
			reqSeatImageInfo(seatInfo.getMemImage(), btnChair);
			btnChair.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// progress dialog
					final ProgressDialog progressDialog = new ProgressDialog(mContext);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					progressDialog.setTitle("请等待");
					progressDialog.setMessage("正在查询会员信息...");
					progressDialog.setCancelable(false);
					progressDialog.show();
					HttpUtil.get(String.format(SFBaseActivity.URL_SERVER_ADDRESS + Const.METHOD_SEAT_SEARCH_MEMBER, seatInfo.getMemID()),
							new JsonBaseHttpResponseHandler() {
								@Override
								public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
									try {
										if (response.getInt("rspCode") == Const.RspCode_Success) {
											Gson gson = new Gson();
											MemberInfo memberInfo = gson.fromJson(response.getString("memberInfo"), MemberInfo.class);
											showSeatModifyPlayerDialog(memberInfo, seatInfo, tableData, btnChair);
										} else {
											Toast.makeText(mContext, response.getString("msg"), Toast.LENGTH_SHORT).show();
											LogUtil.e(TAG, response.getString("msg"));
										}
									} catch (JSONException e) {
										LogUtil.e(TAG, "服务器通讯错误！");
									} finally {
										progressDialog.dismiss();
									}
								}
							});
				}
			});
		}
	}

	private void doBurstTable(int tabID) {
		HttpUtil.get(String.format(SFBaseActivity.URL_SERVER_ADDRESS + Const.METHOD_BURST_TABLE, empUuid, competitionManageActivity.compID, tabID),
				new JsonBaseHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
							AckNewSeatInfoList newSeatInfoListObj = gson.fromJson(response.toString(), new TypeToken<AckNewSeatInfoList>() {
							}.getType());
							if (newSeatInfoListObj.getRspCode() == Const.RspCode_Success) {
								Toast.makeText(mContext, "爆桌成功，正在打印小条...", Toast.LENGTH_SHORT).show();
								// String strBurstTableInfo = "";

								View layout = mInflater.inflate(R.layout.burst_table_dialog,
										(ViewGroup) competitionManageActivity.findViewById(R.id.llBurstTableDlg));
								ListView lvBurstTable = (ListView) layout.findViewById(R.id.lvBurstTable);
								BurstTableInfoAdapter burstTableInfoAdapter = new BurstTableInfoAdapter(mContext, newSeatInfoListObj
										.getBurstTableRes().getNewSeatInfoList());
								lvBurstTable.setAdapter(burstTableInfoAdapter);

								new AlertDialog.Builder(mContext).setTitle("此桌选手被随机分配到以下座位").setView(layout).setNegativeButton("关闭", cancelListener)
										.show();

								// 爆桌成功后逐一打印小条
								if (newSeatInfoListObj.getBurstTableRes().getNewSeatInfoList().size() > 0) {
//									for (NewSeatInfo seatInfo : newSeatInfoListObj.getBurstTableRes().getNewSeatInfoList()) {
//										competitionManageActivity.printBurstTable(competitionManageActivity.strCompName,
//												competitionManageActivity.strCompTime, seatInfo.getMemName(), String.valueOf(seatInfo.getMemID()),
//												seatInfo.getTableNO(), seatInfo.getSeatNO(), seatInfo.getMemSex() == 1 ? true : false);
//									}
									
									competitionManageActivity.printBurstTable(competitionManageActivity.strCompName,
											competitionManageActivity.strCompTime, newSeatInfoListObj.getBurstTableRes().getNewSeatInfoList());

								} else {
									LogUtil.e(TAG, "爆桌打印异常！被爆桌子无有效玩家");
								}
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

	private void doReleaseTable(int tabID) {
		HttpUtil.get(String.format(SFBaseActivity.URL_SERVER_ADDRESS + Const.METHOD_RELEASE_TABLE, empUuid, competitionManageActivity.compID, tabID),
				new JsonBaseHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							if (response.getInt("rspCode") == Const.RspCode_Success) {
								Toast.makeText(mContext, "释放牌桌成功", Toast.LENGTH_SHORT).show();
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

	class TableViewHolder {
		View table1;
		View table2;
	}

	class ChairViewHolder {
		int tableNo;
		CompManSeatInfo seatInfo;
	}
}
