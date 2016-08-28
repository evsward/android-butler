package com.evsward.butler.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.evsward.bulter.adapter.ADListAdapter;
import com.evsward.butler.activity.base.CompetitionManageBaseFragment;
import com.evsward.butler.entities.Advert;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.evsward.butler.R;

public class CompetitionAdvertisementFragment extends CompetitionManageBaseFragment {
	private ListView adListInfo;
	private ADListAdapter adListAdapter;
	private List<Advert> adListData = new ArrayList<Advert>();
	// advertisement selected
	private Advert advertSelected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	private void initADList() {
		HttpUtil.get(URL_SERVER_ADDRESS + String.format(Const.METHOD_ADVERTISEMENT_LIST, empUuid, compID), new JsonBaseHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					if (response.getInt("rspCode") == Const.RspCode_Success) {
						Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
						adListData = gson.fromJson(response.getString("adverts"), new TypeToken<List<Advert>>() {
						}.getType());
						int adSelectedItemsID = -1;
						if (response.toString().indexOf("compAdvert")>-1) {
							adSelectedItemsID = response.getJSONObject("compAdvert").getInt("advertID");
						}
						adListAdapter = new ADListAdapter(mContext, adListData);
						adListAdapter.setSelectedAdvertID(adSelectedItemsID);
						adListInfo.setAdapter(adListAdapter);
						adListInfo.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								advertSelected = adListData.get(position);
								adListAdapter.setSelectedAdvertID(advertSelected.getAdvertID());
								adListAdapter.notifyDataSetChanged();
							}
						});
					} else {
						Toast.makeText(mContext, "获取广告列表:" + response.getString("msg"), Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, String.format("获取广告:%s", response.getString("msg")));
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_competition_advertisement, container, false);
		adListInfo = (ListView) view.findViewById(R.id.adListInfo);
		initADList();
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.competition_manage_ad, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.opSave:
			if (advertSelected == null) {
				new AlertDialog.Builder(competitionManageActivity).setTitle("提示").setMessage("请选择一项广告内容！").show();
			} else {
				RequestParams params = new RequestParams();
				params.put("compID", compID);
				params.put("empUuid", empUuid);
				params.put("advertID", advertSelected.getAdvertID());
				final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "提示", "正在设置广告...");
				HttpUtil.post(URL_SERVER_ADDRESS + Const.METHOD_ADVERTISEMENT_SET, params, new JsonHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						progressDialog.dismiss();
						Toast.makeText(mContext, "服务器连接异常！", Toast.LENGTH_SHORT).show();
						LogUtil.se(TAG, throwable);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
						progressDialog.dismiss();
						Toast.makeText(mContext, "设置比赛广告请求失败！", Toast.LENGTH_SHORT).show();
						LogUtil.e(TAG, String.format("设置比赛广告请求失败:%s", throwable.toString()));
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						progressDialog.dismiss();
						try {
							if (response.getInt("rspCode") == Const.RspCode_Success) {
								Toast.makeText(mContext, "设置比赛广告请求成功！", Toast.LENGTH_SHORT).show();
								LogUtil.i(TAG, "设置比赛广告请求成功！");
							} else {
								Toast.makeText(mContext, String.format("设置比赛广告请求失败:%s", response.getString("msg")), Toast.LENGTH_SHORT).show();
								LogUtil.e(TAG, String.format("设置比赛广告请求失败:%s", response.getString("msg")));
							}
						} catch (JSONException e) {
							LogUtil.e(TAG, "服务器通讯错误！");
						}
					}

				});
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
