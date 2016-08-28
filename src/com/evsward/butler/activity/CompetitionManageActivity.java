package com.evsward.butler.activity;

import java.util.List;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Toast;

import com.evsward.butler.activity.base.SFBaseActivity;
import com.evsward.butler.entities.AckNewSeatInfoList.NewSeatInfo;
import com.evsward.butler.fragment.CompetitionAdvertisementFragment;
import com.evsward.butler.fragment.CompetitionChipsFragment;
import com.evsward.butler.fragment.CompetitionPlayerFragment;
import com.evsward.butler.fragment.CompetitionProcessFragment;
import com.evsward.butler.fragment.CompetitionRewardsFragment;
import com.evsward.butler.fragment.CompetitionSeatFragment;
import com.evsward.butler.service.PrintUtil;
import com.evsward.butler.util.CommonUtil;
import com.evsward.butler.util.LogUtil;
import com.evsward.butler.R;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;

/**
 * 比赛管理
 * 
 * @Date Apr 28, 2015
 * @author liuwb.edward
 */
public class CompetitionManageActivity extends SFBaseActivity {

	private static final String[] CONTENT = new String[] { "座位", "进程", "玩家", "筹码", "奖池", "广告" };

	// nfc
	protected long decimalNFCID;
	protected NfcAdapter nfcAdapter;
	protected PendingIntent pendingIntent;
	protected IntentFilter[] mFilters;
	protected String[][] mTechLists;

	// match manage
	protected TabFragmentAdapter mAdapter;
	protected ViewPager mPager;
	protected PageIndicator mIndicator;

	public int compID, amountUnit;
	public String strCompName;
	public String strCompTime;

	public Fragment competitionPlayerFragment, competitionChipsFragment;

	public void printBurstTable(final String strMatchName, final String strTime, List<NewSeatInfo> burstTableSeatInfoList) {
		PrintUtil.printBurstTable(strMatchName, strTime, burstTableSeatInfoList);
	}

	public void printBalancePlayer(final String strMatchName, final String strTime, final String strName, final String strCardID, final int nTableID,
			final int nChairID, final Boolean bMale) {
		PrintUtil.printBalancePlayer(strMatchName, strTime, strName, strCardID, nTableID, nChairID, bMale);
	}

	public void printRewards(final String strMatchName, final String strTime, final String strName, final String strCardID, final int nTableID,
			final int nChairID, final Boolean bMale, final String strPlayerID, final int nPrize, final int nPosition) {
		PrintUtil.printChips(strMatchName, strTime, strName, strCardID, nTableID, nChairID, bMale, strPlayerID, nPrize, nPosition);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		useActionBarMenu("比赛管理");

		initNFC();
		setContentView(R.layout.activity_competition_manage);

		Bundle bundle = this.getIntent().getExtras();
		compID = bundle.getInt("compID");
		strCompName = bundle.getString("compName");
		strCompTime = bundle.getString("compTime");
		amountUnit = bundle.getInt("compAmountUnit");
		mAdapter = new TabFragmentAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	class TabFragmentAdapter extends FragmentPagerAdapter {
		private int mCount = CONTENT.length;

		public TabFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new CompetitionSeatFragment();
				break;
			case 1:
				fragment = new CompetitionProcessFragment();
				break;
			case 2:
				competitionPlayerFragment = new CompetitionPlayerFragment();
				fragment = competitionPlayerFragment;
				break;
			case 3:
				competitionChipsFragment = new CompetitionChipsFragment();
				fragment = competitionChipsFragment;
				break;
			case 4:
				fragment = new CompetitionRewardsFragment();
				break;
			case 5:
				fragment = new CompetitionAdvertisementFragment();
				break;
			default:
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length];
		}
	}

	private void initNFC() {
		// nfc
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		checkNFC();
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		ndef.addCategory("*/*");
		mFilters = new IntentFilter[] { ndef };// 过滤器
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() }, new String[] { NfcA.class.getName() } };// 允许扫描的标签类型
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
	public void onResume() {
		super.onResume();
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, mFilters, mTechLists);
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
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			decimalNFCID = processIntent(intent);
			LogUtil.i(TAG, "比赛管理 activity onNewIntent,nfc id = " + decimalNFCID);
			if (competitionChipsFragment != null) {
				((CompetitionChipsFragment) competitionChipsFragment).getChipsByNFCPlayerID(decimalNFCID);
			}
		}
	}

}
