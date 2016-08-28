package com.evsward.butler.entities;

/**
 * 大屏幕比赛计时信息
 * 
 * @author liubin
 * 
 */

public class ScreenCompTimeInfo {

	// 主题
	private String topic;
	// 比赛ID
	private int compID;
	// 比赛名称
	private String compName;
	// 比赛运行状态
	private int compState;
	// 比赛是否带奖励表
	private int aword;
	// 比赛暂停状态
	private int compPause;
	// 比赛运行状态文字描述
	private String compStateDesc;
	// 比赛中存活选手数量
	private int livedPlayerCount;
	// 比赛中存活选手平均筹码数量
	private int livedAvgChipCount;
	// 比赛总共报名选手数量
	private int totalRegedPlayerCount;
	// 比赛中总筹码数量
	private int totalChipCount;
	// 当前盲注信息
	private ScreenCompCurRound curRound;
	// 下一级盲注信息
	private Round nextRound;
	// 最近的休息盲注信息
	private Round nextBreakRound;
	// 比赛广告信息
	private AdvertInfo compAdvertInfo;

	public int getAword() {
		return aword;
	}

	public void setAword(int aword) {
		this.aword = aword;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getCompID() {
		return compID;
	}

	public void setCompID(int compID) {
		this.compID = compID;
	}

	public int getCompPause() {
		return compPause;
	}

	public void setCompPause(int compPause) {
		this.compPause = compPause;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public int getCompState() {
		return compState;
	}

	public void setCompState(int compState) {
		this.compState = compState;
	}

	public String getCompStateDesc() {
		return compStateDesc;
	}

	public void setCompStateDesc(String compStateDesc) {
		this.compStateDesc = compStateDesc;
	}

	public int getLivedPlayerCount() {
		return livedPlayerCount;
	}

	public void setLivedPlayerCount(int livedPlayerCount) {
		this.livedPlayerCount = livedPlayerCount;
	}

	public int getLivedAvgChipCount() {
		return livedAvgChipCount;
	}

	public void setLivedAvgChipCount(int livedAvgChipCount) {
		this.livedAvgChipCount = livedAvgChipCount;
	}

	public int getTotalRegedPlayerCount() {
		return totalRegedPlayerCount;
	}

	public void setTotalRegedPlayerCount(int totalRegedPlayerCount) {
		this.totalRegedPlayerCount = totalRegedPlayerCount;
	}

	public int getTotalChipCount() {
		return totalChipCount;
	}

	public void setTotalChipCount(int totalChipCount) {
		this.totalChipCount = totalChipCount;
	}

	public ScreenCompCurRound getCurRound() {
		return curRound;
	}

	public void setCurRound(ScreenCompCurRound curRound) {
		this.curRound = curRound;
	}

	public Round getNextRound() {
		return nextRound;
	}

	public void setNextRound(Round nextRound) {
		this.nextRound = nextRound;
	}

	public Round getNextBreakRound() {
		return nextBreakRound;
	}

	public void setNextBreakRound(Round nextBreakRound) {
		this.nextBreakRound = nextBreakRound;
	}

	public AdvertInfo getCompAdvertInfo() {
		return compAdvertInfo;
	}

	public void setCompAdvertInfo(AdvertInfo compAdvertInfo) {
		this.compAdvertInfo = compAdvertInfo;
	}
}
