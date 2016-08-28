package com.evsward.butler.entities.prize;

public class CompManPrizeInfo {
	private int ranking;
	private int compID;
	private int memID;
	private int tableNO;
	private int seatNO;
	private float percent;
	private int amountInt;
	private String memName;
	private String memIdentNO;
	private int memSex;

	public int getMemSex() {
		return memSex;
	}

	public void setMemSex(int memSex) {
		this.memSex = memSex;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
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

	public float getPercent() {
		return percent;
	}

	public void setPercent(float percent) {
		this.percent = percent;
	}

	public int getAmountInt() {
		return amountInt;
	}

	public void setAmountInt(int amountInt) {
		this.amountInt = amountInt;
	}

	public String getMemName() {
		return memName;
	}

	public void setMemName(String memName) {
		this.memName = memName;
	}

	public String getMemIdentNO() {
		return memIdentNO;
	}

	public void setMemIdentNO(String memIdentNO) {
		this.memIdentNO = memIdentNO;
	}

}
