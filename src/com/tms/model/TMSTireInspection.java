package com.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMS_TireInspection")
public class TMSTireInspection {

	@Id
	@GeneratedValue
	@Column(name = "TireInspectionId")
	private Long tireInspectionId;

	@Column(name = "TireId", columnDefinition = " int default 0", nullable = false)
	private Long tireId;

	@Column(name = "TireNumber", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String tireNumber;
	
	@Column(name = "InspectionDate", columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ", nullable = false)
	private Date inspectionDate;
	
	@Column(name = "Location", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String location;
	
	@Column(name = "KMSReading", columnDefinition = " int default 0 ", nullable = false)
	private Long KMSReading;
	
	@Column(name = "TreadDepthLocation1", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String depthLocation1;
	
	@Column(name = "TreadDepthLocation2", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String depthLocation2;
	
	@Column(name = "TreadDepthLocation3", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String depthLocation3;
	
	@Column(name = "TirePressure", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String tirePressure;
	
	@Column(name = "CreatedBy", columnDefinition = " int default 0 ", nullable = true)
	private Long createdBy;
	
	@Column(name = "CreatedByName", columnDefinition = " VARCHAR(45) ", nullable = true)
	private String createdByName;

	@Column(name = "UpdatedDateTime",
			columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date updatedDateTime;

	public Long getTireInspectionId() {
		return tireInspectionId;
	}

	public void setTireInspectionId(Long tireInspectionId) {
		this.tireInspectionId = tireInspectionId;
	}

	public Long getTireId() {
		return tireId;
	}

	public void setTireId(Long tireId) {
		this.tireId = tireId;
	}

	public Date getInspectionDate() {
		return inspectionDate;
	}

	public void setInspectionDate(Date inspectionDate) {
		this.inspectionDate = inspectionDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Long getKMSReading() {
		return KMSReading;
	}

	public void setKMSReading(Long kMSReading) {
		KMSReading = kMSReading;
	}

	public String getDepthLocation1() {
		return depthLocation1;
	}

	public void setDepthLocation1(String depthLocation1) {
		this.depthLocation1 = depthLocation1;
	}

	public String getDepthLocation2() {
		return depthLocation2;
	}

	public void setDepthLocation2(String depthLocation2) {
		this.depthLocation2 = depthLocation2;
	}

	public String getDepthLocation3() {
		return depthLocation3;
	}

	public void setDepthLocation3(String depthLocation3) {
		this.depthLocation3 = depthLocation3;
	}

	public String getTirePressure() {
		return tirePressure;
	}

	public void setTirePressure(String tirePressure) {
		this.tirePressure = tirePressure;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedDateTime() {
		return updatedDateTime;
	}

	public String getTireNumber() {
		return tireNumber;
	}

	public void setTireNumber(String tireNumber) {
		this.tireNumber = tireNumber;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public void setUpdatedDateTime(Date updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	
}
