package com.evsward.butler.entities;

import java.util.ArrayList;

public class AckNewSeatInfoList {
	private int rspCode;
	private String msg;
	private BurstTableRes burstTableRes;

	public AckNewSeatInfoList(int rspCode, String msg, BurstTableRes burstTableRes) {
		super();
		this.rspCode = rspCode;
		this.msg = msg;
		this.burstTableRes = burstTableRes;
	}

	public AckNewSeatInfoList() {
		super();
	}

	public BurstTableRes getBurstTableRes() {
		return burstTableRes;
	}

	public void setBurstTableRes(BurstTableRes burstTableRes) {
		this.burstTableRes = burstTableRes;
	}

	public int getRspCode() {
		return rspCode;
	}

	public void setRspCode(int rspCode) {
		this.rspCode = rspCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public class BurstTableRes {
		private int rspCode;
		private int compID;
		private ArrayList<NewSeatInfo> newSeatInfoList;

		public ArrayList<NewSeatInfo> getNewSeatInfoList() {
			return newSeatInfoList;
		}

		public void setNewSeatInfoList(ArrayList<NewSeatInfo> newSeatInfoList) {
			this.newSeatInfoList = newSeatInfoList;
		}

		public int getRspCode() {
			return rspCode;
		}

		public void setRspCode(int rspCode) {
			this.rspCode = rspCode;
		}

		public int getCompID() {
			return compID;
		}

		public void setCompID(int compID) {
			this.compID = compID;
		}

		public BurstTableRes(int rspCode, int compID, ArrayList<NewSeatInfo> newSeatInfoList) {
			super();
			this.rspCode = rspCode;
			this.compID = compID;
			this.newSeatInfoList = newSeatInfoList;
		}

	}

	public class NewSeatInfo {
		private int memID;
		private String cardNO;
		private String memName;
		private int tableNO;
		private int seatNO;
		private int memSex;

		public NewSeatInfo(int memID, String cardNO, String memName, int tableNO, int seatNO, int memSex) {
			super();
			this.memID = memID;
			this.cardNO = cardNO;
			this.memName = memName;
			this.tableNO = tableNO;
			this.seatNO = seatNO;
			this.memSex = memSex;
		}
		
		public String getCardNO() {
			return cardNO;
		}

		public void setCardNO(String cardNO) {
			this.cardNO = cardNO;
		}

		public int getMemID() {
			return memID;
		}

		public void setMemID(int memID) {
			this.memID = memID;
		}

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

		public int getTableNO() {
			return tableNO;
		}

		public void setSeatNO(int seatNO) {
			this.seatNO = seatNO;
		}

		public int getSeatNO() {
			return seatNO;
		}

		public void setTableNO(int tableNO) {
			this.tableNO = tableNO;
		}
	}
}
