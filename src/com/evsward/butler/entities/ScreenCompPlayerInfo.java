package com.evsward.butler.entities;

/**
 * 大屏幕选手列表--选手信息
 * 
 * @author liubin
 * 
 */

public class ScreenCompPlayerInfo {

	// 序号
	private int serialNO;
	// 卡号
	private String cardNO;
	// 选手名
	private String memName;
	// 桌号
	private int tableNO;
	// 座位号
	private int seatNO;
	// 选手当前筹码
	private int chip;

	public int getSerialNO() {
		return serialNO;
	}

	public void setSerialNO(int serialNO) {
		this.serialNO = serialNO;
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

	public int getChip() {
		return chip;
	}

	public void setChip(int chip) {
		this.chip = chip;
	}

	public ScreenCompPlayerInfo(int serialNO, String cardNO, String memName, int tableNO, int seatNO, int chip) {
		super();
		this.serialNO = serialNO;
		this.cardNO = cardNO;
		this.memName = memName;
		this.tableNO = tableNO;
		this.seatNO = seatNO;
		this.chip = chip;
	}

	public ScreenCompPlayerInfo() {
		super();
	}
}
