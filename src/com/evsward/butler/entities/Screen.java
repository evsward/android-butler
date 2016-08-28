package com.evsward.butler.entities;

import java.util.ArrayList;
import java.util.Date;

public class Screen {
	private String devImei;

	private String devStateShow;
	private String pushTypeShow;
	private String ipStr;
	private String devName;

	private int compID;
	private String compName;

	private long ip;
	private int devState;
	private int pushType;
	// 语言类型：1、英文；0、中文
	private int language;
	private String languageShow;
	private Date createTime;
	private Date updateTime;
	private ArrayList<Competition> noEndCompsOnday;

	public ArrayList<Competition> getNoEndCompsOnday() {
		return noEndCompsOnday;
	}

	public void setNoEndCompsOnday(ArrayList<Competition> noEndCompsOnday) {
		this.noEndCompsOnday = noEndCompsOnday;
	}

	public String getDevImei() {
		return devImei;
	}

	public void setDevImei(String devImei) {
		this.devImei = devImei;
	}

	public String getLanguageShow() {
		return languageShow;
	}

	public void setLanguageShow(String languageShow) {
		this.languageShow = languageShow;
	}

	public String getDevStateShow() {
		return devStateShow;
	}

	public void setDevStateShow(String devStateShow) {
		this.devStateShow = devStateShow;
	}

	public String getPushTypeShow() {
		return pushTypeShow;
	}

	public void setPushTypeShow(String pushTypeShow) {
		this.pushTypeShow = pushTypeShow;
	}

	public String getIpStr() {
		return ipStr;
	}

	public void setIpStr(String ipStr) {
		this.ipStr = ipStr;
	}

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public int getCompID() {
		return compID;
	}

	public void setCompID(int compID) {
		this.compID = compID;
	}

	public long getIp() {
		return ip;
	}

	public void setIp(long ip) {
		this.ip = ip;
	}

	public int getDevState() {
		return devState;
	}

	public void setDevState(int devState) {
		this.devState = devState;
	}

	public int getPushType() {
		return pushType;
	}

	public void setPushType(int pushType) {
		this.pushType = pushType;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public int getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = language;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "Screen [devImei=" + devImei + ", compID=" + compID + ", ip=" + ip + ", ipStr=" + ipStr + ", devName=" + devName + ", devState="
				+ devState + ", pushType=" + pushType + ", compName=" + compName + ", language=" + language + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + "]";
	}
}
