package com.evsward.butler.entities;

import java.util.Date;

public class Advert {
	private int advertID;
	private String advertName;
	private String path;
	private int sysType;
	private Date createTime;

	public String getAdvertName() {
		return advertName;
	}

	public void setAdvertName(String advertName) {
		this.advertName = advertName;
	}

	public int getAdvertID() {
		return advertID;
	}

	public void setAdvertID(int advertID) {
		this.advertID = advertID;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getSysType() {
		return sysType;
	}

	public void setSysType(int sysType) {
		this.sysType = sysType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
