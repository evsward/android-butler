package com.evsward.butler.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.evsward.butler.activity.base.BaseActivity;
import com.evsward.butler.entities.RoundTemplate;
import com.evsward.butler.entities.RoundTemplate.RoundTemplateList;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.JsonBaseHttpResponseHandler;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.widget.TitleBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.evsward.butler.R;

@SuppressLint("SimpleDateFormat")
public class NewMatchActivity extends BaseActivity {

	private static final int SHOW_DATAPICK = 0, DATE_DIALOG_ID = 1, SHOW_TIMEPICK = 2, TIME_DIALOG_ID = 3;
	private EditText edNewMatchDate, edNewMatchTime, edMatchName, edMatchCost, edManageCost, edInitChip, edMangZhuLvl, edBaodiJiangjin;
	private int nJiangliMobanManage = 0, nChongjinManage = 0, nTableManage = 9, nChipType = 1, nMangZhuMoban = 0;
	private RadioGroup rgJiangliMobanManage, rgChongjinManage, rgTableManage, rgSeatManage, rgCompType;
	private Button pickDate = null, pickTime = null;
	private int mYear, mMonth, mDay, mHour, mMinute;
	private Spinner spChipType, spMangZhuMoban;
	private int nAssignSeat = 1, nCompType = 1;

