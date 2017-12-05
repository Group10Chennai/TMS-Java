package com.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Organizations")
public class Organizations {

	@Id
	@GeneratedValue
	@Column(name="org_id")
	private long orgId;
	
	@Column(name = "org_name", columnDefinition = " VARCHAR(45) ", nullable = false, unique = true)
	private String orgName;

	@Column(name = "CreatedBy", columnDefinition = " int default 0 ", nullable = false)
	private long createdBy;
	
	@Column(name = "CreatedDateTime", columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ", nullable = false)
	private Date createdDateTime;
	
	@Column(name = "UpdatedDateTime",
			columnDefinition = " TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date updatedDateTime;
	
	@Column(name = "Status", columnDefinition = " int default 1 ", nullable = false)
	private int status;

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
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

	public void setUpdatedDateTime(Date updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
	
}
