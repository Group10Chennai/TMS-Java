package com.tms.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.beans.Response;
import com.tms.beans.VehicleTyreCount;
import com.tms.dao.MySQLDAO;
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

@Service("MySQLService")
@Transactional
public class MySQLServiceImpl implements MySQLService {

	@Autowired
	private MySQLDAO mySQLDAO;
	
	@Override
	public UserMaster getLoginStatus(UserMaster user) {
		return mySQLDAO.getLoginStatus(user);
	}

	@Override
	public Response saveOrUpdate(TMSVehicles tmsVehicles) {
		return mySQLDAO.saveOrUpdate(tmsVehicles);
	}

	@Override
	public TMSVehicles getVehByName(String vehicleName) {
		
		return mySQLDAO.getVehByName(vehicleName);
	}

	@Override
	public TMSVehicles getVehById(long vehicleId) {
		
		return mySQLDAO.getVehById(vehicleId);
	}

	@Override
	public Response deleteVehicle(TMSVehicles tmsVehicle) {
		
		return mySQLDAO.deleteVehicle(tmsVehicle);
	}

	@Override
	public TMSRFID getRFIDByRFIDUID(String RFIDUID) {
		
		return mySQLDAO.getRFIDByRFIDUID(RFIDUID);
	}

	@Override
	public TMSRFID getRFIDByRFID(long RFID) {
		
		return mySQLDAO.getRFIDByRFID(RFID);
	}

	@Override
	public Response deleteRFIDByRFID(TMSRFID tmsRFID) {
		
		return mySQLDAO.deleteRFIDByRFID(tmsRFID);
	}

	@Override
	public Response saveOrUpdateRFID(TMSRFID tmsRFID) {
		
		return mySQLDAO.saveOrUpdateRFID(tmsRFID);
	}


	@Override
	public Response saveOrUpdateBController(TMSBController tmsBController) {
		
		return mySQLDAO.saveOrUpdateBController(tmsBController);
	}

	@Override
	public TMSBController getBControllerByBCtrlUID(String BCtrlUID) {
		
		return mySQLDAO.getBControllerByBCtrlUID(BCtrlUID);
	}

	@Override
	public TMSBController getBCtrlByBCtrlId(long BCtrlId) {
		
		return mySQLDAO.getBCtrlByBCtrlId(BCtrlId);
	}

	@Override
	public Response deleteBCtrlByBCtrl(TMSBController tmsBController) {
		
		return mySQLDAO.deleteBCtrlByBCtrl(tmsBController);
	}

	@Override
	public Response saveOrUpdateSensor(TMSSensor tmsSensor) {
		
		return mySQLDAO.saveOrUpdateSensor(tmsSensor);
	}

	@Override
	public TMSSensor getSensorBySensorUID(String sensorUID) {
		
		return mySQLDAO.getSensorBySensorUID(sensorUID);
	}

	@Override
	public TMSSensor getSensorBySensorId(long sensorId) {
		
		return mySQLDAO.getSensorBySensorId(sensorId);
	}

	@Override
	public Response deleteSensor(TMSSensor tmsSensor) {
		
		return mySQLDAO.deleteSensor(tmsSensor);
	}

	@Override
	public Response saveOrUpdateTire(TMSTire tmsTire) {
		
		return mySQLDAO.saveOrUpdateTire(tmsTire);
	}

	@Override
	public TMSTire getTireByTireNumber(String tireNumber) {
		
		return mySQLDAO.getTireByTireNumber(tireNumber);
	}

	@Override
	public TMSTire getTireByTireId(long tireId) {
		
		return mySQLDAO.getTireByTireId(tireId);
	}

	@Override
	public Response deleteTire(TMSTire tmsTire) {
		
		return mySQLDAO.deleteTire(tmsTire);
	}

	@Override
	public List<TMSBasicVehicleDetails> getVehicles() {
		
		return mySQLDAO.getVehicles();
	}

	@Override
	public Response getRFID(String status, String searchWord, int limit, int startIndex) {
		
		return mySQLDAO.getRFID(status, searchWord, limit, startIndex);
	}

	@Override
	public Response getBController(String status, String searchWord, int limit, int startIndex) {
		
		return mySQLDAO.getBController(status, searchWord, limit, startIndex);
	}

	@Override
	public Response getSensors(String status, String searchWord, int limit, int startIndex) {
		
		return mySQLDAO.getSensors(status, searchWord, limit, startIndex);
	}

	@Override
	public List<TMSTireView> getTireViewDetials(long orgId, String status, int limit, int startIndex) {
		return mySQLDAO.getTireViewDetials(orgId, status, limit, startIndex);
	}

