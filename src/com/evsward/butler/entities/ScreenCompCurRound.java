package com.evsward.butler.entities;

/**
 * 大屏幕计时信息用的当前盲注的信息
 * 
 * @author liubin
 * 
 */

public class ScreenCompCurRound {

	private int curBigBlind;
	private int curSmallBlind;
	private int curRank;
	private int curBeforeChip;
	private int curType;// 1、计时；0、休息
	private int restTime;// 倒计时，单位秒

	public int getCurBigBlind() {
		return curBigBlind;
	}

	public void setCurBigBlind(int curBigBlind) {
		this.curBigBlind = curBigBlind;
	}

	public int getCurSmallBlind() {
		return curSmallBlind;
	}

	public void setCurSmallBlind(int curSmallBlind) {
		this.curSmallBlind = curSmallBlind;
	}

	public int getCurRank() {
		return curRank;
	}

	public void setCurRank(int curRank) {
		this.curRank = curRank;
	}

	public int getCurBeforeChip() {
		return curBeforeChip;
	}

	public void setCurBeforeChip(int curBeforeChip) {
		this.curBeforeChip = curBeforeChip;
	}

	public int getCurType() {
		return curType;
	}

	public void setCurType(int curType) {
		this.curType = curType;
	}

	public int getRestTime() {
		return restTime;
	}

	public void setRestTime(int restTime) {
		this.restTime = restTime;
	}
}
