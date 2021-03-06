package com.tms.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.tms.model.UserMaster;

public interface MySQLDAO {

	public UserMaster getLoginStatus(UserMaster user);

	public List<UserMaster> getAllTMSUsersByUserIds(List<Long> userIds);
	
	public List<TMSTireView> searchTires(String searchWord, long orgId, int limit, int startIndex);
	
	public Response getAllUserVehDetails(String searchWord, List<Long> vehIds, int limit, int startIndex);
	
	public long getTireCountBasedOnStatus(String status, boolean sensorStatus, long orgId);
	
	public long getTireServiceCount(long orgId);
	
	public long getTireInspectionsCount(long orgId);
	
	public Response processServiceDetails(TMSTireService existingService, TMSTireInspection inspection, TMSTire tire);
	
	// Add, Update & Delete Vehicles services
	public List<TMSBasicVehicleDetails> getVehicles();

	public List<TMSBasicVehicleDetails> getAllBasicVehDetials(long orgId);

	public List<TMSBasicVehicleDetails> getAllBasicVehDetialsByVehIds(List<Long> vehIds);
	
	public List<TMSBasicVehicleDetails> getAllBasicVehDetialsByUserId(long userId);

	public List<TMSUserVehiclesView> getVehiclesByLimit(long userId, int limit, int startIndex);
	
	public List<TMSUserVehiclesView> getVehiclesByVehIds(List<Long> vehIds, long userId);

	public Response searchVehicles(String searchWord, long userId, int limit, int startIndex);

	public List<TMSUserVehiclesView> getModifiedVehDetails(Date lastUpdatedDate);

	public Response saveOrUpdate(TMSVehicles tmsVehicles);

	public TMSVehicles getVehByName(String vehicleName);

	public TMSVehicles getVehById(long vehicleId);
	
	public List<TMSVehicles> getVehByVehIds(List<Long> vehIds);

	public Response deleteVehicle(TMSVehicles tmsVehicles);

	// Assign Vehicle to User
	public Response saveOrUpdateUserVehMapping(TMSUserVehicleMapping userVehMapping);

	public List<TMSUserVehicleMapping> getVehicleIdsByUserId(long userId);
	
	public long getVehiclesCountByUserId(long userId, boolean uniqueVehs);
	
	public TMSUserVehicleMapping getTMSUserVehicleMappingDetails(long vehId, long userId);

	// RFID details services
	public Response getRFID(String status, String searchWord, int limit, int startIndex);

	public TMSRFID getRFIDByRFIDUID(String RFIDUID);

	public TMSRFID getRFIDByRFID(long RFID);

	public Response deleteRFIDByRFID(TMSRFID tmsRFID);

	public Response saveOrUpdateRFID(TMSRFID tmsRFID);

	public TMSRFID getRFIDByVehId(long vehId);

	// Bluetooth Controller details services
	public Response getBController(String status, String searchWord, int limit, int startIndex);

	public Response saveOrUpdateBController(TMSBController tmsBController);

	public TMSBController getBControllerByBCtrlUID(String BCtrlUID);

	public TMSBController getBCtrlByBCtrlId(long BCtrlId);

	public Response deleteBCtrlByBCtrl(TMSBController tmsBController);

	public TMSBController getBCtrlByVehId(long vehId);

	// Sensor
	public Response getSensors(String status, String searchWord, int limit, int startIndex);
	
	public long getSensorsCount(String status);

	public Response saveOrUpdateSensor(TMSSensor tmsSensor);

	public TMSSensor getSensorBySensorUID(String sensorUID);

	public TMSSensor getSensorBySensorId(long sensorId);

	public TMSSensor getSensorByTireId(long tireId);

	public Response deleteSensor(TMSSensor tmsSensor);

	
	// Tire details services
	public Map< String, Integer> getTireSensorUIDsMap(List<String> sensorUIDs);
	
	public List<TMSTire> getTiresByVehId(long vehId);

	public List<TMSTireView> getTireViewDetials(long orgId, String status, int limit, int startIndex);

	public List<TMSTireView> getModifiedTiresDetails(Date lastUpdatedDate);

	public Response saveOrUpdateTire(TMSTire tmsTire);

	public TMSTire getTireByTireNumber(String tireNumber);

	public TMSTire getTireByTireId(long tireId);
	
	public TMSTireView getTireViewByTireId(long tireId);

	public Response deleteTire(TMSTire tmsTire);

	public List<TMSTireShortDetails> getShortTireDetails(long orgId, String status);

	public long getTMSTiresCount(long orgId, String status, String searchString);
	
	
	// Tire Inspection
	public List<TMSTireInspection> getTMSTireInspections(long orgId, long userId, String searchWord, int limit, int startIndex);

	public Response saveOrUpdateTireInspection(TMSTireInspection inspection);

	public TMSTireInspection getTMSTireInspectionById(long inspectionId);
	
	public long getTMSTireInspectionsCount(long orgId, long userId, String searchWord);

	
	// Tire Service
	public List<TMSTireService> getTMSTireServices(long orgId, long userId, String searchWord, int limit, int startIndex);

	public long getTMSTireServicesCount(long orgId, long userId, String searchWord);
	
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
	
	//Other
	public Response deallocateSensorFromTire(TMSTire tire, TMSSensor sensor);
	
	public TMSMinMaxTempPressure getMinMaxTempPressureValues(long orgId, long userId);
	
	public List<VehicleTyreCount> findSemiAssignedVehCount(List<Long> vehIds);

}
