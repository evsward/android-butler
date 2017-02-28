package com.evsward.butler.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于比赛列表的实体类
 * 
 * @Date May 22, 2015
 * @author liuwb.edward
 */
public class CompetitionInfoDayList {

	private int rspCode;
	private String msg;
	private ArrayList<CompetitionInfoDay> padCompList;

	public class CompetitionInfoDay {
		private String day_week;
		private ArrayList<CompetitionInfo> compListInfo;

		public class CompetitionInfo {

			private String compName;
			private String time;
			/**
			 * -1、已删除；0、未开放；1、正在报名，比赛未开赛 ；2、正在报名，比赛进行中
			 * ；3、停止报名，比赛未开始；4、停止报名，比赛进行中；5、比赛已结束
			 */
			private int compState;
			private String compStateShow;
			private int regFee;
			private int serviceFee;
			private int beginChip;
			private int beforeChip;
			private int curRank;
			private int reEntry;
			private Integer compID;
			private int amountUnit;

			public void setCompStateShow(String compStateShow) {
				this.compStateShow = compStateShow;
			}

			public String getCompStateShow() {
				return compStateShow;
			}

			public void setAmountUnit(int amountUnit) {
				this.amountUnit = amountUnit;
			}

			public int getAmountUnit() {
				return amountUnit;
			}

			public String getCompName() {
				return compName;
			}

			public void setCompName(String compName) {
				this.compName = compName;
			}

			public String getTime() {
				return time;
			}

			public void setTime(String time) {
				this.time = time;
			}

			public int getCompState() {
				return compState;
			}

			public void setCompState(int compState) {
				this.compState = compState;
			}

			public int getRegFee() {
				return regFee;
			}

			public void setRegFee(int regFee) {
				this.regFee = regFee;
			}

			public int getServiceFee() {
				return serviceFee;
			}

			public void setServiceFee(int serviceFee) {
				this.serviceFee = serviceFee;
			}

			public int getBeginChip() {
				return beginChip;
			}

			public void setBeginChip(int beginChip) {
				this.beginChip = beginChip;
			}

			public int getBeforeChip() {
				return beforeChip;
			}

			public void setBeforeChip(int beforeChip) {
				this.beforeChip = beforeChip;
			}

			public int getCurRank() {
				return curRank;
			}

			public void setCurRank(int curRank) {
				this.curRank = curRank;
			}

			public int getReEntry() {
				return reEntry;
			}

			public void setReEntry(int reEntry) {
				this.reEntry = reEntry;
			}

			public Integer getCompID() {
				return compID;
			}

			public void setCompID(Integer compID) {
				this.compID = compID;
			}

			public CompetitionInfo() {
				super();
			}
		}

		public String getDay_week() {
			return day_week;
		}

		public void setDay_week(String day_week) {
			this.day_week = day_week;
		}

		public List<CompetitionInfo> getCompListInfo() {
			return compListInfo;
		}

		public void setCompListInfo(ArrayList<CompetitionInfo> compListInfo) {
			this.compListInfo = compListInfo;
		}

		public CompetitionInfoDay(String day_week, ArrayList<CompetitionInfo> compListInfo) {
			super();
			this.day_week = day_week;
			this.compListInfo = compListInfo;
		}
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getRspCode() {
		return rspCode;
	}

	public void setRspCode(int rspCode) {
		this.rspCode = rspCode;
	}

	public List<CompetitionInfoDay> getPadCompList() {
		return padCompList;
	}

	public void setPadCompList(ArrayList<CompetitionInfoDay> padCompList) {
		this.padCompList = padCompList;
	}

}
