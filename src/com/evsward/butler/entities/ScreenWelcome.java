package com.evsward.butler.entities;

public class ScreenWelcome {
	private int memID;
	private String memNickName;
	private String memName;

	public int getMemID() {
		return memID;
	}

	public void seMemID(int memID) {
		this.memID = memID;
	}

	public String getMemNickName() {
		return memNickName;
	}

	public void setMemNickName(String memNickName) {
		this.memNickName = memNickName;
	}

	public String getMemName() {
		return memName;
	}

	public void setMemName(String memName) {
		this.memName = memName;
	}

	public ScreenWelcome(int memID, String memNickName, String memName) {
		super();
		this.memID = memID;
		this.memNickName = memNickName;
		this.memName = memName;
	}
}