	@Override
	public List<TMSUserVehiclesView> getVehiclesByLimit(long userId, int limit, int startIndex) {
		
		return mySQLDAO.getVehiclesByLimit(userId, limit, startIndex);
	}

	@Override
	public List<TMSTire> getTiresByVehId(long vehId) {
		
		return mySQLDAO.getTiresByVehId(vehId);
	}

	@Override
	public Response saveOrUpdateOrg(Organizations org) {
		
		return mySQLDAO.saveOrUpdateOrg(org);
	}

	@Override
	public Organizations getOrgByName(String name) {

		return mySQLDAO.getOrgByName(name);
	}

	@Override
	public List<Organizations> getAllTMSOrgs() {

		return mySQLDAO.getAllTMSOrgs();
	}

	@Override
	public Response saveOrUpdateDepot(TMSDepot org) {
	
		return mySQLDAO.saveOrUpdateDepot(org);
	}

	@Override
	public TMSDepot getTMSDepotByName(String name) {
		
		return mySQLDAO.getTMSDepotByName(name);
	}

	@Override
	public List<TMSDepot> getAllTMSDepots(long orgId) {

		return mySQLDAO.getAllTMSDepots(orgId);
	}

	@Override
	public List<TMSTyreMake> getAllTMSTyreMake(long orgId) {
		
		return mySQLDAO.getAllTMSTyreMake(orgId);
	}

	@Override
	public TMSRFID getRFIDByVehId(long vehId) {
		
		return mySQLDAO.getRFIDByVehId(vehId);
	}

	@Override
	public TMSBController getBCtrlByVehId(long vehId) {

		return mySQLDAO.getBCtrlByVehId(vehId);
	}

	@Override
	public List<UserMaster> getAllTMSUsersByOrgId(long orgId) {
		
		return mySQLDAO.getAllTMSUsersByOrgId(orgId);
	}

	@Override
	public TMSSensor getSensorByTireId(long tireId) {
		
		return mySQLDAO.getSensorByTireId(tireId);
	}

	@Override
	public List<TMSTireInspection> getTMSTireInspections(long orgId, long userId, String searchWord, int limit, int startIndex) {
		
		return mySQLDAO.getTMSTireInspections(orgId, userId, searchWord, limit, startIndex);
	}

	@Override
	public Response saveOrUpdateTireInspection(TMSTireInspection inspection) {
		
		return mySQLDAO.saveOrUpdateTireInspection(inspection);
	}

	@Override
	public List<TMSTireShortDetails> getShortTireDetails(long orgId, String status) {
		
		return mySQLDAO.getShortTireDetails(orgId, status);
	}

	@Override
	public TMSTireInspection getTMSTireInspectionById(long inspectionId) {
		
		return mySQLDAO.getTMSTireInspectionById(inspectionId);
	}

	@Override
	public List<TMSTireService> getTMSTireServices(long orgId, long userId, String searchWord, int limit, int startIndex) {
		
		return mySQLDAO.getTMSTireServices(orgId, userId,searchWord, limit, startIndex);
	}

	@Override
	public Response saveOrUpdateTireServices(TMSTireService service) {
		
		return mySQLDAO.saveOrUpdateTireServices(service);
	}

	@Override
	public TMSTireService getTMSTireServiceById(long serviceId) {

		return mySQLDAO.getTMSTireServiceById(serviceId);
	}

	@Override
	public List<TMSUserVehiclesView> getModifiedVehDetails(Date lastUpdatedDate) {
		
		return mySQLDAO.getModifiedVehDetails(lastUpdatedDate);
	}

	@Override
	public List<TMSTireView> getModifiedTiresDetails(Date lastUpdatedDate) {
		
		return mySQLDAO.getModifiedTiresDetails(lastUpdatedDate);
	}

	@Override
	public List<TMSBasicVehicleDetails> getAllBasicVehDetials(long orgId) {
		
		return mySQLDAO.getAllBasicVehDetials(orgId);
	}

	@Override
	public List<UserMaster> getAllTMSUsersByUserIds(List<Long> userIds) {
		
		return mySQLDAO.getAllTMSUsersByUserIds(userIds);
	}

	@Override
	public List<TMSBasicVehicleDetails> getAllBasicVehDetialsByVehIds(List<Long> vehIds) {
		
		return mySQLDAO.getAllBasicVehDetialsByVehIds(vehIds);
	}

	@Override
	public Response saveOrUpdateUserVehMapping(TMSUserVehicleMapping userVehMapping) {
		
		return mySQLDAO.saveOrUpdateUserVehMapping(userVehMapping);
	}

