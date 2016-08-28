package com.evsward.butler.entities;

import java.util.Date;

public class CompRunningPrize {

	private int ranking;
	private int compID;
	private int memID;
	private double percent;
	private int amountInt;
	private double amount;
	private int sysType;
	private int tableNO;
	private int seatNO;
	private Date createTime;
	private Date updateTime;

	private String memName;
	private String memIdentNO;

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

	public String getMemIdentNO() {
		return memIdentNO;
	}

	public void setMemIdentNO(String memIdentNO) {
		this.memIdentNO = memIdentNO;
	}

	public int getSysType() {
		return sysType;
	}

	public void setSysType(int sysType) {
		this.sysType = sysType;
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

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public int getAmountInt() {
		return amountInt;
	}

	public void setAmountInt(int amountInt) {
		this.amountInt = amountInt;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public CompRunningPrize() {
		super();
	}

	@Override
	public String toString() {
		return "CompRunningPrize [compID=" + compID + ", memID=" + memID + ", percent=" + percent + ", ranking=" + ranking + ", amountInt="
				+ amountInt + ", amount=" + amount + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
	}
}
