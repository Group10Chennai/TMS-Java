package com.tms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tms.beans.MyConstants;
import com.tms.beans.Response;
import com.tms.dto.TMSDtoI;
import com.tms.model.TMSBController;
import com.tms.model.TMSDepot;
import com.tms.model.Organizations;
import com.tms.model.TMSRFID;
import com.tms.model.TMSSensor;
import com.tms.model.TMSTire;
import com.tms.model.TMSTireInspection;
import com.tms.model.TMSTireService;
import com.tms.model.TMSUserVehicleMapping;
import com.tms.model.TMSVehicles;
import com.tms.model.UserMaster;
import com.tms.service.MySQLService;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@Controller
@RequestMapping("/api/tms")
public class TMSController {

	@Autowired
	private MySQLService mySQLService;
	
	@Autowired
	private TMSDtoI tMSDtoI;

	static Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

	static {
		root.setLevel(Level.INFO);
	}

	@RequestMapping(value = "/Vehicle/Add", method = RequestMethod.GET)
	public @ResponseBody Response addVehicle(@RequestParam(value = "vehName", required = false) String vehName,
			@RequestParam(value = "depotId", required = false) Integer depotId,
			@RequestParam(value = "orgId", required = false) Integer orgId,
			@RequestParam(value = "RFID", required = false) Long RFID,
			@RequestParam(value = "ControllerId", required = false) Long controllerId, HttpServletRequest request) {
		Response response = new Response();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					TMSBController bCtrl = null;
					TMSRFID tmsRFID = null;
					if (null == vehName || vehName.trim().length() == 0) {
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.VEHICLE_NAME_REQUIRED);
						response.setErrorMsg(MyConstants.VEHICLE_NAME_REQUIRED);
						return response;
					}
					// Check whether user has permission to add vehicle or not
					// Only SysAdmins and Organization admin can do

