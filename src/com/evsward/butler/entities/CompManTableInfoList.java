package com.evsward.butler.entities;

import java.util.ArrayList;
import java.util.List;

public class CompManTableInfoList {

	public static final class TABLEBUTOPER {
		public static final int LOCK = 0;// 开启按钮（牌桌空闲，可以被开启）
		public static final int RELEASE = 1;// 释放按钮（牌桌被比赛占用，桌上没有选手，可以被释放）
		public static final int BURSTTABLE = 2;// 爆桌按钮（牌桌可以被爆掉）
		public static final int DEFAULT = -1;
	}

	public class CompManTableInfo {

		private int tableNO;
		private int tableState;
		private int tableType;

		private int button = TABLEBUTOPER.DEFAULT;
		private List<CompManSeatInfo> memSeatInfoList = new ArrayList<CompManSeatInfo>();

		public CompManTableInfo() {
			super();
		}

		public CompManTableInfo(int tableNO, int tableState, int tableType) {
			super();
			this.tableNO = tableNO;
			this.tableState = tableState;
			this.tableType = tableType;
		}

		public int getTableType() {
			return tableType;
		}

		public void setTableType(int tableType) {
			this.tableType = tableType;
		}

		public int getTableNO() {
			return tableNO;
		}

		public void setTableNO(int tableNO) {
			this.tableNO = tableNO;
		}

		public int getTableState() {
			return tableState;
		}

		public void setTableState(int tableState) {
			this.tableState = tableState;
		}

		public int getButton() {
			return button;
		}

		public void setButton(int button) {
			this.button = button;
		}

		public List<CompManSeatInfo> getMemSeatInfoList() {
			return memSeatInfoList;
		}
	}

	private int rspCode;
	private String msg;
	private int compID;
	private List<CompManTableInfo> compTableInfos;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCompID() {
		return compID;
	}

	public void setCompID(int compID) {
		this.compID = compID;
	}

	public int getRspCode() {
		return rspCode;
	}

	public void setRspCode(int rspCode) {
		this.rspCode = rspCode;
	}

	public List<CompManTableInfo> getCompTableInfos() {
		return compTableInfos;
	}

	public void setCompTableInfos(List<CompManTableInfo> compTableInfos) {
		this.compTableInfos = compTableInfos;
	}

	public CompManTableInfoList(int rspCode, String msg, List<CompManTableInfo> compTableInfos, int compID) {
		super();
		this.compTableInfos = compTableInfos;
		this.rspCode = rspCode;
		this.msg = msg;
		this.compID = compID;
	}
}
