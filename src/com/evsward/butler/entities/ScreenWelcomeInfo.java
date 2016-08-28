package com.evsward.butler.entities;

/**
 * 大屏幕入场安检欢迎信息
 * 
 * @author liubin
 * 
 */

public class ScreenWelcomeInfo {

	// 大屏幕盒子设备号
	private String imei;
	// 大屏幕盒子推送类型
	private int pushType;
	// 语言版本
	private int language;
	// 会员ID
	private int memID;
	// 会员昵称
	private String memNickName;
	// 会员姓名
	private String memName;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public int getPushType() {
		return pushType;
	}

	public void setPushType(int pushType) {
		this.pushType = pushType;
	}

	public int getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = language;
	}

	public int getMemID() {
		return memID;
	}

	public void setMemID(int memID) {
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
}
