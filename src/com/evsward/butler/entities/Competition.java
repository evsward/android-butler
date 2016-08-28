package com.evsward.butler.entities;

import java.util.List;

/**
 * 比赛实体
 * 
 * @Date May 11, 2015
 * @author liuwb.edward
 */
public class Competition {
	public interface COMPSTATE {
		public static int CLOSE = 0;
		public static int SIGNNOTSTART = 1;
		public static int SIGNSTART = 2;
		public static int ENDSIGNNOTSTART = 3;
		public static int ENDSIGNSTART = 4;
		public static int FINISH = 5;
		public static int DELETED = -1;
	}

	public interface COMPPAUSE {
		public static int START = 0;
		public static int PAUSE = 2;
	}

	private int compID;
	private String compName;
	private int compState;
	private String regFee;
	private String mcStateShow;
	private String compStateShow;
	private String dateNoTime;
	private String mcState;
	private String regCount;
	private String regBut;
	private String time;
	private List<RunCompSeatInfoList> runCompSeatInfoList;

	@Override
	public String toString() {
		return compName;
	}

	public class RunCompSeatInfoList {
		private int tableNO;
		private int seatNO;

		public int getTableNO() {
			return tableNO;
		}

		public void setTableNO(int tableNO) {
			this.tableNO = tableNO;
		}

		public int getSeatNO() {
			return seatNO;
		}

		public void setSeatNO(int seatNO) {
			this.seatNO = seatNO;
		}

		@Override
		public String toString() {
			return tableNO + "/" + seatNO;
		}
	}

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

	public String getRegFee() {
		return regFee;
	}

	public void setRegFee(String regFee) {
		this.regFee = regFee;
	}

	public String getMcStateShow() {
		return mcStateShow;
	}

	public void setMcStateShow(String mcStateShow) {
		this.mcStateShow = mcStateShow;
	}

	public String getCompStateShow() {
		return compStateShow;
	}

	public void setCompStateShow(String compStateShow) {
		this.compStateShow = compStateShow;
	}

	public String getDateNoTime() {
		return dateNoTime;
	}

	public void setDateNoTime(String dateNoTime) {
		this.dateNoTime = dateNoTime;
	}

	public String getMcState() {
		return mcState;
	}

	public void setMcState(String mcState) {
		this.mcState = mcState;
	}

	public String getRegCount() {
		return regCount;
	}

	public void setRegCount(String regCount) {
		this.regCount = regCount;
	}

	public String getRegBut() {
		return regBut;
	}

	public void setRegBut(String regBut) {
		this.regBut = regBut;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<RunCompSeatInfoList> getRunCompSeatInfoList() {
		return runCompSeatInfoList;
	}

	public void setRunCompSeatInfoList(List<RunCompSeatInfoList> runCompSeatInfoList) {
		this.runCompSeatInfoList = runCompSeatInfoList;
	}

}