	private ArrayAdapter<RoundTemplate> aMangZhuMobans;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.getTitleBar(this, "新建比赛", "保存", btnSaveListener);
		setContentView(R.layout.activity_new_match);
		initializeViews();
	}

	private void UpdateMobanListSpinner(RoundTemplateList roundtemplates) {
		List<RoundTemplate> lMangZhuMobans;

		spMangZhuMoban = (Spinner) findViewById(R.id.spMangZhuMoban);
		lMangZhuMobans = new ArrayList<RoundTemplate>();
		for (int i = 0; i < roundtemplates.getRoundTemplates().length; i++) {
			lMangZhuMobans.add(roundtemplates.getRoundTemplates()[i]);
		}
		aMangZhuMobans = new ArrayAdapter<RoundTemplate>(this, android.R.layout.simple_spinner_item, lMangZhuMobans);
		aMangZhuMobans.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spMangZhuMoban.setAdapter(aMangZhuMobans);
	}

	private boolean checkNewMatch() {
		if (StringUtils.isBlank((edNewMatchDate.getText().toString()))) {
			new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先填写比赛日期！").show();
			return false;
		}

		if (StringUtils.isBlank((edNewMatchTime.getText().toString()))) {
			new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先填写比赛时间！").show();
			return false;
		}

		if (StringUtils.isBlank((edMatchName.getText().toString()))) {
			new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先填写比赛名称！").show();
			return false;
		}
		if (StringUtils.isBlank((edMatchCost.getText().toString()))) {
			new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先填写比赛费用！").show();
			return false;
		}
		if (StringUtils.isBlank((edManageCost.getText().toString()))) {
			new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先填写比赛管理费！").show();
			return false;
		}
		if (StringUtils.isBlank((edBaodiJiangjin.getText().toString()))) {
			new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先填写比赛保底金额！").show();
			return false;
		}
		if (StringUtils.isBlank((edInitChip.getText().toString()))) {
			new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先填写比赛初始筹码！").show();
			return false;
		}

		if (StringUtils.isBlank((edMangZhuLvl.getText().toString()))) {
			new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先填写比赛盲注等级！").show();
			return false;
		}

		try {
			DateFormat newdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date newD = newdf.parse(edNewMatchDate.getText().toString() + " " + edNewMatchTime.getText().toString() + ":00");
			Date curD = new Date();

			if (curD.after(newD)) {
				new AlertDialog.Builder(mContext).setTitle("提示").setMessage("比赛时间不能晚于当前时间！").show();
				return false;
			}
		} catch (ParseException e) {
			LogUtil.se(TAG, e);
		}

		return true;
	}

	/**
	 * 初始化控件和UI视图
	 */
	private void initializeViews() {
		Bundle bundle = this.getIntent().getExtras();
		String mangzhuList = bundle.getString("mangzhuList");
		Gson gson = new GsonBuilder().setDateFormat(Const.DATE_PATTERN).create();
		RoundTemplateList roundtemplates = gson.fromJson(mangzhuList, new TypeToken<RoundTemplateList>() {
		}.getType());

		UpdateMobanListSpinner(roundtemplates);
		edNewMatchDate = (EditText) findViewById(R.id.edSetDate);
		edNewMatchTime = (EditText) findViewById(R.id.edSetTime);
		edMatchName = (EditText) findViewById(R.id.edMatchName);
		edMatchCost = (EditText) findViewById(R.id.edMatchCost);
		edManageCost = (EditText) findViewById(R.id.edManageCost);
		edBaodiJiangjin = (EditText) findViewById(R.id.edBaodiJiangjin);
		edInitChip = (EditText) findViewById(R.id.edInitChip);
		edMangZhuLvl = (EditText) findViewById(R.id.edMangZhuLvl);

		spChipType = (Spinner) findViewById(R.id.spChipType);
		spChipType.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				nChipType = arg2;

			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		spMangZhuMoban = (Spinner) findViewById(R.id.spMangZhuMoban);
		spMangZhuMoban.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				RoundTemplate rt = aMangZhuMobans.getItem(arg2);
				nMangZhuMoban = rt.getRoundTempID();
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		rgJiangliMobanManage = (RadioGroup) findViewById(R.id.rgJiangliMobanManage);
		rgJiangliMobanManage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rbUnJiangliMoban: {
					nJiangliMobanManage = 0;
				}
					break;
				case R.id.rbJiangliMoban: {
					nJiangliMobanManage = 1;
				}
					break;
				default:
					break;
				}
			}
		});

		rgChongjinManage = (RadioGroup) findViewById(R.id.rgChongjinManage);
		rgChongjinManage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rbChongjinNo: {
					nChongjinManage = 0;
				}
					break;
				case R.id.rbChongjinYes: {
					nChongjinManage = 1;
				}
					break;
				default:
					break;
				}
			}
		});

		rgTableManage = (RadioGroup) findViewById(R.id.rgTableManage);
		rgTableManage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb10PTable: {
					nTableManage = 10;
				}
					break;
				case R.id.rb9PTable: {
					nTableManage = 9;
				}
					break;
				case R.id.rb6PTable: {
					nTableManage = 6;
				}
					break;
				default:
					break;
				}
			}
		});

		rgSeatManage = (RadioGroup) findViewById(R.id.rgSeatManage);
		rgSeatManage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rbassignSeatYes: {
					nAssignSeat = 1;
				}
					break;
				case R.id.rbassignSeatNo: {
					nAssignSeat = 0;
				}
					break;
				default:
					break;
				}
			}
		});

		rgCompType = (RadioGroup) findViewById(R.id.rgCompType);
		rgCompType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rbCompType_JinJi: {
					nCompType = 1;
				}
					break;
				case R.id.rbCompType_NoJinJi: {
					nCompType = 0;
				}
					break;
				default:
					break;
				}
			}
		});

		pickDate = (Button) findViewById(R.id.btnSetDate);
		pickTime = (Button) findViewById(R.id.btnSetTime);

		pickDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				if (pickDate.equals((Button) v)) {
					msg.what = SHOW_DATAPICK;
				}
				dateandtimeHandler.sendMessage(msg);
			}
		});

		pickTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				if (pickTime.equals((Button) v)) {
					msg.what = SHOW_TIMEPICK;
				}
				dateandtimeHandler.sendMessage(msg);
			}
		});
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		updateDateDisplay();
		updateTimeDisplay();
	}

	/**
	 * 更新日期显示
	 */
	private void updateDateDisplay() {
		edNewMatchDate.setText(new StringBuilder().append(mYear).append("-").append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
	}

	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			updateDateDisplay();
		}
	};

	/**
	 * 更新时间显示
	 */
	private void updateTimeDisplay() {
		edNewMatchTime.setText(new StringBuilder().append(mHour).append(":").append((mMinute < 10) ? "0" + mMinute : mMinute));
	}

	/**
	 * 时间控件事件
	 */
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;

			updateTimeDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, true);
		}

		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		case TIME_DIALOG_ID:
			((TimePickerDialog) dialog).updateTime(mHour, mMinute);
			break;
		}
	}

	/**
	 * 处理日期和时间控件的Handler
	 */
	@SuppressLint("HandlerLeak")
	Handler dateandtimeHandler = new Handler() {
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_DATAPICK:
				showDialog(DATE_DIALOG_ID);
				break;
			case SHOW_TIMEPICK:
				showDialog(TIME_DIALOG_ID);
				break;
			}
		}

	};

	OnClickListener btnSaveListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			boolean bCheck = checkNewMatch();
			if (bCheck != true)
				return;

			String strNewMatchDate = edNewMatchDate.getText().toString();
			String strNewMatchTime = edNewMatchTime.getText().toString();
			String strMatchName = edMatchName.getText().toString();
			String strMatchCost = edMatchCost.getText().toString();
			String strManageCost = edManageCost.getText().toString();
			String strInitChip = edInitChip.getText().toString();
			String strMangZhuLvl = edMangZhuLvl.getText().toString();
			String strBaodiJiangjin = edBaodiJiangjin.getText().toString();

			String strTime = strNewMatchDate + " " + strNewMatchTime;
			Date d = getFormatDate(strTime, "yyyy-MM-dd HH:mm");

			SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
			String strdecimalNFCID = pref.getString("decimalNFCID", "");
			// progress dialog
			final ProgressDialog progressDialog = new ProgressDialog(mContext);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setTitle("请等待");
			progressDialog.setMessage("正在创建比赛...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_NEW_MATCH, strdecimalNFCID, strMatchName,
					Integer.parseInt(strBaodiJiangjin), Integer.parseInt(strMatchCost), Integer.parseInt(strManageCost),
					Integer.parseInt(strInitChip), nChipType, nMangZhuMoban, Integer.parseInt(strMangZhuLvl), nChongjinManage, nTableManage,
					nJiangliMobanManage, d.getTime(), nAssignSeat, nCompType), new JsonBaseHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					try {
						if (response.getInt("rspCode") == Const.RspCode_Success) {
							Toast.makeText(mContext, "新建比赛成功", Toast.LENGTH_SHORT).show();
							finish();
						} else {
							Toast.makeText(mContext, "新建比赛失败：" + response.getString("msg"), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						LogUtil.se(TAG, e);
					} finally {
						progressDialog.dismiss();
					}
				}
			});
		}
	};

	/**
	 * 根据录入的文本，返回日期
	 * 
	 * @param strdate
	 * @param format
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public Date getFormatDate(String strdate, String format) {
		Date d = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			d = sdf.parse(strdate);
		} catch (Exception e) {
			LogUtil.e(TAG, "error to parse date::" + strdate);
		}
		return d;
	}
}
