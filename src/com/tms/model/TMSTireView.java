package com.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMS_TireView")
public class TMSTireView {

	@Id
	@Column(name = "TireId")
	private Long tireId;

	@Column(name = "TireNumber")
	private String tireNumber;

	@Column(name = "TireMakeId")
	private Long tireMakeId;
	
	@Column(name = "TireMake")
	private String tireMake;
	
	@Column(name = "TireType")
	private String tireType;
	
	@Column(name = "Status")
	private String status;
	
	@Column(name = "TirePosition")
	private String tirePosition;
	
	@Column(name = "ThreadDepth")
	private String threadDepth;
	
	@Column(name = "SensorId")
	private Long sensorId;
	
	@Column(name = "SensorUID")
	private String sensorUID;
	
	@Column(name = "CreatedUserId")
	private Long createdUserId;
	
	@Column(name = "CreatedUser")
	private String createdUse;
	
	@Column(name = "DepotId")
	private Long depotId;
	
	@Column(name = "DepotName")
	private String depotName;
	
	@Column(name = "VehId")
	private Long vehId;
	
	@Column(name = "VehName")
	private String vehName;
	
	@Column(name="Tire_CreatedDateTime")
	private Date tireCreatedDateTime;
	
	@Column(name="Tire_UpdatedDateTime")
	private Date tireUpdatedDateTime;
	
	@Column(name="Sensor_UpdatedDateTime")
	private Date sensorUpdatedDateTime;

	public Long getTireId() {
		return tireId;
	}

	public String getTireNumber() {
		return tireNumber;
	}

	public Long getTireMakeId() {
		return tireMakeId;
	}

	public String getTireMake() {
		return tireMake;
	}

	public String getStatus() {
		return status;
	}

	public Long getSensorId() {
		return sensorId;
	}

	public String getSensorUID() {
		return sensorUID;
	}

	public Long getCreatedUserId() {
		return createdUserId;
	}

	public String getCreatedUse() {
		return createdUse;
	}

	public Long getDepotId() {
		return depotId;
	}

	public String getDepotName() {
		return depotName;
	}

	public Long getVehId() {
		return vehId;
	}

	public String getVehName() {
		return vehName;
	}

	public Date getTireCreatedDateTime() {
		return tireCreatedDateTime;
	}

	public Date getTireUpdatedDateTime() {
		return tireUpdatedDateTime;
	}

	public Date getSensorUpdatedDateTime() {
		return sensorUpdatedDateTime;
	}

	public String getTirePosition() {
		return tirePosition;
	}

	public String getThreadDepth() {
		return threadDepth;
	}

	public String getTireType() {
		return tireType;
	}

}
