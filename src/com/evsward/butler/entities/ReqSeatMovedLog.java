package com.evsward.butler.entities;

import java.util.ArrayList;
import java.util.List;

public class ReqSeatMovedLog {

	private int rspCode;
	private String msg;
	private ArrayList<BurstMovedLog> burstLogList = new ArrayList<BurstMovedLog>();
	private List<CompetitionHistoryLog> balanceLogList = new ArrayList<CompetitionHistoryLog>();

	public enum OpType {
		BURST("爆桌", 1), BALANCE("平衡", 2);
		private String name;
		private int index;

		private OpType(String name, int index) {
			this.name = name;
			this.index = index;
		}

		public static String getName(int index) {
			for (OpType c : OpType.values()) {
				if (c.getIndex() == index) {
					return c.name;
				}
			}
			return null;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

	public class BurstMovedLog {

		private int tableNO;
		private int opType;

		private List<CompetitionHistoryLog> logList = new ArrayList<CompetitionHistoryLog>();

		public int getTableNO() {
			return tableNO;
		}

		public void setTableNO(int tableNO) {
			this.tableNO = tableNO;
		}

		public int getOpType() {
			return opType;
		}

		public void setOpType(int opType) {
			this.opType = opType;
		}

		public List<CompetitionHistoryLog> getLogList() {
			return logList;
		}
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

	public List<CompetitionHistoryLog> getBalanceLogList() {
		return balanceLogList;
	}

	public void setBalanceLogList(List<CompetitionHistoryLog> balanceLogList) {
		this.balanceLogList = balanceLogList;
	}

	public ArrayList<BurstMovedLog> getBurstLogList() {
		return burstLogList;
	}

	public void setBurstLogList(ArrayList<BurstMovedLog> burstLogList) {
		this.burstLogList = burstLogList;
	}

}
