package com.evsward.butler.entities;

/**
 * 大屏幕显示比赛列表--比赛信息
 * 
 * @author liubin
 * 
 */

public class ScreenCompInfo {
	public interface COMPSTATE {
		/** 比赛已删除 */
		public static final int STATE_DEL = -1;
		public static final String STATE_DELSHOW = "已删除";
		/** 未开放 */
		public static final int STATE_NOREG = 0;
		public static final String STATE_NOREGSHOW = "未开放";
		/** 正在报名，比赛未开赛 */
		public static final int STATE_REGING_NOBEGIN = 1;
		public static final String STATE_REGING_NOBEGINSHOW = "正在报名-比赛未开赛";
		/** 正在报名，比赛进行中 */
		public static final int STATE_REGING_BEGINED = 2;
		public static final String STATE_REGING_BEGINEDSHOW = "正在报名-比赛进行中";
		/** 停止报名，比赛未开始 */
		public static final int STATE_REGEND_NOBEGIN = 3;
		public static final String STATE_REGEND_NOBEGINSHOW = "停止报名-比赛未开始";
		/** 停止报名，比赛进行中 */
		public static final int STATE_RUNNING = 4;
		public static final String STATE_RUNNINGSHOW = "停止报名-比赛进行中";
		/** 比赛已结束 */
		public static final int STATE_END = 5;
		public static final String STATE_ENDSHOW = "已结束";
	}

	// 比赛ID
	private int compID;
	// 比赛名称
	private String compName;
	// 比赛运行状态
	private int compState;
	// 比赛运行状态文字描述
	private String compStateDesc;
	// 比赛初始筹码
	private int beginChip;
	// 比赛开始日期：yyyy-MM-dd
	private String compStartDateStr;
	// 比赛开始时间：hh:mm
	private String compStartTime;
	//
	private int totalRegedPlayerCount;
	// 比赛当前运行盲注级别
	private int curRoundRank;

	public int getCompID() {
		return compID;
	}

	public void setCompID(int compID) {
		this.compID = compID;
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
		switch (compState) {
		case COMPSTATE.STATE_NOREG:
			this.compStateDesc = COMPSTATE.STATE_NOREGSHOW;
			break;
		case COMPSTATE.STATE_REGING_NOBEGIN:
			this.compStateDesc = COMPSTATE.STATE_REGING_NOBEGINSHOW;
			break;
		case COMPSTATE.STATE_REGING_BEGINED:
			this.compStateDesc = COMPSTATE.STATE_REGING_BEGINEDSHOW;
			break;
		case COMPSTATE.STATE_REGEND_NOBEGIN:
			this.compStateDesc = COMPSTATE.STATE_REGEND_NOBEGINSHOW;
			break;
		case COMPSTATE.STATE_RUNNING:
			this.compStateDesc = COMPSTATE.STATE_RUNNINGSHOW;
			break;
		default:
			this.compStateDesc = COMPSTATE.STATE_ENDSHOW;
		}
		return compStateDesc;
	}

	public int getBeginChip() {
		return beginChip;
	}

	public void setBeginChip(int beginChip) {
		this.beginChip = beginChip;
	}

	public String getCompStartDateStr() {
		return compStartDateStr;
	}

	public void setCompStartDateStr(String compStartDateStr) {
		this.compStartDateStr = compStartDateStr;
	}

	public String getCompStartTime() {
		return compStartTime;
	}

	public void setCompStartTime(String compStartTime) {
		this.compStartTime = compStartTime;
	}

	public int getTotalRegedPlayerCount() {
		return totalRegedPlayerCount;
	}

	public void setTotalRegedPlayerCount(int totalRegedPlayerCount) {
		this.totalRegedPlayerCount = totalRegedPlayerCount;
	}

	public int getCurRoundRank() {
		return curRoundRank;
	}

	public void setCurRoundRank(int curRoundRank) {
		this.curRoundRank = curRoundRank;
	}
}
