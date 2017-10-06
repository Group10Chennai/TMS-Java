package com.tms.beans;

public class TMSUserBean {

	private long userId;
	
	private String userName;
	
	private String orgName;
	
	private long userLevel;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public long getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(long userLevel) {
		this.userLevel = userLevel;
	}
	
	
}
