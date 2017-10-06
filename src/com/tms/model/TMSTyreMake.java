package com.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMS_TireMake")
public class TMSTyreMake {

	@Id
	@GeneratedValue
	@Column(name = "TireMakeId")
	private long TyreMakeId;

	@Column(name = "TireMake", columnDefinition = " VARCHAR(45) ", nullable = false, unique = true)
	private String tyreMake;
	
	@Column(name = "OrgId", columnDefinition = " int default 0 ", nullable = true)
	private long orgId;
	
	@Column(name = "CreatedBy", columnDefinition = " int default 0 ", nullable = true)
	private long createdBy;

	@Column(name = "UpdatedDateTime",
			columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date updatedDateTime;

	@Column(name = "CreatedDateTime", columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ", nullable = false)
	private Date createdDateTime;

	public long getTyreMakeId() {
		return TyreMakeId;
	}

	public void setTyreMakeId(long tyreMakeId) {
		TyreMakeId = tyreMakeId;
	}

	public String getTyreMake() {
		return tyreMake;
	}

	public void setTyreMake(String tyreMake) {
		this.tyreMake = tyreMake;
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

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	
}
