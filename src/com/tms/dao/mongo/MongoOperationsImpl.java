package com.tms.dao.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.tms.beans.MyConstants;
import com.tms.beans.Response;
import com.tms.model.mongo.TPMSData_Report;
import com.tms.model.mongo.TPMSData_latest;

@Service("MongoOperations")
public class MongoOperationsImpl implements MongoOperations{

	@Autowired
	private MongoTemplate mongoTemplate_Source;

//	{ "vehId": {"$in":[4,5,6]}, 
//		"$or":  [ {"tyres.pressure":{"$lte": 65} }, { "tyres.pressure":{"$gte": 75} } ] 
//	}
//	{ "vehId": {"$in":[4,5,6]}, "$or":  [ {"tyres.pressure":{"$lte": 65} }, { "tyres.pressure":{"$gte": 75} } ] }
	@Override
	public Response saveOrUpdateTPMSDataLatest(TPMSData_latest latest) {
		Response response = new Response();
		try {
			mongoTemplate_Source.save(latest);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.LATEST_DATA_INSERT_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.LATEST_DATA_INSERT_FAIL);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}
	
	public Response saveTPMSDataReport(TPMSData_Report report){
		Response response = new Response();
		try {
			mongoTemplate_Source.insert(report);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.LATEST_DATA_INSERT_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.LATEST_DATA_INSERT_FAIL);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}
	
	@Override
	public Response getLiveData(List<Long> vehIds) {
		Response response = new Response();
		try {
			Query query2 = new Query();
			query2.addCriteria(Criteria.where("vehId").in(vehIds));
			
			List<TPMSData_latest> liveData = mongoTemplate_Source.find(query2, TPMSData_latest.class);
			response.setResult(liveData);
			response.setStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.LATEST_DATA_GET_FAIL);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}
	
	@Override
	public Response getProblematicVehicles(List<Long> vehIds, double pressureMinValue, double pressureMaxValue,
			double tempMinValue, double tempMaxValue) {
		Response response = new Response();
		try {
			Query query2 = new Query();
			query2.addCriteria(Criteria.where("vehId").in(vehIds));
			query2.addCriteria(Criteria.where("tyres.pressure").exists(true).orOperator(
					Criteria.where("tyres.pressure").gte(pressureMaxValue),
					Criteria.where("tyres.pressure").lte(pressureMinValue),
					Criteria.where("tyres.temp").gte(tempMaxValue),
					Criteria.where("tyres.temp").lte(tempMinValue)));

			List<TPMSData_latest> liveData = mongoTemplate_Source.find(query2, TPMSData_latest.class);
			response.setResult(liveData);
			response.setCount(liveData.size());
			response.setStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.LATEST_DATA_GET_FAIL);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}
	
	@Override
	public Response getProblematicVehCount(List<Long> vehIds, double pressureMinValue, double pressureMaxValue,
			double tempMinValue, double tempMaxValue) {
		Response response = new Response();
		try {
			Query query2 = new Query();
			query2.addCriteria(Criteria.where("vehId").in(vehIds));
			query2.addCriteria(Criteria.where("tyres.pressure").exists(true).orOperator(
					Criteria.where("tyres.pressure").gte(pressureMaxValue),
					Criteria.where("tyres.pressure").lte(pressureMinValue),
					Criteria.where("tyres.temp").gte(tempMaxValue),
					Criteria.where("tyres.temp").lte(tempMinValue)));
			long count = mongoTemplate_Source.count(query2, TPMSData_latest.class);
			response.setCount(count);
			response.setStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.LATEST_DATA_GET_FAIL);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}
	
}
