package com.evsward.butler.entities;

import java.util.ArrayList;

public class ReqCardTableList {

	public interface TABLESTATE {
		public static final int TABLEUSING = 1;// 被比赛占用
		public static final int TABLEIDLE = 0;// 空闲
		public static final int TABLEDISABLED = -1;// 不可用
	}

	public class CardTable {
		private int tableNO;
		private String compName;
		private int compID;
		private int tableState;
		private long uuidLong;
		private String address;
		private int sysType;

		public int getSysType() {
			return sysType;
		}

		public void setSysType(int sysType) {
			this.sysType = sysType;
		}

		public int getTableNO() {
			return tableNO;
		}

		public void setTableNO(int tableNO) {
			this.tableNO = tableNO;
		}

		public String getCompName() {
			return compName;
		}

		public void setCompName(String compName) {
			this.compName = compName;
		}

		public int getCompID() {
			return compID;
		}

		public void setCompID(int compID) {
			this.compID = compID;
		}

		public int getTableState() {
			return tableState;
		}

		public void setTableState(int tableState) {
			this.tableState = tableState;
		}

		public long getUuidLong() {
			return uuidLong;
		}

		public void setUuidLong(long uuidLong) {
			this.uuidLong = uuidLong;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public CardTable(int tableNO, int compID, int tableState, long uuidLong, String address, int sysType, String compName) {
			super();
			this.tableNO = tableNO;
			this.compID = compID;
			this.tableState = tableState;
			this.uuidLong = uuidLong;
			this.address = address;
			this.sysType = sysType;
			this.compName = compName;
		}

		public CardTable(int tableNO, int compID, int sysType, String compName) {
			super();
			this.tableNO = tableNO;
			this.compID = compID;
			this.sysType = sysType;
			this.compName = compName;
		}

		public CardTable() {
			super();
		}

		@Override
		public String toString() {
			return "CardTable [tableNO=" + tableNO + ", compID=" + compID + ", tableState=" + tableState + ", uuidLong=" + uuidLong + ", address="
					+ address + "]";
		}
	}

	private int rspCode;
	private String msg;
	private ArrayList<CardTable> cardTables;

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

	public ArrayList<CardTable> getCardTables() {
		return cardTables;
	}

	public void setCardTables(ArrayList<CardTable> cardTables) {
		this.cardTables = cardTables;
	}

	public ReqCardTableList(int rspCode, String msg, ArrayList<CardTable> cardTables) {
		super();
		this.rspCode = rspCode;
		this.msg = msg;
		this.cardTables = cardTables;
	}
}
