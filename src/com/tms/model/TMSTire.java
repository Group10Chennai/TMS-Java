package com.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "TMS_Tire")
public class TMSTire {

	@Id
	@GeneratedValue
	@Column(name = "TireId")
	private Long tireId;

	@Column(name = "TireNumber", columnDefinition = " VARCHAR(45) ", nullable = false, unique = true)
	private String tireNumber;

	@Column(name = "TireMakeId", columnDefinition = " int default 0", nullable = false)
	private Long tireMakeId;
	
	@Column(name = "TireType", columnDefinition = " VARCHAR(45) ", nullable = false, unique = true)
	private String tireType;

	@Column(name = "TirePosition", columnDefinition = " VARCHAR(45) ")
	private String tirePosition;
	
	@Column(name = "ThreadDepth", columnDefinition = " VARCHAR(45) ")
	private String threadDepth;

	@Column(name = "SensorId", columnDefinition = " int default 0", nullable = false)
	private Long sensorId;

	@Column(name = "Status", columnDefinition = " VARCHAR(45) ", nullable = true)
	private String status;

	@Column(name = "VehId", columnDefinition = " int default 0 ", nullable = true)
	private Long vehId;

	@Column(name = "DepotId", columnDefinition = " int default 0 ", nullable = true)
	private Long depotId;
		
	@Column(name = "CreatedBy", columnDefinition = " int default 0 ", nullable = true)
	private Long createdBy;

	@Column(name = "UpdatedDateTime",
			columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date updatedDateTime;

	@Column(name = "CreatedDateTime", columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ", nullable = false)
	private Date createdDateTime;

	@Column(name = "LastServiceId", columnDefinition = " int default 0 ", nullable = true)
	private Long lastServiceId;
	
	@Column(name = "TotalTyreKM", columnDefinition = " int default 0 ", nullable = true)
	private Long totalTyreKM;
	
	public Long getTireId() {
		return tireId;
	}

	public void setTireId(Long tireId) {
		this.tireId = tireId;
	}

	public String getTireNumber() {
		return tireNumber;
	}

	public void setTireNumber(String tireNumber) {
		this.tireNumber = tireNumber;
	}

	public Long getTireMakeId() {
		return tireMakeId;
	}

	public void setTireMakeId(Long tireMakeId) {
		this.tireMakeId = tireMakeId;
	}

	public String getTirePosition() {
		return tirePosition;
	}

	public void setTirePosition(String tirePosition) {
		this.tirePosition = tirePosition;
	}

	public String getThreadDepth() {
		return threadDepth;
	}

	public void setThreadDepth(String threadDepth) {
		this.threadDepth = threadDepth;
	}

	public Long getSensorId() {
		return sensorId;
	}

	public void setSensorId(Long sensorId) {
		this.sensorId = sensorId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getVehId() {
		return vehId;
	}

	public void setVehId(Long vehId) {
		this.vehId = vehId;
	}

	public Long getDepotId() {
		return depotId;
	}

	public void setDepotId(Long depotId) {
		this.depotId = depotId;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Date getUpdatedDateTime() {
		return updatedDateTime;
	}

	public void setUpdatedDateTime(Date updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	public String getTireType() {
		return tireType;
	}

	public void setTireType(String tireType) {
		this.tireType = tireType;
	}

	public Long getLastServiceId() {
		return lastServiceId;
	}

	public void setLastServiceId(Long lastServiceId) {
		this.lastServiceId = lastServiceId;
	}

	public Long getTotalTyreKM() {
		return totalTyreKM;
	}

	public void setTotalTyreKM(Long totalTyreKM) {
		this.totalTyreKM = totalTyreKM;
	}

}
