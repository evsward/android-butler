package com.evsward.butler.entities;

import java.util.ArrayList;

import com.evsward.butler.entities.CompetitionInfoDayList.CompetitionInfoDay.CompetitionInfo;

public class CompAdvanManage {
	private int rspCode;
	private String msg;
	private ArrayList<CompetitionInfo> origList;
	private ArrayList<CompetitionInfo> destList;

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

	public ArrayList<CompetitionInfo> getOrigList() {
		return origList;
	}

	public void setOrigList(ArrayList<CompetitionInfo> origList) {
		this.origList = origList;
	}

	public ArrayList<CompetitionInfo> getDestList() {
		return destList;
	}

	public void setDestList(ArrayList<CompetitionInfo> destList) {
		this.destList = destList;
	}

	public CompAdvanManage(int rspCode, String msg, ArrayList<CompetitionInfo> origList, ArrayList<CompetitionInfo> destList) {
		super();
		this.rspCode = rspCode;
		this.msg = msg;
		this.origList = origList;
		this.destList = destList;
	}
}
