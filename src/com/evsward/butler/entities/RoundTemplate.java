package com.evsward.butler.entities;

import java.util.Date;

public class RoundTemplate {

	private int roundTempID;
	private Date createTime;
	private String name;

	public int getRoundTempID() {
		return roundTempID;
	}

	public void setRoundTempID(int roundTempID) {
		this.roundTempID = roundTempID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public RoundTemplate(int roundTempID, Date createTime, String name) {
		super();
		this.roundTempID = roundTempID;
		this.name = name;
		this.createTime = createTime;
	}

	public RoundTemplate() {
		super();
	}

	public String toString() {
		return name;
	}

	public class RoundTemplateList {
		private int rspCode;
		private RoundTemplate[] roundTemps;
		private String msg;

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

		public RoundTemplate[] getRoundTemplates() {
			return roundTemps;
		}

		public void setRoundTemplates(RoundTemplate[] roundtemplates) {
			this.roundTemps = roundtemplates;
		}

		public RoundTemplateList(int rspCode, RoundTemplate[] roundTemps, String msg) {
			super();
			this.rspCode = rspCode;
			this.roundTemps = roundTemps;
			this.msg = msg;
		}
	}

}
