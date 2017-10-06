package com.tms.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tms.beans.MyConstants;
import com.tms.beans.Response;
import com.tms.beans.TMSUserBean;
import com.tms.customheaders.CommonClass;
import com.tms.model.Organizations;
import com.tms.model.TMSBController;
import com.tms.model.TMSBasicVehicleDetails;
import com.tms.model.TMSDepot;
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
import com.tms.service.MySQLService;

@Controller
@RequestMapping("/api")
public class TMSActionController {
	
	private static final Logger logr = Logger.getLogger(TMSActionController.class);

	@Autowired
	private MySQLService mySQLService;

	@RequestMapping(value = "/getModifiedVehList", method = RequestMethod.GET)
	public @ResponseBody List<TMSUserVehiclesView> getModifiedVehDetails(
			@RequestParam(value = "lastUpdateDateTime", required = false) long lastUpdateDateTime) {
		List<TMSUserVehiclesView> vehiclesList = new ArrayList<>();
		try {
			DateFormat df = new SimpleDateFormat(MyConstants.MYSQL_DATE_TIME_FORMATER);
			vehiclesList = mySQLService.getModifiedVehDetails(df.parse(df.format(new Date(lastUpdateDateTime))));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vehiclesList;
	}

	@RequestMapping(value = "/getModifiedTiresList", method = RequestMethod.GET)
	public @ResponseBody List<TMSTireView> getModifiedTiresList(
			@RequestParam(value = "lastUpdateDateTime", required = false) long lastUpdateDateTime) {
		List<TMSTireView> tiresList = new ArrayList<>();
		try {
			DateFormat df = new SimpleDateFormat(MyConstants.MYSQL_DATE_TIME_FORMATER);
			tiresList = mySQLService.getModifiedTiresDetails(df.parse(df.format(new Date(lastUpdateDateTime))));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tiresList;
	}

	// Get vehicle details based on limit (GET)
//	@RequestMapping(value = "/getVehicles", method = RequestMethod.GET)
//	public @ResponseBody Response getVehiclesList(HttpServletRequest request, HttpServletResponse resp) {
//		Response response = new Response();
//		response.setStatus(false);
//		System.out.println("<<<<<<<<<<<<<<<<<< >>>>>>>>>>>>>>>>>>");
//		try {
//			HttpSession session = request.getSession(false);
//			if (null != session && session.isNew() == false) {
//				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
//				if (loginUser != null) {
//					CommonClass.fixHeaders(request, resp);
//					List<TMSUserVehiclesView> vehiclesList = new ArrayList<>();
//					// Checking search condition based on vehIds
//					if (!(String.valueOf(request.getParameter("RequestParam")).equalsIgnoreCase(null)
//							|| String.valueOf(request.getParameter("RequestParam")).equalsIgnoreCase("null"))) {
//
//						JSONObject reqObj = new JSONObject(request.getParameter("RequestParam"));
//						JSONArray jsonVehIds = reqObj.getJSONArray("vehIds");
//						// Get vehicle details
//						List<Long> vehIds = new ArrayList<>(jsonVehIds.length());
//						for (int i = 0; i < jsonVehIds.length(); i++) {
//							vehIds.add(Long.parseLong(jsonVehIds.getString(i)));
//						}
//						vehiclesList = mySQLService.getVehiclesByVehIds(vehIds, loginUser.getUserId());
////						for (TMSUserVehiclesView vehicle : vehiclesList) {
////							List<TMSTire> tires = mySQLService.getTiresByVehId(vehicle.getVehId());
////							vehicle.setTires(tires);
////						}
//					} else {
//						Integer limit = 50;
//						Integer startIndex = 0;
//						if (null != request.getParameter("limit")) {
//							limit = Integer.valueOf(request.getParameter("limit"));
//						}
//						if (null != request.getParameter("startIndex")) {
//							startIndex = Integer.valueOf(request.getParameter("startIndex"));
//						}
//						vehiclesList = mySQLService.getVehiclesByLimit(loginUser.getUserId(), limit, startIndex);
////						for (TMSUserVehiclesView vehicle : vehiclesList) {
////							List<TMSTire> tires = mySQLService.getTiresByVehId(vehicle.getVehId());
////							vehicle.setTires(tires);
////						}
//					}
//					response.setResult(vehiclesList);
//					response.setStatus(true);
//				} else {
//					// Session expired
//					CommonClass.fixInitialHeaders(request, resp);
//					response.setStatus(false);
//					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
//					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
//				}
//			} else {
//				// Session expired
//				CommonClass.fixInitialHeaders(request, resp);
//				response.setStatus(false);
//				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
//				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.setStatus(false);
//			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
//			response.setErrorMsg(e.getMessage());
//		}
//		return response;
//	}

	// Get vehicle details based on limit or Vehicle ids (POST)
	@RequestMapping(value = "/getVehicles", method = RequestMethod.POST)
	public @ResponseBody Response getVehiclesList1(HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			logr.info("get vehicle details");
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					List<TMSUserVehiclesView> vehiclesList = new ArrayList<>();
					// Checking search condition based on vehIds
					if (!(String.valueOf(request.getParameter("RequestParam")).equalsIgnoreCase(null)
							|| String.valueOf(request.getParameter("RequestParam")).equalsIgnoreCase("null"))) {

						JSONObject reqObj = new JSONObject(request.getParameter("RequestParam"));
						JSONArray jsonVehIds = reqObj.getJSONArray("vehIds");
						// Get vehicle details
						List<Long> vehIds = new ArrayList<>(jsonVehIds.length());
						for (int i = 0; i < jsonVehIds.length(); i++) {
							vehIds.add(Long.parseLong(jsonVehIds.getString(i)));
						}
						vehiclesList = mySQLService.getVehiclesByVehIds(vehIds, loginUser.getUserId());
						for (TMSUserVehiclesView vehicle : vehiclesList) {
							List<TMSTire> tires = mySQLService.getTiresByVehId(vehicle.getVehId());
							vehicle.setTires(tires);
						}
					} else {
						Integer limit = 50;
						Integer startIndex = 0;
						if (null != request.getParameter("limit")) {
							limit = Integer.valueOf(request.getParameter("limit"));
						}
						if (null != request.getParameter("startIndex")) {
							startIndex = Integer.valueOf(request.getParameter("startIndex"));
						}
						logr.info(limit + " " + startIndex);
						vehiclesList = mySQLService.getVehiclesByLimit(loginUser.getUserId(), limit, startIndex);
						for (TMSUserVehiclesView vehicle : vehiclesList) {
							List<TMSTire> tires = mySQLService.getTiresByVehId(vehicle.getVehId());
							vehicle.setTires(tires);
						}
					}
					response.setResult(vehiclesList);
					response.setStatus(true);
					response.setCount(mySQLService.getVehiclesCountByUserId(loginUser.getUserId()));
				} else {
					// Session expired
					response.setStatus(false);
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				response.setStatus(false);
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	// Search vehicles by search string
	@RequestMapping(value = "/searchVehicles", method = RequestMethod.GET)
	public @ResponseBody Response searchVehicles(@RequestParam("searchWord") String searchWord,
			HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					List<TMSUserVehiclesView> vehiclesList = new ArrayList<>();
					// Checking search condition based on vehIds
					if (null != searchWord && searchWord.trim().length() > 0) {

						vehiclesList = mySQLService.searchVehicles(searchWord, loginUser.getUserId());
						response.setResult(vehiclesList);
						response.setStatus(true);
					} else {
						response.setDisplayMsg(MyConstants.SEARCH_WORD_REQUIRED);
						response.setErrorMsg(searchWord + " - " + MyConstants.SEARCH_WORD_REQUIRED);
						response.setStatus(false);
					}
				} else {
					// Session expired
					response.setStatus(false);
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				response.setStatus(false);
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	// Get all the vehicles basic details
	@RequestMapping(value = "/getBasicVehDetails", method = RequestMethod.GET)
	public @ResponseBody Response getBasicVehDetails(@RequestParam(value = "orgId", required = false) Long orgId,
			HttpServletRequest request, HttpServletResponse resp) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					CommonClass.fixInitialHeaders(request, resp);
					List<TMSBasicVehicleDetails> veh_list = new ArrayList<>();
					if (null != orgId && orgId > 0 && loginUser.getTMSUserLevel() < 5) {
						// SysAdmins can access all vehicles
						veh_list = mySQLService.getAllBasicVehDetials(orgId);
					} else {
						veh_list = mySQLService.getAllBasicVehDetialsByUserId(loginUser.getUserId());
					}
					response.setStatus(true);
					response.setResult(veh_list);
				} else {
					// Session expired
					CommonClass.fixInitialHeaders(request, resp);
					response.setStatus(false);
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				CommonClass.fixInitialHeaders(request, resp);
				response.setStatus(false);
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	// Get all basic user details
	@RequestMapping(value = "/getAllUserDetails", method = RequestMethod.GET)
	public @ResponseBody Response getAllUserDetails(@RequestParam(value = "orgId", required = false) Long orgId,
			HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (loginUser.getTMSUserLevel() < 5) {
						// SysAdmin Users
						if (null == orgId || orgId.equals("undefined")) {
							orgId = 0l;
						}
					} else {
						orgId = loginUser.getOrgId();
					}
					List<UserMaster> users = mySQLService.getAllTMSUsersByOrgId(orgId);
					List<TMSUserBean> basicUserDetails = new ArrayList<>(users.size());
					for (UserMaster user : users) {
						TMSUserBean userB = new TMSUserBean();
						userB.setUserId(user.getUserId());
						userB.setUserLevel(user.getTMSUserLevel());
						userB.setUserName(user.getUserName());
						basicUserDetails.add(userB);
					}
					response.setStatus(true);
					response.setResult(basicUserDetails);
				} else {
					// Session expired
					response.setStatus(false);
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				response.setStatus(false);
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/assignVehToUsers", method = RequestMethod.POST)
	public @ResponseBody Response assignVehToUsers(HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (!(String.valueOf(request.getParameter("RequestParam")).equalsIgnoreCase(null)
							|| String.valueOf(request.getParameter("RequestParam")).equalsIgnoreCase("null"))) {

						JSONObject reqObj = new JSONObject(request.getParameter("RequestParam"));
						JSONArray jsonUserIds = reqObj.getJSONArray("userIds");
						JSONArray jsonVehIds = reqObj.getJSONArray("vehIds");
						if (jsonUserIds.length() == 0) {
							response.setDisplayMsg(MyConstants.ATLEAST_ONE_USER_NEEDED);
							response.setErrorMsg(MyConstants.ATLEAST_ONE_USER_NEEDED);
						} else if (jsonVehIds.length() == 0) {
							response.setDisplayMsg(MyConstants.ATLEAST_ONE_VEHICLE_NEEDED);
							response.setErrorMsg(MyConstants.ATLEAST_ONE_VEHICLE_NEEDED);
						} else {
							// Get user details
							List<Long> userIds = new ArrayList<>(jsonUserIds.length());
							for (int i = 0; i < jsonUserIds.length(); i++) {
								userIds.add(Long.parseLong(jsonUserIds.getString(i)));
							}
							List<UserMaster> users = mySQLService.getAllTMSUsersByUserIds(userIds);

							// Get vehicle details
							List<Long> vehIds = new ArrayList<>(jsonVehIds.length());
							for (int i = 0; i < jsonVehIds.length(); i++) {
								vehIds.add(Long.parseLong(jsonVehIds.getString(i)));
							}

							List<TMSBasicVehicleDetails> vehicles = mySQLService.getAllBasicVehDetialsByVehIds(vehIds);

							int count = 0;
							for (UserMaster user : users) {
								for (TMSBasicVehicleDetails veh : vehicles) {
									// Save data
									try {
										TMSUserVehicleMapping userVehMapping = new TMSUserVehicleMapping();
										userVehMapping.setCreatedDateTime(new Date());
										userVehMapping.setAssignedBy(loginUser.getUserId());
										userVehMapping.setStatus(0);
										userVehMapping.setUserId(user.getUserId());
										userVehMapping.setVehId(veh.getVehId());

										mySQLService.saveOrUpdateUserVehMapping(userVehMapping);
										count++;
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							response.setStatus(true);
							response.setDisplayMsg(count + " " + MyConstants.VEHICLE_MAPPED_USER_SUCCESS);
						}
					} else {
						response.setDisplayMsg(MyConstants.CHECK_YOUR_REQUEST);
						response.setErrorMsg(MyConstants.CHECK_YOUR_REQUEST + " RequestParam needed");
					}
				} else {
					// Session expired
					response.setStatus(false);
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				response.setStatus(false);
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	// Assign Tires to Vehicle
	@RequestMapping(value = "/assignTyres", method = RequestMethod.GET)
	public @ResponseBody Response assignTyresToVehicle(@RequestParam(value = "vehId", required = false) Integer vehId,
			@RequestParam(value = "tyreId1", required = false) Integer tireId1,
			@RequestParam(value = "tyrePosition1", required = false) String tirePosition1,
			@RequestParam(value = "tyreId2", required = false) Integer tireId2,
			@RequestParam(value = "tyrePosition2", required = false) String tirePosition2,
			@RequestParam(value = "tyreId3", required = false) Integer tireId3,
			@RequestParam(value = "tyrePosition3", required = false) String tirePosition3,
			@RequestParam(value = "tyreId4", required = false) Integer tireId4,
			@RequestParam(value = "tyrePosition4", required = false) String tirePosition4,
			@RequestParam(value = "tyreId5", required = false) Integer tireId5,
			@RequestParam(value = "tyrePosition5", required = false) String tirePosition5,
			@RequestParam(value = "tyreId6", required = false) Integer tireId6,
			@RequestParam(value = "tyrePosition6", required = false) String tirePosition6) {
		Response response = new Response();
		try {
			if (null != vehId && vehId > 0) {
				// Check vehicle exists with this id or not
				TMSVehicles vehicleDetials = mySQLService.getVehById(vehId);
				if (null != vehicleDetials) {
					// Check tire exists or not
					List<TMSTire> existingTires = mySQLService.getTiresByVehId(vehId);
					if (null != tireId1 && tireId1 != 0 && null != tirePosition1 && tirePosition1.trim().length() > 0) {

						TMSTire tyre1 = mySQLService.getTireByTireId(tireId1);
						if (null != tyre1) {
							// Find & change the status of the existing tire
							// details
							for (TMSTire existingTire : existingTires) {
								if (existingTire.getTirePosition().equalsIgnoreCase(tirePosition1)) {
									existingTire.setVehId(0l);
									existingTire.setTirePosition("");
									existingTire.setStatus(MyConstants.STATUS_INSTOCK);
									existingTire.setUpdatedDateTime(new Date());
									mySQLService.saveOrUpdateTire(existingTire);
								}
							}
							tyre1.setTirePosition(tirePosition1);
							tyre1.setVehId(vehicleDetials.getVehId());
							tyre1.setStatus(MyConstants.STATUS_INSTALLED);
							tyre1.setUpdatedDateTime(new Date());
							mySQLService.saveOrUpdateTire(tyre1);
						}
					}
					if (null != tireId2 && tireId2 != 0 && null != tirePosition2 && tirePosition2.trim().length() > 0) {
						TMSTire tyre2 = mySQLService.getTireByTireId(tireId2);
						if (null != tyre2) {
							// Find & change the status of the existing tire
							// details
							for (TMSTire existingTire : existingTires) {
								if (existingTire.getTirePosition().equalsIgnoreCase(tirePosition2)) {
									existingTire.setVehId(0l);
									existingTire.setTirePosition("");
									existingTire.setStatus(MyConstants.STATUS_INSTOCK);
									existingTire.setUpdatedDateTime(new Date());
									mySQLService.saveOrUpdateTire(existingTire);
								}
							}

							tyre2.setTirePosition(tirePosition2);
							tyre2.setVehId(vehicleDetials.getVehId());
							tyre2.setStatus(MyConstants.STATUS_INSTALLED);
							tyre2.setUpdatedDateTime(new Date());
							mySQLService.saveOrUpdateTire(tyre2);
						}
					}
					if (null != tireId3 && tireId3 != 0 && null != tirePosition3 && tirePosition3.trim().length() > 0) {
						TMSTire tyre3 = mySQLService.getTireByTireId(tireId3);
						if (null != tyre3) {
							// Find & change the status of the existing tire
							// details
							for (TMSTire existingTire : existingTires) {
								if (existingTire.getTirePosition().equalsIgnoreCase(tirePosition3)) {
									existingTire.setVehId(0l);
									existingTire.setTirePosition("");
									existingTire.setStatus(MyConstants.STATUS_INSTOCK);
									existingTire.setUpdatedDateTime(new Date());
									mySQLService.saveOrUpdateTire(existingTire);
								}
							}

							tyre3.setTirePosition(tirePosition3);
							tyre3.setVehId(vehicleDetials.getVehId());
							tyre3.setStatus(MyConstants.STATUS_INSTALLED);
							tyre3.setUpdatedDateTime(new Date());
							mySQLService.saveOrUpdateTire(tyre3);
						}
					}
					if (null != tireId4 && tireId4 != 0 && null != tirePosition4 && tirePosition4.trim().length() > 0) {
						TMSTire tyre4 = mySQLService.getTireByTireId(tireId4);
						if (null != tyre4) {

							// Find & change the status of the existing tire
							// details
							for (TMSTire existingTire : existingTires) {
								if (existingTire.getTirePosition().equalsIgnoreCase(tirePosition4)) {
									existingTire.setVehId(0l);
									existingTire.setTirePosition("");
									existingTire.setStatus(MyConstants.STATUS_INSTOCK);
									existingTire.setUpdatedDateTime(new Date());
									mySQLService.saveOrUpdateTire(existingTire);
								}
							}

							tyre4.setTirePosition(tirePosition4);
							tyre4.setVehId(vehicleDetials.getVehId());
							tyre4.setStatus(MyConstants.STATUS_INSTALLED);
							tyre4.setUpdatedDateTime(new Date());

							mySQLService.saveOrUpdateTire(tyre4);
						}
					}
					if (null != tireId5 && tireId5 != 0 && null != tirePosition5 && tirePosition5.trim().length() > 0) {
						TMSTire tyre5 = mySQLService.getTireByTireId(tireId5);
						if (null != tyre5) {
							// Find & change the status of the existing tire
							// details
							for (TMSTire existingTire : existingTires) {
								if (existingTire.getTirePosition().equalsIgnoreCase(tirePosition5)) {
									existingTire.setVehId(0l);
									existingTire.setTirePosition("");
									existingTire.setStatus(MyConstants.STATUS_INSTOCK);
									existingTire.setUpdatedDateTime(new Date());
									mySQLService.saveOrUpdateTire(existingTire);
								}
							}

							tyre5.setTirePosition(tirePosition5);
							tyre5.setVehId(vehicleDetials.getVehId());
							tyre5.setStatus(MyConstants.STATUS_INSTALLED);
							tyre5.setUpdatedDateTime(new Date());
							mySQLService.saveOrUpdateTire(tyre5);
						}
					}
					if (null != tireId6 && tireId6 != 0 && null != tirePosition6 && tirePosition6.trim().length() > 0) {
						TMSTire tyre6 = mySQLService.getTireByTireId(tireId6);
						if (null != tyre6) {
							// Find & change the status of the existing tire
							// details
							for (TMSTire existingTire : existingTires) {
								if (existingTire.getTirePosition().equalsIgnoreCase(tirePosition6)) {
									existingTire.setVehId(0l);
									existingTire.setTirePosition("");
									existingTire.setStatus(MyConstants.STATUS_INSTOCK);
									existingTire.setUpdatedDateTime(new Date());
									mySQLService.saveOrUpdateTire(existingTire);
								}
							}

							tyre6.setTirePosition(tirePosition6);
							tyre6.setVehId(vehicleDetials.getVehId());
							tyre6.setStatus(MyConstants.STATUS_INSTALLED);
							tyre6.setUpdatedDateTime(new Date());
							mySQLService.saveOrUpdateTire(tyre6);
						}
					}
					List<Long> vehIds = new ArrayList<>();
					vehIds.add(Long.valueOf(vehId));
					List<TMSUserVehiclesView> vehiclesList = mySQLService.getVehiclesByVehIds(vehIds, 0l);
					for (TMSUserVehiclesView vehicle : vehiclesList) {
						List<TMSTire> tires = mySQLService.getTiresByVehId(vehicle.getVehId());
						vehicle.setTires(tires);
					}
					response.setResult(vehiclesList);
					response.setStatus(true);
					response.setDisplayMsg(MyConstants.TIRE_ASSIGNED_SUCCESSFULLY);
				} else {
					// Vehicle not exists
					response.setStatus(false);
					response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
					response.setErrorMsg(vehId + " " + MyConstants.VEHICLEID_NOT_EXISTS);
				}
			} else {
				// Vehicle id is important
				response.setStatus(false);
				response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
				response.setErrorMsg(vehId + " " + MyConstants.VEHICLE_ID_REQUIRED);
			}
		} catch (Exception e) {
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	// Allocate tire to vehicle
	@RequestMapping(value = "/assignTyreToVeh", method = RequestMethod.GET)
	public @ResponseBody Response assignTyresToVehicle(@RequestParam(value = "vehId", required = false) Integer vehId,
			@RequestParam(value = "tyreId", required = false) Long tireId,
			@RequestParam(value = "tyrePosition", required = false) String tirePosition,
			@RequestParam(value = "fitmentDate", required = false) Long fitmentDate,
			@RequestParam(value = "fitmentKM", required = false) Long fitmentKM, HttpServletRequest request,
			HttpServletResponse resp) {
		Response response = new Response();
		CommonClass.fixInitialHeaders(request, resp);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {

					if (null == vehId || vehId < 1) {
						// Vehicle id is important
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
						response.setErrorMsg(vehId + " - " + MyConstants.VEHICLE_ID_REQUIRED);
					} else if (null == tireId || tireId == 0) {
						// Vehicle id is important
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
						response.setErrorMsg(tireId + " - " + MyConstants.TIREID_REQUIRED);
					} else if (null == tirePosition || tirePosition.trim().length() < 1) {
						// Vehicle id is important
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
						response.setErrorMsg(tirePosition + " - " + MyConstants.TIREPOSITION_REQUIRED);
					} else if (null == fitmentDate || fitmentDate < 1) {
						// Vehicle id is important
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.FITTED_DATE_REQUIRED);
						response.setErrorMsg(fitmentDate + " - " + MyConstants.FITTED_DATE_REQUIRED);
					} else if (null == fitmentKM || fitmentKM < 1) {
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.FITTED_KMS_REQUIRED);
						response.setErrorMsg(fitmentKM + " - " + MyConstants.FITTED_KMS_REQUIRED);
					} else {
						TMSVehicles vehicleDetials = mySQLService.getVehById(vehId);

						if (null != vehicleDetials) {
							// Check tire exists or not
							List<TMSTire> existingTires = mySQLService.getTiresByVehId(vehId);

							TMSTireView tire = mySQLService.getTireViewByTireId(tireId);

							if (null != tire) {
								SimpleDateFormat sdf = new SimpleDateFormat(MyConstants.MYSQL_DATE_TIME_FORMATER);
								// Save service details
								List<TMSTireService> services = mySQLService
										.getServicesBetweenDate(new Date(fitmentDate), tireId);
								if (services.size() > 0) {
									// Invalid fitted date
									// Service already exists on this fitted
									// date
									response.setStatus(false);
									response.setDisplayMsg(MyConstants.SERVICE_EXISTS_ON_FITTED_DATE + ": "
											+ sdf.format(new Date(fitmentDate)));
									response.setErrorMsg(fitmentDate + " - " + MyConstants.SERVICE_EXISTS_ON_FITTED_DATE
											+ ": " + sdf.format(new Date(fitmentDate)));
								} else {
									TMSTireService service = new TMSTireService();
									service.setTireId(tireId);
									service.setTireNumber(tire.getTireNumber());
									service.setDepot(tire.getDepotName());
									service.setTireMake(tire.getTireMake());
									service.setVehId(vehicleDetials.getVehId());
									service.setVehName(vehicleDetials.getVehName());
									service.setFittedDate(new Date(fitmentDate));
									service.setKmsAtTyreFitted(fitmentKM);
									service.setLocation(tirePosition);
									service.setCreatedDate(new Date());
									service.setTyreKms(0l);
									service.setCreatedBy(loginUser.getUserId());
									service.setCreatedByName(loginUser.getUserName());
									service.setOrgId(loginUser.getOrgId());
									response = mySQLService.saveOrUpdateTireServices(service);

									if (response.isStatus()) {
										// Find existing tire details if exists
										// on the position
										// If there then show the alert position
										for (TMSTire existingTire : existingTires) {
											if (existingTire.getTirePosition().equalsIgnoreCase(tirePosition)) {
												existingTire.setVehId(0l);
												existingTire.setTirePosition("");
												existingTire.setStatus(MyConstants.STATUS_INSTOCK);
												existingTire.setUpdatedDateTime(new Date());
												mySQLService.saveOrUpdateTire(existingTire);
											}
										}
										TMSTire tireDetails = mySQLService.getTireByTireId(tireId);
										tireDetails.setTirePosition(tirePosition);
										tireDetails.setVehId(vehicleDetials.getVehId());
										tireDetails.setStatus(MyConstants.STATUS_INSTALLED);
										tireDetails.setUpdatedDateTime(new Date());
										tireDetails.setLastServiceId(service.getTireServiceId());

										response = mySQLService.saveOrUpdateTire(tireDetails);
										if (response.isStatus()) {
											List<Long> vehIds = new ArrayList<>();
											vehIds.add(Long.valueOf(vehId));
											List<TMSUserVehiclesView> vehiclesList = mySQLService
													.getVehiclesByVehIds(vehIds, 0l);
											for (TMSUserVehiclesView vehicle : vehiclesList) {
												List<TMSTire> tires = mySQLService.getTiresByVehId(vehicle.getVehId());
												vehicle.setTires(tires);
											}
											response.setResult(vehiclesList);
											response.setDisplayMsg(MyConstants.TIRE_ASSIGNED_SUCCESSFULLY);
										} else {
											response.setStatus(false);
											response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
										}
									} else {
										response.setStatus(false);
										response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
									}
								}
							} else {
								response.setStatus(false);
								response.setDisplayMsg(MyConstants.TIRE_NOT_EXISTS);
								response.setErrorMsg(tireId + " " + MyConstants.TIRE_NOT_EXISTS);
							}

						} else {
							// Vehicle not exists
							response.setStatus(false);
							response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
							response.setErrorMsg(vehId + " " + MyConstants.VEHICLEID_NOT_EXISTS);
						}
					}
				} else {// Session expired
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {// Session expired
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (

		Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	// Deallocate Tire to vehicle
	@RequestMapping(value = "/deallocateTyre", method = RequestMethod.GET)
	public @ResponseBody Response deAllocateTire(HttpServletRequest request,
			@RequestParam(value = "tyreId", required = false) Long tyreId,
			@RequestParam(value = "vehId", required = false) Long vehId,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "removalDate", required = false) Long removalDate,
			@RequestParam(value = "removalKM", required = false) Long removalKM,
			@RequestParam(value = "reason", required = false) String reason,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "tyreCondition", required = false) String tyreCondition,
			@RequestParam(value = "scrappedParty", required = false) String scrappedParty) {

		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == tyreId || tyreId == 0) {
						// Please select a tire
						response.setDisplayMsg(MyConstants.SELECT_TIRE);
						response.setErrorMsg(tyreId + " - " + MyConstants.TIREID_REQUIRED);
					} else if (null == vehId || vehId == 0) {
						response.setDisplayMsg(MyConstants.BUS_NO_REQUIRED);
						response.setErrorMsg(vehId + " - " + MyConstants.BUS_NO_REQUIRED);
					} else if (null == status || status.equalsIgnoreCase("undefined") || status.equalsIgnoreCase("null")
							|| status.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.STATUS_ERROR);
						response.setErrorMsg(status + " - " + MyConstants.STATUS_ERROR);
					} else if (null == removalDate || removalDate == 0) {
						response.setDisplayMsg(MyConstants.TIRE_REMOVAL_DATE_REQUIRED);
						response.setErrorMsg(removalDate + " - " + MyConstants.TIRE_REMOVAL_DATE_REQUIRED);
					} else if (null == removalKM || removalKM == 0) {
						response.setDisplayMsg(MyConstants.REMOVAL_KMS_REQUIRED);
						response.setErrorMsg(removalKM + " - " + MyConstants.REMOVAL_KMS_REQUIRED);
					} else if (null == reason || reason.equalsIgnoreCase("undefined") || reason.equalsIgnoreCase("null")
							|| reason.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.REASON_REQUIRED);
						response.setErrorMsg(reason + " - " + MyConstants.REASON_REQUIRED);
					} else if (null == action || action.equalsIgnoreCase("undefined") || action.equalsIgnoreCase("null")
							|| action.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.ACTION_REQUIRED);
						response.setErrorMsg(action + " - " + MyConstants.ACTION_REQUIRED);
					} else if (null == tyreCondition || tyreCondition.equalsIgnoreCase("undefined")
							|| tyreCondition.equalsIgnoreCase("null") || tyreCondition.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.TIRE_CONDITION_REQUIRED);
						response.setErrorMsg(tyreCondition + " - " + MyConstants.TIRE_CONDITION_REQUIRED);
					} else if (null == scrappedParty || scrappedParty.equalsIgnoreCase("undefined")
							|| scrappedParty.equalsIgnoreCase("null") || scrappedParty.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.TIRE_SCRAPPED_PARTY_REQUIRED);
						response.setErrorMsg(scrappedParty + " - " + MyConstants.TIRE_SCRAPPED_PARTY_REQUIRED);
					} else {
						TMSTire tire = mySQLService.getTireByTireId(tyreId);
						if (null != tire) {
							if (tire.getVehId() == vehId) {
								SimpleDateFormat sdf = new SimpleDateFormat(MyConstants.MYSQL_DATE_TIME_FORMATER);

								List<TMSTireService> conditionService = mySQLService
										.getServicesBetweenDate(new Date(removalDate), tire.getTireId());

								if (conditionService.size() == 0) {
									// Update the service
									TMSTireService existingService = mySQLService
											.getTMSTireServiceById(tire.getLastServiceId());

									if (null != existingService && existingService.getVehId() == vehId) {

										if (new Date(removalDate).after(existingService.getFittedDate())) {
											if (removalKM > existingService.getKmsAtTyreFitted()) {
												existingService.setActionTaken(action);
												existingService.setKmsAtTyreRemoved(removalKM);
												existingService.setReason(reason);
												existingService.setScrappedToParty(scrappedParty);
												existingService.setTyreCondition(tyreCondition);
												existingService
														.setTyreKms(removalKM - existingService.getKmsAtTyreFitted());
												existingService.setRemovalDate(new Date(removalDate));

												response = mySQLService.saveOrUpdateTireServices(existingService);
												if (response.isStatus()) {
													// Deallocate tire and
													// change
													// the
													// status
													tire.setVehId(0l);
													tire.setStatus(status);
													tire.setLastServiceId(0l);
													tire.setTotalTyreKM(tire.getTotalTyreKM()
															+ (removalKM - existingService.getKmsAtTyreFitted()));

													response = mySQLService.saveOrUpdateTire(tire);
													if (response.isStatus()) {
														response.setDisplayMsg(MyConstants.TIRE_DEALLOCATED_SUCCESS);
														List<Long> vehIds = new ArrayList<>();
														vehIds.add(Long.valueOf(vehId));
														List<TMSUserVehiclesView> vehiclesList = mySQLService
																.getVehiclesByVehIds(vehIds, 0l);
														for (TMSUserVehiclesView vehicle : vehiclesList) {
															List<TMSTire> tires = mySQLService
																	.getTiresByVehId(vehicle.getVehId());
															vehicle.setTires(tires);
														}
														response.setResult(vehiclesList);
													} else {
														response.setDisplayMsg(MyConstants.TIRE_DEALLOCATED_FAILED);
													}
												} else {
													response.setDisplayMsg(MyConstants.TIRE_SERVICE_UPDATE_FAILED);
												}
											} else {
												// Removal KM should not be
												// greater
												// than
												// the
												// fitment KM
												response.setDisplayMsg(MyConstants.FITMENT_KM_GREATERTHAN_REMOVAL_KM
														+ ": " + existingService.getKmsAtTyreFitted());
											}
										} else {
											// Removal Date should be greater
											// than
											// the
											// fitment Date
											response.setDisplayMsg(MyConstants.FITMENT_DATE_GREATERTHAN_REMOVAL_DATE
													+ ": " + sdf.format(existingService.getFittedDate()));
										}
									} else {
										// tyre fitment details are not exists
										// (service
										// not exists)
										response.setDisplayMsg(MyConstants.FITMENT_DETAILS_NOT_EXISTS);
									}
								} else {
									response.setStatus(false);
									response.setDisplayMsg(MyConstants.SERVICE_EXISTS_ON_REMOVAL_DATE + ": "
											+ sdf.format(new Date(removalDate)));
									response.setErrorMsg(
											removalDate + " - " + MyConstants.SERVICE_EXISTS_ON_REMOVAL_DATE + ": "
													+ sdf.format(new Date(removalDate)));
								}
							} else {
								response.setDisplayMsg(
										tire.getTireNumber() + " " + MyConstants.TIRE_NOT_ASSIGNED_TO_THIS_VEHICLE);
								response.setErrorMsg(
										tyreId + " - " + MyConstants.TIRE_NOT_ASSIGNED_TO_THIS_VEHICLE + " - " + vehId);
							}
						} else {
							response.setDisplayMsg(MyConstants.TIRE_NOT_EXISTS);
							response.setErrorMsg(tyreId + " - " + MyConstants.TIRE_NOT_EXISTS);
						}
					}

				} else {
					// Session expired
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	// Get tire Inspection details
	@RequestMapping(value = "/Tire/getInspections", method = RequestMethod.GET)
	public @ResponseBody Response getInspections(HttpServletRequest request,
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "startIndex", required = false) Integer startIndex) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == limit || limit == 0) {
						limit = 50;
					}
					if (null == startIndex) {
						startIndex = 0;
					}
					if (loginUser.getTMSUserLevel() < 5) {
						// All Tire Inspections
						List<TMSTireInspection> list = mySQLService.getTMSTireInspections(0, limit, startIndex);
						response.setStatus(true);
						response.setResult(list);
					} else {
						// Get the records only created by the login user
						List<TMSTireInspection> list = mySQLService.getTMSTireInspections(loginUser.getUserId(), limit,
								startIndex);
						response.setStatus(true);
						response.setResult(list);
					}
				} else {
					// Session expired
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	// Get tire service history detials
	@RequestMapping(value = "/Tire/getServices", method = RequestMethod.GET)
	public @ResponseBody Response getServices(HttpServletRequest request,
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "startIndex", required = false) Integer startIndex) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == limit || limit == 0) {
						limit = 50;
					}
					if (null == startIndex) {
						startIndex = 0;
					}
					System.out.println("user level in action ctrl :" + loginUser.getUserLevel());

					System.out.println("user org in action ctrl :" + loginUser.getOrgId());
					if (loginUser.getTMSUserLevel() == 5) {
						// Get the records belongs to login user org id
						List<TMSTireService> list = mySQLService.getTMSTireServices(loginUser.getOrgId(), 0, limit,
								startIndex);
						response.setStatus(true);
						response.setResult(list);
					} else if (loginUser.getTMSUserLevel() > 5) {
						// Get the records only created by the login user
						List<TMSTireService> list = mySQLService.getTMSTireServices(0, loginUser.getUserId(), limit,
								startIndex);
						response.setStatus(true);
						response.setResult(list);
					} else {
						// All Tire Inspections
						List<TMSTireService> list = mySQLService.getTMSTireServices(0, 0, limit, startIndex);
						response.setStatus(true);
						response.setResult(list);
					}
				} else {
					// Session expired
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	// Get sensors by limit
	@RequestMapping(value = "/getSensors", method = RequestMethod.GET)
	public @ResponseBody Response getSensorsByLimit(@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "startIndex", required = false) Integer startIndex, HttpServletRequest request,
			HttpServletResponse resp) {
		CommonClass.fixInitialHeaders(request, resp);
		Response response = new Response();
		response.setStatus(false);
		List<TMSSensor> sensors = new ArrayList<>();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					CommonClass.fixHeaders(request, resp);
					if (limit == null || limit.equals(null) || limit.equals("undefied")) {
						limit = 100;
					}
					if (null == startIndex || startIndex.equals("null") || startIndex.equals("undefined")) {
						startIndex = 0;
					}
					if (null == status || status.trim().length() == 0) {
						sensors = mySQLService.getSensors(null, limit, startIndex);
						response.setStatus(true);
					} else {
						// Check status exists in the predefined list are not
						String actualStatus = getStatus(status);
						if (null != actualStatus) {
							response.setStatus(true);
							sensors = mySQLService.getSensors(actualStatus, limit, startIndex);
						} else {
							response.setStatus(false);
							response.setDisplayMsg(MyConstants.STATUS_ERROR);
							response.setErrorMsg(status + " - " + MyConstants.STATUS_ERROR);
						}
					}
					response.setResult(sensors);
				} else {
					// Session expired
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	// Get tire details from TMS_TireView
	@RequestMapping(value = "/getTyreDetails", method = RequestMethod.GET)
	public @ResponseBody Response getTires(@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "startIndex", required = false) Integer startIndex,
			@RequestParam(value = "fullDetails", required = false) Boolean fullDetails, HttpServletRequest request,
			HttpServletResponse resp) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					CommonClass.fixHeaders(request, resp);
					if (null == fullDetails || fullDetails == false) {
						// Get less details only
						if (null == status || status.equalsIgnoreCase("null") || status.trim().length() < 1) {
							List<TMSTireShortDetails> tireDeails = mySQLService.getShortTireDetails();
							response.setResult(tireDeails);
							response.setStatus(true);
						} else {

						}
					} else {
						// Get full details
						if (null == limit) {
							limit = 50;
						}
						if (null == startIndex) {
							startIndex = 0;
						}

						if (null == status || status.equalsIgnoreCase("null") || status.trim().length() < 1) {
							// Get only InStock tires
						} else {

						}
					}

				} else {
					// Session expired
					CommonClass.fixInitialHeaders(request, resp);
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				CommonClass.fixInitialHeaders(request, resp);
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	// Get tire details from TMS_tires table
	@RequestMapping(value = "/getTyres", method = RequestMethod.GET)
	public @ResponseBody Response getTires(@RequestParam(value = "status", required = false) String status) {
		Response response = new Response();
		List<TMSTireView> tires = new ArrayList<>();
		response.setStatus(true);
		try {
			if (null == status || status.trim().length() == 0)
				tires = mySQLService.getTireViewDetials(null);
			else {
				// Check status exists in the predefined list are not
				String actualStatus = getStatus(status);
				if (null != actualStatus) {
					tires = mySQLService.getTireViewDetials(actualStatus);
				} else {
					response.setStatus(false);
					response.setDisplayMsg(MyConstants.STATUS_ERROR);
					response.setErrorMsg(status + " - " + MyConstants.STATUS_ERROR);
				}
			}
			response.setResult(tires);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getBController", method = RequestMethod.GET)
	public @ResponseBody Response getBController(@RequestParam(value = "status", required = false) String status) {
		Response response = new Response();
		response.setStatus(true);
		List<TMSBController> bCtrls = new ArrayList<>();
		try {
			if (null == status || status.trim().length() == 0)
				bCtrls = mySQLService.getBController(null);
			else {
				// Check status exists in the predefined list are not
				String actualStatus = getStatus(status);
				if (null != actualStatus)
					bCtrls = mySQLService.getBController(status);
				else {
					response.setStatus(false);
					response.setDisplayMsg(MyConstants.STATUS_ERROR);
					response.setErrorMsg(status + " - " + MyConstants.STATUS_ERROR);
				}
			}
			response.setResult(bCtrls);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getRFID", method = RequestMethod.GET)
	public @ResponseBody Response getRFID(@RequestParam(value = "status", required = false) String status) {
		Response response = new Response();
		response.setStatus(true);
		List<TMSRFID> rfids = new ArrayList<>();
		try {
			if (null == status || status.trim().length() == 0)
				rfids = mySQLService.getRFID(null);
			else {
				// Check status exists in the predefined list are not
				String actualStatus = getStatus(status);
				if (null != actualStatus)
					rfids = mySQLService.getRFID(actualStatus);
				else {
					response.setStatus(false);
					response.setDisplayMsg(MyConstants.STATUS_ERROR);
					response.setErrorMsg(status + " - " + MyConstants.STATUS_ERROR);
				}
			}
			response.setResult(rfids);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getTyreMakeList", method = RequestMethod.GET)
	public @ResponseBody Response getTyreMakeList(HttpServletRequest request, HttpServletResponse resp) {
		Response response = new Response();
		response.setStatus(true);
		List<TMSTyreMake> tyreMakeList = new ArrayList<>();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				CommonClass.fixHeaders(request, resp);
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					tyreMakeList = mySQLService.getAllTMSTyreMake(loginUser.getOrgId());
					response.setResult(tyreMakeList);
				} else {
					// Session expired
					CommonClass.fixInitialHeaders(request, resp);
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				CommonClass.fixInitialHeaders(request, resp);
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getTMSDepotList", method = RequestMethod.GET)
	public @ResponseBody Response getTMSDepotList(HttpServletRequest request, HttpServletResponse resp) {
		Response response = new Response();
		response.setStatus(true);
		System.out.println("getTMSDepotList 1: " + request.getSession().getId());
		System.out.println("getTMSDepotList 2: " + request.getSession().getLastAccessedTime());
		System.out.println("getTMSDepotList 3: " + request.getSession().getMaxInactiveInterval());
		List<TMSDepot> tmsDepotList = new ArrayList<>();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				CommonClass.fixHeaders(request, resp);
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					tmsDepotList = mySQLService.getAllTMSDepots(loginUser.getOrgId());
					response.setResult(tmsDepotList);
				} else {
					// Session expired
					CommonClass.fixInitialHeaders(request, resp);
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				CommonClass.fixInitialHeaders(request, resp);
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getTMSOrgList", method = RequestMethod.GET)
	public @ResponseBody Response getTMSOrgList(HttpServletRequest request, HttpServletResponse resp) {
		Response response = new Response();
		response.setStatus(true);
		List<Organizations> tmsOrgList = new ArrayList<>();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				CommonClass.fixHeaders(request, resp);
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					tmsOrgList = mySQLService.getAllTMSOrgs();
					response.setResult(tmsOrgList);
				} else {
					// Session expired
					CommonClass.fixInitialHeaders(request, resp);
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				CommonClass.fixInitialHeaders(request, resp);
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getTMSUsersList", method = RequestMethod.GET)
	public @ResponseBody Response getTMSUsersList(HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(true);
		List<TMSUserBean> tmsUsersList = new ArrayList<>();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					List<UserMaster> usersList = null;

					// All uses if TMS admin logged in
					if (loginUser.getTMSUserLevel() <= 4) {
						usersList = mySQLService.getAllTMSUsersByOrgId(0);
					} else if (loginUser.getTMSUserLevel() == 5) {
						usersList = mySQLService.getAllTMSUsersByOrgId(loginUser.getOrgId());
					} else {
						// Dont have permission to access TMS users list
						response.setDisplayMsg(MyConstants.DONT_HAVE_PERMISSION);
						response.setErrorMsg(MyConstants.DONT_HAVE_PERMISSION);
					}
					if (null != usersList) {
						for (UserMaster user : usersList) {
							TMSUserBean tmsUser = new TMSUserBean();
							try {
								tmsUser.setUserId(user.getUserId());
								tmsUser.setUserName(user.getUserName());
								tmsUser.setOrgName(user.getOrgName());
								tmsUser.setUserLevel(user.getUserLevel());
							} catch (Exception e) {
								e.printStackTrace();
							}
							tmsUsersList.add(tmsUser);
						}
						response.setResult(tmsUsersList);
					}
				} else {
					// Session expired
					response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					response.setErrorMsg(MyConstants.SESSION_EXPIRED);
				}
			} else {
				// Session expired
				response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
				response.setErrorMsg(MyConstants.SESSION_EXPIRED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	private String getStatus(String status) {
		try {
			if (null != status) {
				if (status.equalsIgnoreCase(MyConstants.STATUS_INSTALLED)) {
					return MyConstants.STATUS_INSTALLED;
				} else if (status.equalsIgnoreCase(MyConstants.STATUS_INSTOCK)) {
					return MyConstants.STATUS_INSTOCK;
				} else if (status.equalsIgnoreCase(MyConstants.STATUS_SCRAPED)) {
					return MyConstants.STATUS_SCRAPED;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
