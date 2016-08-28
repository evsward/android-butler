package com.evsward.butler.activity.member;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.evsward.butler.activity.base.BaseActivity;
import com.evsward.butler.entities.NFCInfo;
import com.evsward.butler.service.PrintUtil;
import com.evsward.butler.util.CommonUtil;
import com.evsward.butler.util.Const;
import com.evsward.butler.util.HttpUtil;
import com.evsward.butler.util.ImageUtil;
import com.evsward.butler.util.LogUtil;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.evsward.butler.R;

/**
 * 基类 编辑会员基本信息
 * 
 * @Date Apr 10, 2015
 * @author liuwb.edward
 */
public abstract class RegisterBaseActivity extends BaseActivity implements OnClickListener {
	// 是否需要补卡
	protected boolean NeedSuppyCard = true;
	// post parameters
	protected ImageButton memberAvatar;
	protected TextView nfcCardMemberNo;
	protected EditText memberName, memberID, memberMobile;
	protected long decimalNFCID;
	// remember current info
	protected String cardNO, memName, gender;
	// switch
	protected TextView genderShow;
	protected Switch switchGender;
	// nfc
	protected NfcAdapter nfcAdapter;
	protected PendingIntent pendingIntent;
	protected IntentFilter[] mFilters;
	protected String[][] mTechLists;
	// avatar
	protected Uri imageUri;
	protected Bitmap avatarBitmap;
	public static final int TAKE_PHOTO = 1;
	protected static String avatarPath = CommonUtil.createFolder(Const.HIGHLEVLE_AVATAR_FOLDER);
	// post
	protected Button saveAndPrintMember;
	public static final int POST_SUCCESS = 0;
	public static final int POST_FAILURE = 1;
	// 是否更改性别
	protected boolean ifUpdateGender = false;

