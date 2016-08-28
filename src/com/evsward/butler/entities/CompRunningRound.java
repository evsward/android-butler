package com.evsward.butler.entities;

public class CompRunningRound {

	private int compID;
	private int roundTempID;
	private int curBigBlind;
	private int curSmallBlind;
	private int curRank;
	private int curBeforeChip;
	private int curType;
	private int stepLen;
	private long curStartTime;
	private long reStartTime;// 比赛从暂停中恢复的时间
	private int sysType;

	public long getReStartTime() {
		return reStartTime;
	}

	public void setReStartTime(long reStartTime) {
		this.reStartTime = reStartTime;
	}

	public int getRoundTempID() {
		return roundTempID;
	}

	public void setRoundTempID(int roundTempID) {
		this.roundTempID = roundTempID;
	}

	public int getStepLen() {
		return stepLen;
	}

	public void setStepLen(int stepLen) {
		this.stepLen = stepLen;
	}

	public int getSysType() {
		return sysType;
	}

	public void setSysType(int sysType) {
		this.sysType = sysType;
	}

	public int getCompID() {
		return compID;
	}

	public void setCompID(int compID) {
		this.compID = compID;
	}

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

	public int getCurBeforeChip() {
		return curBeforeChip;
	}

	public void setCurBeforeChip(int curBeforeChip) {
		this.curBeforeChip = curBeforeChip;
	}

	public int getCurRank() {
		return curRank;
	}

	public void setCurRank(int curRank) {
		this.curRank = curRank;
	}

	public int getCurType() {
		return curType;
	}

	public void setCurType(int curType) {
		this.curType = curType;
	}

	public long getCurStartTime() {
		return curStartTime;
	}

	public void setCurStartTime(long curStartTime) {
		this.curStartTime = curStartTime;
	}

	public CompRunningRound() {
		super();
	}

	public CompRunningRound(int roundTempID, int compID, int curBigBlind, int curSmallBlind, int curRank, int curBeforeChip, int curType,
			int stepLen, long curStartTime, long reStartTime, int sysType) {
		super();
		this.roundTempID = roundTempID;
		this.compID = compID;
		this.curBigBlind = curBigBlind;
		this.curSmallBlind = curSmallBlind;
		this.curRank = curRank;
		this.curBeforeChip = curBeforeChip;
		this.curType = curType;
		this.stepLen = stepLen;
		this.curStartTime = curStartTime;
		this.reStartTime = reStartTime;
		this.sysType = sysType;
	}

	@Override
	public String toString() {
		return "CompRunningRound [compID=" + compID + ", roundTempID=" + roundTempID + ", curBigBlind=" + curBigBlind + ", curSmallBlind="
				+ curSmallBlind + ", curRank=" + curRank + ", curBeforeChip=" + curBeforeChip + ", curType=" + curType + ", stepLen=" + stepLen
				+ ", curStartTime=" + curStartTime + ", reStartTime=" + reStartTime + ", sysType=" + sysType + "]";
	}
}
