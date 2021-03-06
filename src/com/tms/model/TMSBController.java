package com.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMS_BController")
public class TMSBController {

	@Id
	@GeneratedValue
	@Column(name="ControllerId")
	private long controllerId;
	
	@Column(name = "ControllerUID", columnDefinition = " VARCHAR(45) ", nullable = false, unique = true)
	private String controllerUID;
	
	@Column(name = "VehId", columnDefinition = " int default 0 ", nullable = false)
	private long vehId;
	
	@Column(name = "CreatedBy", columnDefinition = " int default 0 ", nullable = false)
	private long createdBy;
	
	@Column(name = "CreatedDateTime", columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ", nullable = false)
	private Date createdDateTime;
	
	@Column(name = "UpdatedDateTime",
			columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date updatedDateTime;
	
	@Column(name = "Status", columnDefinition = " VARCHAR(45) ", nullable = false)
	private String status;

	public long getControllerId() {
		return controllerId;
	}

	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}

	public String getControllerUID() {
		return controllerUID;
	}

	public void setControllerUID(String controllerUID) {
		this.controllerUID = controllerUID;
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

	public Date getUpdatedDateTime() {
		return updatedDateTime;
	}

	public long getVehId() {
		return vehId;
	}

	public void setVehId(long vehId) {
		this.vehId = vehId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setUpdatedDateTime(Date updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

}
