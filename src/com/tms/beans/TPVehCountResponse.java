package com.tms.beans;

public class TPVehCountResponse {

	private boolean status;

	private String displayMsg;

	private String errorMsg;

	private long goodTPVehCount;

	private long badTPVehCount;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getDisplayMsg() {
		return displayMsg;
	}

	public void setDisplayMsg(String displayMsg) {
		this.displayMsg = displayMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public long getGoodTPVehCount() {
		return goodTPVehCount;
	}

	public void setGoodTPVehCount(long goodTPVehCount) {
		this.goodTPVehCount = goodTPVehCount;
	}

	public long getBadTPVehCount() {
		return badTPVehCount;
	}

	public void setBadTPVehCount(long badTPVehCount) {
		this.badTPVehCount = badTPVehCount;
	}

}
