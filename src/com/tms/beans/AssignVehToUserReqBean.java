package com.tms.beans;

import java.util.List;

public class AssignVehToUserReqBean {
	
	private List<Long> userIds;
	
	private List<Long> vehIds;

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public List<Long> getVehIds() {
		return vehIds;
	}

	public void setVehIds(List<Long> vehIds) {
		this.vehIds = vehIds;
	}

	@Override
	public String toString() {
		return "AssignVehToUserReqBean [userIds=" + userIds + ", vehIds=" + vehIds + "]";
	}

}
