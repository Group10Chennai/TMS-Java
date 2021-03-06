package com.tms.model.mongo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonIgnoreProperties
@Document(collection = "TPMSData_Report.tyres")
public class Tyre2 {
	
	public long tyreId;
	
	public String position;
	
	public double pressure;
	
	public double temp;
	

	public long getTyreId() {
		return tyreId;
	}

	public void setTyreId(long tyreId) {
		this.tyreId = tyreId;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public double getPressure() {
		return pressure;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	@Override
	public String toString() {
		return "[tyreId=" + tyreId + ", position=" + position + ", pressure=" + pressure + ", temp=" + temp
				+ "]";
	}
	


}
