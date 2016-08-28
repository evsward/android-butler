package com.evsward.butler.entities;

import java.util.Date;

public class Privilege {

	private Integer sysType;
	private Date createTime;
	private Date updateTime;
	private Date delTime;

	private Integer privID;
	private String privName;
	private String privPath;
	private Integer privState;
	private Integer privParentID;
	private String privNameShow;
	private String privDesc;
	private String privUUID;
	private Integer privDepth;

	public Integer getPrivID() {
		return privID;
	}

	public void setPrivID(Integer privID) {
		this.privID = privID;
	}

	public String getPrivName() {
		return privName;
	}

	public void setPrivName(String privName) {
		this.privName = privName;
	}

	public String getPrivPath() {
		return privPath;
	}

	public void setPrivPath(String privPath) {
		this.privPath = privPath;
	}

	public Integer getPrivState() {
		return privState;
	}

	public void setPrivState(Integer privState) {
		this.privState = privState;
	}

	public Integer getPrivParentID() {
		return privParentID;
	}

	public void setPrivParentID(Integer privParentID) {
		this.privParentID = privParentID;
	}

	public String getPrivNameShow() {
		return privNameShow;
	}

	public void setPrivNameShow(String privNameShow) {
		this.privNameShow = privNameShow;
	}

	public String getPrivDesc() {
		return privDesc;
	}

	public void setPrivDesc(String privDesc) {
		this.privDesc = privDesc;
	}

	public String getPrivUUID() {
		return privUUID;
	}

	public void setPrivUUID(String privUUID) {
		this.privUUID = privUUID;
	}

	public Integer getPrivDepth() {
		return privDepth;
	}

	public void setPrivDepth(Integer privDepth) {
		this.privDepth = privDepth;
	}

	public Integer getSysType() {
		return sysType;
	}

	public void setSysType(Integer sysType) {
		this.sysType = sysType;
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

	public Date getDelTime() {
		return delTime;
	}

	public void setDelTime(Date delTime) {
		this.delTime = delTime;
	}

	public Privilege(Integer sysType, Date createTime, Date updateTime, Date delTime, Integer privID, String privName, String privPath,
			Integer privState, Integer privParentID, String privNameShow, String privDesc, String privUUID, Integer privDepth) {
		super();
		this.privID = privID;
		this.privName = privName;
		this.privPath = privPath;
		this.privState = privState;
		this.privParentID = privParentID;
		this.privNameShow = privNameShow;
		this.privDesc = privDesc;
		this.privUUID = privUUID;
		this.privDepth = privDepth;
		this.sysType = sysType;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.delTime = delTime;
	}

	public Privilege() {
		super();
	}

}
