package com.evsward.butler.activity;

import java.util.ArrayList;

import org.apache.http.Header;
import org.droidparts.widget.ClearableEditText;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.evsward.bulter.adapter.TableManageAdapter;
import com.evsward.butler.activity.base.SFBaseActivity;
import com.evsward.butler.entities.ReqCardTableList;
import com.evsward.butler.entities.ReqCardTableList.CardTable;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

public class TableManageActivity extends SFBaseActivity {
	private View addNewPlaceDialog;
	private TableManageAdapter tableManageAdapter;
	private ReqCardTableList reqCardTableList;
	// base
	private TextView tvFieldName, tvTableCount;
	private ListView lvTableStatusInfo;
	// menu
	private Menu mMenu;
	private MenuItem newPlace;
	private MenuItem delPlace;
	private boolean ifInfoFlag;
	// tool
	private LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		useActionBarMenu("牌桌资源管理");
		setContentView(R.layout.activity_table_manage);
		inflater = getLayoutInflater();
		reqResInitUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		mMenu = menu;
		getSupportMenuInflater().inflate(R.menu.new_table, menu);
		newPlace = menu.findItem(R.id.opNewTable);
		delPlace = menu.findItem(R.id.opDeleteTable);
		if (ifInfoFlag) {
			delPlace.setVisible(true);
			newPlace.setVisible(false);
		} else {
			delPlace.setVisible(false);
			newPlace.setVisible(true);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.opNewTable:
			showNewTableDialog();
			break;
		case R.id.opDeleteTable:
			delPlaceInfo();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void delPlaceInfo() {
		String fieldName = tvFieldName.getText().toString().trim();
		String tableCount = tvTableCount.getText().toString().trim();

		AlertDialog.Builder builder = new Builder(TableManageActivity.this);
		builder.setMessage("是否确定删除场地【" + fieldName + "】及其（" + tableCount + "）张桌子？");
		builder.setTitle("警告！");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// progress dialog
				final ProgressDialog progressDialog = new ProgressDialog(mContext);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setTitle("请等待");
				progressDialog.setMessage("正在删除场地...");
				progressDialog.setCancelable(false);
				progressDialog.show();
				HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_DELETE_PLACE, empUuid), new JsonBaseHttpResponseHandler() {
					@Override
					public void onSuccess(int i, Header[] aheader, JSONObject jsonobject) {
						try {
							if (jsonobject.getInt("rspCode") == Const.RspCode_Success) {
								delPlace.setVisible(false);
								newPlace.setVisible(true);
								Toast.makeText(mContext, "--删除场地成功--", Toast.LENGTH_SHORT).show();
								reqResInitUI();
								progressDialog.dismiss();
							} else {
								Toast.makeText(mContext, jsonobject.getString("msg"), Toast.LENGTH_SHORT).show();
								progressDialog.dismiss();
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

	private void showNewTableDialog() {
		addNewPlaceDialog = inflater.inflate(R.layout.new_table_dialog, (ViewGroup) findViewById(R.id.llNewTableDlg));
		final EditText edFieldName = (EditText) addNewPlaceDialog.findViewById(R.id.edFieldName);
		final EditText edTableCount = (EditText) addNewPlaceDialog.findViewById(R.id.edTableCount);
		new AlertDialog.Builder(this).setTitle("新建牌桌").setView(addNewPlaceDialog).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				String fieldName = edFieldName.getText().toString().trim();
				String tableCount = edTableCount.getText().toString().trim();

				showConfirmNewTableDialog(fieldName, tableCount);
			}
		}).setNegativeButton("取消", cancelListener).show();
	}

	private void showConfirmNewTableDialog(String fieldName, String tableCount) {
		if (fieldName.isEmpty()) {
			new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先输入场地名词！").show();
			return;
		}

		if (tableCount.isEmpty()) {
			new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先输入牌桌数量！").show();
			return;
		}

		AlertDialog.Builder builder = new Builder(TableManageActivity.this);
		builder.setMessage("是否确认在场地【" + fieldName + "】新建（" + tableCount + "）张桌子？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				ClearableEditText edFieldName = (ClearableEditText) addNewPlaceDialog.findViewById(R.id.edFieldName);
				ClearableEditText edTableCount = (ClearableEditText) addNewPlaceDialog.findViewById(R.id.edTableCount);

				String strFieldName = edFieldName.getText().toString().trim();
				String strTableCount = edTableCount.getText().toString().trim();

				addNewPlaceREQ(strFieldName, Integer.valueOf(strTableCount), dialog);
			}
		});
		builder.setNegativeButton("取消", cancelListener);
		builder.create().show();
	}

	private void addNewPlaceREQ(String strFieldName, int nTableCount, final DialogInterface dialog) {
		// progress dialog
		final ProgressDialog progressDialog = new ProgressDialog(mContext);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle("请等待");
		progressDialog.setMessage("正在创建场地...");
		progressDialog.show();
		progressDialog.setCancelable(false);
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_NEW_TABLE, strFieldName, nTableCount, strDecimalNFCID),
				new JsonBaseHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						super.onFailure(statusCode, headers, responseString, throwable);
						dialog.dismiss();
						progressDialog.dismiss();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable, errorResponse);
						dialog.dismiss();
						progressDialog.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						delPlace.setVisible(true);
						newPlace.setVisible(false);
						dialog.dismiss();
						progressDialog.dismiss();
						reqResInitUI();
						Toast.makeText(mContext, "---新建牌桌 成功---", Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, "---新建牌桌 成功---");
					}
				});
	}

	DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int i) {
			dialog.dismiss();
		}
	};

	private void reqResInitUI() {
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_REQ_TABLE_RES), new JsonBaseHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					if (response.getInt("rspCode") == Const.RspCode_Success) {
						Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
						reqCardTableList = gson.fromJson(response.toString(), new TypeToken<ReqCardTableList>() {
						}.getType());
						initView();
						((TableManageActivity) mContext).onCreateOptionsMenu(mMenu);
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

	private void initView() {
		lvTableStatusInfo = (ListView) findViewById(R.id.lvTableStatusInfo);
		tvFieldName = (TextView) findViewById(R.id.tvFieldName);
		tvTableCount = (TextView) findViewById(R.id.tvTableCount);
		if (reqCardTableList == null || reqCardTableList.getCardTables().size() == 0) {
			tvFieldName.setText("");
			tvTableCount.setText("");
			lvTableStatusInfo.setAdapter(null);
			ifInfoFlag = false;
		} else {
			ArrayList<CardTable> tabList = reqCardTableList.getCardTables();
			tvFieldName.setText(tabList.get(0).getAddress());
			tvTableCount.setText(Integer.toString(tabList.size()));
			tableManageAdapter = new TableManageAdapter(mContext, tabList);
			lvTableStatusInfo.setAdapter(tableManageAdapter);
			ifInfoFlag = true;
		}
	}
}
