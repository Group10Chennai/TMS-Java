package com.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_master")
public class UserMaster {

	@Id
	@GeneratedValue
	@Column(name="user_id")
	public long userId;
	
	@Column(name = "user_name")
	public String userName;
	
	@Column(name = "user_level")
	public long userLevel;
	
	@Column(name="parent_user_id")
	public long parentUserId;
	
	@Column(name="org_id")
	public long orgId;
	
	@Column(name = "org_name")
	public String orgName;
	
	@Column(name = "mobile_number")
	public long mobileNumber;
	
	@Column(name = "email_id")
	public String emailId;
	
	@Column(name = "password")
	public String password;
	
	@Column(name = "timestamp")
	public Date timestamp;

	@Column(name = "name")
	public String name;
	
	@Column(name = "group_id")
	public long group_id;
	
	@Column(name = "TMS_login_status")
	public boolean TMSLoginStatus;
	
	@Column(name = "TMS_user_level")
	public long TMSUserLevel;

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

	public long getParentUserId() {
		return parentUserId;
	}

	public void setParentUserId(long parentUserId) {
		this.parentUserId = parentUserId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public long getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(long group_id) {
		this.group_id = group_id;
	}

	public long getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(long userLevel) {
		this.userLevel = userLevel;
	}

	public boolean isTMSLoginStatus() {
		return TMSLoginStatus;
	}

	public void setTMSLoginStatus(boolean tMSLoginStatus) {
		TMSLoginStatus = tMSLoginStatus;
	}

	public long getTMSUserLevel() {
		return TMSUserLevel;
	}

	public void setTMSUserLevel(long tMSUserLevel) {
		TMSUserLevel = tMSUserLevel;
	}

	@Override
	public String toString(){
		return "{\"userId\":"+getUserId()+", \"userName\" : \""+getUserName()+"\", \"mobileNumber\" : \""+getMobileNumber()+"\"}";
	}
}
