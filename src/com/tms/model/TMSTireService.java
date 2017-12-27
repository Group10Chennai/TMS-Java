package com.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMS_TireService")
public class TMSTireService {

	@Id
	@GeneratedValue
	@Column(name = "TireServiceId")
	private Long tireServiceId;

	@Column(name = "TireId", columnDefinition = " int default 0", nullable = false)
	private Long tireId;

	@Column(name = "TireNumber", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String tireNumber;
	
	@Column(name = "Depot", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String depot;
	
	@Column(name = "TireMake", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String tireMake;
	
	@Column(name = "VehName", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String vehName;

	@Column(name = "VehId", columnDefinition = " int default 0 ", nullable = false)
	private long vehId;
	
	@Column(name = "FittedDate", columnDefinition = " DATE", nullable = false)
	private Date fittedDate;
	
	@Column(name = "KmsAtTyreFitted", columnDefinition = " int default 0 ", nullable = false)
	private Long kmsAtTyreFitted;
	
	@Column(name = "Location", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String location;
	
	@Column(name = "RemovalDate", columnDefinition = "DATE", nullable = true)
	private Date removalDate;
	
	@Column(name = "KmsAtTyreRemoved", columnDefinition = " int default 0 ", nullable = true)
	private Long kmsAtTyreRemoved;
	
	@Column(name = "TyreKms", columnDefinition = " int default 0 ", nullable = true)
	private Long tyreKms;
	
	@Column(name = "Reason", columnDefinition = " VARCHAR(45) ", nullable = true)
	private String reason;
	
	@Column(name = "ActionTaken", columnDefinition = " VARCHAR(45) ", nullable = true)
	private String actionTaken;
	
	@Column(name = "TyreCondition", columnDefinition = " VARCHAR(45) ", nullable = true)
	private String tyreCondition;
	
	@Column(name = "ScrappedToParty", columnDefinition = " VARCHAR(45) ", nullable = true)
	private String scrappedToParty;
	
	@Column(name = "OrgId", columnDefinition = " int default 0 ", nullable = true)
	private Long orgId;
	
	@Column(name = "CreatedBy", columnDefinition = " int default 0 ", nullable = true)
	private Long createdBy;
	
	@Column(name = "CreatedByName", columnDefinition = " VARCHAR(45) ", nullable = true)
	private String createdByName;

	@Column(name = "UpdatedDateTime",
			columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date updatedDateTime;

	@Column(name = "CreatedDate", columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP", nullable = false)
	private Date createdDate;
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getTireServiceId() {
		return tireServiceId;
	}

	public void setTireServiceId(Long tireServiceId) {
		this.tireServiceId = tireServiceId;
	}

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

	public String getVehName() {
		return vehName;
	}

	public void setVehName(String vehName) {
		this.vehName = vehName;
	}

	public long getVehId() {
		return vehId;
	}

	public void setVehId(long vehId) {
		this.vehId = vehId;
	}

	public Date getFittedDate() {
		return fittedDate;
	}

	public void setFittedDate(Date fittedDate) {
		this.fittedDate = fittedDate;
	}

	public Long getKmsAtTyreFitted() {
		return kmsAtTyreFitted;
	}

	public void setKmsAtTyreFitted(Long kmsAtTyreFitted) {
		this.kmsAtTyreFitted = kmsAtTyreFitted;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getRemovalDate() {
		return removalDate;
	}

	public void setRemovalDate(Date removalDate) {
		this.removalDate = removalDate;
	}

	public Long getKmsAtTyreRemoved() {
		return kmsAtTyreRemoved;
	}

	public void setKmsAtTyreRemoved(Long kmsAtTyreRemoved) {
		this.kmsAtTyreRemoved = kmsAtTyreRemoved;
	}

	public Long getTyreKms() {
		return tyreKms;
	}

	public void setTyreKms(Long tyreKms) {
		this.tyreKms = tyreKms;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	public String getTyreCondition() {
		return tyreCondition;
	}

	public void setTyreCondition(String tyreCondition) {
		this.tyreCondition = tyreCondition;
	}

	public String getScrappedToParty() {
		return scrappedToParty;
	}

	public void setScrappedToParty(String scrappedToParty) {
		this.scrappedToParty = scrappedToParty;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public Date getUpdatedDateTime() {
		return updatedDateTime;
	}

	public String getDepot() {
		return depot;
	}

	public void setDepot(String depot) {
		this.depot = depot;
	}

	public String getTireMake() {
		return tireMake;
	}

	public void setTireMake(String tireMake) {
		this.tireMake = tireMake;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public void setUpdatedDateTime(Date updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	@Override
	public String toString() {
		return "TMSTireService [tireId=" + tireId + ", tireNumber=" + tireNumber + ", vehName=" + vehName + "]";
	}
	
	
}
