package com.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMS_Vehicles")
public class TMSVehicles {

	@Id
	@GeneratedValue
	@Column(name="VehId")
	private long vehId;
	
	@Column(name = "VehName", columnDefinition = " VARCHAR(45) ", nullable = false, unique = true)
	private String vehName;
	
	@Column(name = "DepotId", columnDefinition = " int default 0 ", nullable = false)
	private long depotId;
	
	@Column(name = "OrgId", columnDefinition = " int default 0 ", nullable = false)
	private long orgId;
	
	@Column(name = "CreatedBy", columnDefinition = " int default 0 ", nullable = false)
	private long createdBy;
	
	@Column(name = "CreatedDateTime", columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ", nullable = false)
	private Date createdDateTime;
	
	@Column(name = "DeviceId", columnDefinition = " int default 0 ", nullable = false)
	private long deviceId;
	
	@Column(name = "RFID", columnDefinition = " int default 0 ", nullable = false)
	private long RFID;
	
	@Column(name = "ControllerId", columnDefinition = " int default 0 ", nullable = false)
	private long controllerId;
	
	@Column(name = "UpdatedDateTime",
			columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date updatedDateTime;

	public long getVehId() {
		return vehId;
	}

	public void setVehId(long vehId) {
		this.vehId = vehId;
	}

	public String getVehName() {
		return vehName;
	}

	public void setVehName(String vehName) {
		this.vehName = vehName;
	}

	public long getDepotId() {
		return depotId;
	}

	public void setDepotId(long depotId) {
		this.depotId = depotId;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
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

	public long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}

	public long getRFID() {
		return RFID;
	}

	public void setRFID(long rFID) {
		RFID = rFID;
	}

	public long getControllerId() {
		return controllerId;
	}

	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}

	public Date getUpdatedDateTime() {
		return updatedDateTime;
	}
	
	public void setUpdatedDateTime(Date updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	@Override
	public String toString()
	{
		return "{\"vehId:\""+getVehId()+", \"vehName:\""+getVehName()+"}";
	}
}