	@Override
	public List<TMSUserVehicleMapping> getVehicleIdsByUserId(long userId) {
		
		return mySQLDAO.getVehicleIdsByUserId(userId);
	}

	@Override
	public List<TMSBasicVehicleDetails> getAllBasicVehDetialsByUserId(long userId) {
		
		return mySQLDAO.getAllBasicVehDetialsByUserId(userId);
	}

	@Override
	public List<TMSUserVehiclesView> getVehiclesByVehIds(List<Long> vehIds, long userId) {
		
		return mySQLDAO.getVehiclesByVehIds(vehIds, userId);
	}

	@Override
	public Response searchVehicles(String searchWord, long userId, int limit, int startIndex) {
		
		return mySQLDAO.searchVehicles(searchWord, userId, limit, startIndex);
	}

	@Override
	public TMSDepot getTMSDepotById(long depotId) {
		
		return mySQLDAO.getTMSDepotById(depotId);
	}

	@Override
	public List<TMSTireService> getServicesBetweenDate(Date date, long tireId) {
		
		return mySQLDAO.getServicesBetweenDate(date, tireId);
	}

	@Override
	public TMSTireView getTireViewByTireId(long tireId) {
		
		return mySQLDAO.getTireViewByTireId(tireId);
	}

	@Override
	public List<TMSTireService> getServicesBetweenDate(Date fitmentDate, Date removalDate, long tireId) {
		
		return mySQLDAO.getServicesBetweenDate(fitmentDate, removalDate, tireId);
	}

	@Override
	public long getVehiclesCountByUserId(long userId, boolean uniqueVehs) {
	
		return mySQLDAO.getVehiclesCountByUserId(userId, uniqueVehs);
	}

	@Override
	public long getTMSTireInspectionsCount(long orgId, long userId, String searchWord) {
		
		return mySQLDAO.getTMSTireInspectionsCount(orgId, userId, searchWord);
	}

	@Override
	public TMSUserVehicleMapping getTMSUserVehicleMappingDetails(long vehId, long userId) {
		
		return mySQLDAO.getTMSUserVehicleMappingDetails(vehId, userId);
	}

	@Override
	public List<TMSVehicles> getVehByVehIds(List<Long> vehIds) {
		
		return mySQLDAO.getVehByVehIds(vehIds);
	}

	@Override
	public Response deallocateSensorFromTire(TMSTire tire, TMSSensor sensor) {
		
		return mySQLDAO.deallocateSensorFromTire(tire, sensor);
	}

	@Override
	public long getTMSTireServicesCount(long orgId, long userId, String searchWord) {
		
		return mySQLDAO.getTMSTireServicesCount(orgId, userId, searchWord);
	}

	@Override
	public List<TMSTireView> searchTires(String searchWord, long orgId, int limit, int startIndex) {
		
		return mySQLDAO.searchTires(searchWord, orgId, limit, startIndex);
	}

	@Override
	public long getTMSTiresCount(long orgId, String status, String searchString) {
		
		return mySQLDAO.getTMSTiresCount(orgId, status, searchString);
	}

	@Override
	public TMSMinMaxTempPressure getMinMaxTempPressureValues(long orgId, long userId) {
		
		return mySQLDAO.getMinMaxTempPressureValues(orgId, userId);
	}

	@Override
	public Response getAllUserVehDetails(String searchWord, List<Long> vehIds, int limit, int startIndex) {
		
		return mySQLDAO.getAllUserVehDetails(searchWord, vehIds, limit, startIndex);
	}

	@Override
	public List<VehicleTyreCount> findSemiAssignedVehCount(List<Long> vehIds) {
		
		return mySQLDAO.findSemiAssignedVehCount(vehIds);
	}

	@Override
	public long getTireCountBasedOnStatus(String status, boolean sensorStatus, long orgId) {
		
		return mySQLDAO.getTireCountBasedOnStatus(status, sensorStatus, orgId);
	}

	@Override
	public long getTireServiceCount(long orgId) {
		
		return mySQLDAO.getTireServiceCount(orgId);
	}
	
	@Override
	public long getTireInspectionsCount(long orgId) {
		
		return mySQLDAO.getTireInspectionsCount(orgId);
	}

	@Override
	public long getSensorsCount(String status) {
		
		return mySQLDAO.getSensorsCount(status);
	}

	@Override
	public Response processServiceDetails(TMSTireService existingService, TMSTireInspection inspection, TMSTire tire) {
		
		return mySQLDAO.processServiceDetails(existingService, inspection, tire);
	}

	@Override
	public Map<String, Integer> getTireSensorUIDsMap(List<String> sensorUIDs) {
		
		return mySQLDAO.getTireSensorUIDsMap(sensorUIDs);
	}

}
