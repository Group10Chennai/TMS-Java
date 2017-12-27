package com.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMS_Sensor")
public class TMSSensor {

	@Id
	@GeneratedValue
	@Column(name="SensorId")
	private long sensorId;
	
	@Column(name = "SensorUID", columnDefinition = " VARCHAR(45) ", nullable = false, unique = true)
	private String sensorUID;
	
	@Column(name = "RIMNo", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String rimNo;
	
	@Column(name = "TireId", columnDefinition=" int default 0 ", nullable = false)
	private long tireId;
	
	@Column(name = "CreatedBy", columnDefinition=" int default 0 ", nullable = false)
	private long createdBy;
	
	@Column(name = "CreatedDateTime", columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ", nullable = false)
	private Date createdDateTime;
	
	@Column(name = "UpdatedDateTime",
			columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date updatedDateTime;
	
	@Column(name = "Status", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String status;

	public long getSensorId() {
		return sensorId;
	}

	public void setSensorId(long sensorId) {
		this.sensorId = sensorId;
	}

	public String getSensorUID() {
		return sensorUID;
	}

	public void setSensorUID(String sensorUID) {
		this.sensorUID = sensorUID;
	}

	public long getTireId() {
		return tireId;
	}

	public void setTireId(long tireId) {
		this.tireId = tireId;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getUpdatedDateTime() {
		return updatedDateTime;
	}

	public void setUpdatedDateTime(Date updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	public String getRimNo() {
		return rimNo;
	}

	public void setRimNo(String rimNo) {
		this.rimNo = rimNo;
	}
	
	
}
