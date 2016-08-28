package com.evsward.butler.entities;

public class TVList {
	private int rspCode;
	private String msg;
	private Screen[] screens;
	
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

	public Screen[] getScreens() {
		return screens;
	}

	public void setScreens(Screen[] screens) {
		this.screens = screens;
	}

	public TVList(int rspCode, String msg, Screen[] screens) {
		super();
		this.rspCode = rspCode;
		this.screens = screens;
		this.msg = msg;
	}

}
