package com.evsward.butler.entities;

/**
 * 比赛进程实体
 * 
 * @Date Jun 03, 2015
 * @author liuwb.edward
 * @note 以下时间单位为：s
 */
public class CompetitionProgress {
	private int compPause;
	private int roundType;
	private int initedStepLen;
	private int beforeChip;
	private int bigBlind;
	private int curRank;
	private int totalRegedPlayerEdit;
	private int totalRegedPlayer;
	private int restTime;
	private int smallBlind;
	private int compID;
	private int averChip;

	public int getCompPause() {
		return compPause;
	}

	public void setCompPause(int compPause) {
		this.compPause = compPause;
	}

	public int getRoundType() {
		return roundType;
	}

	public void setRoundType(int roundType) {
		this.roundType = roundType;
	}

	public int getInitedStepLen() {
		return initedStepLen;
	}

	public void setInitedStepLen(int initedStepLen) {
		this.initedStepLen = initedStepLen;
	}

	public int getBeforeChip() {
		return beforeChip;
	}

	public void setBeforeChip(int beforeChip) {
		this.beforeChip = beforeChip;
	}

	public int getBigBlind() {
		return bigBlind;
	}

	public void setBigBlind(int bigBlind) {
		this.bigBlind = bigBlind;
	}

	public int getCurRank() {
		return curRank;
	}

	public void setCurRank(int curRank) {
		this.curRank = curRank;
	}

	public int getTotalRegedPlayerEdit() {
		return totalRegedPlayerEdit;
	}

	public void setTotalRegedPlayerEdit(int totalRegedPlayerEdit) {
		this.totalRegedPlayerEdit = totalRegedPlayerEdit;
	}

	public int getTotalRegedPlayer() {
		return totalRegedPlayer;
	}

	public void setTotalRegedPlayer(int totalRegedPlayer) {
		this.totalRegedPlayer = totalRegedPlayer;
	}

	public int getRestTime() {
		return restTime;
	}

	public void setRestTime(int restTime) {
		this.restTime = restTime;
	}

	public int getSmallBlind() {
		return smallBlind;
	}

	public void setSmallBlind(int smallBlind) {
		this.smallBlind = smallBlind;
	}

	public int getCompID() {
		return compID;
	}

	public void setCompID(int compID) {
		this.compID = compID;
	}

	public int getAverChip() {
		return averChip;
	}

	public void setAverChip(int averChip) {
		this.averChip = averChip;
	}

}