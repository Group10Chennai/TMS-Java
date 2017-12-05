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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tms.beans.MyConstants;
import com.tms.beans.Response;
import com.tms.beans.TMSUserBean;
import com.tms.beans.VehicleTyreCount;
import com.tms.customheaders.CommonClass;
import com.tms.dao.mongo.MongoOperations;
import com.tms.dto.TMSDtoI;
import com.tms.model.Organizations;
import com.tms.model.TMSBController;
import com.tms.model.TMSBasicVehicleDetails;
import com.tms.model.TMSDepot;
import com.tms.model.TMSMinMaxTempPressure;
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
import com.tms.model.UserMaster;
import com.tms.model.mongo.TPMSData_Report;
import com.tms.model.mongo.TPMSData_latest;
import com.tms.service.MySQLService;

@Controller
@RequestMapping("/api/tms")
public class TMSActionController {

	private static final Logger logr = Logger.getLogger(TMSActionController.class);

	@Autowired
	private MySQLService mySQLService;

	@Autowired
	private TMSDtoI tMSDtoI;

	@Autowired
	private MongoOperations mongoOperations;

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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveTPMSLatestData", method = RequestMethod.POST)
	public @ResponseBody Response saveTPMSLatestData_POST(@RequestBody TPMSData_latest tpmsData_latest) {
		Response response = new Response();
		response.setStatus(false);
		try {
			// Insert or update the record into Latest table
			tpmsData_latest.setServer_date_time(new Date().getTime());
			List<Long> vehIds = new ArrayList<>(1);
			vehIds.add(tpmsData_latest.getVehId());
			response = mongoOperations.getLiveData(vehIds);
			if (null != response.getResult() && (!response.getResult().isEmpty())) {
				List<TPMSData_latest> resutl = response.getResult();
				if (resutl.size() > 0) {
					TPMSData_latest latest = resutl.get(0);
					latest.setDevice_date_time(tpmsData_latest.getDevice_date_time());
					latest.setServer_date_time(new Date().getTime());
					latest.setTyres(tpmsData_latest.getTyres());
					
					mongoOperations.saveOrUpdateTPMSDataLatest(latest);
				}
			} else {
				response = mongoOperations.saveOrUpdateTPMSDataLatest(tpmsData_latest);
			}
			
			//Insert the record into report table
			TPMSData_Report report = new TPMSData_Report();
			report.setDevice_date_time(tpmsData_latest.getDevice_date_time());
			report.setServer_date_time(tpmsData_latest.getServer_date_time());
			report.setTyres(tpmsData_latest.getTyres());
			report.setVehId(tpmsData_latest.getVehId());
			
			mongoOperations.saveTPMSDataReport(report);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@RequestMapping(value = "/getProblematicVehicles", method = RequestMethod.GET)
	public @ResponseBody Response getProblematicVehicles(HttpServletRequest request, HttpServletResponse resp) {
		Response response = new Response();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					List<Long> vehIds = tMSDtoI.getAndSetUserVehToSession(null, mySQLService, session);
					logr.info("getProblematicVehicles: " + vehIds.toString());
					TMSMinMaxTempPressure minMaxTempPressureValues = mySQLService
							.getMinMaxTempPressureValues(loginUser.getOrgId(), loginUser.getUserId());
					response = mongoOperations.getProblematicVehicles(vehIds, minMaxTempPressureValues.getMinPressure(),
							minMaxTempPressureValues.getMaxPressure(), minMaxTempPressureValues.getMinTemp(),
							minMaxTempPressureValues.getMaxTemp());
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

	@RequestMapping(value = "/getProblematicVehCount", method = RequestMethod.GET)
	public @ResponseBody Response getProblematicVehCount(HttpServletRequest request, HttpServletResponse resp) {
		Response response = new Response();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					List<Long> vehIds = tMSDtoI.getAndSetUserVehToSession(null, mySQLService, session);

					TMSMinMaxTempPressure minMaxTempPressureValues = mySQLService
							.getMinMaxTempPressureValues(loginUser.getOrgId(), loginUser.getUserId());
					response = mongoOperations.getProblematicVehCount(vehIds, minMaxTempPressureValues.getMinPressure(),
							minMaxTempPressureValues.getMaxPressure(), minMaxTempPressureValues.getMinTemp(),
							minMaxTempPressureValues.getMaxTemp());
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

	@RequestMapping(value = "/getDashboardDetails", method = RequestMethod.GET)
	public @ResponseBody Response getDashboardDetails(HttpServletRequest request, HttpServletResponse resp) {
		Response response = new Response();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					TMSUserBean userDetails = new TMSUserBean();

					// General details
					userDetails.setUserId(loginUser.getUserId());
					userDetails.setUserLevel(loginUser.getTMSUserLevel());
					userDetails.setUserName(loginUser.getUserName());
					userDetails.setOrgName(loginUser.getOrgName());

					// Get user vehicle Details
					List<TMSBasicVehicleDetails> vehicles = new ArrayList<>();
					if (loginUser.getTMSUserLevel() >= 5) {
						vehicles = mySQLService.getAllBasicVehDetialsByUserId(loginUser.getUserId());
					} else {
						vehicles = mySQLService.getVehicles();
					}

					userDetails.setVehicles(vehicles);

					List<Long> vehIds = new ArrayList<>();
					for (TMSBasicVehicleDetails veh : vehicles) {
						vehIds.add(veh.getVehId());
					}
					session.setAttribute("vehIds", vehIds);

					// Get min, max values of Temp & Pressure
					TMSMinMaxTempPressure minMaxTempPressureValues = mySQLService
							.getMinMaxTempPressureValues(loginUser.getOrgId(), loginUser.getUserId());
					minMaxTempPressureValues.setTMSMinMaxTempPressureId(0);
					userDetails.setMinMaxTempPressureValues(minMaxTempPressureValues);

					// Find the Vehicle Tyre count
					logr.info("Dashboard : " + vehIds);
					long allTiresConfigVehCount = 0;
					List<VehicleTyreCount> vehTireCount = mySQLService.findSemiAssignedVehCount(vehIds);
					for (VehicleTyreCount vehTire : vehTireCount) {
						if (vehTire.getTireCount() == 6) {
							allTiresConfigVehCount++;
						}
					}

					userDetails.setAllTiresConfigVehCount(allTiresConfigVehCount);

					// Get assigned depot list

					// Get differe Tyre count details
					if (loginUser.getTMSUserLevel() >= 5) {
						// For org related
						userDetails
								.setTireCount_all(mySQLService.getTireCountBasedOnStatus(null, loginUser.getOrgId()));

						userDetails.setTireCount_installed(mySQLService
								.getTireCountBasedOnStatus(MyConstants.STATUS_INSTALLED, loginUser.getOrgId()));

						userDetails.setTireCount_instock(mySQLService
								.getTireCountBasedOnStatus(MyConstants.STATUS_INSTOCK, loginUser.getOrgId()));

						userDetails.setTireCount_scraped(mySQLService
								.getTireCountBasedOnStatus(MyConstants.STATUS_SCRAPED, loginUser.getOrgId()));

						userDetails.setTireCount_services(mySQLService.getTireServiceCount(loginUser.getOrgId()));

						userDetails
								.setTireCount_inspections(mySQLService.getTireInspectionsCount(loginUser.getOrgId()));
					} else {
						// For SysAdmin
						userDetails.setTireCount_all(mySQLService.getTireCountBasedOnStatus(null, 0));

						userDetails.setTireCount_installed(
								mySQLService.getTireCountBasedOnStatus(MyConstants.STATUS_INSTALLED, 0));

						userDetails.setTireCount_instock(
								mySQLService.getTireCountBasedOnStatus(MyConstants.STATUS_INSTOCK, 0));

						userDetails.setTireCount_scraped(
								mySQLService.getTireCountBasedOnStatus(MyConstants.STATUS_SCRAPED, 0));

						userDetails.setTireCount_services(mySQLService.getTireServiceCount(0));

						userDetails.setTireCount_inspections(mySQLService.getTireInspectionsCount(0));
					}

					// Final response
					List<TMSUserBean> userDetailsList = new ArrayList<>(1);
					userDetailsList.add(userDetails);

					response.setStatus(true);
					response.setResult(userDetailsList);
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getSemiConfiguredVehicles", method = RequestMethod.GET)
	public @ResponseBody Response getSemiConfiguredVehicles(HttpServletRequest request, HttpServletResponse resp,
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "startIndex", required = false) Integer startIndex) {
		Response response = new Response();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					// Find all assigned vehicle details
					List<Long> vehIds = tMSDtoI.getAndSetUserVehToSession(null, mySQLService, session);
					// Find the vehicles which have less no of tyres
					List<VehicleTyreCount> vehTyreCountList = mySQLService.findSemiAssignedVehCount(vehIds);
					for (VehicleTyreCount vehTyreCount : vehTyreCountList) {
						if (vehTyreCount.getTireCount() == 6) {
							vehIds.remove(vehTyreCount.getVehId());
						}
					}

					// Now VehIds list have only vehicles which are not having
					// all tyres
					Response response1 = mySQLService.getAllUserVehDetails(null, vehIds, limit, startIndex);
					List<TMSUserVehiclesView_CompositeKey> userVehDetails = response1.getResult();
					for (TMSUserVehiclesView_CompositeKey vehicle : userVehDetails) {
						List<TMSTire> tires = mySQLService
								.getTiresByVehId(vehicle.getTmsUserVehiclesView_Keys().getVehId());
						vehicle.setTires(tires);
					}

					response.setResult(userVehDetails);
					response.setStatus(true);
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

	// Get vehicle details based on limit or Vehicle ids (POST)
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getVehicles", method = RequestMethod.POST)
	public @ResponseBody Response getVehicles(HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					// vehiclesList = new ArrayList<>();
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
						List<TMSUserVehiclesView> vehiclesList = mySQLService.getVehiclesByVehIds(vehIds,
								loginUser.getUserId());
						for (TMSUserVehiclesView vehicle : vehiclesList) {
							List<TMSTire> tires = mySQLService.getTiresByVehId(vehicle.getVehId());
							vehicle.setTires(tires);
						}

						response.setResult(vehiclesList);
						response.setCount(vehiclesList.size());
					} else {
						Integer limit = 50;
						Integer startIndex = 0;
						if (null != request.getParameter("limit")) {
							limit = Integer.valueOf(request.getParameter("limit"));
						}
						if (null != request.getParameter("startIndex")) {
							startIndex = Integer.valueOf(request.getParameter("startIndex"));
						}

						if (null != request.getParameter("configType")
								&& request.getParameter("configType").equalsIgnoreCase("semi")) {

							// Find all assigned vehicle details
							List<Long> vehIds = new ArrayList<>();
							if (null != request.getParameter("searchWord")
									&& request.getParameter("searchWord").trim().length() > 0) {

								// Vehicle ids based on search string
								response = mySQLService.searchVehicles(request.getParameter("searchWord"),
										loginUser.getUserId(), limit, startIndex);
								List<TMSUserVehiclesView> veh_list = response.getResult();
								for (TMSUserVehiclesView veh : veh_list) {
									vehIds.add(veh.getVehId());
								}
							} else {
								// Get veh ids
								vehIds = tMSDtoI.getAndSetUserVehToSession(null, mySQLService, session);
							}
							List<Long> vehIds_copy = new ArrayList<>(vehIds.size());
							for (long vehId : vehIds) {
								vehIds_copy.add(vehId);
							}

							// Find the vehicles which have less no of tyres
							List<VehicleTyreCount> vehTyreCountList = mySQLService.findSemiAssignedVehCount(vehIds);
							for (VehicleTyreCount vehTyreCount : vehTyreCountList) {
								if (vehTyreCount.getTireCount() == 6) {
									vehIds_copy.remove(vehTyreCount.getVehId());
									// vehIds_fullyConfig.add(vehTyreCount.getVehId());
								}
							}
							// Now VehIds list have only vehicles which are not
							// having all tyres
							Response response1 = mySQLService.getAllUserVehDetails(null, vehIds_copy, limit,
									startIndex);
							List<TMSUserVehiclesView_CompositeKey> userVehDetails = response1.getResult();
							for (TMSUserVehiclesView_CompositeKey vehicle : userVehDetails) {
								List<TMSTire> tires = mySQLService
										.getTiresByVehId(vehicle.getTmsUserVehiclesView_Keys().getVehId());
								vehicle.setTires(tires);
							}

							response.setResult(userVehDetails);
							response.setCount(vehIds_copy.size());
							response.setStatus(true);
						} else if (null != request.getParameter("searchWord")
								&& request.getParameter("searchWord").trim().length() > 0) {
							response = mySQLService.searchVehicles(request.getParameter("searchWord"),
									loginUser.getUserId(), limit, startIndex);
							// List<TMSUserVehiclesView> veh_list =
							// response.getResult();
							// for (TMSUserVehiclesView veh : veh_list) {
							// System.out.println(veh.getVehId() + " - " +
							// veh.getVehName());
							// }
						} else {
							List<TMSUserVehiclesView> vehiclesList = new ArrayList<>();
							if (loginUser.getTMSUserLevel() >= 5) {

								// Org admin or below cader
								vehiclesList = mySQLService.getVehiclesByLimit(loginUser.getUserId(), limit,
										startIndex);
								response.setCount(mySQLService.getVehiclesCountByUserId(loginUser.getUserId(), true));
							} else {
								// Sys Admin
								// Unique Vehicles list
								vehiclesList = mySQLService.getVehiclesByLimit(0l, limit, startIndex);
								response.setCount(mySQLService.getVehiclesCountByUserId(0l, true));
							}
							for (TMSUserVehiclesView vehicle : vehiclesList) {
								List<TMSTire> tires = mySQLService.getTiresByVehId(vehicle.getVehId());
								vehicle.setTires(tires);
							}
							response.setResult(vehiclesList);
						}
					}
					response.setStatus(true);

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

	@RequestMapping(value = "/getUserVehDetials", method = RequestMethod.GET)
	public @ResponseBody Response getUserVehDetials(HttpServletRequest request,
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "startIndex", required = false) Integer startIndex,
			@RequestParam(value = "searchWord", required = false) String searchWord) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (limit == null || limit == 0) {
						response.setDisplayMsg(MyConstants.LIMIT_REQUIRED);
						response.setErrorMsg(limit + " - " + MyConstants.LIMIT_REQUIRED);
					} else if (startIndex == null) {
						response.setDisplayMsg(MyConstants.START_INDEX_REQUIRED);
						response.setErrorMsg(startIndex + " - " + MyConstants.START_INDEX_REQUIRED);
					} else {
						try {
							if (null != searchWord && (!searchWord.equalsIgnoreCase("undefined"))
									&& searchWord.trim().length() > 0) {
								// Search the vehicles based on search word
								response = mySQLService.getAllUserVehDetails(searchWord, null, limit, startIndex);
							} else {
								// Get the vehicles list
								response = mySQLService.getAllUserVehDetails(null, null, limit, startIndex);
							}
						} catch (Exception e) {
							e.printStackTrace();
							response.setDisplayMsg(MyConstants.SPECIFY_LIMIT_START_INDEX_VALUES);
							response.setErrorMsg(
									limit + " - " + startIndex + " - " + MyConstants.SPECIFY_LIMIT_START_INDEX_VALUES);
							return response;
						}
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

	// Search vehicles by search string
	// @RequestMapping(value = "/searchVehicles", method = RequestMethod.GET)
	// public @ResponseBody Response searchVehicles(@RequestParam("searchWord")
	// String searchWord,
	// HttpServletRequest request) {
	// Response response = new Response();
	// response.setStatus(false);
	// try {
	// HttpSession session = request.getSession(false);
	// if (null != session && session.isNew() == false) {
	// UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
	// if (loginUser != null) {
	// List<TMSUserVehiclesView> vehiclesList = new ArrayList<>();
	// // Checking search condition based on vehIds
	// if (null != searchWord && searchWord.trim().length() > 0) {
	// Integer limit = 50;
	// Integer startIndex = 0;
	// if (null != request.getParameter("limit")) {
	// limit = Integer.valueOf(request.getParameter("limit"));
	// }
	// if (null != request.getParameter("startIndex")) {
	// startIndex = Integer.valueOf(request.getParameter("startIndex"));
	// }
	//
	// vehiclesList = mySQLService.searchVehicles(searchWord,
	// loginUser.getUserId(), limit, startIndex);
	// response.setResult(vehiclesList);
	// response.setStatus(true);
	// } else {
	// response.setDisplayMsg(MyConstants.SEARCH_WORD_REQUIRED);
	// response.setErrorMsg(searchWord + " - " +
	// MyConstants.SEARCH_WORD_REQUIRED);
	// response.setStatus(false);
	// }
	// } else {
	// // Session expired
	// response.setStatus(false);
	// response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
	// response.setErrorMsg(MyConstants.SESSION_EXPIRED);
	// }
	// } else {
	// // Session expired
	// response.setStatus(false);
	// response.setDisplayMsg(MyConstants.SESSION_EXPIRED);
	// response.setErrorMsg(MyConstants.SESSION_EXPIRED);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return response;
	// }

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

					if (loginUser.getTMSUserLevel() <= 5) { // Above or Equal to
															// the Org Admins
															// only

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

								List<TMSVehicles> vehicles = mySQLService.getVehByVehIds(vehIds);

								int count = 0;
								for (UserMaster user : users) {
									for (TMSVehicles veh : vehicles) {
										// Save data
										try {
											// Check whether this vehicle is
											// assigned to this user or not
											TMSUserVehicleMapping userVehMapping = mySQLService
													.getTMSUserVehicleMappingDetails(veh.getVehId(), user.getUserId());
											if (userVehMapping == null) {
												// New Vehicle assign to user
												userVehMapping = new TMSUserVehicleMapping();
												userVehMapping.setCreatedDateTime(new Date());
												userVehMapping.setAssignedBy(loginUser.getUserId());
												userVehMapping.setStatus(1);
												userVehMapping.setUserId(user.getUserId());
												userVehMapping.setVehId(veh.getVehId());

												// Save new object
												mySQLService.saveOrUpdateUserVehMapping(userVehMapping);
											} else {
												// Vehicle is assigned to user
												// Check status whether it is
												// disabled or not
												if (userVehMapping.getStatus() == 0) { // Disabled
													// Enable it
													userVehMapping.setStatus(1);
													// Update in DB
													mySQLService.saveOrUpdateUserVehMapping(userVehMapping);
												}
											}
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
						response.setDisplayMsg(MyConstants.DONT_HAVE_PERMISSION);
						response.setErrorMsg(MyConstants.DONT_HAVE_PERMISSION);
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
						// Check vehicle exists or not
						TMSVehicles vehicleDetials = mySQLService.getVehById(vehId);
						if (null != vehicleDetials) {

							// Check tire exists or not
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
									// Check tire exists on the given position
									// or not
									List<TMSTire> existingTires = mySQLService.getTiresByVehId(vehId);
									for (TMSTire existingTire : existingTires) {
										if (existingTire.getTirePosition().equalsIgnoreCase(tirePosition)) {
											// Tire exists on this position
											response.setErrorMsg(MyConstants.TIRE_EXISTS_IN_THIS_POSITION);
											response.setErrorMsg(
													tirePosition + " - " + MyConstants.TIRE_EXISTS_IN_THIS_POSITION);
											response.setStatus(false);
											return response;
										}
									}

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
			@RequestParam(value = "scrappedParty", required = false) String scrappedParty,
			@RequestParam(value = "depthLocation1", required = false) Double depthLocation1,
			@RequestParam(value = "depthLocation2", required = false) Double depthLocation2,
			@RequestParam(value = "depthLocation3", required = false) Double depthLocation3,
			@RequestParam(value = "tirePressure", required = false) Double tirePressure) {

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
								boolean saveInspection = false;
								if (null != depthLocation1 && depthLocation1 > 0 && null != depthLocation2
										&& depthLocation2 > 0 && null != depthLocation3 && depthLocation3 > 0) {
									if (null != tirePressure && tirePressure > 0) {
										saveInspection = true;
									} else {
										response.setDisplayMsg(MyConstants.TIRE_PRESSURE_REQUIRED);
										response.setErrorMsg(tirePressure + " - " + MyConstants.TIRE_PRESSURE_REQUIRED);
										return response;
									}
								} else if ((null != depthLocation1 && depthLocation1 > 0)
										|| (null != depthLocation2 && depthLocation2 > 0)
										|| (null != depthLocation3 && depthLocation3 > 0)) {
									response.setDisplayMsg(MyConstants.TREAD_DEPTH_REQUIRED);
									response.setErrorMsg(depthLocation1 + " - " + depthLocation2 + " - "
											+ depthLocation3 + " - " + MyConstants.TREAD_DEPTH_REQUIRED);
									return response;
								}

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

													// Response inspectionResp =
													// null;
													// Add new inspection
													if (saveInspection) {
														TMSTireInspection inspection = new TMSTireInspection();
														inspection.setCreatedBy(loginUser.getUserId());
														inspection.setCreatedByName(loginUser.getUserName());
														inspection.setDepthLocation1(depthLocation1);
														inspection.setDepthLocation2(depthLocation2);
														inspection.setDepthLocation3(depthLocation3);
														inspection.setAvgThreadDepth(MyConstants.calculateAvgDepth(
																depthLocation1, depthLocation2, depthLocation3));
														inspection.setInspectionDate(new Date());
														inspection.setKMSReading(removalKM);
														inspection.setLocation(tire.getTirePosition());
														inspection.setTireId(tire.getTireId());
														inspection.setTireNumber(tire.getTireNumber());
														inspection.setTirePressure(tirePressure.toString());
														inspection.setOrgId(loginUser.getOrgId());
														mySQLService.saveOrUpdateTireInspection(inspection);
													}

													// Deallocate tire and
													// change
													// the
													// status
													tire.setVehId(0l);
													tire.setStatus(status);
													tire.setLastServiceId(0l);
													tire.setTotalTyreKM(tire.getTotalTyreKM()
															+ (removalKM - existingService.getKmsAtTyreFitted()));
													tire.setUpdatedDateTime(new Date());

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

	@RequestMapping(value = "/deallocateSensor", method = RequestMethod.GET)
	public @ResponseBody Response deAllocateSensor(HttpServletRequest request,
			@RequestParam(value = "tyreNumber", required = false) String tireNumber,
			@RequestParam(value = "tireId", required = false) Long tireId,
			@RequestParam(value = "sensorId", required = false) Long sensorId,
			@RequestParam(value = "status", required = false) String status) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == tireNumber || tireNumber.equalsIgnoreCase("undefined")
							|| tireNumber.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.TIRENUMBER_REQUIRED);
						response.setErrorMsg(tireNumber + " " + MyConstants.TIRENUMBER_REQUIRED);
					} else if (null == tireId || tireId == 0) {
						response.setDisplayMsg(MyConstants.TIREID_REQUIRED);
						response.setErrorMsg(tireId + " " + MyConstants.TIREID_REQUIRED);
					} else if (null == sensorId || sensorId == 0) {
						response.setDisplayMsg(MyConstants.SENSORID_REQUIRED);
						response.setErrorMsg(sensorId + " " + MyConstants.SENSORID_REQUIRED);
					} else if (null == status || status.equalsIgnoreCase("null") || status.equalsIgnoreCase("undefined")
							|| status.trim().length() < 1) {
						response.setDisplayMsg(MyConstants.DEALLOCATING_STATUS_ERROR);
						response.setErrorMsg(status + " " + MyConstants.DEALLOCATING_STATUS_ERROR);
					} else {
						status = getStatus(status);
						if (status != null) {
							TMSTire tmsTire = mySQLService.getTireByTireNumber(tireNumber);
							// TMSTire tempTire =
							// mySQLService.getTireByTireNumber(tireNumber);
							if (null != tmsTire) {
								if (tmsTire.getTireId() == tireId) {

									TMSSensor sensor = mySQLService.getSensorBySensorId(sensorId);
									if (null != sensor) {
										if (sensor.getTireId() == tireId || tmsTire.getSensorId() == sensorId) {
											tmsTire.setSensorId(0l);
											sensor.setStatus(status);
											sensor.setTireId(0);
											sensor.setUpdatedDateTime(new Date());
											response = mySQLService.deallocateSensorFromTire(tmsTire, sensor);
											return response;
										} else {
											response.setDisplayMsg(MyConstants.SENSOR_ASSIGNED_ALREADY);
											response.setErrorMsg(sensor.getSensorUID() + " - "
													+ MyConstants.SENSOR_ASSIGNED_ALREADY + " - " + sensor.getTireId());
											return response;
										}
									} else {
										response.setDisplayMsg(MyConstants.SENSOR_NOT_EXISTS);
										response.setErrorMsg(sensorId + " - " + MyConstants.SENSOR_NOT_EXISTS);
										return response;
									}
								} else {
									// Tire number & Tire id mismatched
									response.setDisplayMsg(MyConstants.TIRE_NUMBER_ID_MISMATCH);
									response.setErrorMsg(
											tireNumber + " - " + tireId + " - " + MyConstants.TIRE_NUMBER_ID_MISMATCH);
								}
							} else {
								// Tire not exists
								response.setDisplayMsg(MyConstants.TIRE_NOT_EXISTS);
								response.setErrorMsg(tireNumber + " " + MyConstants.TIRE_NOT_EXISTS);
							}
						} else {
							// Incorrect status
							response.setDisplayMsg(MyConstants.DEALLOCATING_STATUS_ERROR);
							response.setErrorMsg(status + " " + MyConstants.DEALLOCATING_STATUS_ERROR);
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
			@RequestParam(value = "startIndex", required = false) Integer startIndex,
			@RequestParam(value = "searchWord", required = false) String searchWord) {
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
					if (loginUser.getTMSUserLevel() == 5) {
						if (null != searchWord && (!searchWord.equalsIgnoreCase("undefined"))
								&& searchWord.trim().length() > 0) {
							List<TMSTireInspection> list = mySQLService.getTMSTireInspections(loginUser.getOrgId(), 0,
									searchWord, limit, startIndex);
							response.setStatus(true);
							response.setResult(list);

							response.setCount(
									mySQLService.getTMSTireInspectionsCount(loginUser.getOrgId(), 0, searchWord));
						} else {
							// All Tire Inspections based on org
							List<TMSTireInspection> list = mySQLService.getTMSTireInspections(loginUser.getOrgId(), 0,
									null, limit, startIndex);
							response.setStatus(true);
							response.setResult(list);

							response.setCount(mySQLService.getTMSTireInspectionsCount(loginUser.getOrgId(), 0, null));
						}
					} else if (loginUser.getTMSUserLevel() < 5) {
						// All Tire Inspections For sysAdmin
						if (null != searchWord && (!searchWord.equalsIgnoreCase("undefined"))
								&& searchWord.trim().length() > 0) {
							List<TMSTireInspection> list = mySQLService.getTMSTireInspections(0, 0, searchWord, limit,
									startIndex);
							response.setStatus(true);
							response.setResult(list);

							response.setCount(mySQLService.getTMSTireInspectionsCount(0, 0, searchWord));
						} else {
							List<TMSTireInspection> list = mySQLService.getTMSTireInspections(0, 0, null, limit,
									startIndex);
							response.setStatus(true);
							response.setResult(list);

							response.setCount(mySQLService.getTMSTireInspectionsCount(0, 0, null));
						}
					} else {
						// Get the records only created by the login user
						if (null != searchWord && (!searchWord.equalsIgnoreCase("undefined"))
								&& searchWord.trim().length() > 0) {
							List<TMSTireInspection> list = mySQLService.getTMSTireInspections(0, loginUser.getUserId(),
									searchWord, limit, startIndex);
							response.setStatus(true);
							response.setResult(list);

							response.setCount(
									mySQLService.getTMSTireInspectionsCount(0, loginUser.getUserId(), searchWord));
						} else {
							List<TMSTireInspection> list = mySQLService.getTMSTireInspections(0, loginUser.getUserId(),
									null, limit, startIndex);
							response.setStatus(true);
							response.setResult(list);
							response.setCount(mySQLService.getTMSTireInspectionsCount(0, loginUser.getUserId(), null));
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

	// Get tire service history detials

	@RequestMapping(value = "/Tire/getServices", method = RequestMethod.GET)
	public @ResponseBody Response getServices(HttpServletRequest request,
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "startIndex", required = false) Integer startIndex,
			@RequestParam(value = "searchWord", required = false) String searchWord) {
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
					if (loginUser.getTMSUserLevel() == 5) {
						// Get the records belongs to login user org id
						if (null != searchWord && (!searchWord.equalsIgnoreCase("undefined"))
								&& searchWord.trim().length() > 0) {
							List<TMSTireService> list = mySQLService.getTMSTireServices(loginUser.getOrgId(), 0,
									searchWord, limit, startIndex);
							response.setStatus(true);
							response.setResult(list);

							// Get count
							response.setCount(
									mySQLService.getTMSTireServicesCount(loginUser.getOrgId(), 0, searchWord));
						} else {
							List<TMSTireService> list = mySQLService.getTMSTireServices(loginUser.getOrgId(), 0, null,
									limit, startIndex);
							response.setStatus(true);
							response.setResult(list);

							// Get count
							response.setCount(mySQLService.getTMSTireServicesCount(loginUser.getOrgId(), 0, null));
						}
					} else if (loginUser.getTMSUserLevel() > 5) {
						// Get the records only created by the login user
						if (null != searchWord && (!searchWord.equalsIgnoreCase("undefined"))
								&& searchWord.trim().length() > 0) {
							List<TMSTireService> list = mySQLService.getTMSTireServices(0, loginUser.getUserId(),
									searchWord, limit, startIndex);
							response.setStatus(true);
							response.setResult(list);

							// Get count
							response.setCount(
									mySQLService.getTMSTireServicesCount(0, loginUser.getUserId(), searchWord));
						} else {
							List<TMSTireService> list = mySQLService.getTMSTireServices(0, loginUser.getUserId(), null,
									limit, startIndex);
							response.setStatus(true);
							response.setResult(list);

							// Get count
							response.setCount(mySQLService.getTMSTireServicesCount(0, loginUser.getUserId(), null));
						}
					} else {
						// All Tire Inspections
						if (null != searchWord && (!searchWord.equalsIgnoreCase("undefined"))
								&& searchWord.trim().length() > 0) {
							List<TMSTireService> list = mySQLService.getTMSTireServices(0, 0, searchWord, limit,
									startIndex);
							response.setStatus(true);
							response.setResult(list);

							// Get count
							response.setCount(mySQLService.getTMSTireServicesCount(0, 0, searchWord));
						} else {

							List<TMSTireService> list = mySQLService.getTMSTireServices(0, 0, null, limit, startIndex);
							response.setStatus(true);
							response.setResult(list);

							// Get count
							response.setCount(mySQLService.getTMSTireServicesCount(0, 0, null));
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
		}
		return response;
	}

	@RequestMapping(value = "/Tire/getServiceDetailsById", method = RequestMethod.GET)
	public @ResponseBody Response getServiceById(HttpServletRequest request,
			@RequestParam(value = "serviceId", required = false) Integer serviceId) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == serviceId || serviceId == 0) {
						response.setDisplayMsg(MyConstants.INVALID_SERVICE_ID);
						response.setErrorMsg(serviceId + " - " + MyConstants.INVALID_SERVICE_ID);
					} else {
						TMSTireService service = mySQLService.getTMSTireServiceById(serviceId);
						List<TMSTireService> list = new ArrayList<>(1);
						list.add(service);
						response.setResult(list);
						response.setStatus(true);
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
			@RequestParam(value = "fullDetails", required = false) Boolean fullDetails,
			@RequestParam(value = "searchString", required = false) String searchString, HttpServletRequest request,
			HttpServletResponse resp) {
		Response response = new Response();
		response.setStatus(false);

		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					CommonClass.fixHeaders(request, resp);
					if (null == limit) {
						limit = 50;
					}
					if (null == startIndex) {
						startIndex = 0;
					}
					if (null != searchString && (!searchString.equalsIgnoreCase("undefined"))
							&& (!searchString.equalsIgnoreCase("null")) && searchString.trim().length() > 0) {
						// Find the search
						List<TMSTireView> tires = new ArrayList<>();
						if (loginUser.getTMSUserLevel() >= 5) {
							tires = mySQLService.searchTires(searchString, loginUser.getOrgId(), limit, startIndex);
						} else {
							tires = mySQLService.searchTires(searchString, 0, limit, startIndex);
						}
						response.setResult(tires);
						response.setStatus(true);
					} else {
						if (null == status || status.equalsIgnoreCase("null") || status.equalsIgnoreCase("undefined")
								|| status.trim().length() < 1) {

							status = null;
						} else {
							status = getStatus(status);
							if (status == null) {
								response.setDisplayMsg(MyConstants.STATUS_ERROR);
								response.setErrorMsg(status + " - " + MyConstants.STATUS_ERROR);
								return response;
							}
						}
						if (null == fullDetails || fullDetails == false) {
							// Get Basic details only
							if (loginUser.getTMSUserLevel() >= 5) {
								// Org Admin or below
								List<TMSTireShortDetails> tireDeails = mySQLService
										.getShortTireDetails(loginUser.getOrgId(), status);
								response.setResult(tireDeails);
								response.setStatus(true);
							} else {
								// SysAdmin
								List<TMSTireShortDetails> tireDeails = mySQLService.getShortTireDetails(0, status);
								response.setResult(tireDeails);
								response.setStatus(true);
							}
						} else {
							// Get full details
							List<TMSTireView> tireDetails = null;
							if (loginUser.getTMSUserLevel() >= 5) {
								// Org Admin or below
								tireDetails = mySQLService.getTireViewDetials(loginUser.getOrgId(), status, limit,
										startIndex);
							} else {
								// SysAdmin
								tireDetails = mySQLService.getTireViewDetials(0, status, limit, startIndex);
							}
							response.setResult(tireDetails);
							response.setStatus(true);
						}
					}
					// Find the tire count
					if (loginUser.getTMSUserLevel() >= 5) {
						// With org id
						response.setCount(mySQLService.getTMSTiresCount(loginUser.getOrgId(), status, searchString));
					} else {
						// Without org id
						response.setCount(mySQLService.getTMSTiresCount(0, status, searchString));
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
	// @RequestMapping(value = "/getTyres", method = RequestMethod.GET)
	// public @ResponseBody Response getTires(@RequestParam(value = "status",
	// required = false) String status
	// ) {
	// Response response = new Response();
	// List<TMSTireView> tires = new ArrayList<>();
	// response.setStatus(true);
	// try {
	// if (null == status || status.trim().length() == 0)
	// tires = mySQLService.getTireViewDetials(0, null);
	// else {
	// // Check status exists in the predefined list are not
	// String actualStatus = getStatus(status);
	// if (null != actualStatus) {
	// tires = mySQLService.getTireViewDetials(0, actualStatus);
	// } else {
	// response.setStatus(false);
	// response.setDisplayMsg(MyConstants.STATUS_ERROR);
	// response.setErrorMsg(status + " - " + MyConstants.STATUS_ERROR);
	// }
	// }
	// response.setResult(tires);
	// } catch (Exception e) {
	// e.printStackTrace();
	// response.setStatus(false);
	// response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
	// response.setErrorMsg(e.getMessage());
	// }
	// return response;
	// }

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
		List<TMSDepot> tmsDepotList = new ArrayList<>();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				CommonClass.fixHeaders(request, resp);
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (loginUser.getTMSUserLevel() >= 5) {
						// Org Admin or below cader
						tmsDepotList = mySQLService.getAllTMSDepots(loginUser.getOrgId());
						response.setResult(tmsDepotList);
					} else {
						// Sys Admin
						tmsDepotList = mySQLService.getAllTMSDepots(0);
						response.setResult(tmsDepotList);
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
