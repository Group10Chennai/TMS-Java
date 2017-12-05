package com.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMS_TireView")
public class TMSTireShortDetails {

	@Id
	@Column(name = "TireId")
	private Long tireId;

	@Column(name = "TireNumber")
	private String tireNumber;
	
	@Column(name = "TireMake")
	private String tireMake;
	
	@Column(name = "DepotName")
	private String depotName;

	@Column(name = "Status")
	private String status;
	
	@Column(name = "OrgId")
	private Long orgId;
	
	public Long getTireId() {
		return tireId;
	}

	public String getTireNumber() {
		return tireNumber;
	}

	public String getTireMake() {
		return tireMake;
	}

	public String getDepotName() {
		return depotName;
	}

	public String getStatus() {
		return status;
	}

	public Long getOrgId() {
		return orgId;
	}
		
}
