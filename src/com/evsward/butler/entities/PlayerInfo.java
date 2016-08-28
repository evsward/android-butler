package com.evsward.butler.entities;

import java.util.Date;

public class PlayerInfo {
	private int id;
	private int memID;
	private String cardNO;
	private long nfcID;
	private String memMobile;
	private int compID;
	private int mcState;
	private int tableNO;
	private int seatNO;
	private String memName;
	private int sysType;
	private Date regTime;
	private Date outTime;
	private int chip;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMemID() {
		return memID;
	}

	public void setMemID(int memID) {
		this.memID = memID;
	}

	public String getCardNO() {
		return cardNO;
	}

	public void setCardNO(String cardNO) {
		this.cardNO = cardNO;
	}

	public long getNfcID() {
		return nfcID;
	}

	public void setNfcID(long nfcID) {
		this.nfcID = nfcID;
	}

	public String getMemMobile() {
		return memMobile;
	}

	public void setMemMobile(String memMobile) {
		this.memMobile = memMobile;
	}

	public int getCompID() {
		return compID;
	}

	public void setCompID(int compID) {
		this.compID = compID;
	}

	public int getMcState() {
		return mcState;
	}

	public void setMcState(int mcState) {
		this.mcState = mcState;
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

	public String getMemName() {
		return memName;
	}

	public void setMemName(String memName) {
		this.memName = memName;
	}

	public int getSysType() {
		return sysType;
	}

	public void setSysType(int sysType) {
		this.sysType = sysType;
	}

	public Date getRegTime() {
		return regTime;
	}

	public void setRegTime(Date regTime) {
		this.regTime = regTime;
	}

	public Date getOutTime() {
		return outTime;
	}

	public void setOutTime(Date outTime) {
		this.outTime = outTime;
	}

	public int getChip() {
		return chip;
	}

	public void setChip(int chip) {
		this.chip = chip;
	}

}
