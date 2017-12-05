package com.tms.model.mongo;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.mapping.Document;


@JsonIgnoreProperties
@Document(collection = "TPMSData_Report")
public class TPMSData_Report {

	@Id
	@GeneratedValue
	private String id;
	
	private long vehId;

	private long device_date_time;
	
	private long server_date_time;

	private List<Tyre> tyres;
	
	public long getVehId() {
		return vehId;
	}

	public void setVehId(long vehId) {
		this.vehId = vehId;
	}

	public long getDevice_date_time() {
		return device_date_time;
	}

	public void setDevice_date_time(long device_date_time) {
		this.device_date_time = device_date_time;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Tyre> getTyres() {
		return tyres;
	}

	public void setTyres(List<Tyre> tyres) {
		this.tyres = tyres;
	}

	public long getServer_date_time() {
		return server_date_time;
	}

	public void setServer_date_time(long server_date_time) {
		this.server_date_time = server_date_time;
	}

	@Override
	public String toString() {
		return "TPMSData_latest [id=" + id + ", vehId=" + vehId + ", device_date_time=" + device_date_time
				+ ", server_date_time=" + server_date_time + ", tyres=" + tyres + "]";
	}

	
}
