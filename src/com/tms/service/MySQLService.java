package com.tms.service;

import java.util.Date;
import java.util.List;

import com.tms.beans.Response;
import com.tms.model.TMSBController;
import com.tms.model.TMSBasicVehicleDetails;
import com.tms.model.TMSDepot;
import com.tms.model.Organizations;
import com.tms.model.TMSTireShortDetails;
import com.tms.model.TMSTireView;
import com.tms.model.TMSRFID;
import com.tms.model.TMSSensor;
import com.tms.model.TMSTire;
import com.tms.model.TMSTireInspection;
import com.tms.model.TMSTireService;
import com.tms.model.TMSTyreMake;
import com.tms.model.TMSUserVehicleMapping;
import com.tms.model.TMSVehicles;
import com.tms.model.TMSUserVehiclesView;
import com.tms.model.UserMaster;

public interface MySQLService {

	public UserMaster getLoginStatus(UserMaster user);
	
	public List<UserMaster> getAllTMSUsersByUserIds(List<Long> userIds);

	// Vehicle details services
	public List<TMSUserVehiclesView> getVehicles();
	
	public List<TMSBasicVehicleDetails> getAllBasicVehDetials(long orgId);
	
	public List<TMSBasicVehicleDetails> getAllBasicVehDetialsByVehIds(List<Long> vehIds);
	
	public List<TMSBasicVehicleDetails> getAllBasicVehDetialsByUserId(long userId);
	
	public List<TMSUserVehiclesView> getVehiclesByLimit(long userId, int limit, int startIndex);
	
	public List<TMSUserVehiclesView> getVehiclesByVehIds(List<Long> vehIds, long userId);
	
	public List<TMSUserVehiclesView> searchVehicles(String searchWord, long userId);
	
	public List<TMSUserVehiclesView> getModifiedVehDetails(Date lastUpdatedDate);
	
	public Response saveOrUpdate(TMSVehicles tmsVehicles);

	public TMSVehicles getVehByName(String vehicleName);

	public TMSVehicles getVehById(long vehicleId);

	public Response deleteVehicle(TMSVehicles tmsVehicle);
	
	public Response saveOrUpdateUserVehMapping(TMSUserVehicleMapping userVehMapping);
	
	public List<TMSUserVehicleMapping> getVehicleIdsByUserId(long userId);
	
	public long getVehiclesCountByUserId(long userId);

	// RFID details services
	public List<TMSRFID> getRFID(String status);
	
	public Response saveOrUpdateRFID(TMSRFID tmsRFID);

	public TMSRFID getRFIDByRFIDUID(String RFIDUID);

	public TMSRFID getRFIDByRFID(long RFID);

	public Response deleteRFIDByRFID(TMSRFID tmsRFID);
	
	public TMSRFID getRFIDByVehId(long vehId);

	// Bluetooth Controller details services
	public List<TMSBController> getBController(String status);
	
	public Response saveOrUpdateBController(TMSBController tmsBController);

	public TMSBController getBControllerByBCtrlUID(String BCtrlUID);

	public TMSBController getBCtrlByBCtrlId(long BCtrlId);

	public Response deleteBCtrlByBCtrl(TMSBController tmsBController);
	
	public TMSBController getBCtrlByVehId(long vehId);
	
	//Sensor details services
//	public List<TMSSensor> getSensors(String status);
	
	public List<TMSSensor> getSensors(String status, int limit, int startIndex);
	
	public Response saveOrUpdateSensor(TMSSensor tmsSensor);

	public TMSSensor getSensorBySensorUID(String sensorUID);

	public TMSSensor getSensorBySensorId(long sensorId);
	
	public TMSSensor getSensorByTireId(long tireId);

	public Response deleteSensor(TMSSensor tmsSensor);
	
	// Tire details services
	public List<TMSTire> getTiresByVehId(long vehId);
	
	public List<TMSTireView> getTireViewDetials(String status);
	
	public List<TMSTireView> getModifiedTiresDetails(Date lastUpdatedDate);
	
	public Response saveOrUpdateTire(TMSTire tmsTire);

	public TMSTire getTireByTireNumber(String tireNumber);

	public TMSTire getTireByTireId(long tireId);
	
	public TMSTireView getTireViewByTireId(long tireId);

	public Response deleteTire(TMSTire tmsTire);
	
	public List<TMSTireShortDetails> getShortTireDetails();
	
	
	// Tire Inspection
	public List<TMSTireInspection> getTMSTireInspections(long userId, int limit, int startIndex);
	
	public Response saveOrUpdateTireInspection(TMSTireInspection inspection);
	
	public TMSTireInspection getTMSTireInspectionById(long inspectionId);
	
	
	// Tire Service
	public List<TMSTireService> getTMSTireServices(long orgId, long userId, int limit, int startIndex);
	
	public Response saveOrUpdateTireServices(TMSTireService service);
	
	public TMSTireService getTMSTireServiceById(long serviceId);
	
	public List<TMSTireService> getServicesBetweenDate(Date date, long tireId);
	
	public List<TMSTireService> getServicesBetweenDate(Date fitmentDate,Date removalDate, long tireId);
	
	
	// Organization services
	public Response saveOrUpdateOrg(Organizations org);
	
	public Organizations getOrgByName(String name);
	
	public List<Organizations> getAllTMSOrgs();

	// Depot services
	public Response saveOrUpdateDepot(TMSDepot org);
	
	public TMSDepot getTMSDepotByName(String name);
	
	public TMSDepot getTMSDepotById(long depotId);
	
	public List<TMSDepot> getAllTMSDepots(long orgId);
	
	// Get Tyre make list
	public List<TMSTyreMake> getAllTMSTyreMake(long orgId);
	
	// TMS Users list
	public List<UserMaster> getAllTMSUsersByOrgId(long orgId);
	
	
}
