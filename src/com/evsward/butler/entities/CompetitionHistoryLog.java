package com.evsward.butler.entities;

public class CompetitionHistoryLog {
	private int logID;
	private int compID;
	private int memID;
	private String memName;
	private String cardNO;
	private int memSex;// 1是男，0是女
	private int oldTableNO;
	private int oldSeatNO;
	private String empUuid;
	private int newTableNO;
	private int newSeatNO;
	private int opType;
	private long createTime;
	private int sysType;

	public int getMemSex() {
		return memSex;
	}

	public void setMemSex(int memSex) {
		this.memSex = memSex;
	}

	public String getMemName() {
		return memName;
	}

	public void setMemName(String memName) {
		this.memName = memName;
	}

	public String getCardNO() {
		return cardNO;
	}

	public void setCardNO(String cardNO) {
		this.cardNO = cardNO;
	}

	public int getOpType() {
		return opType;
	}

	public void setOpType(int opType) {
		this.opType = opType;
	}

	public int getSysType() {
		return sysType;
	}

	public void setSysType(int sysType) {
		this.sysType = sysType;
	}

	public int getLogID() {
		return logID;
	}

	public void setLogID(int logID) {
		this.logID = logID;
	}

	public int getCompID() {
		return compID;
	}

	public void setCompID(int compID) {
		this.compID = compID;
	}

	public int getMemID() {
		return memID;
	}

	public void setMemID(int memID) {
		this.memID = memID;
	}

	public int getOldTableNO() {
		return oldTableNO;
	}

	public void setOldTableNO(int oldTableNO) {
		this.oldTableNO = oldTableNO;
	}

	public int getOldSeatNO() {
		return oldSeatNO;
	}

	public void setOldSeatNO(int oldSeatNO) {
		this.oldSeatNO = oldSeatNO;
	}

	public String getEmpUuid() {
		return empUuid;
	}

	public void setEmpUuid(String empUuid) {
		this.empUuid = empUuid;
	}

	public int getNewTableNO() {
		return newTableNO;
	}

	public void setNewTableNO(int newTableNO) {
		this.newTableNO = newTableNO;
	}

	public int getNewSeatNO() {
		return newSeatNO;
	}

	public void setNewSeatNO(int newSeatNO) {
		this.newSeatNO = newSeatNO;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
