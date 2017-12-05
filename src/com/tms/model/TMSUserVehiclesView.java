package com.tms.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "TMS_User_Vehicles_View")
public class TMSUserVehiclesView {
	
	@Id
	@Column(name="VehId")
	private long vehId;
	
	@Column(name = "VehName")
	private String vehName;
	
	@Column(name="DepotId")
	private long depotId;
	
	@Column(name="OrgId")
	private long orgId;
	
	@Column(name="RFID")
	private long RFID;
	
	@Column(name = "RFIDUID")
	private String RFIDUID;
	
	@Column(name="ControllerID")
	private long controllerID;
	
	@Column(name = "ControllerUID")
	private String controllerUID;
	
	@Column(name = "RFID_UpdatedDateTime")
	private Date rfid_UpdatedDateTime;
	
	@Column(name = "BController_UpdatedDateTime")
	private Date bCtrl_UpdatedDateTime;
	
	@Column(name = "Vehicle_UpdatedDateTime")
	private Date veh_UpdatedDateTime;
	
	@Column(name = "Vehicle_CreatedDateTime")
	private Date veh_CreatedDateTime;
	
	@Column(name = "Status")
	private long status;
	
	@Column(name = "UserId")
	private long userId;
	
	@Column(name = "UserName")
	private String userName;
	
	@Transient
	private List<TMSTire> tires;

	public long getVehId() {
		return vehId;
	}

	public String getVehName() {
		return vehName;
	}

	public long getDepotId() {
		return depotId;
	}

	public long getOrgId() {
		return orgId;
	}

	public long getRFID() {
		return RFID;
	}

	public String getRFIDUID() {
		return RFIDUID;
	}

	public long getControllerID() {
		return controllerID;
	}

	public String getControllerUID() {
		return controllerUID;
	}

	public Date getRfid_UpdatedDateTime() {
		return rfid_UpdatedDateTime;
	}

	public Date getbCtrl_UpdatedDateTime() {
		return bCtrl_UpdatedDateTime;
	}

	public Date getVeh_UpdatedDateTime() {
		return veh_UpdatedDateTime;
	}

	public Date getVeh_CreatedDateTime() {
		return veh_CreatedDateTime;
	}

	public long getStatus() {
		return status;
	}

	public long getUserId() {
		return userId;
	}

	public List<TMSTire> getTires() {
		return tires;
	}

	public void setTires(List<TMSTire> tires) {
		this.tires = tires;
	}
	
	public String getUserName() {
		return userName;
	}

	@Override
	public String toString() {
		return "TMSUserVehiclesView [vehId=" + vehId + ", vehName=" + vehName + ", orgId=" + orgId + ", status="
				+ status + ", userId=" + userId + ", userName=" + userName + "]";
	}
	
}