	protected void initUI() {
		// nfc
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		checkNFC();
		nfcCardMemberNo = (TextView) findViewById(R.id.nfcCardMemberNo);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		ndef.addCategory("*/*");
		mFilters = new IntentFilter[] { ndef };// 过滤器
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() }, new String[] { NfcA.class.getName() } };// 允许扫描的标签类型
		// 基本文本信息
		memberName = (EditText) findViewById(R.id.memberName);
		memberID = (EditText) findViewById(R.id.memberID);
		memberMobile = (EditText) findViewById(R.id.memberMobile);
		gender = "1";// 初始化
		// switch
		genderShow = (TextView) findViewById(R.id.genderShow);
		switchGender = (Switch) findViewById(R.id.switchGender);
		switchGender.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ifUpdateGender = true;
				if (isChecked) {
					gender = "1";
					genderShow.setText("男");
				} else {
					gender = "0";
					genderShow.setText("女");
				}
			}
		});
		// avatar
		memberAvatar = (ImageButton) findViewById(R.id.memberAvatar);
		memberAvatar.setOnClickListener(this);
		// post
		saveAndPrintMember = (Button) findViewById(R.id.saveAndPrintMember);
		saveAndPrintMember.setOnClickListener(this);
	}

	protected void checkNFC() {
		if (nfcAdapter == null) {
			Toast.makeText(this, "该设备不支持NFC！", Toast.LENGTH_SHORT).show();
			finish();
			return;
		} else if (!nfcAdapter.isEnabled()) {
			Toast.makeText(this, "您的NFC未开启", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case -1:
			showPhoto();
			break;
		case 0:
			new File(imageUri.getPath()).delete();
			memberAvatar.setImageResource(R.drawable.big_avatar);
			if (avatarBitmap != null && !avatarBitmap.isRecycled()) {
				avatarBitmap.recycle();
				avatarBitmap = null;
			}
			break;
		default:
			break;
		}
	}

	protected void showPhoto() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 5;
		avatarBitmap = BitmapFactory.decodeFile(imageUri.getPath(), options);
		byte[] a = ImageUtil.compressBitmap(avatarBitmap, 200);
		ImageUtil.getFileFromBytes(a, imageUri.getPath());
		memberAvatar.setImageBitmap(avatarBitmap);
	}

	/**
	 * 获取tab标签中的内容
	 * 
	 * @param intent
	 * @return 十进制NFC卡号
	 */
	protected long processIntent(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		byte[] bytesid = tag.getId();
		long result = CommonUtil.convertHex2Long(CommonUtil.bytesToHexString(bytesid));
		return result;
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.d(TAG, "*************onResume*****************");
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, mFilters, mTechLists);
		if (avatarBitmap == null) {
			memberAvatar.setImageResource(R.drawable.big_avatar);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.memberAvatar:
			if (StringUtils.isBlank((nfcCardMemberNo.getText().toString()))) {
				new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先刷卡！").show();
				return;
			}
			// 创建File对象，用于存储拍照后的照片
			File outputImage = new File(avatarPath, "avatartemp.jpg");
			try {
				if (outputImage.exists()) {
					outputImage.delete();
				}
				outputImage.createNewFile();
			} catch (Exception e) {
				LogUtil.se(TAG, e);
			}
			imageUri = Uri.fromFile(outputImage);
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, TAKE_PHOTO);// 启动相机程序
			break;
		case R.id.saveAndPrintMember:
			// Validation
			if (StringUtils.isBlank((nfcCardMemberNo.getText().toString()))) {
				new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请先刷卡！").show();
				return;
			}
			if (StringUtils.isBlank((memberName.getText().toString()))) {
				new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请输入姓名！").show();
				return;
			}
			if (StringUtils.isBlank((memberID.getText().toString()))) {
				new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请输入证件号！").show();
				return;
			}
			if (StringUtils.isBlank((memberMobile.getText().toString()))) {
				new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请输入手机号！").show();
				return;
			}
			if (StringUtils.isBlank(gender)) {
				new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请选择性别！").show();
				return;
			}
			if (avatarBitmap == null) {
				new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请为报名者拍照！").show();
				return;
			}
			cardNO = nfcCardMemberNo.getText().toString();
			memName = memberName.getText().toString();
			// progress dialog
			final ProgressDialog progressDialog = new ProgressDialog(mContext);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setTitle("请等待");
			progressDialog.setMessage("正在提交会员信息...");
			progressDialog.show();
			final long start_time = System.currentTimeMillis();
			final String memberNameForMessage = memberName.getText().toString();
			HttpUtil.post(postSaveOrUpdateUrl(), postSaveOrUpdateParam(), new JsonHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					progressDialog.dismiss();
					long cost_time = System.currentTimeMillis() - start_time;
					try {
						if (response.getInt("rspCode") == Const.RspCode_Success) {
							Toast.makeText(mContext, "正在打印...", Toast.LENGTH_SHORT).show();
							callBackHandler();
						} else {
							LogUtil.e(TAG, String.format("保存会员失败:%s", response.getString("msg").toString()));
						}
						Toast.makeText(mContext,
								String.format("保存会员【%s】信息：%s！耗时：%d ms", memberNameForMessage, response.getString("msg").toString(), cost_time),
								Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						LogUtil.e(TAG, "服务器通讯错误！");
					}
				}

				@Override
				public void onFailure(int i, Header[] aheader, String s, Throwable throwable) {
					progressDialog.dismiss();
					Toast.makeText(mContext, "请检查网络连接！", Toast.LENGTH_SHORT).show();
					LogUtil.se(TAG, throwable);
				}

				@Override
				public void onFailure(int i, Header[] aheader, Throwable throwable, JSONObject jsonobject) {
					progressDialog.dismiss();
					Toast.makeText(mContext, "保存会员【" + memberNameForMessage + "】信息：失败！", Toast.LENGTH_SHORT).show();
					LogUtil.e(TAG, String.format("保存会员:%s", throwable.toString()));
				}

				@Override
				public void onProgress(int bytesWritten, int totalSize) {
					super.onProgress(bytesWritten, totalSize);
					progressDialog.setProgress((int) (totalSize <= 0 ? -1D : (((double) bytesWritten * 1.0D) / (double) totalSize) * 100D));
				}

			});
			break;
		default:
			break;
		}
	}

	/**
	 * 查询NFC卡号接口
	 */
	protected void getcardno(String dialogTitle) {
		// progress dialog
		final ProgressDialog progressDialog = ProgressDialog.show(mContext, dialogTitle, "正在查询NFC卡信息...");
		HttpUtil.get(String.format(URL_SERVER_ADDRESS + Const.METHOD_NFC_GET_CARD_NO, decimalNFCID), new JsonHttpResponseHandler() {

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
				nfcCardMemberNo.setText(null);
				nfcCardMemberNo.setHint("扫卡操作出现异常");
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
						NFCInfo nfc = gson.fromJson(response.getString("nfcInfo"), NFCInfo.class);
						String cardNo = nfc.getCardno();
						nfcCardMemberNo.setText(cardNo);
						cardNO = cardNo;
					} else {
						initRegister();
						nfcCardMemberNo.setText(null);
						nfcCardMemberNo.setHint(response.getString("msg").toString());
						LogUtil.e(TAG, String.format("扫卡失败:%s", response.getString("msg").toString()));
					}
				} catch (JSONException e) {
					nfcCardMemberNo.setText(null);
					nfcCardMemberNo.setHint("扫卡失败:请检查后台日志");
					LogUtil.e(TAG, "服务器通讯错误！");
				}
			}
		});
	}

	protected void registPrintTags() {
		PrintUtil.registPrint(cardNO, memName, Integer.parseInt(gender) == 1 ? "M" : "F");
	}

	/**
	 * 销毁图片文件
	 */
	protected void destoryBimap() {
		if (avatarBitmap != null && !avatarBitmap.isRecycled()) {
			avatarBitmap.recycle();
			avatarBitmap = null;
		}
	}

	/**
	 * 不重启Activity，清空UI
	 */
	protected void initRegister() {
		NeedSuppyCard = false;
		destoryBimap();// 把照片从手机内存中清除
		memberAvatar.setImageResource(R.drawable.big_avatar);
		switchGender.setChecked(true);
		nfcCardMemberNo.setText(null);
		nfcCardMemberNo.setHint("请刷有效会员卡");
		memberName.setText(null);
		memberID.setText(null);
		memberMobile.setText(null);
		// 清空比赛
		LinearLayout root = (LinearLayout) findViewById(R.id.matchInfoLayout);
		if (root != null) {
			root.removeAllViews();
		}
	}

	protected void closeKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}

	protected abstract void callBackHandler();

	protected abstract RequestParams postSaveOrUpdateParam();

	protected abstract String postSaveOrUpdateUrl();

}