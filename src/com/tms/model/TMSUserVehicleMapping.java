package com.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMS_User_Vehicle_Mapping")
public class TMSUserVehicleMapping {

	@Id
	@GeneratedValue
	@Column(name="TMSUserVehicleMappingId")
	private long userVehicleMappingId;
	
	@Column(name = "userId", columnDefinition = " int default 0 ", nullable = true)
	private long userId;
	
	@Column(name = "vehId", columnDefinition = " int default 0 ", nullable = true)
	private long vehId;
	
	@Column(name = "status", columnDefinition = " int default 0 ", nullable = true)
	private long status;
	
	@Column(name = "assignedBy", columnDefinition = " int default 0 ", nullable = true)
	private long assignedBy;
	
	@Column(name = "CreatedDateTime", columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ", nullable = false)
	private Date createdDateTime;

	public long getUserVehicleMappingId() {
		return userVehicleMappingId;
	}

	public void setUserVehicleMappingId(long userVehicleMappingId) {
		this.userVehicleMappingId = userVehicleMappingId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getVehId() {
		return vehId;
	}

	public void setVehId(long vehId) {
		this.vehId = vehId;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(long assignedBy) {
		this.assignedBy = assignedBy;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
		
}
