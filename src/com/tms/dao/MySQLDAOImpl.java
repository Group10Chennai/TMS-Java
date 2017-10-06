package com.tms.dao;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tms.beans.MyConstants;
import com.tms.beans.Response;
import com.tms.model.TMSBController;
import com.tms.model.TMSBasicVehicleDetails;
import com.tms.model.TMSDepot;
import com.tms.model.Organizations;
import com.tms.model.TMSRFID;
import com.tms.model.TMSSensor;
import com.tms.model.TMSTire;
import com.tms.model.TMSTireInspection;
import com.tms.model.TMSTireService;
import com.tms.model.TMSTireShortDetails;
import com.tms.model.TMSTireView;
import com.tms.model.TMSTyreMake;
import com.tms.model.TMSUserVehicleMapping;
import com.tms.model.TMSVehicles;
import com.tms.model.TMSUserVehiclesView;
import com.tms.model.UserMaster;

@Repository("MySQLDAO")
public class MySQLDAOImpl implements MySQLDAO {

	@Autowired
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public UserMaster getLoginStatus(UserMaster user) {

		// UserMaster user;
		try {
			if ((user = (UserMaster) sessionFactory.getCurrentSession()
					.createQuery(
							"from UserMaster where userName='" + URLEncoder.encode(user.getUserName().trim(), "UTF-8")
									+ "' and password=md5('" + user.getPassword().trim() + "')")
					.uniqueResult()) != null) {
				return user;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public Response saveOrUpdate(TMSVehicles tmsVehicles) {

		Response response = new Response();
		response.setStatus(false);
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(tmsVehicles);
			response.setStatus(true);
			return response;
		} catch (Exception e) {
			response.setStatus(false);
			response.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public TMSVehicles getVehByName(String vehicleName) {
		TMSVehicles vehicle = new TMSVehicles();
		try {
			if ((vehicle = (TMSVehicles) sessionFactory.getCurrentSession()
					.createQuery("from TMSVehicles where vehName=:vehName").setParameter("vehName", vehicleName)
					.uniqueResult()) != null) {
				return vehicle;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TMSVehicles getVehById(long vehicleId) {

		TMSVehicles vehicle = new TMSVehicles();
		try {
			if ((vehicle = (TMSVehicles) sessionFactory.getCurrentSession()
					.createQuery("from TMSVehicles where vehId=:vehId").setParameter("vehId", vehicleId)
					.uniqueResult()) != null) {
				return vehicle;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Response deleteVehicle(TMSVehicles tmsVehicle) {

		Response response = new Response();
		response.setStatus(false);
		try {
			sessionFactory.getCurrentSession().delete(tmsVehicle);
			response.setStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.VEHICLE_DELETING_FAILED);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@Override
	public Response saveOrUpdateRFID(TMSRFID tmsRFID) {

		Response response = new Response();
		response.setStatus(false);
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(tmsRFID);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.RFID_ADDED_SUCCESSFULLY);
			return response;
		} catch (Exception e) {
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.RFID_ADDING_FAILED);
			response.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public TMSRFID getRFIDByRFIDUID(String RFIDUID) {

		TMSRFID rfidDetials = new TMSRFID();
		try {
			if ((rfidDetials = (TMSRFID) sessionFactory.getCurrentSession()
					.createQuery("from TMSRFID where RFIDUID=:RFIDUID").setParameter("RFIDUID", RFIDUID)
					.uniqueResult()) != null) {
				return rfidDetials;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TMSRFID getRFIDByRFID(long RFID) {

		TMSRFID rfidDetials = new TMSRFID();
		try {
			if ((rfidDetials = (TMSRFID) sessionFactory.getCurrentSession().createQuery("from TMSRFID where RFID=:RFID")
					.setParameter("RFID", RFID).uniqueResult()) != null) {
				return rfidDetials;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Response deleteRFIDByRFID(TMSRFID tmsRFID) {

		Response response = new Response();
		response.setStatus(false);
		try {
			sessionFactory.getCurrentSession().delete(tmsRFID);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.RFID_DELETED_SUCCESSFULL);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.RFID_DELETING_FAILED);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@Override
	public Response saveOrUpdateBController(TMSBController tmsBController) {
		Response response = new Response();
		response.setStatus(false);
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(tmsBController);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.BController_ADDED_SUCCESSFULLY);
			return response;
		} catch (Exception e) {
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.BController_ADDING_FAILED);
			response.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public TMSBController getBControllerByBCtrlUID(String BCtrlUID) {
		TMSBController tmsBController = new TMSBController();
		try {
			if ((tmsBController = (TMSBController) sessionFactory.getCurrentSession()
					.createQuery("from TMSBController where controllerUID=:controllerUID")
					.setParameter("controllerUID", BCtrlUID).uniqueResult()) != null) {
				return tmsBController;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TMSBController getBCtrlByBCtrlId(long BCtrlId) {
		TMSBController tmsBController = new TMSBController();
		try {
			if ((tmsBController = (TMSBController) sessionFactory.getCurrentSession()
					.createQuery("from TMSBController where controllerId=:controllerId")
					.setParameter("controllerId", BCtrlId).uniqueResult()) != null) {
				return tmsBController;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Response deleteBCtrlByBCtrl(TMSBController tmsBController) {
		Response response = new Response();
		response.setStatus(false);
		try {
			sessionFactory.getCurrentSession().delete(tmsBController);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.BController_DELETED_SUCCESSFULL);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.BController_DELETING_FAILED);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@Override
	public Response saveOrUpdateSensor(TMSSensor tmsSensor) {

		Response response = new Response();
		response.setStatus(false);
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(tmsSensor);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.SENSOR_ADDED_SUCCESSFULLY);
			return response;
		} catch (Exception e) {
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.SENSOR_ADDING_FAILED);
			response.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public TMSSensor getSensorBySensorUID(String sensorUID) {

		TMSSensor tmsSensor = new TMSSensor();
		try {
			if ((tmsSensor = (TMSSensor) sessionFactory.getCurrentSession()
					.createQuery("from TMSSensor where sensorUID=:sensorUID").setParameter("sensorUID", sensorUID)
					.uniqueResult()) != null) {
				return tmsSensor;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TMSSensor getSensorBySensorId(long sensorId) {

		TMSSensor tmsSensor = new TMSSensor();
		try {
			if ((tmsSensor = (TMSSensor) sessionFactory.getCurrentSession()
					.createQuery("from TMSSensor where sensorId=:sensorId").setParameter("sensorId", sensorId)
					.uniqueResult()) != null) {
				return tmsSensor;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Response deleteSensor(TMSSensor tmsSensor) {
		Response response = new Response();
		response.setStatus(false);
		try {
			sessionFactory.getCurrentSession().delete(tmsSensor);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.SENSOR_DELETED_SUCCESSFULL);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.SENSOR_DELETING_FAILED);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@Override
	public Response saveOrUpdateTire(TMSTire tmsTire) {
		Response response = new Response();
		response.setStatus(false);
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(tmsTire);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.TIRE_ADDED_SUCCESSFULLY);
			return response;
		} catch (Exception e) {
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.TIRE_ADDING_FAILED);
			response.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public TMSTire getTireByTireNumber(String tireNumber) {
		TMSTire tmsTire = new TMSTire();
		try {
			if ((tmsTire = (TMSTire) sessionFactory.getCurrentSession()
					.createQuery("from TMSTire where tireNumber=:tireNumber").setParameter("tireNumber", tireNumber)
					.uniqueResult()) != null) {
				return tmsTire;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TMSTire getTireByTireId(long tireId) {
		TMSTire tmsTire = new TMSTire();
		try {
			if ((tmsTire = (TMSTire) sessionFactory.getCurrentSession().createQuery("from TMSTire where tireId=:tireId")
					.setParameter("tireId", tireId).uniqueResult()) != null) {
				return tmsTire;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TMSTireView getTireViewByTireId(long tireId) {

		TMSTireView tmsTire = new TMSTireView();
		try {
			if ((tmsTire = (TMSTireView) sessionFactory.getCurrentSession()
					.createQuery("from TMSTireView where tireId=:tireId").setParameter("tireId", tireId)
					.uniqueResult()) != null) {
				return tmsTire;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Response deleteTire(TMSTire tmsTire) {
		Response response = new Response();
		response.setStatus(false);
		try {
			sessionFactory.getCurrentSession().delete(tmsTire);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.TIRE_DELETED_SUCCESSFULL);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.TIRE_DELETING_FAILED);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSUserVehiclesView> getVehicles() {
		List<TMSUserVehiclesView> vehiclesList = new ArrayList<>();
		try {
			vehiclesList = sessionFactory.getCurrentSession().createQuery("from TMSUserVehiclesView").list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vehiclesList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSUserVehiclesView> getVehiclesByLimit(long userId, int limit, int startIndex) {
		List<TMSUserVehiclesView> vehiclesList = new ArrayList<>();
		try {
			vehiclesList = sessionFactory.getCurrentSession()
					.createQuery(
							"from TMSUserVehiclesView where userId = :userId and status = :status order by veh_CreatedDateTime desc")
					.setParameter("userId", userId).setParameter("status", 1l).setMaxResults(limit)
					.setFirstResult(startIndex).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vehiclesList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSUserVehiclesView> getVehiclesByVehIds(List<Long> vehIds, long userId) {
		List<TMSUserVehiclesView> vehiclesList = new ArrayList<>();
		try {
			if (userId != 0) {
				vehiclesList = sessionFactory.getCurrentSession()
						.createQuery("from TMSUserVehiclesView where userId = :userId and vehId in (:vehIds)"
								+ "and status = :status order by veh_CreatedDateTime desc")
						.setParameterList("vehIds", vehIds).setParameter("status", 1l).setParameter("userId", userId)
						.list();
			} else {
				vehiclesList = sessionFactory.getCurrentSession()
						.createQuery("from TMSUserVehiclesView where vehId in (:vehIds)"
								+ "and status = :status order by veh_CreatedDateTime desc")
						.setParameterList("vehIds", vehIds).setParameter("status", 1l).list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vehiclesList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSUserVehiclesView> searchVehicles(String searchWord, long userId) {
		List<TMSUserVehiclesView> veh_list = new ArrayList<>();
		try {
			veh_list = sessionFactory.getCurrentSession()
					.createQuery("from TMSUserVehiclesView where userId = :userId and status = :status"
							+ " and (vehName like :searchWord or RFIDUID like :searchWord or "
							+ " controllerUID like :searchWord) order by veh_CreatedDateTime desc")
					.setParameter("searchWord", "%" + searchWord + "%").setParameter("status", 1l)
					.setParameter("userId", userId).list();
			if (veh_list.size() > 0) {
				// Vehicles found
				// Find tire detials
				for (TMSUserVehiclesView vehicle : veh_list) {
					List<TMSTire> tires = getTiresByVehId(vehicle.getVehId());
					vehicle.setTires(tires);
				}
			} else {
				// Search tires then vehicles
				List<TMSTire> tire_list = sessionFactory.getCurrentSession()
						.createQuery("from TMSTire where tireNumber like :searchWord")
						.setParameter("searchWord", "%" + searchWord + "%").list();
				if (tire_list.size() > 0) {
					List<Long> vehIds = new ArrayList<>(tire_list.size());
					for (TMSTire tire : tire_list) {
						vehIds.add(tire.getVehId());
					}
					veh_list = getVehiclesByVehIds(vehIds, userId);
					if (veh_list.size() > 0) {
						// Vehicles found
						// Find tire detials
						for (TMSUserVehiclesView vehicle : veh_list) {
							List<TMSTire> tires = getTiresByVehId(vehicle.getVehId());
							vehicle.setTires(tires);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return veh_list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSRFID> getRFID(String status) {
		List<TMSRFID> rfids = new ArrayList<>();
		try {
			if (null != status) {
				rfids = sessionFactory.getCurrentSession().createQuery("from TMSRFID where status=:status")
						.setParameter("status", status).list();
			} else {
				rfids = sessionFactory.getCurrentSession().createQuery("from TMSRFID").list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rfids;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSBController> getBController(String status) {
		List<TMSBController> bControllers = new ArrayList<>();
		try {
			if (null != status) {
				bControllers = sessionFactory.getCurrentSession()
						.createQuery("from TMSBController where status=:status").setParameter("status", status).list();
			} else {
				bControllers = sessionFactory.getCurrentSession().createQuery("from TMSBController").list();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bControllers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSSensor> getSensors(String status, int limit, int startIndex) {
		List<TMSSensor> sensors = new ArrayList<>();
		try {
			if (null != status) {
				sensors = sessionFactory.getCurrentSession()
						.createQuery("from TMSSensor where status=:status order by createdDateTime desc ")
						.setParameter("status", status).list();
			} else {
				sensors = sessionFactory.getCurrentSession()
						.createQuery("from TMSSensor order by createdDateTime desc ").setMaxResults(limit)
						.setFirstResult(startIndex).list();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sensors;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSTireView> getTireViewDetials(String status) {
		List<TMSTireView> tires = new ArrayList<>();
		try {
			if (null != status && status.equalsIgnoreCase(MyConstants.STATUS_INSTOCK)) {
				tires = sessionFactory.getCurrentSession()
						.createQuery("from TMSTireView where status=:status and sensorId != 0")
						.setParameter("status", status).list();
			} else if (null != status) {
				tires = sessionFactory.getCurrentSession().createQuery("from TMSTireView where status=:status")
						.setParameter("status", status).list();
			} else {
				tires = sessionFactory.getCurrentSession().createQuery("from TMSTireView").list();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tires;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSTire> getTiresByVehId(long vehId) {

		List<TMSTire> tires = new ArrayList<>();
		try {

			tires = sessionFactory.getCurrentSession().createQuery("from TMSTire where vehId=:vehId")
					.setParameter("vehId", vehId).list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tires;
	}

	@Override
	public Response saveOrUpdateOrg(Organizations org) {
		Response response = new Response();
		response.setStatus(false);
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(org);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.ORG_ADDING_SUCCESS);
			return response;
		} catch (Exception e) {
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.ORG_ADDING_FAILED);
			response.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Organizations getOrgByName(String name) {
		Organizations org = new Organizations();
		try {
			if ((org = (Organizations) sessionFactory.getCurrentSession()
					.createQuery("from Organizations where orgName=:orgName").setParameter("orgName", name)
					.uniqueResult()) != null) {
				return org;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Organizations> getAllTMSOrgs() {
		List<Organizations> orgs = new ArrayList<>();
		try {
			orgs = sessionFactory.getCurrentSession().createQuery("from Organizations").list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orgs;
	}

	@Override
	public Response saveOrUpdateDepot(TMSDepot depot) {
		Response response = new Response();
		response.setStatus(false);
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(depot);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.DEPOT_ADDING_SUCCESS);
			return response;
		} catch (Exception e) {
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.DEPOT_ADDING_FAILED);
			response.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public TMSDepot getTMSDepotByName(String name) {
		TMSDepot depot = new TMSDepot();
		try {
			if ((depot = (TMSDepot) sessionFactory.getCurrentSession()
					.createQuery("from TMSDepot where depotName=:depotName").setParameter("depotName", name)
					.uniqueResult()) != null) {
				return depot;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TMSDepot getTMSDepotById(long depotId) {
		TMSDepot depot = new TMSDepot();
		try {
			if ((depot = (TMSDepot) sessionFactory.getCurrentSession()
					.createQuery("from TMSDepot where depotId=:depotId").setParameter("depotId", depotId)
					.uniqueResult()) != null) {
				return depot;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSDepot> getAllTMSDepots(long orgId) {
		List<TMSDepot> depots = new ArrayList<>();
		try {
			depots = sessionFactory.getCurrentSession().createQuery("from TMSDepot where orgId=:orgId")
					.setParameter("orgId", orgId).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return depots;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSTyreMake> getAllTMSTyreMake(long orgId) {
		List<TMSTyreMake> tyreMakeList = new ArrayList<>();
		try {
			tyreMakeList = sessionFactory.getCurrentSession().createQuery("from TMSTyreMake where orgId=:orgId")
					.setParameter("orgId", orgId).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tyreMakeList;
	}

	@Override
	public TMSRFID getRFIDByVehId(long vehId) {
		TMSRFID rfidDetials = new TMSRFID();
		try {
			if ((rfidDetials = (TMSRFID) sessionFactory.getCurrentSession()
					.createQuery("from TMSRFID where vehId=:vehId").setParameter("vehId", vehId)
					.uniqueResult()) != null) {
				return rfidDetials;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TMSBController getBCtrlByVehId(long vehId) {
		TMSBController tmsBController = new TMSBController();
		try {
			if ((tmsBController = (TMSBController) sessionFactory.getCurrentSession()
					.createQuery("from TMSBController where vehId=:vehId").setParameter("vehId", vehId)
					.uniqueResult()) != null) {
				return tmsBController;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserMaster> getAllTMSUsersByOrgId(long orgId) {

		List<UserMaster> userList = new ArrayList<>();
		try {
			if (orgId == 0) {
				// Get all TMS enabled users List
				userList = sessionFactory.getCurrentSession()
						.createQuery("from UserMaster where TMSLoginStatus=:TMSLoginStatus")
						.setParameter("TMSLoginStatus", true).list();
			} else {
				userList = sessionFactory.getCurrentSession()
						.createQuery("from UserMaster where TMSLoginStatus=:TMSLoginStatus and orgId=:orgId")
						.setParameter("TMSLoginStatus", true).setParameter("orgId", orgId).list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UserMaster> getAllTMSUsersByUserIds(List<Long> userIds) {

		List<UserMaster> userList = new ArrayList<>();
		try {
			userList = sessionFactory.getCurrentSession().createQuery("from UserMaster where userId IN (:userIds)")
					.setParameterList("userIds", userIds).list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return userList;
	}

	@Override
	public TMSSensor getSensorByTireId(long tireId) {
		TMSSensor sensor = new TMSSensor();
		try {
			if ((sensor = (TMSSensor) sessionFactory.getCurrentSession()
					.createQuery("from TMSSensor where tireId=:tireId").setParameter("tireId", tireId)
					.uniqueResult()) != null) {
				return sensor;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSTireInspection> getTMSTireInspections(long userId, int limit, int startIndex) {

		List<TMSTireInspection> tireInspections = new ArrayList<>();
		try {
			if (userId != 0) {
				tireInspections = sessionFactory.getCurrentSession()
						.createQuery("from TMSTireInspection where createdBy=:createdBy")
						.setParameter("createdBy", userId).setMaxResults(limit).setFirstResult(startIndex).list();
			} else {
				tireInspections = sessionFactory.getCurrentSession().createQuery("from TMSTireInspection")
						.setMaxResults(limit).setFirstResult(startIndex).list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tireInspections;
	}

	@Override
	public Response saveOrUpdateTireInspection(TMSTireInspection inspection) {
		Response response = new Response();
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(inspection);

			response.setStatus(true);
			response.setDisplayMsg(MyConstants.TIRE_INSPECTION_ADDING_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.TIRE_INSPECTION_ADDING_FAILED);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSTireShortDetails> getShortTireDetails() {
		List<TMSTireShortDetails> tireDetails = new ArrayList<>();
		try {
			tireDetails = sessionFactory.getCurrentSession().createQuery("from TMSTireShortDetails").list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tireDetails;
	}

	@Override
	public TMSTireInspection getTMSTireInspectionById(long inspectionId) {
		TMSTireInspection inspection = new TMSTireInspection();
		try {
			if ((inspection = (TMSTireInspection) sessionFactory.getCurrentSession()
					.createQuery("from TMSTireInspection where tireInspectionId=:tireInspectionId")
					.setParameter("tireInspectionId", inspectionId).uniqueResult()) != null) {
				return inspection;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSTireService> getTMSTireServices(long orgId, long userId, int limit, int startIndex) {

		List<TMSTireService> tireServices = new ArrayList<>();
		try {
			if (userId != 0) {
				tireServices = sessionFactory.getCurrentSession()
						.createQuery("from TMSTireService where createdBy=:createdBy").setParameter("createdBy", userId)
						.setMaxResults(limit).setFirstResult(startIndex).list();
			} else if (orgId != 0) {
				tireServices = sessionFactory.getCurrentSession().createQuery("from TMSTireService where orgId=:orgId")
						.setParameter("orgId", orgId).setMaxResults(limit).setFirstResult(startIndex).list();
			} else {
				tireServices = sessionFactory.getCurrentSession().createQuery("from TMSTireService")
						.setMaxResults(limit).setFirstResult(startIndex).list();
			}
			System.out.println("in daoimple " + tireServices.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tireServices;
	}

	@Override
	public Response saveOrUpdateTireServices(TMSTireService service) {

		Response response = new Response();
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(service);

			response.setStatus(true);
			response.setDisplayMsg(MyConstants.TIRE_SERVICE_ADDING_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.TIRE_SERVICE_ADDING_FAILED);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@Override
	public TMSTireService getTMSTireServiceById(long serviceId) {
		TMSTireService service = new TMSTireService();
		try {
			if ((service = (TMSTireService) sessionFactory.getCurrentSession()
					.createQuery("from TMSTireService where tireServiceId=:tireServiceId")
					.setParameter("tireServiceId", serviceId).uniqueResult()) != null) {
				return service;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSUserVehiclesView> getModifiedVehDetails(Date lastUpdatedDate) {
		List<TMSUserVehiclesView> veh_list = new ArrayList<>();
		DateFormat df = new SimpleDateFormat(MyConstants.MYSQL_DATE_TIME_FORMATER);
		try {
			veh_list = sessionFactory.getCurrentSession()
					.createQuery("from TMSUserVehiclesView where " + "RFID_UpdatedDateTime > :RFID_UpdatedDateTime or "
							+ "Vehicle_UpdatedDateTime > :Vehicle_UpdatedDateTime or "
							+ "BController_UpdatedDateTime > :BController_UpdatedDateTime")
					.setParameter("RFID_UpdatedDateTime", df.format(lastUpdatedDate))
					.setParameter("Vehicle_UpdatedDateTime", df.format(lastUpdatedDate))
					.setParameter("BController_UpdatedDateTime", df.format(lastUpdatedDate)).list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return veh_list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSTireView> getModifiedTiresDetails(Date lastUpdatedDate) {

		List<TMSTireView> tire_list = new ArrayList<>();
		DateFormat df = new SimpleDateFormat(MyConstants.MYSQL_DATE_TIME_FORMATER);
		try {
			tire_list = sessionFactory.getCurrentSession()
					.createQuery("from TMSTireView where "
							+ "(tireUpdatedDateTime > :updatedDateTime or sensorUpdatedDateTime > :updatedDateTime)"
							+ " and SensorId > 0")
					.setParameter("updatedDateTime", df.parse(df.format(lastUpdatedDate))).list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tire_list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSBasicVehicleDetails> getAllBasicVehDetials(long orgId) {

		List<TMSBasicVehicleDetails> veh_list = new ArrayList<>();
		try {
			if (orgId == 0) {
				veh_list = sessionFactory.getCurrentSession().createQuery("from TMSBasicVehicleDetails").list();
			} else {
				veh_list = sessionFactory.getCurrentSession()
						.createQuery("from TMSBasicVehicleDetails where " + "orgId = :orgId")
						.setParameter("orgId", orgId).list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return veh_list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSBasicVehicleDetails> getAllBasicVehDetialsByVehIds(List<Long> vehIds) {

		List<TMSBasicVehicleDetails> veh_list = new ArrayList<>();
		try {

			veh_list = sessionFactory.getCurrentSession()
					.createQuery("from TMSBasicVehicleDetails where vehId IN (:vehIds)")
					.setParameterList("vehIds", vehIds).list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return veh_list;
	}

	@Override
	public Response saveOrUpdateUserVehMapping(TMSUserVehicleMapping userVehMapping) {
		Response response = new Response();
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(userVehMapping);
			response.setStatus(true);
			response.setDisplayMsg(MyConstants.VEHICLE_MAPPED_USER_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.VEHICLE_MAPPED_USER_FAILED);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSUserVehicleMapping> getVehicleIdsByUserId(long userId) {
		List<TMSUserVehicleMapping> userVehicleMappings = new ArrayList<>();
		try {
			userVehicleMappings = sessionFactory.getCurrentSession()
					.createQuery("from TMSUserVehicleMapping where userId = :userId and status = :status")
					.setParameter("userId", userId).setParameter("status", 1l).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userVehicleMappings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSBasicVehicleDetails> getAllBasicVehDetialsByUserId(long userId) {
		List<TMSBasicVehicleDetails> veh_list = new ArrayList<>();
		try {

			veh_list = sessionFactory.getCurrentSession()
					.createQuery("from TMSBasicVehicleDetails where userId = :userId and status = :status")
					.setParameter("userId", userId).setParameter("status", 1l).list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return veh_list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSTireService> getServicesBetweenDate(Date date, long tireId) {

		List<TMSTireService> service_list = new ArrayList<>();
		try {
			service_list = sessionFactory.getCurrentSession()
					.createQuery("from TMSTireService where (:date between fittedDate and removalDate "
							+ "or :date = fittedDate or :date = removalDate) and tireId = :tireId")
					.setParameter("date", date).setParameter("tireId", tireId).list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return service_list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSTireService> getServicesBetweenDate(Date fitmentDate, Date removalDate, long tireId) {
		List<TMSTireService> service_list = new ArrayList<>();
		try {
			service_list = sessionFactory.getCurrentSession().createQuery(
					"from TMSTireService where tireId = :tireId and (:fDate between fittedDate and removalDate "
							+ "or :fDate = fittedDate or :fDate = removalDate "
							+ "or :rDate between fittedDate and removalDate "
							+ "or :rDate = fittedDate or :rDate = removalDate "
							+ "or fittedDate between :fDate and :rDate " + "or removalDate between :fDate and :rDate)")
					.setParameter("fDate", fitmentDate).setParameter("rDate", removalDate)
					.setParameter("tireId", tireId).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return service_list;
	}

	@Override
	public long getVehiclesCountByUserId(long userId) {
		try {
			Query q =  sessionFactory.getCurrentSession()
			.createQuery("select count(*) from TMSUserVehiclesView where userId = :userId and status = :status")
			.setParameter("userId", userId)
			.setParameter("status", 1l);
			System.out.println(q.toString());
			long count = (long) q.uniqueResult();
			System.out.println(count);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
