package com.evsward.butler.entities;

import java.util.List;

/**
 * 用于接收会员报名 ：一天的比赛
 * 
 * @Date May 6, 2015
 * @author liuwb.edward
 */
public class DayCompetitionList {
	private String day_week;
	private List<Competition> list;

	public String getDay_week() {
		return day_week;
	}

	public void setDay_week(String day_week) {
		this.day_week = day_week;
	}

	public List<Competition> getList() {
		return list;
	}

	public void setList(List<Competition> list) {
		this.list = list;
	}
}
