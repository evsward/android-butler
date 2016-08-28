package com.evsward.butler.entities;

/**
 * 大屏幕比赛计时信息里的奖励信息
 * 
 * @author liubin
 * 
 */

public class ScreenPrizeInfo {

	private int ranking;
	private int awordMoneyInt;
	private int unit = 1;// 单位，万元

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public int getAwordMoneyInt() {
		return awordMoneyInt;
	}

	public void setAwordMoneyInt(int awordMoneyInt) {
		this.awordMoneyInt = awordMoneyInt;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public ScreenPrizeInfo(int ranking, int awordMoneyInt) {
		super();
		this.ranking = ranking;
		this.awordMoneyInt = awordMoneyInt;
		// this.unit = unit;
	}
}
