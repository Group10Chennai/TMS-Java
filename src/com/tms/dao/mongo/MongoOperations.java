package com.tms.dao.mongo;

import java.util.List;

import com.tms.beans.Response;
import com.tms.model.mongo.TPMSData_Report;
import com.tms.model.mongo.TPMSData_latest;

public interface MongoOperations {

	public Response saveOrUpdateTPMSDataLatest(TPMSData_latest latest);
	
	public Response saveTPMSDataReport(TPMSData_Report report);
		
	public Response getLiveData(List<Long> vehIds);
	
	public Response getProblematicVehicles(List<Long> vehIds, double pressureMinValue, double pressureMaxValue,
			double tempMinValue, double tempMaxValue);
	
	public Response getProblematicVehCount(List<Long> vehIds, double pressureMinValue, double pressureMaxValue,
			double tempMinValue, double tempMaxValue);
}
