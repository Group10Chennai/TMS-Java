package com.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMS_MinMax_TempPressure")
public class TMSMinMaxTempPressure {

	@Id
	@GeneratedValue
	@Column(name="TMS_MinMax_TempPressure_Id")
	private long TMSMinMaxTempPressureId;
	
	@Column(name = "minTemp", columnDefinition = " Double default 0 ", nullable = false)
	private double minTemp;
	
	@Column(name = "maxTemp", columnDefinition = " Double default 0 ", nullable = false)
	private double maxTemp;
	
	@Column(name = "minPressure", columnDefinition = " Double default 0 ", nullable = false)
	private double minPressure;
	
	@Column(name = "maxPressure", columnDefinition = " Double default 0 ", nullable = false)
	private double maxPressure;
	
	@Column(name = "orgId", columnDefinition = " int default 0 ", nullable = false)
	private long orgId;
	
	@Column(name = "userId", columnDefinition = " int default 0 ", nullable = false)
	private long userId;

	public long getTMSMinMaxTempPressureId() {
		return TMSMinMaxTempPressureId;
	}

	public void setTMSMinMaxTempPressureId(long tMSMinMaxTempPressureId) {
		TMSMinMaxTempPressureId = tMSMinMaxTempPressureId;
	}

	public double getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(double minTemp) {
		this.minTemp = minTemp;
	}

	public double getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(double maxTemp) {
		this.maxTemp = maxTemp;
	}

	public double getMinPressure() {
		return minPressure;
	}

	public void setMinPressure(double minPressure) {
		this.minPressure = minPressure;
	}

	public double getMaxPressure() {
		return maxPressure;
	}

	public void setMaxPressure(double maxPressure) {
		this.maxPressure = maxPressure;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	@Override
	public String toString() {
		return "TMSMinMaxTempPressure [TMSMinMaxTempPressureId=" + TMSMinMaxTempPressureId + ", minTemp=" + minTemp
				+ ", maxTemp=" + maxTemp + ", minPressure=" + minPressure + ", maxPressure=" + maxPressure + ", orgId="
				+ orgId + ", userId=" + userId + "]";
	}
}