					// Check whether vehicle name exists or not
					TMSVehicles existingVeh = mySQLService.getVehByName(vehName);
					if (existingVeh == null) {
						// Vehicle not exists in DB

						TMSVehicles tmsVehicles = new TMSVehicles();
						tmsVehicles.setVehName(vehName);
						if (null != depotId) {
							tmsVehicles.setDepotId(depotId);
						}
						if (null == orgId || orgId == 0) {
							// Created by Organization member
							tmsVehicles.setOrgId(loginUser.getOrgId());
						} else
							tmsVehicles.setOrgId(orgId);
						
						if (null != RFID && RFID != 0) {
							// Check RFID exists or not
							tmsRFID = mySQLService.getRFIDByRFID(RFID);
							if (null != tmsRFID)
								tmsVehicles.setRFID(RFID);
							else {
								response.setStatus(false);
								response.setDisplayMsg(MyConstants.RFID_NOT_EXISTS);
								response.setErrorMsg(RFID + " " + MyConstants.RFID_NOT_EXISTS);

								return response;
							}
						}
						if (null != controllerId && controllerId != 0) {
							// Check Bluetooth Controller exists or not
							bCtrl = mySQLService.getBCtrlByBCtrlId(controllerId);
							if (null != bCtrl)
								tmsVehicles.setControllerId(controllerId);
							else {
								response.setStatus(false);
								response.setDisplayMsg(MyConstants.BControllerId_NOT_EXISTS);
								response.setErrorMsg(controllerId + " " + MyConstants.BControllerId_NOT_EXISTS);
								return response;
							}
						}

						tmsVehicles.setCreatedBy(loginUser.getUserId());
						tmsVehicles.setCreatedDateTime(new Date());
						tmsVehicles.setUpdatedDateTime(new Date());
						// Save the Vehicle details
						response = mySQLService.saveOrUpdate(tmsVehicles);
						if (response.isStatus()) {
							// Assign this vehicle to this user
							TMSUserVehicleMapping userVehMapping = new TMSUserVehicleMapping();
							userVehMapping.setCreatedDateTime(new Date());
							userVehMapping.setAssignedBy(loginUser.getUserId());
							userVehMapping.setStatus(1);
							userVehMapping.setUserId(loginUser.getUserId());
							userVehMapping.setVehId(tmsVehicles.getVehId());

							mySQLService.saveOrUpdateUserVehMapping(userVehMapping);

							if (null != tmsRFID) {
								// Find and change the status of existing RFID
								// controller
								TMSRFID existingRFID = mySQLService.getRFIDByVehId(tmsVehicles.getVehId());
								if (null != existingRFID) {
									existingRFID.setStatus(MyConstants.STATUS_INSTOCK);
									existingRFID.setVehId(0);
									existingRFID.setUpdatedDateTime(new Date());
									mySQLService.saveOrUpdateRFID(existingRFID);
								}
								// Update the vehicle id & status in the new
								// RFID record
								tmsRFID.setVehId(tmsVehicles.getVehId());
								tmsRFID.setStatus(MyConstants.STATUS_INSTALLED);
								tmsRFID.setUpdatedDateTime(new Date());
								mySQLService.saveOrUpdateRFID(tmsRFID);
							}
							if (null != bCtrl) {

								// Find and change the status of existing
								// Bluetooth controller

								TMSBController existingBCtrl = mySQLService.getBCtrlByVehId(tmsVehicles.getVehId());
								if (null != existingBCtrl) {
									existingBCtrl.setStatus(MyConstants.STATUS_INSTOCK);
									existingBCtrl.setVehId(0);
									existingBCtrl.setUpdatedDateTime(new Date());
									mySQLService.saveOrUpdateBController(existingBCtrl);
								}
								// Update the vehicle id & status in new
								// Bluetooth controller record
								bCtrl.setVehId(tmsVehicles.getVehId());
								bCtrl.setStatus(MyConstants.STATUS_INSTALLED);
								bCtrl.setUpdatedDateTime(new Date());
								mySQLService.saveOrUpdateBController(bCtrl);
							}
							// Vehicle details are inserted
							response.setStatus(true);
							response.setDisplayMsg(MyConstants.VEHICLE_ADDED_SUCCESSFULL);
							response.setErrorMsg(MyConstants.VEHICLE_ADDED_SUCCESSFULL);
							// Add this vehicle into session
							
							List<Long> vehIds = new ArrayList<>(1);
							vehIds.add(userVehMapping.getVehId());
							
							tMSDtoI.getAndSetUserVehToSession(vehIds, mySQLService, session);
							
						} else {
							// Error in vehicle insertion
							response.setDisplayMsg(MyConstants.VEHICLE_ADDING_FAILED);
						}
					} else if (existingVeh != null) {
						response.setStatus(false);
						response.setDisplayMsg(vehName + " " + MyConstants.VEHICLE_EXISTS);
						response.setErrorMsg(vehName + " " + MyConstants.VEHICLE_EXISTS);
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

	@RequestMapping(value = "/Vehicle/Update", method = RequestMethod.GET)
	public @ResponseBody Response updateVehicle(@RequestParam(value = "vehName", required = false) String vehName,
			@RequestParam(value = "depotId", required = false) Integer depotId,
			@RequestParam(value = "vehId", required = false) Integer vehId,
			@RequestParam(value = "orgId", required = false) Integer orgId,
			@RequestParam(value = "RFID", required = false) Long RFID,
			@RequestParam(value = "ControllerId", required = false) Long controllerId, HttpServletRequest request) {
		Response response = new Response();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {

					if (null == vehId || vehId == 0) {
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.VEHICLE_UPDATING_FAILED);
						response.setErrorMsg(MyConstants.VEHICLE_ID_REQUIRED);
						return response;
					} else if (null == vehName || vehName.trim().length() == 0) {
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.VEHICLE_NAME_REQUIRED);
						response.setErrorMsg(MyConstants.VEHICLE_NAME_REQUIRED);
						return response;
					}
					if (null == RFID) {
						RFID = 0l;
					}
					if (null == controllerId) {
						controllerId = 0l;
					}

					TMSVehicles existingVeh = mySQLService.getVehById(vehId);
					TMSBController bCtrl = null;
					TMSRFID tmsRFID = null;
					if (null != existingVeh && existingVeh.getVehId() == vehId) {
						// Update the Vehicle details
						existingVeh.setVehName(vehName);
						if (null != orgId && orgId > 0) {
							existingVeh.setOrgId(orgId);
						}
						if (null != depotId && depotId > 0) {
							existingVeh.setDepotId(depotId);
						}
						if (null != RFID && RFID != 0) {
							// Check RFID exists or not
							tmsRFID = mySQLService.getRFIDByRFID(RFID);
							if (null != tmsRFID)
								if (tmsRFID.getVehId() == 0 || tmsRFID.getVehId() == existingVeh.getVehId())
									existingVeh.setRFID(RFID);
								else {
									// RFID already assigned to other vehicle
								}
							else {
								response.setStatus(false);
								response.setDisplayMsg(MyConstants.RFID_NOT_EXISTS);
								response.setErrorMsg(RFID + " " + MyConstants.RFID_NOT_EXISTS);

								return response;
							}
						}
						if (null != controllerId && controllerId != 0) {
							// Check Bluetooth Controller exists or not
							bCtrl = mySQLService.getBCtrlByBCtrlId(controllerId);
							if (null != bCtrl) {
								if (bCtrl.getVehId() == 0 || bCtrl.getVehId() == existingVeh.getVehId())
									existingVeh.setControllerId(controllerId);
								else {
									// Bluetooth controller is assigned to other
									// vehicle
								}
							} else {
								response.setStatus(false);
								response.setDisplayMsg(MyConstants.BControllerId_NOT_EXISTS);
								response.setErrorMsg(controllerId + " " + MyConstants.BControllerId_NOT_EXISTS);
								return response;
							}
						}

						// Save the Vehicle details
						response = mySQLService.saveOrUpdate(existingVeh);
						if (response.isStatus()) {
							// Vehicle details are inserted
							if (null != tmsRFID) {
								// Find and change the status of existing RFID
								// controller
								TMSRFID existingRFID = mySQLService.getRFIDByVehId(existingVeh.getVehId());
								if (null != existingRFID) {
									existingRFID.setStatus(MyConstants.STATUS_INSTOCK);
									existingRFID.setVehId(0);
									existingRFID.setUpdatedDateTime(new Date());
									mySQLService.saveOrUpdateRFID(existingRFID);
								}
								// Update the vehicle id & status in the new
								// RFID record
								tmsRFID.setVehId(existingVeh.getVehId());
								tmsRFID.setStatus(MyConstants.STATUS_INSTALLED);
								tmsRFID.setUpdatedDateTime(new Date());
								mySQLService.saveOrUpdateRFID(tmsRFID);
							}
							if (null != bCtrl) {
								// Find and change the status of existing
								// Bluetooth controller
								TMSBController existingBCtrl = mySQLService.getBCtrlByVehId(existingVeh.getVehId());
								if (null != existingBCtrl) {
									existingBCtrl.setStatus(MyConstants.STATUS_INSTOCK);
									existingBCtrl.setVehId(0);
									existingBCtrl.setUpdatedDateTime(new Date());
									mySQLService.saveOrUpdateBController(existingBCtrl);
								}
								// Update the vehicle id & status in new
								// Bluetooth controller record
								bCtrl.setVehId(existingVeh.getVehId());
								bCtrl.setStatus(MyConstants.STATUS_INSTALLED);
								bCtrl.setUpdatedDateTime(new Date());
								mySQLService.saveOrUpdateBController(bCtrl);
							}
							response.setStatus(true);
							response.setDisplayMsg(MyConstants.VEHICLE_UPDATED_SUCCESSFULL);
							response.setErrorMsg(MyConstants.VEHICLE_UPDATED_SUCCESSFULL);
						} else {
							// Error in vehicle insertion
							response.setDisplayMsg(MyConstants.VEHICLE_UPDATING_FAILED);
						}

					} else {
						// Vehicle not exists in DB
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.VEHICLE_UPDATING_FAILED);
						response.setErrorMsg(vehId + " " + MyConstants.VEHICLEID_NOT_EXISTS);
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

	@RequestMapping(value = "/Vehicle/Delete", method = RequestMethod.GET)
	public @ResponseBody Response deleteVehicle(@RequestParam(value = "vehName", required = false) String vehName,
			@RequestParam(value = "vehId", required = false) Integer vehId, HttpServletRequest request) {
		Response response = new Response();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {

					if (null == vehId || vehId == 0) {
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.VEHICLE_UPDATING_FAILED);
						response.setErrorMsg(MyConstants.VEHICLE_ID_REQUIRED);
						return response;
					} else if (null == vehName || vehName.trim().length() == 0) {
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.VEHICLE_NAME_REQUIRED);
						response.setErrorMsg(MyConstants.VEHICLE_NAME_REQUIRED);
						return response;
					} else {
						// Check login user level and delete vehicle

						// Find the vehicle
						TMSVehicles existingVeh = mySQLService.getVehByName(vehName);
						if (null == existingVeh) {
							// There is no vehicle with the vehicle name
							existingVeh = mySQLService.getVehById(vehId);
							if (null != existingVeh) {
								response = mySQLService.deleteVehicle(existingVeh);
								if (response.isStatus()) {
									response.setDisplayMsg(MyConstants.VEHICLE_DELETED_SUCCESSFULL);
								}
							} else {
								// Vehicle not exists
								response.setStatus(false);
								response.setDisplayMsg(MyConstants.VEHICLE_DELETING_FAILED);
								response.setErrorMsg(vehName + " (" + vehId + ") " + MyConstants.VEHICLEID_NOT_EXISTS);
							}
						} else {
							response.setStatus(false);
							response.setDisplayMsg(vehName + " " + MyConstants.VEHICLE_EXISTS);
							response.setErrorMsg(vehName + " " + MyConstants.VEHICLE_EXISTS);
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

	@RequestMapping(value = "/Vehicle/deallocateDevice", method = RequestMethod.GET)
	public @ResponseBody Response deallocateDevice(@RequestParam(value = "vehName", required = false) String vehName,
			@RequestParam(value = "vehId", required = false) Integer vehId,
			@RequestParam(value = "RFID", required = false) Long RFID,
			@RequestParam(value = "ControllerId", required = false) Long controllerId,
			@RequestParam(value = "status", required = false) String status, HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == vehName || vehName.equals("undefined") || vehName.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.VEHICLE_NAME_REQUIRED);
						response.setErrorMsg(vehName + " " + MyConstants.VEHICLE_NAME_REQUIRED);
					} else if (null == vehId || vehId == 0) {

					} else if ((null == controllerId || controllerId == 0) && (RFID == null || RFID == 0)) {

					} else if (null == status || status.equals("undefined") || status.trim().length() == 0) {

					} else {
						status = getStatus(status);
						if (null == status) {
							response.setStatus(false);
							response.setDisplayMsg(MyConstants.STATUS_ERROR);
							response.setErrorMsg(status + " - " + MyConstants.STATUS_ERROR);
						} else {
							TMSVehicles veh = mySQLService.getVehById(vehId);
							if (null != veh) {
								if (null != controllerId && controllerId != 0
										&& controllerId == veh.getControllerId()) {
									// Deallocate BT
									TMSBController btCtrl = mySQLService.getBCtrlByBCtrlId(controllerId);
									if (null != btCtrl) {
										veh.setControllerId(0);
										response = mySQLService.saveOrUpdate(veh);
										if (response.isStatus()) {
											btCtrl.setStatus(status);
											btCtrl.setVehId(0);
											mySQLService.saveOrUpdateBController(btCtrl);
											if (response.isStatus()) {
												response.setDisplayMsg(MyConstants.VEHICLE_UPDATED_SUCCESSFULL);
											} else {
												response.setDisplayMsg(MyConstants.VEHICLE_UPDATING_FAILED);
											}
										}
									} else {
										response.setDisplayMsg(controllerId + MyConstants.NOT_EXISTS);
										response.setErrorMsg(controllerId + " - " + MyConstants.NOT_EXISTS);
									}
								}
								if (null != RFID && RFID != 0 && RFID == veh.getRFID()) {
									// Deallocate RFID
									TMSRFID rfid = mySQLService.getRFIDByRFID(RFID);
									if (null != rfid) {
										veh.setRFID(0);
										response = mySQLService.saveOrUpdate(veh);
										if (response.isStatus()) {
											rfid.setVehId(0);
											rfid.setStatus(status);
											response = mySQLService.saveOrUpdateRFID(rfid);
											if (response.isStatus()) {
												response.setStatus(true);
												response.setDisplayMsg(MyConstants.VEHICLE_UPDATED_SUCCESSFULL);
											} else {
												response.setDisplayMsg(MyConstants.VEHICLE_UPDATING_FAILED);
											}
										}
									} else {
										response.setDisplayMsg(rfid + MyConstants.NOT_EXISTS);
										response.setErrorMsg(rfid + " - " + MyConstants.NOT_EXISTS);
									}
								}
							} else {
								response.setDisplayMsg(vehName + MyConstants.NOT_EXISTS);
								response.setErrorMsg(vehName + " - " + MyConstants.NOT_EXISTS);
							}
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

	@RequestMapping(value = "/RFID/Add", method = RequestMethod.GET)
	public @ResponseBody Response addRFID(@RequestParam(value = "RFIDUID", required = false) String RFIDUID,
			HttpServletRequest request) {
		Response response = new Response();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == RFIDUID || RFIDUID.trim().length() == 0) {
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.RFIDUID_REQUIRED);
						response.setErrorMsg(MyConstants.RFIDUID_REQUIRED);
					} else {
						TMSRFID rfidDetails = mySQLService.getRFIDByRFIDUID(RFIDUID);
						if (null == rfidDetails) {
							rfidDetails = new TMSRFID();
							rfidDetails.setRFIDUID(RFIDUID);
							rfidDetails.setCreatedDateTime(new Date());
							rfidDetails.setCreatedBy(loginUser.getUserId());
							rfidDetails.setStatus(MyConstants.STATUS_INSTOCK);
							rfidDetails.setUpdatedDateTime(new Date());
							response = mySQLService.saveOrUpdateRFID(rfidDetails);
							return response;
						} else {
							response.setStatus(false);
							response.setDisplayMsg(RFIDUID + " " + MyConstants.RFIDUID__EXISTS);
							response.setErrorMsg(RFIDUID + " " + MyConstants.RFIDUID__EXISTS);
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

	@RequestMapping(value = "/RFID/Update", method = RequestMethod.GET)
	public @ResponseBody Response updateRFID(@RequestParam(value = "RFID", required = false) Long RFID,
			@RequestParam(value = "RFIDUID", required = false) String RFIDUID, HttpServletRequest request) {
		Response response = new Response();
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == RFIDUID || RFIDUID.trim().length() == 0) {
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.RFIDUID_REQUIRED);
						response.setErrorMsg(MyConstants.RFIDUID_REQUIRED);
					} else if (null == RFID || RFID == 0) {
						response.setStatus(false);
						response.setDisplayMsg(MyConstants.RFID_REQUIRED);
						response.setErrorMsg(RFID + " " + MyConstants.RFID_REQUIRED);
					} else {

						TMSRFID rfidDetails = mySQLService.getRFIDByRFIDUID(RFIDUID);
						if (null == rfidDetails) {

							rfidDetails = mySQLService.getRFIDByRFID(RFID);
							if (null != rfidDetails) {

								rfidDetails.setRFIDUID(RFIDUID);
								rfidDetails.setUpdatedDateTime(new Date());
								response = mySQLService.saveOrUpdateRFID(rfidDetails);
								if (response.isStatus()) {
									response.setDisplayMsg(MyConstants.RFID_UPDATED_SUCCESSFULL);
								} else {
									response.setDisplayMsg(MyConstants.RFID_UPDATING_FAILED);
								}
								return response;
							} else {
								response.setStatus(false);
								response.setDisplayMsg(RFIDUID + " " + MyConstants.RFID_UPDATING_FAILED);
								response.setErrorMsg(RFID + " " + MyConstants.RFID_NOT_EXISTS);
							}
						} else {
							response.setStatus(false);
							response.setDisplayMsg(RFIDUID + " " + MyConstants.RFIDUID__EXISTS);
							response.setErrorMsg(RFIDUID + " " + MyConstants.RFIDUID__EXISTS);
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

	@RequestMapping(value = "/RFID/Delete", method = RequestMethod.GET)
	public @ResponseBody Response deleteRFID(@RequestParam(value = "RFID", required = false) Long RFID,
			HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == RFID || RFID == 0) {
						response.setDisplayMsg(MyConstants.RFID_REQUIRED);
						response.setErrorMsg(RFID + " " + MyConstants.RFID_REQUIRED);
					} else {
						TMSRFID rfidDetails = mySQLService.getRFIDByRFID(RFID);
						if (null != rfidDetails) {
							response = mySQLService.deleteRFIDByRFID(rfidDetails);
						} else {
							response.setDisplayMsg(MyConstants.RFID_NOT_EXISTS);
							response.setErrorMsg(RFID + " " + MyConstants.RFID_NOT_EXISTS);
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
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}

		return response;
	}

	@RequestMapping(value = "/BController/Add", method = RequestMethod.GET)
	public @ResponseBody Response addBControllerID(
			@RequestParam(value = "BControllerUID", required = false) String BControllerUID,
			HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == BControllerUID || BControllerUID.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.BControllerUID_REQUIRED);
						response.setErrorMsg(MyConstants.BControllerUID_REQUIRED);
					} else {
						TMSBController tmsBController = mySQLService.getBControllerByBCtrlUID(BControllerUID);
						if (null == tmsBController) {
							tmsBController = new TMSBController();
							tmsBController.setControllerUID(BControllerUID);
							tmsBController.setCreatedBy(loginUser.getUserId());
							tmsBController.setCreatedDateTime(new Date());
							tmsBController.setStatus(MyConstants.STATUS_INSTOCK);
							tmsBController.setUpdatedDateTime(new Date());
							response = mySQLService.saveOrUpdateBController(tmsBController);
						} else {
							response.setDisplayMsg(BControllerUID + " " + MyConstants.BControllerUID__EXISTS);
							response.setErrorMsg(BControllerUID + " " + MyConstants.BControllerUID__EXISTS);
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

	@RequestMapping(value = "/BController/Update", method = RequestMethod.GET)
	public @ResponseBody Response updateBController(
			@RequestParam(value = "BControllerId", required = false) Long BControllerId,
			@RequestParam(value = "BControllerUID", required = false) String BControllerUID,
			HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == BControllerUID || BControllerUID.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.BControllerUID_REQUIRED);
						response.setErrorMsg(BControllerUID + " " + MyConstants.BControllerUID_REQUIRED);
					} else if (null == BControllerId || BControllerId == 0) {
						response.setDisplayMsg(MyConstants.BControllerID_REQUIRED);
						response.setErrorMsg(BControllerId + " " + MyConstants.BControllerID_REQUIRED);
					} else {

						TMSBController tmsBController = mySQLService.getBControllerByBCtrlUID(BControllerUID);
						if (null == tmsBController) {

							tmsBController = mySQLService.getBCtrlByBCtrlId(BControllerId);
							if (null != tmsBController) {

								tmsBController.setControllerUID(BControllerUID);
								tmsBController.setUpdatedDateTime(new Date());
								response = mySQLService.saveOrUpdateBController(tmsBController);
								if (response.isStatus()) {
									response.setDisplayMsg(MyConstants.BController_UPDATED_SUCCESSFULL);
								} else {
									response.setDisplayMsg(MyConstants.BController_UPDATING_FAILED);
								}
								return response;
							} else {

								response.setDisplayMsg(MyConstants.BController_UPDATING_FAILED);
								response.setErrorMsg(BControllerId + " " + MyConstants.BControllerId_NOT_EXISTS);
							}
						} else {
							response.setDisplayMsg(BControllerUID + MyConstants.BControllerUID__EXISTS);
							response.setErrorMsg(BControllerUID + " " + MyConstants.BControllerUID__EXISTS);
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
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/BController/Delete", method = RequestMethod.GET)
	public @ResponseBody Response deleteBController(
			@RequestParam(value = "BControllerId", required = false) Long BControllerId, HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == BControllerId || BControllerId == 0) {
						response.setDisplayMsg(MyConstants.BControllerID_REQUIRED);
						response.setErrorMsg(BControllerId + " " + MyConstants.BControllerID_REQUIRED);
					} else {
						TMSBController tmsBController = mySQLService.getBCtrlByBCtrlId(BControllerId);
						if (null != tmsBController) {
							response = mySQLService.deleteBCtrlByBCtrl(tmsBController);
						} else {
							response.setDisplayMsg(MyConstants.BControllerId_NOT_EXISTS);
							response.setErrorMsg(BControllerId + " " + MyConstants.BControllerId_NOT_EXISTS);
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
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}

		return response;
	}

	@RequestMapping(value = "/Sensor/Add", method = RequestMethod.GET)
	public @ResponseBody Response addSensor(@RequestParam(value = "sensorUID", required = false) String sensorUID,
			@RequestParam(value = "rimNo", required = false) String rimNo, HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == sensorUID || sensorUID.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.SENSORUID_REQUIRED);
						response.setErrorMsg(MyConstants.SENSORUID_REQUIRED);
					} else {
						TMSSensor tmsSensor = mySQLService.getSensorBySensorUID(sensorUID);
						if (null == tmsSensor) {
							tmsSensor = new TMSSensor();
							tmsSensor.setSensorUID(sensorUID);
							try {
								if(null == rimNo || rimNo.equalsIgnoreCase("undefined") || rimNo.trim().length() == 0){
									tmsSensor.setRimNo("0");
								} else {
									tmsSensor.setRimNo(rimNo);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							tmsSensor.setCreatedBy(loginUser.getUserId());
							tmsSensor.setCreatedDateTime(new Date());
							tmsSensor.setStatus(MyConstants.STATUS_INSTOCK);
							tmsSensor.setUpdatedDateTime(new Date());
							response = mySQLService.saveOrUpdateSensor(tmsSensor);
						} else {
							response.setDisplayMsg(sensorUID + " " + MyConstants.SENSORUID__EXISTS);
							response.setErrorMsg(sensorUID + " " + MyConstants.SENSORUID__EXISTS);
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

	@RequestMapping(value = "/Sensor/Update", method = RequestMethod.GET)
	public @ResponseBody Response updateSensor(@RequestParam(value = "sensorId", required = false) Long sensorId,
			@RequestParam(value = "sensorUID", required = false) String sensorUID, 
			@RequestParam(value = "rimNo", required = false) String rimNo, HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == sensorUID || sensorUID.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.SENSORUID_REQUIRED);
						response.setErrorMsg(sensorUID + " " + MyConstants.SENSORUID_REQUIRED);
					} else if (null == sensorId || sensorId == 0) {
						response.setDisplayMsg(MyConstants.SENSORID_REQUIRED);
						response.setErrorMsg(sensorId + " " + MyConstants.SENSORID_REQUIRED);
					} else {

						TMSSensor tmsSensor = mySQLService.getSensorBySensorUID(sensorUID);
						if(null != tmsSensor && tmsSensor.getSensorId() !=  sensorId){
							// Sensor UID already exists
							response.setDisplayMsg(MyConstants.SENSORUID__EXISTS);
							response.setErrorMsg(sensorUID + " " + MyConstants.SENSORUID__EXISTS);
						} else {
							// Sensor UID not exists
							tmsSensor = mySQLService.getSensorBySensorId(sensorId);
							if (null != tmsSensor) {

								tmsSensor.setSensorUID(sensorUID);
								try {
									if(null != rimNo && (! rimNo.equalsIgnoreCase("undefined")) 
											&& rimNo.trim().length() > 0){
										tmsSensor.setRimNo(rimNo);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								tmsSensor.setUpdatedDateTime(new Date());
								response = mySQLService.saveOrUpdateSensor(tmsSensor);
								if (response.isStatus()) {
									response.setDisplayMsg(MyConstants.SENSOR_UPDATED_SUCCESSFULL);
								} else {
									response.setDisplayMsg(MyConstants.SENSOR_UPDATING_FAILED);
								}
								return response;
							} else {
								response.setDisplayMsg(MyConstants.SENSOR_UPDATING_FAILED);
								response.setErrorMsg(sensorId + " " + MyConstants.SENSORID_NOT_EXISTS);
							}
						}
						if (null == tmsSensor || tmsSensor.getSensorId() ==  sensorId) {

							
						} else {
							
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
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/Sensor/Delete", method = RequestMethod.GET)
	public @ResponseBody Response deleteSensor(@RequestParam(value = "sensorId", required = false) Long sensorId,
			HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == sensorId || sensorId == 0) {
						response.setDisplayMsg(MyConstants.SENSORID_REQUIRED);
						response.setErrorMsg(sensorId + " " + MyConstants.SENSORID_REQUIRED);
					} else {
						TMSSensor tmsSensor = mySQLService.getSensorBySensorId(sensorId);
						if (null != tmsSensor) {
							response = mySQLService.deleteSensor(tmsSensor);
						} else {
							response.setDisplayMsg(MyConstants.SENSORID_NOT_EXISTS);
							response.setErrorMsg(sensorId + " " + MyConstants.SENSORID_NOT_EXISTS);
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
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}

		return response;
	}

	@RequestMapping(value = "/Tyre/Add", method = RequestMethod.GET)
	public @ResponseBody Response addTire(@RequestParam(value = "tyreNumber", required = false) String tyreNumber,
			@RequestParam(value = "tyreMakeId", required = false) Long tyreMakeId,
			@RequestParam(value = "tyreType", required = false) String tyreType,
			@RequestParam(value = "depotId", required = false) Long depotId,
			@RequestParam(value = "sensorId", required = false) Long sensorId,
			@RequestParam(value = "orgId", required = false) Long orgId,
			@RequestParam(value = "threadDepth", required = false) String threadDepth, HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == tyreNumber || tyreNumber.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.TIRENUMBER_REQUIRED);
						response.setErrorMsg(MyConstants.TIRENUMBER_REQUIRED);
					} else if (null == tyreMakeId || tyreMakeId == 0) {
						response.setDisplayMsg(MyConstants.TIREMAKE_REQUIRED);
						response.setErrorMsg(tyreMakeId + " " + MyConstants.TIREMAKE_REQUIRED);
					}
					if (null == tyreType || tyreType.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.TIRE_TYPE_REQUIRED);
						response.setErrorMsg(MyConstants.TIRE_TYPE_REQUIRED);
					} else if (null == depotId || depotId == 0) {
						response.setDisplayMsg(MyConstants.DEPOT_REQUIRED);
						response.setErrorMsg(depotId + " " + MyConstants.DEPOT_REQUIRED);
					} else if (null == threadDepth || threadDepth.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.THREAD_DEPTH_REQUIRED);
						response.setErrorMsg(depotId + " " + MyConstants.THREAD_DEPTH_REQUIRED);
					} else if (null == sensorId || sensorId.equals("undefined")) {
						sensorId = 0l;
					} else {
						TMSTire tmsTire = mySQLService.getTireByTireNumber(tyreNumber);
						TMSSensor sensor = null;
						if (null == tmsTire) {
							if (loginUser.getTMSUserLevel() < 5) {
								// Sys Amdin
								// Org id required
								if (null == orgId || orgId == 0) {
									response.setStatus(false);
									response.setDisplayMsg(MyConstants.ORG_ID_REQUIRED);
									response.setErrorMsg(orgId + " - " + MyConstants.ORG_ID_REQUIRED);
									return response;
								}
							} else {
								orgId = loginUser.getOrgId();
							}
							tmsTire = new TMSTire();
							tmsTire.setTireNumber(tyreNumber);
							tmsTire.setTireMakeId(tyreMakeId);
							tmsTire.setTireType(tyreType);
							tmsTire.setDepotId(depotId);
							tmsTire.setThreadDepth(threadDepth);
							tmsTire.setCreatedBy(loginUser.getUserId());
							tmsTire.setCreatedDateTime(new Date());
							tmsTire.setUpdatedDateTime(new Date());
							tmsTire.setOrgId(orgId);
							try {
								if (sensorId == 0) {
									tmsTire.setSensorId(0l);
								} else if (null != sensorId && (!sensorId.equals("undefined"))) {
									sensor = mySQLService.getSensorBySensorId(sensorId);
									if (null != sensor)
										tmsTire.setSensorId(sensorId);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							tmsTire.setStatus(MyConstants.STATUS_INSTOCK);
							tmsTire.setUpdatedDateTime(new Date());
							tmsTire.setVehId(0l);
							tmsTire.setTotalTyreKM(0l);
							tmsTire.setLastServiceId(0l);

							response = mySQLService.saveOrUpdateTire(tmsTire);
							if (response.isStatus()) {
								try {
									if (null != sensor) {
										// Find the existing sensor id and
										// change the status to Stock
										TMSSensor existingSensor = mySQLService.getSensorByTireId(tmsTire.getTireId());

										if (null != existingSensor) {
											existingSensor.setTireId(0);
											existingSensor.setStatus(MyConstants.STATUS_INSTOCK);
											existingSensor.setUpdatedDateTime(new Date());
											mySQLService.saveOrUpdateSensor(existingSensor);
										}
										// Update the sensor details
										sensor.setStatus(MyConstants.STATUS_INSTALLED);
										sensor.setTireId(tmsTire.getTireId());
										sensor.setUpdatedDateTime(new Date());
										mySQLService.saveOrUpdateSensor(sensor);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else {
							response.setDisplayMsg(tyreNumber + " " + MyConstants.TIRENUMBER__EXISTS);
							response.setErrorMsg(tyreNumber + " " + MyConstants.TIRENUMBER__EXISTS);
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

	@RequestMapping(value = "/Tyre/Update", method = RequestMethod.GET)
	public @ResponseBody Response updateTire(@RequestParam(value = "tyreNumber", required = false) String tireNumber,
			@RequestParam(value = "tyreId", required = false) Long tireId,
			@RequestParam(value = "tyreMakeId", required = false) Long tireMakeId,
			@RequestParam(value = "tyreType", required = false) String tyreType,
			@RequestParam(value = "depotId", required = false) Long depotId,
			@RequestParam(value = "sensorId", required = false) Long sensorId,
			@RequestParam(value = "vehId", required = false) Long vehId,
			@RequestParam(value = "tyrePosition", required = false) String tirePosition,
			@RequestParam(value = "threadDepth", required = false) String threadDepth, HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == tireNumber || tireNumber.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.TIRENUMBER_REQUIRED);
						response.setErrorMsg(tireNumber + " " + MyConstants.TIRENUMBER_REQUIRED);
					} else if (null == tireId || tireId == 0) {
						response.setDisplayMsg(MyConstants.TIREID_REQUIRED);
						response.setErrorMsg(tireId + " " + MyConstants.TIREID_REQUIRED);
					} else {

						TMSTire tmsTire = mySQLService.getTireByTireNumber(tireNumber);
						if (null == tmsTire || tmsTire.getTireId() == tireId) {

							tmsTire = mySQLService.getTireByTireId(tireId);
							TMSSensor sensor = null;
							if (null != tmsTire) {
								tmsTire.setTireNumber(tireNumber);
								if (null != tireMakeId && tireMakeId > 0)
									tmsTire.setTireMakeId(tireMakeId);
								if (null != tirePosition && tirePosition.trim().length() > 0)
									tmsTire.setTirePosition(tirePosition);
								if (null != depotId && depotId > 0)
									tmsTire.setDepotId(depotId);
								if (null != threadDepth && threadDepth.trim().length() > 0)
									tmsTire.setThreadDepth(threadDepth);
								if (null != tyreType && tyreType.trim().length() > 0)
									tmsTire.setTireType(tyreType);

								if (null != sensorId && sensorId > 0) {
									sensor = mySQLService.getSensorBySensorId(sensorId);
									if (null != sensor) {
										if (sensor.getTireId() == 0 || sensor.getTireId() == tireId)
											tmsTire.setSensorId(sensorId);
										else {
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
								}

								if (null != vehId && vehId != 0) {
									if ((null != tmsTire.getSensorId() && tmsTire.getSensorId() > 0)
											|| (null != sensorId && sensorId > 0)) {

										if (null != tirePosition && tirePosition.trim().length() > 0) {
											tmsTire.setVehId(vehId);
											tmsTire.setTirePosition(tirePosition);
											tmsTire.setStatus(MyConstants.STATUS_INSTALLED);
										} else {
											response.setDisplayMsg(MyConstants.TIREPOSITION_REQUIRED);
											response.setErrorMsg(
													tirePosition + " - " + MyConstants.TIREPOSITION_REQUIRED);
											return response;
										}
									} else {
										response.setDisplayMsg(MyConstants.ASSIGN_SENSOR);
										response.setErrorMsg(sensorId + " - " + MyConstants.ASSIGN_SENSOR);
										return response;
									}
								}
								// tmsTire.setUpdatedDateTime(new Date());
								response = mySQLService.saveOrUpdateTire(tmsTire);
								if (response.isStatus()) {
									response.setDisplayMsg(MyConstants.TIRE_UPDATED_SUCCESSFULL);

									// If new sensor is updated then update the
									// status in sensor table
									if (null != sensor) {

										// Find the existing sensor id and
										// change the status to Stock
										TMSSensor existingSensor = mySQLService.getSensorByTireId(tmsTire.getTireId());

										if (null != existingSensor) {
											existingSensor.setTireId(0);
											existingSensor.setStatus(MyConstants.STATUS_INSTOCK);
											existingSensor.setUpdatedDateTime(new Date());
											mySQLService.saveOrUpdateSensor(existingSensor);
										}

										sensor.setStatus(MyConstants.STATUS_INSTALLED);
										sensor.setTireId(tmsTire.getTireId());
										sensor.setUpdatedDateTime(new Date());
										mySQLService.saveOrUpdateSensor(sensor);
									}
								} else {
									response.setDisplayMsg(MyConstants.TIRE_UPDATING_FAILED);
								}
								return response;
							} else {
								response.setDisplayMsg(MyConstants.TIRE_UPDATING_FAILED);
								response.setErrorMsg(tireId + " - " + MyConstants.TIREID_NOT_EXISTS);
							}
						} else {
							response.setDisplayMsg(MyConstants.TIRENUMBER__EXISTS);
							response.setErrorMsg(tireNumber + " - " + MyConstants.TIRENUMBER__EXISTS);
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
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/Tire/addInspection", method = RequestMethod.GET)
	public @ResponseBody Response AddTireInspection(@RequestParam(value = "tireId", required = false) Long tireId,
			@RequestParam(value = "Location", required = false) String location,
			@RequestParam(value = "KMSReading", required = false) Long KMSReading,
			@RequestParam(value = "depthLocation1", required = false) Double depthLocation1,
			@RequestParam(value = "depthLocation2", required = false) Double depthLocation2,
			@RequestParam(value = "depthLocation3", required = false) Double depthLocation3,
			@RequestParam(value = "tirePressure", required = false) String tirePressure, HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == tireId || tireId < 1) {
						// Please select tire
						response.setDisplayMsg(MyConstants.SELECT_TIRE);
						response.setErrorMsg(tireId + " - " + MyConstants.TIREID_REQUIRED);
					} else if (null == location || location.trim().length() < 1) {
						// Please enter location
						response.setDisplayMsg(MyConstants.LOCATION_REQUIRED);
						response.setErrorMsg(location + " - " + MyConstants.LOCATION_REQUIRED);
					} else if (null == KMSReading || KMSReading < 1) {
						// Please enter KMS Reading
						response.setDisplayMsg(MyConstants.KMSREADING_REQUIRED);
						response.setErrorMsg(KMSReading + " - " + MyConstants.KMSREADING_REQUIRED);
					} else if (null == depthLocation1) {
						// Please enter Tread depth location 1
						response.setDisplayMsg(MyConstants.TREAD_DEPTH_LOCATION1_REQUIRED);
						response.setErrorMsg(depthLocation1 + "- " + MyConstants.TREAD_DEPTH_LOCATION1_REQUIRED);
					} else if (null == depthLocation2) {
						// Please enter Tread depth location 2
						response.setDisplayMsg(MyConstants.TREAD_DEPTH_LOCATION2_REQUIRED);
						response.setErrorMsg(depthLocation2 + "- " + MyConstants.TREAD_DEPTH_LOCATION2_REQUIRED);
					} else if (null == depthLocation3) {
						// Please enter Tread depth location 3
						response.setDisplayMsg(MyConstants.TREAD_DEPTH_LOCATION3_REQUIRED);
						response.setErrorMsg(depthLocation3 + "- " + MyConstants.TREAD_DEPTH_LOCATION3_REQUIRED);
					} else if (null == tirePressure || tirePressure.equalsIgnoreCase("null")
							|| tirePressure.trim().length() < 1) {
						// Please enter Tire pressure
						response.setDisplayMsg(MyConstants.TIRE_PRESSURE_REQUIRED);
						response.setErrorMsg(tirePressure + "- " + MyConstants.TIRE_PRESSURE_REQUIRED);
					} else {
						TMSTire tire = mySQLService.getTireByTireId(tireId);
						if (null == tire) {
							// Please select a tire
							response.setDisplayMsg(MyConstants.TIRE_NOT_EXISTS);
							response.setErrorMsg(tireId + "- " + MyConstants.TIREID_NOT_EXISTS);
						} else {
							TMSTireInspection inspection = new TMSTireInspection();
							inspection.setCreatedBy(loginUser.getUserId());
							inspection.setDepthLocation1(depthLocation1);
							inspection.setDepthLocation2(depthLocation2);
							inspection.setDepthLocation3(depthLocation3);
							inspection.setAvgThreadDepth(
									MyConstants.calculateAvgDepth(depthLocation1, depthLocation2, depthLocation3));
							inspection.setInspectionDate(new Date());
							inspection.setKMSReading(KMSReading);
							inspection.setLocation(location);
							inspection.setTireId(tireId);
							inspection.setTirePressure(tirePressure);
							inspection.setCreatedByName(loginUser.getUserName());
							inspection.setTireNumber(tire.getTireNumber());
							inspection.setUpdatedDateTime(new Date());
							inspection.setOrgId(loginUser.getOrgId());
							response = mySQLService.saveOrUpdateTireInspection(inspection);
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

	@RequestMapping(value = "/Tire/updateInspection", method = RequestMethod.GET)
	public @ResponseBody Response updateTireInspection(
			@RequestParam(value = "inspectionId", required = false) Long inspectionId,
			@RequestParam(value = "Location", required = false) String location,
			@RequestParam(value = "KMSReading", required = false) Long KMSReading,
			@RequestParam(value = "depthLocation1", required = false) Double depthLocation1,
			@RequestParam(value = "depthLocation2", required = false) Double depthLocation2,
			@RequestParam(value = "depthLocation3", required = false) Double depthLocation3,
			@RequestParam(value = "tirePressure", required = false) String tirePressure, HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == inspectionId || inspectionId < 1) {
						// Please select tire
						response.setDisplayMsg(MyConstants.INVALID_INSPECTION_ID);
						response.setErrorMsg(inspectionId + " - " + MyConstants.INVALID_INSPECTION_ID);
					} else if (null == location || location.trim().length() < 1) {
						// Please enter location
						response.setDisplayMsg(MyConstants.LOCATION_REQUIRED);
						response.setErrorMsg(location + " - " + MyConstants.LOCATION_REQUIRED);
					} else if (null == KMSReading || KMSReading < 1) {
						// Please enter KMS Reading
						response.setDisplayMsg(MyConstants.KMSREADING_REQUIRED);
						response.setErrorMsg(KMSReading + " - " + MyConstants.KMSREADING_REQUIRED);
					} else if (null == depthLocation1) {
						// Please enter Tread depth location 1
						response.setDisplayMsg(MyConstants.TREAD_DEPTH_LOCATION1_REQUIRED);
						response.setErrorMsg(depthLocation1 + "- " + MyConstants.TREAD_DEPTH_LOCATION1_REQUIRED);
					} else if (null == depthLocation2) {
						// Please enter Tread depth location 2
						response.setDisplayMsg(MyConstants.TREAD_DEPTH_LOCATION2_REQUIRED);
						response.setErrorMsg(depthLocation2 + "- " + MyConstants.TREAD_DEPTH_LOCATION2_REQUIRED);
					} else if (null == depthLocation3) {
						// Please enter Tread depth location 3
						response.setDisplayMsg(MyConstants.TREAD_DEPTH_LOCATION3_REQUIRED);
						response.setErrorMsg(depthLocation3 + "- " + MyConstants.TREAD_DEPTH_LOCATION3_REQUIRED);
					} else if (null == tirePressure || tirePressure.equalsIgnoreCase("null")
							|| tirePressure.trim().length() < 1) {
						// Please enter Tire pressure
						response.setDisplayMsg(MyConstants.TIRE_PRESSURE_REQUIRED);
						response.setErrorMsg(tirePressure + "- " + MyConstants.TIRE_PRESSURE_REQUIRED);
					} else {
						TMSTireInspection existingInspection = mySQLService.getTMSTireInspectionById(inspectionId);
						if (null == existingInspection) {
							// Please select a tire
							response.setDisplayMsg(MyConstants.INSPECTION_NOT_FOUND);
							response.setErrorMsg(inspectionId + "- " + MyConstants.INSPECTION_NOT_FOUND);
						} else {
							existingInspection.setDepthLocation1(depthLocation1);
							existingInspection.setDepthLocation2(depthLocation2);
							existingInspection.setDepthLocation3(depthLocation3);
							existingInspection.setAvgThreadDepth(
									MyConstants.calculateAvgDepth(depthLocation1, depthLocation2, depthLocation3));
							existingInspection.setKMSReading(KMSReading);
							existingInspection.setLocation(location);
							existingInspection.setTirePressure(tirePressure);
							existingInspection.setCreatedByName(loginUser.getUserName());
							existingInspection.setUpdatedDateTime(new Date());
							response = mySQLService.saveOrUpdateTireInspection(existingInspection);
							if (response.isStatus()) {
								response.setDisplayMsg(MyConstants.TIRE_INSPECTION_UPDATE_SUCCESS);
							} else {
								response.setDisplayMsg(MyConstants.TIRE_INSPECTION_UPDATE_FAILED);
							}
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

	// Add service history
	@RequestMapping(value = "/Tire/addServiceHistory", method = RequestMethod.GET)
	public @ResponseBody Response addTireServiceHistory(@RequestParam(value = "tireId", required = false) Long tireId,
			@RequestParam(value = "depot", required = false) String depot,
			@RequestParam(value = "tireMake", required = false) String tireMake,
			@RequestParam(value = "vehId", required = false) Long vehId,
			@RequestParam(value = "fittedDate", required = false) Long fittedDate,
			@RequestParam(value = "kmsAtTireFitted", required = false) Long kmsAtTireFitted,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "removalDate", required = false) Long removalDate,
			@RequestParam(value = "kmsAtTireRemoved", required = false) Long kmsAtTireRemoved,
			@RequestParam(value = "orgId", required = false) Long orgId,
			@RequestParam(value = "reason", required = false) String reason,
			@RequestParam(value = "actionTaken", required = false) String actionTaken,
			@RequestParam(value = "tireCondition", required = false) String tireCondition,
			@RequestParam(value = "tireScrappedParty", required = false) String tireScrappedParty,
			HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == tireId || tireId == 0) {
						// Please select a tire
						response.setDisplayMsg(MyConstants.SELECT_TIRE);
						response.setErrorMsg(tireId + " - " + MyConstants.TIREID_REQUIRED);
					} else if (null == depot || depot.equalsIgnoreCase("undefined") || depot.equalsIgnoreCase("null")
							|| depot.trim().length() == 0) {
						// Please enter a depot
						response.setDisplayMsg(MyConstants.DEPOT_REQUIRED);
						response.setErrorMsg(depot + " - " + MyConstants.DEPOT_REQUIRED);
					} else if (null == tireMake || tireMake.equalsIgnoreCase("undefined")
							|| tireMake.equalsIgnoreCase("null") || tireMake.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.TIREMAKE_REQUIRED);
						response.setErrorMsg(tireMake + " - " + MyConstants.TIREMAKE_REQUIRED);
					} else if (null == vehId || vehId == 0) {
						response.setDisplayMsg(MyConstants.BUS_NO_REQUIRED);
						response.setErrorMsg(vehId + " - " + MyConstants.BUS_NO_REQUIRED);
					} else if (null == fittedDate || fittedDate == 0) {
						response.setDisplayMsg(MyConstants.FITTED_DATE_REQUIRED);
						response.setErrorMsg(fittedDate + " - " + MyConstants.FITTED_DATE_REQUIRED);
					} else if (null == kmsAtTireFitted || kmsAtTireFitted == 0) {
						response.setDisplayMsg(MyConstants.FITTED_KMS_REQUIRED);
						response.setErrorMsg(kmsAtTireFitted + " - " + MyConstants.FITTED_KMS_REQUIRED);
					} else if (null == location || location.equalsIgnoreCase("undefined")
							|| location.equalsIgnoreCase("null") || location.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.LOCATION_REQUIRED);
						response.setErrorMsg(location + " - " + MyConstants.LOCATION_REQUIRED);
					} else if (null == removalDate || removalDate == 0) {
						response.setDisplayMsg(MyConstants.TIRE_REMOVAL_DATE_REQUIRED);
						response.setErrorMsg(removalDate + " - " + MyConstants.TIRE_REMOVAL_DATE_REQUIRED);
					} else if (null == kmsAtTireRemoved || kmsAtTireRemoved == 0) {
						response.setDisplayMsg(MyConstants.REMOVAL_KMS_REQUIRED);
						response.setErrorMsg(kmsAtTireFitted + " - " + MyConstants.REMOVAL_KMS_REQUIRED);
					} else if (null == reason || reason.equalsIgnoreCase("undefined") || reason.equalsIgnoreCase("null")
							|| reason.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.REASON_REQUIRED);
						response.setErrorMsg(reason + " - " + MyConstants.REASON_REQUIRED);
					} else if (null == actionTaken || actionTaken.equalsIgnoreCase("undefined")
							|| actionTaken.equalsIgnoreCase("null") || actionTaken.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.ACTION_REQUIRED);
						response.setErrorMsg(actionTaken + " - " + MyConstants.ACTION_REQUIRED);
					} else if (null == tireCondition || tireCondition.equalsIgnoreCase("undefined")
							|| tireCondition.equalsIgnoreCase("null") || tireCondition.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.TIRE_CONDITION_REQUIRED);
						response.setErrorMsg(tireCondition + " - " + MyConstants.TIRE_CONDITION_REQUIRED);
					} else if (null == tireScrappedParty || tireScrappedParty.equalsIgnoreCase("undefined")
							|| tireScrappedParty.equalsIgnoreCase("null") || tireScrappedParty.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.TIRE_SCRAPPED_PARTY_REQUIRED);
						response.setErrorMsg(tireScrappedParty + " - " + MyConstants.TIRE_SCRAPPED_PARTY_REQUIRED);
					} else if ((null == orgId || orgId == 0) && loginUser.getUserLevel() < 5) {
						// Organization is required
						response.setDisplayMsg(MyConstants.ORG_NAME_REQUIRED);
						response.setErrorMsg(orgId + " - " + MyConstants.ORG_ID_REQUIRED);
					} else if (kmsAtTireFitted > kmsAtTireRemoved) {
						response.setDisplayMsg(MyConstants.FITMENT_KM_GREATERTHAN_REMOVAL_KM);
						response.setErrorMsg(kmsAtTireFitted + " - " + kmsAtTireRemoved + " - "
								+ MyConstants.FITMENT_KM_GREATERTHAN_REMOVAL_KM);
					} else if (new Date(fittedDate).after(new Date(removalDate))) {
						response.setDisplayMsg(MyConstants.FITMENT_DATE_GREATERTHAN_REMOVAL_DATE);
						response.setErrorMsg(fittedDate + " - " + removalDate + " - "
								+ MyConstants.FITMENT_DATE_GREATERTHAN_REMOVAL_DATE);
					} else {
						if (loginUser.getUserLevel() >= 5) {
							orgId = loginUser.getOrgId();
						}
						TMSTire tire = mySQLService.getTireByTireId(tireId);
						if (null != tire) {
							TMSVehicles veh = mySQLService.getVehById(vehId);
							if (null != veh) {
								SimpleDateFormat sdf = new SimpleDateFormat(MyConstants.MYSQL_DATE_TIME_FORMATER);

								// // Check service exists on the fitment date
								// List<TMSTireService> dateConditionServices =
								// mySQLService
								// .getServicesBetweenDate(new Date(fittedDate),
								// tireId);
								//
								// if (dateConditionServices.size() == 0) {
								// Check service exists on the removal date
								List<TMSTireService> dateConditionServices = mySQLService
										.getServicesBetweenDate(new Date(fittedDate), new Date(removalDate), tireId);

								if (dateConditionServices.size() == 0) {
									TMSTireService service = new TMSTireService();

									service.setTireId(tireId);
									service.setTireNumber(tire.getTireNumber());
									service.setDepot(depot);
									service.setTireMake(tireMake);
									service.setVehId(veh.getVehId());
									service.setVehName(veh.getVehName());

									service.setFittedDate(new Date(fittedDate));
									service.setKmsAtTyreFitted(kmsAtTireFitted);
									service.setLocation(location);
									service.setRemovalDate(new Date(removalDate));
									service.setKmsAtTyreRemoved(kmsAtTireRemoved);
									service.setTyreKms(kmsAtTireRemoved - kmsAtTireFitted);
									service.setReason(reason);
									service.setActionTaken(actionTaken);
									service.setTyreCondition(tireCondition);
									service.setScrappedToParty(tireScrappedParty);
									service.setOrgId(orgId);

									service.setCreatedBy(loginUser.getUserId());
									service.setCreatedByName(loginUser.getUserName());
									service.setCreatedDate(new Date());
									service.setUpdatedDateTime(new Date());
									response = mySQLService.saveOrUpdateTireServices(service);
									if (response.isStatus()) {

										tire.setTotalTyreKM(
												tire.getTotalTyreKM() + (kmsAtTireRemoved - kmsAtTireFitted));
										mySQLService.saveOrUpdateTire(tire);
									} else {
										response.setDisplayMsg(MyConstants.TIRE_SERVICE_ADDING_FAILED);
									}
								} else {
									response.setDisplayMsg(MyConstants.SERVICE_EXISTS_ON_THESE_DATES + ": "
											+ sdf.format(new Date(removalDate)));
									response.setErrorMsg(removalDate + " - " + MyConstants.SERVICE_EXISTS_ON_THESE_DATES
											+ ": " + sdf.format(new Date(removalDate)));
								}
								// } else {
								// response.setDisplayMsg(MyConstants.SERVICE_EXISTS_ON_FITTED_DATE
								// + ": "
								// + sdf.format(new Date(fittedDate)));
								// response.setErrorMsg(fittedDate + " - " +
								// MyConstants.SERVICE_EXISTS_ON_FITTED_DATE
								// + ": " + sdf.format(new Date(fittedDate)));
								// }
							} else {
								// Vehicle not found
								response.setDisplayMsg(MyConstants.VEHICLE_NOT_EXISTS);
								response.setErrorMsg(vehId + " - " + MyConstants.VEHICLE_NOT_EXISTS);
							}
						} else {
							// Tire not exists
							response.setDisplayMsg(MyConstants.TIRE_NOT_EXISTS);
							response.setErrorMsg(tireId + " - " + MyConstants.TIRE_NOT_EXISTS);
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
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/Tire/updateService", method = RequestMethod.GET)
	public @ResponseBody Response updateTireService(@RequestParam(value = "serviceId", required = false) Long serviceId,
			@RequestParam(value = "vehId", required = false) Long vehId,
			@RequestParam(value = "fittedDate", required = false) Long fittedDate,
			@RequestParam(value = "kmsAtTireFitted", required = false) Long kmsAtTireFitted,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "removalDate", required = false) Long removalDate,
			@RequestParam(value = "kmsAtTireRemoved", required = false) Long kmsAtTireRemoved,
			@RequestParam(value = "orgId", required = false) Long orgId,
			@RequestParam(value = "reason", required = false) String reason,
			@RequestParam(value = "actionTaken", required = false) String actionTaken,
			@RequestParam(value = "tireCondition", required = false) String tireCondition,
			@RequestParam(value = "tireScrappedParty", required = false) String tireScrappedParty,
			HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == serviceId || serviceId == 0) {
						// Please select a tire
						response.setDisplayMsg(MyConstants.INVALID_SERVICE_ID);
						response.setErrorMsg(serviceId + " - " + MyConstants.INVALID_SERVICE_ID);
					} else if (null == vehId || vehId == 0) {
						response.setDisplayMsg(MyConstants.BUS_NO_REQUIRED);
						response.setErrorMsg(vehId + " - " + MyConstants.BUS_NO_REQUIRED);
					} else if (null == fittedDate || fittedDate == 0) {
						response.setDisplayMsg(MyConstants.FITTED_DATE_REQUIRED);
						response.setErrorMsg(fittedDate + " - " + MyConstants.FITTED_DATE_REQUIRED);
					} else if (null == kmsAtTireFitted || kmsAtTireFitted == 0) {
						response.setDisplayMsg(MyConstants.FITTED_KMS_REQUIRED);
						response.setErrorMsg(kmsAtTireFitted + " - " + MyConstants.FITTED_KMS_REQUIRED);
					} else if (null == location || location.equalsIgnoreCase("undefined")
							|| location.equalsIgnoreCase("null") || location.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.LOCATION_REQUIRED);
						response.setErrorMsg(location + " - " + MyConstants.LOCATION_REQUIRED);
					} else if (null == removalDate || removalDate == 0) {
						response.setDisplayMsg(MyConstants.TIRE_REMOVAL_DATE_REQUIRED);
						response.setErrorMsg(removalDate + " - " + MyConstants.TIRE_REMOVAL_DATE_REQUIRED);
					} else if (null == kmsAtTireRemoved || kmsAtTireRemoved == 0) {
						response.setDisplayMsg(MyConstants.REMOVAL_KMS_REQUIRED);
						response.setErrorMsg(kmsAtTireFitted + " - " + MyConstants.REMOVAL_KMS_REQUIRED);
					} else if (null == reason || reason.equalsIgnoreCase("undefined") || reason.equalsIgnoreCase("null")
							|| reason.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.REASON_REQUIRED);
						response.setErrorMsg(reason + " - " + MyConstants.REASON_REQUIRED);
					} else if (null == actionTaken || actionTaken.equalsIgnoreCase("undefined")
							|| actionTaken.equalsIgnoreCase("null") || actionTaken.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.ACTION_REQUIRED);
						response.setErrorMsg(actionTaken + " - " + MyConstants.ACTION_REQUIRED);
					} else if (null == tireCondition || tireCondition.equalsIgnoreCase("undefined")
							|| tireCondition.equalsIgnoreCase("null") || tireCondition.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.TIRE_CONDITION_REQUIRED);
						response.setErrorMsg(tireCondition + " - " + MyConstants.TIRE_CONDITION_REQUIRED);
					} else if (null == tireScrappedParty || tireScrappedParty.equalsIgnoreCase("undefined")
							|| tireScrappedParty.equalsIgnoreCase("null") || tireScrappedParty.trim().length() == 0) {
						response.setDisplayMsg(MyConstants.TIRE_SCRAPPED_PARTY_REQUIRED);
						response.setErrorMsg(tireScrappedParty + " - " + MyConstants.TIRE_SCRAPPED_PARTY_REQUIRED);
					} else if ((null == orgId || orgId == 0) && loginUser.getUserLevel() < 5) {
						// Organization is required
						response.setDisplayMsg(MyConstants.ORG_NAME_REQUIRED);
						response.setErrorMsg(orgId + " - " + MyConstants.ORG_ID_REQUIRED);
					} else {
						if (loginUser.getUserLevel() >= 5) {
							orgId = loginUser.getOrgId();
						}
						TMSTireService service = mySQLService.getTMSTireServiceById(serviceId);
						if (null != service) {
							TMSVehicles veh = mySQLService.getVehById(vehId);
							if (null != veh) {

								service.setVehId(veh.getVehId());
								service.setVehName(veh.getVehName());
								service.setFittedDate(new Date(fittedDate));
								service.setKmsAtTyreFitted(kmsAtTireFitted);
								service.setLocation(location);
								service.setRemovalDate(new Date(removalDate));
								service.setKmsAtTyreRemoved(kmsAtTireRemoved);
								service.setTyreKms(kmsAtTireRemoved - kmsAtTireFitted);
								service.setReason(reason);
								service.setActionTaken(actionTaken);
								service.setTyreCondition(tireCondition);
								service.setScrappedToParty(tireScrappedParty);
								service.setOrgId(orgId);

								service.setCreatedBy(loginUser.getUserId());
								service.setCreatedByName(loginUser.getUserName());
								service.setUpdatedDateTime(new Date());
								response = mySQLService.saveOrUpdateTireServices(service);
								if (response.isStatus()) {
									response.setDisplayMsg(MyConstants.TIRE_SERVICE_UPDATE_SUCCESS);
								} else {
									response.setDisplayMsg(MyConstants.TIRE_SERVICE_UPDATE_FAILED);
								}
							} else {
								// Vehicle not found
								response.setDisplayMsg(MyConstants.VEHICLE_NOT_EXISTS);
								response.setErrorMsg(vehId + " - " + MyConstants.VEHICLE_NOT_EXISTS);
							}
						} else {
							// Tire not exists
							response.setDisplayMsg(MyConstants.SERVICE_NOT_FOUND);
							response.setErrorMsg(serviceId + " - " + MyConstants.SERVICE_NOT_FOUND);
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
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/Tire/Delete", method = RequestMethod.GET)
	public @ResponseBody Response deleteTire(@RequestParam(value = "tireId", required = false) Long tireId,
			HttpServletRequest request) {
		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null == tireId || tireId == 0) {
						response.setDisplayMsg(MyConstants.TIRE_DELETING_FAILED);
						response.setErrorMsg(tireId + " " + MyConstants.TIREID_REQUIRED);
					} else {
						TMSTire tmsTire = mySQLService.getTireByTireId(tireId);
						if (null != tmsTire) {
							response = mySQLService.deleteTire(tmsTire);
						} else {
							response.setDisplayMsg(MyConstants.TIRE_DELETING_FAILED);
							response.setErrorMsg(tireId + " " + MyConstants.TIREID_NOT_EXISTS);
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
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}

		return response;
	}

	@RequestMapping(value = "/addOrganization", method = RequestMethod.GET)
	public @ResponseBody Response addTMSOrganization(@RequestParam(value = "orgName", required = false) String orgName,
			HttpServletRequest request) {

		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {
					if (null != orgName && orgName.trim().length() > 0) {
						Organizations org = mySQLService.getOrgByName(orgName);

						if (null == org) {
							org = new Organizations();
							org.setOrgName(orgName);
							org.setCreatedBy(loginUser.getUserId());
							org.setCreatedDateTime(new Date());
							org.setStatus(1);
							org.setUpdatedDateTime(new Date());
							response = mySQLService.saveOrUpdateOrg(org);
						} else {
							response.setDisplayMsg(MyConstants.ORG_NAME_EXISTS);
							response.setErrorMsg(orgName + " - " + MyConstants.ORG_NAME_EXISTS);
						}
					} else {
						response.setDisplayMsg(MyConstants.ORG_NAME_REQUIRED);
						response.setErrorMsg(orgName + " - " + MyConstants.ORG_NAME_REQUIRED);
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
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/addDepot", method = RequestMethod.GET)
	public @ResponseBody Response addTMSDepot(@RequestParam(value = "depotName", required = false) String depotName,
			HttpServletRequest request) {

		Response response = new Response();
		response.setStatus(false);
		try {
			HttpSession session = request.getSession(false);
			if (null != session && session.isNew() == false) {
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser != null) {

					if (null != depotName && depotName.trim().length() > 0) {
						TMSDepot depot = mySQLService.getTMSDepotByName(depotName);
						if (null == depot) {
							depot = new TMSDepot();
							depot.setDepotName(depotName);
							depot.setCreatedBy(loginUser.getUserId());
							depot.setOrgId(loginUser.getOrgId());
							depot.setCreatedDateTime(new Date());
							depot.setUpdatedDateTime(new Date());

							response = mySQLService.saveOrUpdateDepot(depot);
						} else {
							response.setDisplayMsg(MyConstants.DEPOT_NAME_EXISTS);
							response.setErrorMsg(depotName + " - " + MyConstants.DEPOT_NAME_EXISTS);
						}
					} else {
						response.setDisplayMsg(MyConstants.DEPOT_NAME_REQUIRED);
						response.setErrorMsg(depotName + " - " + MyConstants.DEPOT_NAME_REQUIRED);
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
