package com.tms.beans;

import java.util.List;

public class Response {

	private boolean status;
	
	private String displayMsg;
	
	private String errorMsg;
	
	private String strResponse;
	
	private long count;
	
	@SuppressWarnings("rawtypes")
	private List result;

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

	@SuppressWarnings("rawtypes")
	public List getResult() {
		return result;
	}

	@SuppressWarnings("rawtypes")
	public void setResult(List result) {
		this.result = result;
	}

	public String getStrResponse() {
		return strResponse;
	}

	public void setStrResponse(String strResponse) {
		this.strResponse = strResponse;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "Response [status=" + status + ", displayMsg=" + displayMsg + ", errorMsg=" + errorMsg + ", strResponse="
				+ strResponse + ", count=" + count + ", result=" + result + "]";
	}
	
}
