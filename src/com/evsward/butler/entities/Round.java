package com.evsward.butler.entities;

public class Round {

	public interface ROUNDTYPE {
		public static final int TIME = 1;// 计时
		public static final int REST = 0;// 休息
	}

	private int blindID;
	private int roundTempID;
	private int roundState;
	private int bigBlind;
	private int smallBlind;
	private int stepLen;// 单位秒
	private int roundType;
	private int roundrank;
	private int beforeChip;
	private int sysType;

	public int getSysType() {
		return sysType;
	}

	public void setSysType(int sysType) {
		this.sysType = sysType;
	}

	public int getBlindID() {
		return blindID;
	}

	public void setBlindID(int blindID) {
		this.blindID = blindID;
	}

	public int getRoundTempID() {
		return roundTempID;
	}

	public void setRoundTempID(int roundTempID) {
		this.roundTempID = roundTempID;
	}

	public int getRoundState() {
		return roundState;
	}

	public void setRoundState(int roundState) {
		this.roundState = roundState;
	}

	public int getBigBlind() {
		return bigBlind;
	}

	public void setBigBlind(int bigBlind) {
		this.bigBlind = bigBlind;
	}

	public int getSmallBlind() {
		return smallBlind;
	}

	public void setSmallBlind(int smallBlind) {
		this.smallBlind = smallBlind;
	}

	public int getStepLen() {
		return stepLen;
	}

	public void setStepLen(int stepLen) {
		this.stepLen = stepLen;
	}

	public int getRoundType() {
		return roundType;
	}

	public void setRoundType(int roundType) {
		this.roundType = roundType;
	}

	public int getRoundrank() {
		return roundrank;
	}

	public void setRoundrank(int roundrank) {
		this.roundrank = roundrank;
	}

	public int getBeforeChip() {
		return beforeChip;
	}

	public void setBeforeChip(int beforeChip) {
		this.beforeChip = beforeChip;
	}

	public Round(int blindID, int roundTempID, int roundState, int bigBlind, int smallBlind, int stepLen, int roundType, int roundrank, int beforeChip) {
		super();
		this.blindID = blindID;
		this.roundTempID = roundTempID;
		this.roundState = roundState;
		this.bigBlind = bigBlind;
		this.smallBlind = smallBlind;
		this.stepLen = stepLen;
		this.roundType = roundType;
		this.roundrank = roundrank;
		this.beforeChip = beforeChip;
	}

	public Round() {
		super();
	}

	@Override
	public String toString() {
		return "Round [blindID=" + blindID + ", roundTempID=" + roundTempID + ", roundState=" + roundState + ", bigBlind=" + bigBlind
				+ ", smallBlind=" + smallBlind + ", stepLen=" + stepLen + ", roundType=" + roundType + ", roundrank=" + roundrank + ", beforeChip="
				+ beforeChip + "]";
	}

}
