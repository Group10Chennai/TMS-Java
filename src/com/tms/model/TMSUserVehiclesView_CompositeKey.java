package com.tms.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "TMS_User_Vehicles_View")
public class TMSUserVehiclesView_CompositeKey {

	@EmbeddedId
	private TMSUserVehiclesView_Keys tmsUserVehiclesView_Keys;

	@Column(name = "VehName")
	private String vehName;

	@Column(name = "DepotId")
	private long depotId;

	@Column(name = "OrgId")
	private long orgId;

	@Column(name = "RFID")
	private long RFID;

	@Column(name = "RFIDUID")
	private String RFIDUID;

	@Column(name = "ControllerID")
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

	@Column(name = "UserName")
	private String userName;

	@Transient
	private List<TMSTire> tires;

	public TMSUserVehiclesView_Keys getTmsUserVehiclesView_Keys() {
		return tmsUserVehiclesView_Keys;
	}

	public void setTmsUserVehiclesView_Keys(TMSUserVehiclesView_Keys tmsUserVehiclesView_Keys) {
		this.tmsUserVehiclesView_Keys = tmsUserVehiclesView_Keys;
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

	public long getRFID() {
		return RFID;
	}

	public void setRFID(long rFID) {
		RFID = rFID;
	}

	public String getRFIDUID() {
		return RFIDUID;
	}

	public void setRFIDUID(String rFIDUID) {
		RFIDUID = rFIDUID;
	}

	public long getControllerID() {
		return controllerID;
	}

	public void setControllerID(long controllerID) {
		this.controllerID = controllerID;
	}

	public String getControllerUID() {
		return controllerUID;
	}

	public void setControllerUID(String controllerUID) {
		this.controllerUID = controllerUID;
	}

	public Date getRfid_UpdatedDateTime() {
		return rfid_UpdatedDateTime;
	}

	public void setRfid_UpdatedDateTime(Date rfid_UpdatedDateTime) {
		this.rfid_UpdatedDateTime = rfid_UpdatedDateTime;
	}

	public Date getbCtrl_UpdatedDateTime() {
		return bCtrl_UpdatedDateTime;
	}

	public void setbCtrl_UpdatedDateTime(Date bCtrl_UpdatedDateTime) {
		this.bCtrl_UpdatedDateTime = bCtrl_UpdatedDateTime;
	}

	public Date getVeh_UpdatedDateTime() {
		return veh_UpdatedDateTime;
	}

	public void setVeh_UpdatedDateTime(Date veh_UpdatedDateTime) {
		this.veh_UpdatedDateTime = veh_UpdatedDateTime;
	}

	public Date getVeh_CreatedDateTime() {
		return veh_CreatedDateTime;
	}

	public void setVeh_CreatedDateTime(Date veh_CreatedDateTime) {
		this.veh_CreatedDateTime = veh_CreatedDateTime;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<TMSTire> getTires() {
		return tires;
	}

	public void setTires(List<TMSTire> tires) {
		this.tires = tires;
	}
	
	
}
