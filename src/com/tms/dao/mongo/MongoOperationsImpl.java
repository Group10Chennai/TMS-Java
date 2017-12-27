package com.tms.dao.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.tms.beans.MyConstants;
import com.tms.beans.Response;
import com.tms.model.mongo.TPMSData_Report;
import com.tms.model.mongo.TPMSData_latest;

@Service("MongoOperations")
public class MongoOperationsImpl implements MongoOperations {

	@Autowired
	private MongoTemplate mongoTemplate_Source;

	// { "vehId": {"$in":[4,5,6]},
	// "$or": [ {"tyres.pressure":{"$lte": 65} }, { "tyres.pressure":{"$gte":
	// 75} } ]
	// }
	// { "vehId": {"$in":[4,5,6]}, "$or": [ {"tyres.pressure":{"$lte": 65} }, {
	// "tyres.pressure":{"$gte": 75} } ] }
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

	public Response saveTPMSDataReport(TPMSData_Report report) {
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
			double tempMinValue, double tempMaxValue, boolean needList) {
		Response response = new Response();
		try {
			Query query2 = new Query();
			query2.addCriteria(Criteria.where("vehId").in(vehIds));
			query2.addCriteria(Criteria.where("tyres.pressure").exists(true).orOperator(
					Criteria.where("tyres.pressure").gt(pressureMaxValue),
					Criteria.where("tyres.pressure").lt(pressureMinValue),
					Criteria.where("tyres.temp").gt(tempMaxValue),
					Criteria.where("tyres.temp").lt(tempMinValue)));
			
			long badVehCount = mongoTemplate_Source.count(query2, TPMSData_latest.class);
			response.setCount(badVehCount);
			// If need list
			if (needList) {
				query2.with(new Sort(Sort.Direction.DESC, "device_date_time"));
				List<TPMSData_latest> list = mongoTemplate_Source.find(query2, TPMSData_latest.class);
				response.setResult(list);
			}
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
	public Response getTempPressureData(List<Long> vehIds, boolean needlist) {
		Response response = new Response();
		try {
			Query query2 = new Query();
			query2.addCriteria(Criteria.where("vehId").in(vehIds));

			long count = mongoTemplate_Source.count(query2, TPMSData_latest.class);

			if (needlist) {
				// Find data
				query2.with(new Sort(Sort.Direction.DESC, "device_date_time"));
				List<TPMSData_latest> list = mongoTemplate_Source.find(query2, TPMSData_latest.class);
				response.setResult(list);
			}
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

	// @Override
	// public Response getNonProblematicVehicles(List<Long> vehIds, double
	// pressureMinValue, double pressureMaxValue,
	// double tempMinValue, double tempMaxValue) {
	// Response response = new Response();
	// try {
	// Query query2 = new Query();
	// query2.addCriteria(Criteria.where("vehId").in(vehIds));
	// query2.addCriteria(Criteria.where("tyres.pressure").exists(true).orOperator(
	// Criteria.where("tyres.pressure").lt(pressureMaxValue),
	// Criteria.where("tyres.pressure").gt(pressureMinValue),
	// Criteria.where("tyres.temp").lt(tempMaxValue),
	// Criteria.where("tyres.temp").gt(tempMinValue)));
	//
	// List<TPMSData_latest> liveData = mongoTemplate_Source.find(query2,
	// TPMSData_latest.class);
	// response.setResult(liveData);
	// response.setCount(liveData.size());
	// response.setStatus(true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// response.setStatus(false);
	// response.setDisplayMsg(MyConstants.LATEST_DATA_GET_FAIL);
	// response.setErrorMsg(e.getMessage());
	// }
	// return response;
	// }

	// @Override
	// public Response getProblematicVehCount(List<Long> vehIds, double
	// pressureMinValue, double pressureMaxValue,
	// double tempMinValue, double tempMaxValue) {
	// Response response = new Response();
	// try {
	// Query query2 = new Query();
	// query2.addCriteria(Criteria.where("vehId").in(vehIds));
	// query2.addCriteria(Criteria.where("tyres.pressure").exists(true).orOperator(
	// Criteria.where("tyres.pressure").gte(pressureMaxValue),
	// Criteria.where("tyres.pressure").lte(pressureMinValue),
	// Criteria.where("tyres.temp").gte(tempMaxValue),
	// Criteria.where("tyres.temp").lte(tempMinValue)));
	//
	// System.out.println("Problem : "+query2.toString());
	//// long count = mongoTemplate_Source.count(query2, TPMSData_latest.class);
	// List<TPMSData_latest> list = mongoTemplate_Source.find(query2,
	// TPMSData_latest.class);
	// response.setCount(list.size());
	// response.setResult(list);
	//
	// response.setStatus(true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// response.setStatus(false);
	// response.setDisplayMsg(MyConstants.LATEST_DATA_GET_FAIL);
	// response.setErrorMsg(e.getMessage());
	// }
	// return response;
	// }
	//
	// @Override
	// public Response getNonProblematicVehCount(List<Long> vehIds, double
	// pressureMinValue, double pressureMaxValue,
	// double tempMinValue, double tempMaxValue) {
	// Response response = new Response();
	// try {
	// Query query2 = new Query();
	// query2.addCriteria(Criteria.where("vehId").in(vehIds));
	// query2.addCriteria(Criteria.where("tyres.pressure").exists(true).andOperator(
	// Criteria.where("tyres.pressure").lt(pressureMaxValue),
	// Criteria.where("tyres.pressure").gt(pressureMinValue),
	// Criteria.where("tyres.temp").lt(tempMaxValue),
	// Criteria.where("tyres.temp").gte(tempMinValue)));
	//
	// System.out.println("Non Problem : "+query2.toString());
	//// long count = mongoTemplate_Source.count(query2, TPMSData_latest.class);
	// List<TPMSData_latest> list = mongoTemplate_Source.find(query2,
	// TPMSData_latest.class);
	// response.setCount(list.size());
	// response.setResult(list);
	// response.setStatus(true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// response.setStatus(false);
	// response.setDisplayMsg(MyConstants.LATEST_DATA_GET_FAIL);
	// response.setErrorMsg(e.getMessage());
	// }
	// return response;
	// }

}
