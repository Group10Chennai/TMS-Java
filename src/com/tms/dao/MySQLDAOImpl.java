package com.tms.dao;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.tms.beans.MyConstants;
import com.tms.beans.Response;
import com.tms.beans.VehicleTyreCount;
import com.tms.model.TMSBController;
import com.tms.model.TMSBasicVehicleDetails;
import com.tms.model.TMSDepot;
import com.tms.model.TMSMinMaxTempPressure;
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
import com.tms.model.TMSUserVehiclesView_CompositeKey;
import com.tms.model.TMSUserVehiclesView_Keys;
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

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSVehicles> getVehByVehIds(List<Long> vehIds) {

		List<TMSVehicles> vehicles = new ArrayList<>();
		try {
			if (null != vehIds) {
				vehicles = sessionFactory.getCurrentSession().createQuery("from TMSVehicles where vehId in (:vehIds)")
						.setParameterList("vehIds", vehIds).list();
				return vehicles;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vehicles;
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
	public List<TMSBasicVehicleDetails> getVehicles() {
		List<TMSBasicVehicleDetails> vehiclesList = new ArrayList<>();
		try {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(TMSBasicVehicleDetails.class)
					.setProjection(Projections.projectionList().add(Projections.property("vehId"), "vehId")
							.add(Projections.property("vehName"), "vehName").add(Projections.property("orgId"), "orgId")
							.add(Projections.groupProperty("vehId"), "vehId"))
					.setResultTransformer(Transformers.aliasToBean(TMSBasicVehicleDetails.class));

			vehiclesList = cr.list();
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
			if (userId != 0) {
				vehiclesList = sessionFactory.getCurrentSession()
						.createQuery(
								"from TMSUserVehiclesView where userId = :userId and status = :status order by veh_CreatedDateTime desc")
						.setParameter("userId", userId).setParameter("status", 1l).setMaxResults(limit)
						.setFirstResult(startIndex).list();
			} else {
				Criteria cr = sessionFactory.getCurrentSession().createCriteria(TMSUserVehiclesView.class)
						.setProjection(Projections.projectionList().add(Projections.property("vehId"), "vehId")
								.add(Projections.property("vehName"), "vehName")
								.add(Projections.property("depotId"), "depotId")
								.add(Projections.property("orgId"), "orgId").add(Projections.property("RFID"), "RFID")
								.add(Projections.property("RFIDUID"), "RFIDUID")
								.add(Projections.property("controllerID"), "controllerID")
								.add(Projections.property("controllerUID"), "controllerUID")
								.add(Projections.property("rfid_UpdatedDateTime"), "rfid_UpdatedDateTime")
								.add(Projections.property("bCtrl_UpdatedDateTime"), "bCtrl_UpdatedDateTime")
								.add(Projections.property("veh_UpdatedDateTime"), "veh_UpdatedDateTime")
								.add(Projections.groupProperty("vehId"), "vehId"))
						.setResultTransformer(Transformers.aliasToBean(TMSUserVehiclesView.class));

				vehiclesList = cr.setMaxResults(limit).setFirstResult(startIndex).list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vehiclesList;
	}

	// @SuppressWarnings("unchecked")
	// @Override
	// public List<TMSUserVehiclesView> getUserVehDetailsByLimit(long userId,
	// int limit, int startIndex) {
	// List<TMSUserVehiclesView> vehiclesList = new ArrayList<>();
	// try {
	// if (userId != 0) {
	// vehiclesList = sessionFactory.getCurrentSession()
	// .createQuery(
	// "from TMSUserVehiclesView where userId = :userId and status = :status
	// order by veh_CreatedDateTime desc")
	// .setParameter("userId", userId).setParameter("status",
	// 1l).setMaxResults(limit)
	// .setFirstResult(startIndex).list();
	// } else {
	// vehiclesList = sessionFactory.getCurrentSession()
	// .createQuery("from TMSUserVehiclesView order by veh_CreatedDateTime
	// desc").setMaxResults(limit)
	// .setFirstResult(startIndex).list();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return vehiclesList;
	// }

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
	public Response searchVehicles(String searchWord, long userId, int limit, int startIndex) {
		Response response = new Response();
		List<TMSUserVehiclesView> veh_list = new ArrayList<>();
		try {
			veh_list = sessionFactory.getCurrentSession()
					.createQuery("from TMSUserVehiclesView where userId = :userId and status = :status"
							+ " and (vehName like :searchWord or RFIDUID like :searchWord or "
							+ " controllerUID like :searchWord) order by veh_CreatedDateTime desc")
					.setParameter("searchWord", "%" + searchWord + "%").setParameter("status", 1l)
					.setParameter("userId", userId).setMaxResults(limit).setFirstResult(startIndex).list();

			Query q = sessionFactory.getCurrentSession()
					.createQuery("select count(*) from TMSUserVehiclesView where userId = :userId and status = :status"
							+ " and (vehName like :searchWord or RFIDUID like :searchWord or "
							+ " controllerUID like :searchWord) order by veh_CreatedDateTime desc")
					.setParameter("searchWord", "%" + searchWord + "%").setParameter("status", 1l)
					.setParameter("userId", userId);

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
						.setParameter("searchWord", "%" + searchWord + "%").setMaxResults(limit)
						.setFirstResult(startIndex).list();

				q = sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSTire where tireNumber like :searchWord")
						.setParameter("searchWord", "%" + searchWord + "%");

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
			response.setStatus(true);
			response.setResult(veh_list);
			try {
				// Find the count
				response.setCount((long) q.uniqueResult());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSTireView> searchTires(String searchWord, long orgId, int limit, int startIndex) {
		List<TMSTireView> tires = new ArrayList<>();
		try {
			if (orgId != 0) {
				// Based on Org
				Query q = sessionFactory.getCurrentSession()
						.createQuery("from TMSTireView where orgId = :orgId"
								+ " and (tireNumber like :searchWord or tireMake like :searchWord or"
								+ " tireType like :searchWord or status like :searchWord or "
								+ " totalTyreKM like :searchWord or tirePosition like :searchWord or "
								+ " threadDepth like :searchWord or sensorUID like :searchWord or "
								+ " depotName like :searchWord or vehName like :searchWord or "
								+ " tireType like :searchWord) order by Tire_CreatedDateTime desc")
						.setParameter("searchWord", "%" + searchWord + "%").setParameter("orgId", orgId);
				tires = q.setMaxResults(limit).setFirstResult(startIndex).list();
			} else {
				// For SysAdmin
				tires = sessionFactory.getCurrentSession()
						.createQuery(
								"from TMSTireView where (tireNumber like :searchWord or tireMake like :searchWord or"
										+ " tireType like :searchWord or status like :searchWord or "
										+ " totalTyreKM like :searchWord or tirePosition like :searchWord or "
										+ " threadDepth like :searchWord or sensorUID like :searchWord or "
										+ " depotName like :searchWord or vehName like :searchWord or "
										+ " tireType like :searchWord) order by Tire_CreatedDateTime desc")
						.setParameter("searchWord", "%" + searchWord + "%").setMaxResults(limit)
						.setFirstResult(startIndex).list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tires;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response getRFID(String status, String searchWord, int limit, int startIndex) {
		Response resp = new Response();
		try {
			Query q = sessionFactory.getCurrentSession().createQuery("from TMSRFID order by createdDateTime desc ")
					.setMaxResults(limit).setFirstResult(startIndex);
			Query q1 = sessionFactory.getCurrentSession().createQuery("select count(*) from TMSRFID");

			if (null != status && null != searchWord) {
				q = sessionFactory.getCurrentSession()
						.createQuery("from TMSRFID where status = :status and (RFIDUID like :searchWord "
								+ "or status like :searchWord) order by createdDateTime desc ")
						.setParameter("status", status).setParameter("searchWord", "%" + searchWord + "%")
						.setMaxResults(limit).setFirstResult(startIndex);
				q1 = sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSRFID where status = :status and "
								+ "(RFIDUID like :searchWord or status like :searchWord)")
						.setParameter("status", status).setParameter("searchWord", "%" + searchWord + "%");
			} else if (null != status) {
				// Used to assing bluetooth controller to vehicle // No limit
				q = sessionFactory.getCurrentSession()
						.createQuery("from TMSRFID where status=:status order by createdDateTime desc ")
						.setParameter("status", status);
				q1 = sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSRFID where " + "status=:status")
						.setParameter("status", status);
			} else if (null != searchWord) {
				q = sessionFactory.getCurrentSession()
						.createQuery("from TMSRFID where RFIDUID like :searchWord "
								+ "or status like :searchWord order by createdDateTime desc ")
						.setParameter("searchWord", "%" + searchWord + "%").setMaxResults(limit)
						.setFirstResult(startIndex);
				q1 = sessionFactory.getCurrentSession().createQuery(
						"select count(*) from TMSRFID where RFIDUID like :searchWord " + "or status like :searchWord")
						.setParameter("searchWord", "%" + searchWord + "%");
			}
			List<TMSRFID> rfids = q.list();
			long rfuidCount = (long) q1.uniqueResult();
			resp.setResult(rfids);
			resp.setCount(rfuidCount);
			resp.setStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(false);
			resp.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			resp.setErrorMsg(e.getMessage());
		}
		return resp;
		// List<TMSRFID> rfids = new ArrayList<>();
		// try {
		// if (null != status) {
		// rfids = sessionFactory.getCurrentSession().createQuery("from TMSRFID
		// where status=:status")
		// .setParameter("status", status).list();
		// } else {
		// rfids = sessionFactory.getCurrentSession().createQuery("from
		// TMSRFID").list();
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return rfids;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response getBController(String status, String searchWord, int limit, int startIndex) {
		Response resp = new Response();
		try {
			Query q = sessionFactory.getCurrentSession()
					.createQuery("from TMSBController order by createdDateTime desc").setMaxResults(limit)
					.setFirstResult(startIndex);
			Query q1 = sessionFactory.getCurrentSession().createQuery("select count(*) from TMSBController");
			if (null != status && null != searchWord) {
				q = sessionFactory.getCurrentSession()
						.createQuery("from TMSBController where status = :status and (controllerUID like :searchWord "
								+ "or status like :searchWord) order by createdDateTime desc ")
						.setParameter("status", status).setParameter("searchWord", "%" + searchWord + "%")
						.setMaxResults(limit).setFirstResult(startIndex);
				q1 = sessionFactory.getCurrentSession()
						.createQuery(
								"select count(*) from TMSBController where status = :status and (controllerUID like :searchWord "
										+ "or status like :searchWord)")
						.setParameter("status", status).setParameter("searchWord", "%" + searchWord + "%");
			} else if (null != status) {
				// Used to assing bluetooth controller to vehicle // No limit
				q = sessionFactory.getCurrentSession()
						.createQuery("from TMSBController where status=:status order by createdDateTime desc ")
						.setParameter("status", status);
				q1 = sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSBController where status=:status")
						.setParameter("status", status);
			} else if (null != searchWord) {
				q = sessionFactory.getCurrentSession()
						.createQuery("from TMSBController where controllerUID like :searchWord "
								+ "or status like :searchWord order by createdDateTime desc ")
						.setParameter("searchWord", "%" + searchWord + "%").setMaxResults(limit)
						.setFirstResult(startIndex);
				q1 = sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSBController where controllerUID like :searchWord "
								+ "or status like :searchWord")
						.setParameter("searchWord", "%" + searchWord + "%");
			}
			List<TMSBController> bControllers = q.list();
			long bCtrlCount = (long) q1.uniqueResult();
			resp.setResult(bControllers);
			resp.setCount(bCtrlCount);
			resp.setStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(false);
			resp.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			resp.setErrorMsg(e.getMessage());
		}
		return resp;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response getSensors(String status, String searchWord, int limit, int startIndex) {
		// List<TMSSensor> sensors = new ArrayList<>();
		Response resp = new Response();
		try {
			Query q = sessionFactory.getCurrentSession().createQuery("from TMSSensor order by createdDateTime desc ")
					.setMaxResults(limit).setFirstResult(startIndex);
			Query q1 = sessionFactory.getCurrentSession().createQuery("select count(*) from TMSSensor");
			if (null != status && null != searchWord) {
				q = sessionFactory.getCurrentSession()
						.createQuery("from TMSSensor where status = :status and (sensorUID like :searchWord "
								+ "or status like :searchWord) order by createdDateTime desc ")
						.setParameter("status", status).setParameter("searchWord", "%" + searchWord + "%")
						.setMaxResults(limit).setFirstResult(startIndex);
				q1 = sessionFactory.getCurrentSession()
						.createQuery(
								"select count(*) from TMSSensor where status = :status and (sensorUID like :searchWord "
										+ "or status like :searchWord)")
						.setParameter("status", status).setParameter("searchWord", "%" + searchWord + "%");
			} else if (null != status) {
				// Used to assing sensor to tyre // No limit
				q = sessionFactory.getCurrentSession()
						.createQuery("from TMSSensor where status=:status order by createdDateTime desc ")
						.setParameter("status", status);
				q1 = sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSSensor where status=:status")
						.setParameter("status", status);
			} else if (null != searchWord) {
				q = sessionFactory.getCurrentSession()
						.createQuery("from TMSSensor where sensorUID like :searchWord "
								+ "or status like :searchWord order by createdDateTime desc ")
						.setParameter("searchWord", "%" + searchWord + "%").setMaxResults(limit)
						.setFirstResult(startIndex);
				q1 = sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSSensor where sensorUID like :searchWord "
								+ "or status like :searchWord")
						.setParameter("searchWord", "%" + searchWord + "%");
			}
			List<TMSSensor> sensors = q.list();
			long sensorCount = (long) q1.uniqueResult();
			resp.setResult(sensors);
			resp.setCount(sensorCount);
			resp.setStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(false);
			resp.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			resp.setErrorMsg(e.getMessage());
		}
		return resp;
	}

	@Override
	public long getSensorsCount(String status) {
		long sensorCount = 0;
		try {
			if (null != status) {
				sensorCount = (long) sessionFactory.getCurrentSession()
						.createQuery(
								"select count(*) from TMSSensor where status=:status order by createdDateTime desc ")
						.setParameter("status", status).uniqueResult();
			} else {
				sensorCount = (long) sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSSensor order by createdDateTime desc").uniqueResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sensorCount;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TMSTireView> getTireViewDetials(long orgId, String status, int limit, int startIndex) {
		List<TMSTireView> tires = new ArrayList<>();
		try {
			// All the data For SysAdmin
			if (orgId == 0) {
				Query q = sessionFactory.getCurrentSession().createQuery("from TMSTireView");
				if (null != status && status.equalsIgnoreCase(MyConstants.STATUS_INSTOCK)) {
					// Only InStock
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireView where status=:status "
									+ "and sensorId != 0 order by Tire_CreatedDateTime desc")
							.setParameter("status", status);
				} else if (null != status && status.equalsIgnoreCase(MyConstants.STATUS_WITHOUT_SENSOR)) {
					// Only InStock
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireView where status=:status "
									+ "and sensorId = 0 order by Tire_CreatedDateTime desc")
							.setParameter("status", MyConstants.STATUS_INSTOCK);
				} else if (null != status) {
					// Other than InStock
					q = sessionFactory.getCurrentSession()
							.createQuery(
									"from TMSTireView where " + "status=:status order by Tire_CreatedDateTime desc")
							.setParameter("status", status);
				}

				tires = q.setMaxResults(limit).setFirstResult(startIndex).list();
			} else {
				// Based on Org Id

				Query q = sessionFactory.getCurrentSession()
						.createQuery("from TMSTireView where " + "orgId = :orgId order by Tire_CreatedDateTime desc")
						.setLong("orgId", orgId);

				if (null != status && status.equalsIgnoreCase(MyConstants.STATUS_INSTOCK)) {
					// Only InStock

					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireView where status=:status and sensorId != 0 "
									+ "and orgId = :orgId order by Tire_CreatedDateTime desc")
							.setParameter("status", status).setLong("orgId", orgId);
				} else if (null != status && status.equalsIgnoreCase(MyConstants.STATUS_WITHOUT_SENSOR)) {
					// Only InStock

					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireView where status=:status and sensorId = 0 "
									+ "and orgId = :orgId order by Tire_CreatedDateTime desc")
							.setParameter("status", MyConstants.STATUS_INSTOCK).setLong("orgId", orgId);
				} else if (null != status) {
					// Other than InStock
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireView where status=:status and orgId = :orgId "
									+ "order by Tire_CreatedDateTime desc")
							.setParameter("status", status).setLong("orgId", orgId);
				}

				tires = q.setMaxResults(limit).setFirstResult(startIndex).list();
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
			orgs = sessionFactory.getCurrentSession().createQuery("from Organizations where status = 1").list();
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
			if (orgId != 0) {
				depots = sessionFactory.getCurrentSession().createQuery("from TMSDepot where orgId=:orgId")
						.setParameter("orgId", orgId).list();
			} else {
				depots = sessionFactory.getCurrentSession().createQuery("from TMSDepot").list();
			}
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
	public List<TMSTireInspection> getTMSTireInspections(long orgId, long userId, String searchWord, int limit,
			int startIndex) {

		List<TMSTireInspection> tireInspections = new ArrayList<>();
		try {
			Query q = sessionFactory.getCurrentSession()
					.createQuery("from TMSTireInspection ORDER BY inspectionDate DESC");
			if (orgId != 0) {
				// Based on org
				if (null != searchWord) {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireInspection where orgId=:orgId and "
									+ " (tireNumber like :searchWord or inspectionDate like :searchWord or"
									+ " location like :searchWord or KMSReading like :searchWord or"
									+ " tirePressure like :searchWord or updatedDateTime like :searchWord or"
									+ " avgThreadDepth like :searchWord) ORDER BY inspectionDate DESC")
							.setParameter("orgId", orgId).setParameter("searchWord", "%" + searchWord + "%");
				} else {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireInspection where" + " orgId=:orgId ORDER BY inspectionDate DESC")
							.setParameter("orgId", orgId);
				}
			} else if (userId != 0) {
				// Based on user Id
				if (null != searchWord) {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireInspection where createdBy = :createdBy and "
									+ " (tireNumber like :searchWord or inspectionDate like :searchWord or"
									+ " location like :searchWord or KMSReading like :searchWord or"
									+ " tirePressure like :searchWord or updatedDateTime like :searchWord or"
									+ " avgThreadDepth like :searchWord) ORDER BY inspectionDate DESC")
							.setParameter("createdBy", userId).setParameter("searchWord", "%" + searchWord + "%");
				} else {
					q = sessionFactory.getCurrentSession()
							.createQuery(
									"from TMSTireInspection where createdBy = :createdBy ORDER BY inspectionDate DESC")
							.setParameter("createdBy", userId);
				}
			} else {
				// Only search for admin
				if (null != searchWord) {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireInspection "
									+ " (tireNumber like :searchWord or inspectionDate like :searchWord or"
									+ " location like :searchWord or KMSReading like :searchWord or"
									+ " tirePressure like :searchWord or updatedDateTime like :searchWord or"
									+ " avgThreadDepth like :searchWord) ORDER BY inspectionDate DESC")
							.setParameter("searchWord", "%" + searchWord + "%");
				}
			}
			tireInspections = q.setMaxResults(limit).setFirstResult(startIndex).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tireInspections;
	}

	@Override
	public long getTMSTireInspectionsCount(long orgId, long userId, String searchWord) {

		long count = 0l;
		try {
			if (orgId != 0) {
				// Based on org
				if (null != searchWord) {
					count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireInspection where orgId=:orgId and "
									+ " (tireNumber like :searchWord or inspectionDate like :searchWord or"
									+ " location like :searchWord or KMSReading like :searchWord or"
									+ " tirePressure like :searchWord or updatedDateTime like :searchWord or"
									+ " avgThreadDepth like :searchWord)")
							.setParameter("orgId", orgId).setParameter("searchWord", "%" + searchWord + "%")
							.uniqueResult();
				} else {
					count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireInspection where orgId=:orgId")
							.setParameter("orgId", orgId).uniqueResult();
				}
			} else if (userId != 0) {
				if (null != searchWord) {
					count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireInspection where createdBy = :createdBy"
									+ " and (tireNumber like :searchWord or inspectionDate like :searchWord or"
									+ " location like :searchWord or KMSReading like :searchWord or"
									+ " tirePressure like :searchWord or updatedDateTime like :searchWord or"
									+ " avgThreadDepth like :searchWord)")
							.setParameter("createdBy", userId).setParameter("searchWord", "%" + searchWord + "%")
							.uniqueResult();
				} else {
					count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireInspection where createdBy=:createdBy")
							.setParameter("createdBy", userId).uniqueResult();
				}
			} else {
				if (null != searchWord) {
					count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireInspection where"
									+ " (tireNumber like :searchWord or inspectionDate like :searchWord or"
									+ " location like :searchWord or KMSReading like :searchWord or"
									+ " tirePressure like :searchWord or updatedDateTime like :searchWord or"
									+ " avgThreadDepth like :searchWord)")
							.setParameter("searchWord", "%" + searchWord + "%").uniqueResult();
				} else {
					count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireInspection").uniqueResult();
				}
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public long getTMSTiresCount(long orgId, String status, String searchString) {

		long count = 0l;
		try {
			Query q = sessionFactory.getCurrentSession().createQuery("select count(*) from TMSTireView");
			if (orgId != 0) {
				// Perticular org tires
				// status and search are optional
				// Either search or status may come

				if (null != searchString) {
					q = sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireView where orgId=:orgId "
									+ "and (tireNumber like :searchWord or tireMake like :searchWord or"
									+ " tireType like :searchWord or status like :searchWord or "
									+ " totalTyreKM like :searchWord or tirePosition like :searchWord or "
									+ " threadDepth like :searchWord or sensorUID like :searchWord or "
									+ " depotName like :searchWord or vehName like :searchWord or "
									+ " tireType like :searchWord)")
							.setParameter("searchWord", "%" + searchString + "%").setParameter("orgId", orgId);
				} else if (null != status) {
					if (status.equalsIgnoreCase(MyConstants.STATUS_INSTOCK)) {
						q = sessionFactory.getCurrentSession()
								.createQuery("select count(*) from TMSTireView "
										+ "where orgId=:orgId and status = :status and sensorId != 0")
								.setParameter("status", status).setParameter("orgId", orgId);
					} else if (status.equalsIgnoreCase(MyConstants.STATUS_WITHOUT_SENSOR)) {
						q = sessionFactory.getCurrentSession()
								.createQuery("select count(*) from TMSTireView "
										+ "where orgId=:orgId and status = :status and sensorId = 0")
								.setParameter("status", MyConstants.STATUS_INSTOCK).setParameter("orgId", orgId);
					} else {
						q = sessionFactory.getCurrentSession()
								.createQuery("select count(*) from TMSTireView where orgId=:orgId and status = :status")
								.setParameter("status", status).setParameter("orgId", orgId);
					}
				} else {
					q = sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireView where orgId=:orgId")
							.setParameter("orgId", orgId);
				}
			} else {
				// For SysAdmin
				if (null != searchString) {
					count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireView where"
									+ " (tireNumber like :searchWord or tireMake like :searchWord or"
									+ " tireType like :searchWord or status like :searchWord or "
									+ " totalTyreKM like :searchWord or tirePosition like :searchWord or "
									+ " threadDepth like :searchWord or sensorUID like :searchWord or "
									+ " depotName like :searchWord or vehName like :searchWord or "
									+ " tireType like :searchWord)")
							.setParameter("searchWord", "%" + searchString + "%").uniqueResult();
				} else if (null != status) {
					if (status.equalsIgnoreCase(MyConstants.STATUS_INSTOCK)) {
						q = sessionFactory.getCurrentSession()
								.createQuery("select count(*) from TMSTireView "
										+ "where status = :status and sensorId != 0")
								.setParameter("status", status);
					} else if (status.equalsIgnoreCase(MyConstants.STATUS_WITHOUT_SENSOR)) {
						q = sessionFactory.getCurrentSession()
								.createQuery(
										"select count(*) from TMSTireView " + "where status = :status and sensorId = 0")
								.setParameter("status", MyConstants.STATUS_INSTOCK);
					} else {
						q = sessionFactory.getCurrentSession()
								.createQuery("select count(*) from TMSTireView where status = :status")
								.setParameter("status", status);
					}
				}
			}
			count = (long) q.uniqueResult();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
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
	public List<TMSTireShortDetails> getShortTireDetails(long orgId, String status) {
		List<TMSTireShortDetails> tireDetails = new ArrayList<>();
		try {
			if (orgId == 0) {
				// All Org Data for SysAdmin
				Query q = sessionFactory.getCurrentSession().createQuery("from TMSTireShortDetails");
				if (null != status && status.equalsIgnoreCase(MyConstants.STATUS_INSTOCK)) {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireShortDetails where status = :status "
									+ "and sensorId != 0 order by Tire_CreatedDateTime desc")
							.setParameter("status", status);
				} else if (null != status && status.equalsIgnoreCase(MyConstants.STATUS_WITHOUT_SENSOR)) {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireShortDetails where status = :status "
									+ "and sensorId = 0 order by Tire_CreatedDateTime desc")
							.setParameter("status", MyConstants.STATUS_INSTOCK);
				} else if (null != status) {
					q = sessionFactory.getCurrentSession().createQuery(
							"from TMSTireShortDetails where status = :status " + "order by Tire_CreatedDateTime desc")
							.setParameter("status", status);
				}
				tireDetails = q.list();
			} else {
				// For particular Org
				Query q = sessionFactory.getCurrentSession()
						.createQuery(
								"from TMSTireShortDetails where orgId = :orgId " + "order by Tire_CreatedDateTime desc")
						.setParameter("orgId", orgId);
				if (null != status && status.equalsIgnoreCase(MyConstants.STATUS_INSTOCK)) {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireShortDetails where orgId = :orgId and status = :status "
									+ "and sensorId != 0 order by Tire_CreatedDateTime desc")
							.setParameter("orgId", orgId).setParameter("status", status);
				} else if (null != status && status.equalsIgnoreCase(MyConstants.STATUS_WITHOUT_SENSOR)) {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireShortDetails where orgId = :orgId and status = :status "
									+ "and sensorId = 0 order by Tire_CreatedDateTime desc")
							.setParameter("orgId", orgId).setParameter("status", MyConstants.STATUS_INSTOCK);
				} else if (null != status) {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireShortDetails where orgId = :orgId "
									+ "and status = :status order by Tire_CreatedDateTime desc")
							.setParameter("orgId", orgId).setParameter("status", status);
				}
				tireDetails = q.list();
			}
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
	public List<TMSTireService> getTMSTireServices(long orgId, long userId, String searchWord, int limit,
			int startIndex) {

		List<TMSTireService> tireServices = new ArrayList<>();
		try {
			Query q = sessionFactory.getCurrentSession().createQuery("from TMSTireService ORDER BY createdDate DESC")
					.setMaxResults(limit).setFirstResult(startIndex);
			if (null != searchWord) {
				if (userId != 0) {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireService where createdBy=:createdBy and "
									+ " (tireNumber like :searchWord or depot like :searchWord or "
									+ " tireMake like :searchWord or vehName like :searchWord or "
									+ " fittedDate like :searchWord or kmsAtTyreFitted like :searchWord or "
									+ " location like :searchWord or removalDate like :searchWord or "
									+ " kmsAtTyreRemoved like :searchWord or tyreKms like :searchWord or "
									+ " reason like :searchWord or actionTaken like :searchWord or "
									+ " tyreCondition like :searchWord or scrappedToParty like :searchWord or "
									+ " updatedDateTime like :searchWord or createdDate like :searchWord )"
									+ " ORDER BY createdDate DESC")
							.setParameter("createdBy", userId).setParameter("searchWord", "%" + searchWord + "%")
							.setMaxResults(limit).setFirstResult(startIndex);
				} else if (orgId != 0) {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireService where orgId=:orgId and "
									+ " (tireNumber like :searchWord or depot like :searchWord or "
									+ " tireMake like :searchWord or vehName like :searchWord or "
									+ " fittedDate like :searchWord or kmsAtTyreFitted like :searchWord or "
									+ " location like :searchWord or removalDate like :searchWord or "
									+ " kmsAtTyreRemoved like :searchWord or tyreKms like :searchWord or "
									+ " reason like :searchWord or actionTaken like :searchWord or "
									+ " tyreCondition like :searchWord or scrappedToParty like :searchWord or "
									+ " updatedDateTime like :searchWord or createdDate like :searchWord )"
									+ " ORDER BY createdDate DESC")
							.setParameter("orgId", orgId).setParameter("searchWord", "%" + searchWord + "%")
							.setMaxResults(limit).setFirstResult(startIndex);
				} else {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireService "
									+ " (tireNumber like :searchWord or depot like :searchWord or "
									+ " tireMake like :searchWord or vehName like :searchWord or "
									+ " fittedDate like :searchWord or kmsAtTyreFitted like :searchWord or "
									+ " location like :searchWord or removalDate like :searchWord or "
									+ " kmsAtTyreRemoved like :searchWord or tyreKms like :searchWord or "
									+ " reason like :searchWord or actionTaken like :searchWord or "
									+ " tyreCondition like :searchWord or scrappedToParty like :searchWord or "
									+ " updatedDateTime like :searchWord or createdDate like :searchWord )"
									+ " ORDER BY createdDate DESC")
							.setParameter("searchWord", "%" + searchWord + "%").setMaxResults(limit)
							.setFirstResult(startIndex);
				}
			} else {
				if (userId != 0) {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireService where createdBy=:createdBy ORDER BY createdDate DESC")
							.setParameter("createdBy", userId).setMaxResults(limit).setFirstResult(startIndex);
				} else if (orgId != 0) {
					q = sessionFactory.getCurrentSession()
							.createQuery("from TMSTireService where orgId=:orgId ORDER BY createdDate DESC")
							.setParameter("orgId", orgId).setMaxResults(limit).setFirstResult(startIndex);
				}
			}
			tireServices = q.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tireServices;
	}

	@Override
	public long getTMSTireServicesCount(long orgId, long userId, String searchWord) {
		long count = 0l;
		try {
			Query q = sessionFactory.getCurrentSession().createQuery("select count(*) from TMSTireService");
			if (userId != 0) {
				if (null != searchWord) {
					q = sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireService where createdBy=:createdBy and "
									+ " (tireNumber like :searchWord or depot like :searchWord or "
									+ " tireMake like :searchWord or vehName like :searchWord or "
									+ " fittedDate like :searchWord or kmsAtTyreFitted like :searchWord or "
									+ " location like :searchWord or removalDate like :searchWord or "
									+ " kmsAtTyreRemoved like :searchWord or tyreKms like :searchWord or "
									+ " reason like :searchWord or actionTaken like :searchWord or "
									+ " tyreCondition like :searchWord or scrappedToParty like :searchWord or "
									+ " updatedDateTime like :searchWord or createdDate like :searchWord )")
							.setParameter("createdBy", userId).setParameter("searchWord", "%" + searchWord + "%");
				} else {
					q = sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireService where createdBy=:createdBy")
							.setParameter("createdBy", userId);
				}
			} else if (orgId != 0) {
				if (null != searchWord) {
					q = sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireService where orgId=:orgId and"
									+ " (tireNumber like :searchWord or depot like :searchWord or"
									+ " tireMake like :searchWord or vehName like :searchWord or"
									+ " fittedDate like :searchWord or kmsAtTyreFitted like :searchWord or"
									+ " location like :searchWord or removalDate like :searchWord or"
									+ " kmsAtTyreRemoved like :searchWord or tyreKms like :searchWord or"
									+ " reason like :searchWord or actionTaken like :searchWord or"
									+ " tyreCondition like :searchWord or scrappedToParty like :searchWord or"
									+ " updatedDateTime like :searchWord or createdDate like :searchWord)")
							.setParameter("orgId", orgId).setParameter("searchWord", "%" + searchWord + "%");
				} else {
					q = sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireService where orgId=:orgId")
							.setParameter("orgId", orgId);
				}
			} else {
				if (null != searchWord) {
					q = sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireService where "
									+ " (tireNumber like :searchWord or depot like :searchWord or "
									+ " tireMake like :searchWord or vehName like :searchWord or "
									+ " fittedDate like :searchWord or kmsAtTyreFitted like :searchWord or "
									+ " location like :searchWord or removalDate like :searchWord or "
									+ " kmsAtTyreRemoved like :searchWord or tyreKms like :searchWord or "
									+ " reason like :searchWord or actionTaken like :searchWord or "
									+ " tyreCondition like :searchWord or scrappedToParty like :searchWord or "
									+ " updatedDateTime like :searchWord or createdDate like :searchWord )")
							.setParameter("searchWord", "%" + searchWord + "%");
				}
			}
			count = (long) q.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
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
		// DateFormat df = new
		// SimpleDateFormat(MyConstants.MYSQL_DATE_TIME_FORMATER);
		try {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(TMSUserVehiclesView.class)
					.add(Restrictions.gt("rfid_UpdatedDateTime", lastUpdatedDate))
					.add(Restrictions.gt("bCtrl_UpdatedDateTime", lastUpdatedDate))
					.add(Restrictions.gt("veh_UpdatedDateTime", lastUpdatedDate))
					.setProjection(Projections.projectionList().add(Projections.property("vehId"), "vehId")
							.add(Projections.property("vehName"), "vehName")
							.add(Projections.property("depotId"), "depotId").add(Projections.property("orgId"), "orgId")
							.add(Projections.property("RFID"), "RFID").add(Projections.property("RFIDUID"), "RFIDUID")
							.add(Projections.property("controllerID"), "controllerID")
							.add(Projections.property("controllerUID"), "controllerUID")
							.add(Projections.property("rfid_UpdatedDateTime"), "rfid_UpdatedDateTime")
							.add(Projections.property("bCtrl_UpdatedDateTime"), "bCtrl_UpdatedDateTime")
							.add(Projections.property("veh_UpdatedDateTime"), "veh_UpdatedDateTime")
							.add(Projections.groupProperty("vehName"), "vehName"))
					.setResultTransformer(Transformers.aliasToBean(TMSUserVehiclesView.class));

			veh_list = cr.list();

			// veh_list = sessionFactory.getCurrentSession()
			// .createQuery("from TMSUserVehiclesView where " +
			// "RFID_UpdatedDateTime > :RFID_UpdatedDateTime or "
			// + "Vehicle_UpdatedDateTime > :Vehicle_UpdatedDateTime or "
			// + "BController_UpdatedDateTime > :BController_UpdatedDateTime")
			// .setParameter("RFID_UpdatedDateTime", df.format(lastUpdatedDate))
			// .setParameter("Vehicle_UpdatedDateTime",
			// df.format(lastUpdatedDate))
			// .setParameter("BController_UpdatedDateTime",
			// df.format(lastUpdatedDate)).list();

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
						.createQuery("from TMSBasicVehicleDetails where orgId = :orgId").setParameter("orgId", orgId)
						.list();
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
	public long getVehiclesCountByUserId(long userId, boolean uniqueVehs) {
		try {
			// For sysAdmin - Unique vehicles
			Query q = sessionFactory.getCurrentSession().createQuery("select count(*) from TMSVehicles");
			if (userId != 0) {
				// Based on user id
				q = sessionFactory.getCurrentSession()
						.createQuery(
								"select count(*) from TMSUserVehiclesView where userId = :userId and status = :status")
						.setParameter("userId", userId).setParameter("status", 1l);
			} else if (uniqueVehs == false) {
				// User - Vehicles
				q = sessionFactory.getCurrentSession().createQuery("select count(*) from TMSUserVehiclesView");
			}
			long count = (long) q.uniqueResult();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public TMSUserVehicleMapping getTMSUserVehicleMappingDetails(long vehId, long userId) {
		TMSUserVehicleMapping vehMappingDetails = null;
		try {
			Query q = sessionFactory.getCurrentSession()
					.createQuery("from TMSUserVehicleMapping where userId = :userId and vehId = :vehId")
					.setParameter("userId", userId).setParameter("vehId", vehId);
			vehMappingDetails = (TMSUserVehicleMapping) q.uniqueResult();
			return vehMappingDetails;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vehMappingDetails;
	}

	@Override
	public Response processServiceDetails(TMSTireService existingService, TMSTireInspection inspection, TMSTire tire) {
		Response response = new Response();

		try {
			response = saveOrUpdateTireServices(existingService);
			if (response.isStatus()) {
				// Service saved successfully
				if (null != inspection) {
					response = saveOrUpdateTireInspection(inspection);
					if (response.isStatus()) {
						// Inspection saved successfully
						response = saveOrUpdateTire(tire);
						if (response.isStatus()) {
							// Tire details saved successfully
							response.setDisplayMsg(MyConstants.TIRE_DEALLOCATED_SUCCESS);
							response.setErrorMsg(MyConstants.TIRE_DEALLOCATED_SUCCESS);
							return response;
						}
					}
				} else {
					response = saveOrUpdateTire(tire);
					if (response.isStatus()) {
						response.setDisplayMsg(MyConstants.TIRE_DEALLOCATED_SUCCESS);
						response.setErrorMsg(MyConstants.TIRE_DEALLOCATED_SUCCESS);
						return response;
					}
				}
			}
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.TIRE_DEALLOCATED_FAILED);
			response.setErrorMsg(MyConstants.TIRE_DEALLOCATED_FAILED);
			// Rollback the last transaction
			TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.TIRE_DEALLOCATED_FAILED);
			response.setErrorMsg(e.getMessage());
			try {
				// If there is any exception we are rollback the last
				// transaction
				TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return response;
	}

	@Override
	public Response deallocateSensorFromTire(TMSTire tire, TMSSensor sensor) {

		Response response = new Response();
		response.setStatus(false);
		try {
			response = saveOrUpdateTire(tire);
			if (response.isStatus()) {
				response = saveOrUpdateSensor(sensor);
				if (response.isStatus()) {

					tire = getTireByTireId(tire.getTireId());
					List<TMSTire> tireDetails = new ArrayList<>(1);
					tireDetails.add(tire);
					response.setStatus(true);
					response.setDisplayMsg(MyConstants.DEALLOCATE_SENSOR_SUCCESSFULLY);
					response.setResult(tireDetails);
					return response;
				}
			}
			// Rollback the last transaction
			TransactionInterceptor.currentTransactionStatus().setRollbackOnly();

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.DEALLOCATE_SENSOR_FAILED);
			response.setErrorMsg(e.getMessage());
			try {
				// If there is any exception we are rollback the last
				// transaction
				TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return response;
	}

	@Override
	public TMSMinMaxTempPressure getMinMaxTempPressureValues(long orgId, long userId) {
		TMSMinMaxTempPressure minMaxTempPressureValues = new TMSMinMaxTempPressure();
		try {
			// Default values
			// Max for Sys Admin
			Query q = sessionFactory.getCurrentSession()
					.createQuery("from TMSMinMaxTempPressure where orgId = 0 and userId = 0");
			if (userId != 0) {
				q = sessionFactory.getCurrentSession().createQuery("from TMSMinMaxTempPressure where userId = :userId")
						.setParameter("userId", userId);
			} else if (orgId != 0) {
				q = sessionFactory.getCurrentSession().createQuery("from TMSMinMaxTempPressure where orgId = :orgId")
						.setParameter("orgId", orgId);
			}
			minMaxTempPressureValues = (TMSMinMaxTempPressure) q.uniqueResult();
			// Data is not available with Org id / User ID
			if (minMaxTempPressureValues == null) {
				q = sessionFactory.getCurrentSession()
						.createQuery("from TMSMinMaxTempPressure where orgId = 0 and userId = 0");
				minMaxTempPressureValues = (TMSMinMaxTempPressure) q.uniqueResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return minMaxTempPressureValues;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response getAllUserVehDetails(String searchWord, List<Long> vehIds, int limit, int startIndex) {

		Response response = new Response();
		response.setStatus(false);
		List<TMSUserVehiclesView_CompositeKey> userVehDetails = new ArrayList<>();
		try {
			if (null != searchWord) {
				userVehDetails = sessionFactory.getCurrentSession()
						.createQuery("from TMSUserVehiclesView_CompositeKey where vehName like :searchWord "
								+ "or userName like :searchWord ORDER BY veh_CreatedDateTime DESC")
						.setParameter("searchWord", "%" + searchWord + "%").setMaxResults(limit)
						.setFirstResult(startIndex).list();
				response.setResult(userVehDetails);

				long count = (long) sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSUserVehiclesView_CompositeKey "
								+ "where vehName like :searchWord or userName like :searchWord ")
						.setParameter("searchWord", "%" + searchWord + "%").uniqueResult();
				response.setCount(count);
			} else if (null != vehIds && vehIds.size() > 0) {
				Query q = sessionFactory.getCurrentSession()
						.createQuery("select tmsUserVehiclesView_Keys.vehId, "
								+ "vehName, depotId, orgId, RFID, RFIDUID, controllerID, controllerUID, "
								+ "rfid_UpdatedDateTime, bCtrl_UpdatedDateTime, veh_UpdatedDateTime, "
								+ "veh_CreatedDateTime from TMSUserVehiclesView_CompositeKey "
								+ "where tmsUserVehiclesView_Keys.vehId in :vehIds GROUP BY vehId ORDER BY veh_CreatedDateTime DESC")
						.setParameterList("vehIds", vehIds);
				List<Object[]> rows = q.setMaxResults(limit).setFirstResult(startIndex).list();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for (Object[] row : rows) {
					try {
						TMSUserVehiclesView_CompositeKey vehDetails = new TMSUserVehiclesView_CompositeKey();
						TMSUserVehiclesView_Keys keys = new TMSUserVehiclesView_Keys(
								Integer.parseInt(row[0].toString()), 0);
						vehDetails.setTmsUserVehiclesView_Keys(keys);
						vehDetails.setVehName(row[1].toString());
						vehDetails.setDepotId(Integer.parseInt(row[2].toString()));
						vehDetails.setOrgId(Integer.parseInt(row[3].toString()));
						vehDetails.setRFID(Long.parseLong(row[4].toString()));
						if (Long.parseLong(row[4].toString()) != 0)
							vehDetails.setRFIDUID(row[5].toString());
						vehDetails.setControllerID(Integer.parseInt(row[6].toString()));
						if (Integer.parseInt(row[6].toString()) != 0)
							vehDetails.setControllerUID(row[7].toString());
						if (null != row[8])
							vehDetails.setRfid_UpdatedDateTime(sdf.parse(row[8].toString()));
						if (null != row[9]) {
							vehDetails.setbCtrl_UpdatedDateTime(sdf.parse(row[9].toString()));
						}
						if (null != row[10])
							vehDetails.setVeh_UpdatedDateTime(sdf.parse(row[10].toString()));
						if (null != row[11])
							vehDetails.setVeh_CreatedDateTime(sdf.parse(row[11].toString()));
						userVehDetails.add(vehDetails);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				response.setResult(userVehDetails);
			} else {
				userVehDetails = sessionFactory.getCurrentSession()
						.createQuery("from TMSUserVehiclesView_CompositeKey ORDER BY veh_CreatedDateTime DESC")
						.setMaxResults(limit).setFirstResult(startIndex).list();
				response.setResult(userVehDetails);

				long count = (long) sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSUserVehiclesView_CompositeKey").uniqueResult();
				response.setCount(count);
			}
			response.setStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VehicleTyreCount> findSemiAssignedVehCount(List<Long> vehIds) {
		List<VehicleTyreCount> vehTyreCount = new ArrayList<>();
		try {
			if (null != vehIds && vehIds.size() > 0) {
				List<Object[]> rows = new ArrayList<>();
				if ((rows = sessionFactory.getCurrentSession()
						.createQuery("SELECT vehId, count(*) FROM TMSTire WHERE vehId in :vehIds GROUP BY vehId")
						.setParameterList("vehIds", vehIds).list()) != null) {
					for (Object[] row : rows) {
						try {
							VehicleTyreCount tyreCount = new VehicleTyreCount();
							tyreCount.setVehId(Integer.parseInt(row[0].toString()));
							tyreCount.setTireCount(Integer.parseInt(row[1].toString()));
							vehTyreCount.add(tyreCount);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return vehTyreCount;
	}

	@Override
	public long getTireCountBasedOnStatus(String status, boolean sensorStatus, long orgId) {

		try {
			if (null != status && orgId != 0) {

				if (status.equalsIgnoreCase(MyConstants.STATUS_INSTOCK) && sensorStatus == true) {
					// For Org based on status
					long count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireShortDetails where status = :status "
									+ "and orgId = :orgId and sensorId != 0")
							.setParameter("status", status).setParameter("orgId", orgId).uniqueResult();
					return count;
				} else if (status.equalsIgnoreCase(MyConstants.STATUS_INSTOCK) && sensorStatus == false) {
					// For Org based on status
					long count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireShortDetails where status = :status "
									+ "and orgId = :orgId and sensorId = 0")
							.setParameter("status", status).setParameter("orgId", orgId).uniqueResult();
					return count;
				} else {
					// For Org based on status
					long count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireShortDetails where status = :status "
									+ "and orgId = :orgId")
							.setParameter("status", status).setParameter("orgId", orgId).uniqueResult();
					return count;
				}
			} else if (orgId != 0) {
				// All Tyres count based on Org
				long count = (long) sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSTireShortDetails where orgId = :orgId")
						.setParameter("orgId", orgId).uniqueResult();
				return count;
			} else if (null != status) {
				// Based on status
				if (status.equalsIgnoreCase(MyConstants.STATUS_INSTOCK) && sensorStatus == true) {
					// For Org based on status
					long count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireShortDetails where status = :status "
									+ "and sensorId != 0")
							.setParameter("status", status).uniqueResult();
					return count;
				} else if (status.equalsIgnoreCase(MyConstants.STATUS_INSTOCK) && sensorStatus == false) {
					// For Org based on status
					long count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireShortDetails where status = :status "
									+ "and sensorId = 0")
							.setParameter("status", status).uniqueResult();
					return count;
				} else {
					long count = (long) sessionFactory.getCurrentSession()
							.createQuery("select count(*) from TMSTireShortDetails where status = :status")
							.setParameter("status", status).uniqueResult();
					return count;
				}
			} else {
				// For Sys Admin
				long count = (long) sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSTireShortDetails").uniqueResult();
				return count;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public long getTireServiceCount(long orgId) {

		try {
			if (orgId != 0) {
				// All Tyres Services count based on Org
				long count = (long) sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSTireService where orgId = :orgId")
						.setParameter("orgId", orgId).uniqueResult();
				return count;
			} else {
				// For Sys Admin
				long count = (long) sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSTireService").uniqueResult();
				return count;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public long getTireInspectionsCount(long orgId) {

		try {
			if (orgId != 0) {
				// All Tyres Services count based on Org
				long count = (long) sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSTireInspection where orgId = :orgId")
						.setParameter("orgId", orgId).uniqueResult();
				return count;
			} else {
				// For Sys Admin
				long count = (long) sessionFactory.getCurrentSession()
						.createQuery("select count(*) from TMSTireInspection").uniqueResult();
				return count;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Integer> getTireSensorUIDsMap(List<String> sensorUIDs) {
		Map<String, Integer> tireSensorMap = new HashMap<>();
		try {
			if (null != sensorUIDs && sensorUIDs.size() > 0) {
				List<Object[]> rows = new ArrayList<>();

				if ((rows = sessionFactory.getCurrentSession()
						.createQuery("SELECT tireId, sensorUID FROM TMSTireView WHERE sensorUID in :sensorUIDs")
						.setParameterList("sensorUIDs", sensorUIDs).list()) != null) {
					for (Object[] row : rows) {
						try {
							tireSensorMap.put(row[1].toString(), Integer.parseInt(row[0].toString()));
							// tyreCount.setVehId(Integer.parseInt(row[0].toString()));
							// tyreCount.setTireCount(Integer.parseInt(row[1].toString()));
							// vehTyreCount.add(tyreCount);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return tireSensorMap;
	}

}
