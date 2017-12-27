package com.tms.beans;

import java.util.List;

import com.tms.model.TMSBasicVehicleDetails;
import com.tms.model.TMSMinMaxTempPressure;

public class TMSUserBean {

	private long userId;
	
	private String userName;
	
	private String orgName;
	
	private long userLevel;
	
	private List<TMSBasicVehicleDetails> vehicles;
	
	private TMSMinMaxTempPressure minMaxTempPressureValues;
	
	private long allTiresConfigVehCount;
	
	private long total_fleets;
	
	private long tireCount_all;
	
	private long tireCount_installed;
	
	private long tireCount_instock;
	
	private long tireCount_withoutSensor;
	
	public long getTireCount_withoutSensor() {
		return tireCount_withoutSensor;
	}

	public void setTireCount_withoutSensor(long tireCount_withoutSensor) {
		this.tireCount_withoutSensor = tireCount_withoutSensor;
	}

	private long tireCount_scraped;
	
	private long tireCount_services;
	
	private long tireCount_inspections;
	
	

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public long getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(long userLevel) {
		this.userLevel = userLevel;
	}

	public List<TMSBasicVehicleDetails> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<TMSBasicVehicleDetails> vehicles) {
		this.vehicles = vehicles;
	}

	public TMSMinMaxTempPressure getMinMaxTempPressureValues() {
		return minMaxTempPressureValues;
	}

	public void setMinMaxTempPressureValues(TMSMinMaxTempPressure minMaxTempPressureValues) {
		this.minMaxTempPressureValues = minMaxTempPressureValues;
	}

	public long getAllTiresConfigVehCount() {
		return allTiresConfigVehCount;
	}

	public void setAllTiresConfigVehCount(long allTiresConfigVehCount) {
		this.allTiresConfigVehCount = allTiresConfigVehCount;
	}

	public long getTireCount_all() {
		return tireCount_all;
	}

	public void setTireCount_all(long tireCount_all) {
		this.tireCount_all = tireCount_all;
	}

	public long getTireCount_installed() {
		return tireCount_installed;
	}

	public void setTireCount_installed(long tireCount_installed) {
		this.tireCount_installed = tireCount_installed;
	}

	public long getTireCount_instock() {
		return tireCount_instock;
	}

	public void setTireCount_instock(long tireCount_instock) {
		this.tireCount_instock = tireCount_instock;
	}

	public long getTireCount_scraped() {
		return tireCount_scraped;
	}

	public void setTireCount_scraped(long tireCount_scraped) {
		this.tireCount_scraped = tireCount_scraped;
	}

	public long getTireCount_services() {
		return tireCount_services;
	}

	public void setTireCount_services(long tireCount_services) {
		this.tireCount_services = tireCount_services;
	}

	public long getTireCount_inspections() {
		return tireCount_inspections;
	}

	public void setTireCount_inspections(long tireCount_inspections) {
		this.tireCount_inspections = tireCount_inspections;
	}

	public long getTotal_fleets() {
		return total_fleets;
	}

	public void setTotal_fleets(long total_fleets) {
		this.total_fleets = total_fleets;
	}

	
}
