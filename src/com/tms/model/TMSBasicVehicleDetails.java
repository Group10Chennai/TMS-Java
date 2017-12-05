package com.tms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMS_User_Vehicles_View")
public class TMSBasicVehicleDetails {

	@Id
	@GeneratedValue
	@Column(name="VehId")
	private long vehId;
	
	@Column(name = "VehName")
	private String vehName;
	
	@Column(name = "OrgId")
	private long orgId;
	
	@Column(name = "UserId")
	private long userId;
	
	@Column(name = "Status")
	private long status;


	
	@Override
	public String toString() {
		return "TMSBasicVehicleDetails [vehId=" + vehId + ", vehName=" + vehName + ", orgId=" + orgId + ", userId="
				+ userId + ", status=" + status + "]";
	}

	public long getVehId() {
		return vehId;
	}

	public String getVehName() {
		return vehName;
	}

	public long getOrgId() {
		return orgId;
	}

	public long getUserId() {
		return userId;
	}

	public long getStatus() {
		return status;
	}

}
