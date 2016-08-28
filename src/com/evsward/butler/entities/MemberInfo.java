package com.evsward.butler.entities;


/**
 * 用于接收会员信息的实体类
 * 
 * @Date Apr 14, 2015
 * @author liuwb.edward
 */
public class MemberInfo {
	private int memID;
	private long uuidLong;
	private String cardNO;
	private String memName;
	private int memState;
	private String memImage;
	private String memMobile;
	private int memSex;
	private String memIdentNO;
	private int chip;
	private int balance;
	private int sysType;
	private String createTime;
	private String updateTime;
	private String delTime;
	// 换卡时，原nfc卡、卡号
	private String oldCardNO;
	private long oldUuidLong;

	@Override
	public String toString() {
		return "\n卡号："+cardNO+" 姓名："+memName+" 性别："+(memSex==1?"男":"女")+"\n手机号："+memMobile+"\n证件号："+memIdentNO+"\n";
	}

	public int getMemID() {
		return memID;
	}

	public void setMemID(int memID) {
		this.memID = memID;
	}

	public long getUuidLong() {
		return uuidLong;
	}

	public void setUuidLong(long uuidLong) {
		this.uuidLong = uuidLong;
	}

	public String getCardNO() {
		return cardNO;
	}

	public void setCardNO(String cardNO) {
		this.cardNO = cardNO;
	}

	public String getMemName() {
		return memName;
	}

	public void setMemName(String memName) {
		this.memName = memName;
	}

	public int getMemState() {
		return memState;
	}

	public void setMemState(int memState) {
		this.memState = memState;
	}

	public String getMemImage() {
		return memImage;
	}

	public void setMemImage(String memImage) {
		this.memImage = memImage;
	}

	public String getMemMobile() {
		return memMobile;
	}

	public void setMemMobile(String memMobile) {
		this.memMobile = memMobile;
	}

	public int getMemSex() {
		return memSex;
	}

	public void setMemSex(int memSex) {
		this.memSex = memSex;
	}

	public String getMemIdentNO() {
		return memIdentNO;
	}

	public void setMemIdentNO(String memIdentNO) {
		this.memIdentNO = memIdentNO;
	}

	public int getChip() {
		return chip;
	}

	public void setChip(int chip) {
		this.chip = chip;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getSysType() {
		return sysType;
	}

	public void setSysType(int sysType) {
		this.sysType = sysType;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getDelTime() {
		return delTime;
	}

	public void setDelTime(String delTime) {
		this.delTime = delTime;
	}

	public String getOldCardNO() {
		return oldCardNO;
	}

	public void setOldCardNO(String oldCardNO) {
		this.oldCardNO = oldCardNO;
	}

	public long getOldUuidLong() {
		return oldUuidLong;
	}

	public void setOldUuidLong(long oldUuidLong) {
		this.oldUuidLong = oldUuidLong;
	}

}
