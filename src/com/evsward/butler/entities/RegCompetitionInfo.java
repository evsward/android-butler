package com.evsward.butler.entities;

/**
 * 会员报名成功以后的响应信息
 * 
 * @Date May 7, 2015
 * @author liuwb.edward
 */
public class RegCompetitionInfo {
	private int memID;
	private int compID;
	private String startTime;
	private int tableNO;
	private int seatNO;

	public int getMemID() {
		return memID;
	}

	public void setMemID(int memID) {
		this.memID = memID;
	}

	public int getCompID() {
		return compID;
	}

	public void setCompID(int compID) {
		this.compID = compID;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

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

}
