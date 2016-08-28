package com.evsward.butler.entities;

import java.util.Date;

public class Role {

	private Integer roleID;
	private Integer sysType;

	private Date createTime;
	private Date updateTime;
	private Date delTime;

	// private List<Privilege> privileges;
	private Privilege[] privileges;

	private String roleName;
	private Integer roleState;
	private String roleDesc;
	private String roleNameShow;

	// public List<Privilege> getPrivileges() {
	// return privileges;
	// }
	// public void setPrivileges(List<Privilege> privileges) {
	// this.privileges = privileges;
	// }

	public Privilege[] getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Privilege[] privileges) {
		this.privileges = privileges;
	}

	public Integer getRoleID() {
		return roleID;
	}

	public void setRoleID(Integer roleID) {
		this.roleID = roleID;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Integer getRoleState() {
		return roleState;
	}

	public void setRoleState(Integer roleState) {
		this.roleState = roleState;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getRoleNameShow() {
		return roleNameShow;
	}

	public void setRoleNameShow(String roleNameShow) {
		this.roleNameShow = roleNameShow;
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

	public Role(Integer roleID, Integer sysType, Date createTime, Date updateTime, Date delTime, Privilege[] privileges, String roleName,
			Integer roleState, String roleDesc, String roleNameShow) {
		super();
		this.roleID = roleID;
		this.roleName = roleName;
		this.roleState = roleState;
		this.roleDesc = roleDesc;
		this.roleNameShow = roleNameShow;
		this.sysType = sysType;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.delTime = delTime;
		this.privileges = privileges;
	}

	public Role() {
		super();
	}

}
