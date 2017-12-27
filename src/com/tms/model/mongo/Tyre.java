package com.tms.model.mongo;

public class Tyre {
	
	public long tyreId;
	
	public String position;
	
	public double pressure;
	
	public double temp;
	
	public String sensorUID;
	

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

	public String getSensorUID() {
		return sensorUID;
	}

	public void setSensorUID(String sensorUID) {
		this.sensorUID = sensorUID;
	}

	@Override
	public String toString() {
		return "Tyre [tyreId=" + tyreId + ", position=" + position + ", pressure=" + pressure + ", temp=" + temp
				+ ", sensorUID=" + sensorUID + "]";
	}
	

}
