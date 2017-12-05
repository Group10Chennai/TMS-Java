package com.tms.dto;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.tms.model.TMSBasicVehicleDetails;
import com.tms.model.UserMaster;
import com.tms.service.MySQLService;

@Service("TMSDtoI")
public class TMSDtoImpl implements TMSDtoI {

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getAndSetUserVehToSession(List<Long> vehIds, MySQLService mySQLService, 
			HttpSession session) {
		List<Long> session_vehId = new ArrayList<>();
		try {
			session_vehId = (List<Long>) session.getAttribute("vehIds");
			if (null == session_vehId){
				// Get Vehicle ids list
				UserMaster loginUser = (UserMaster) session.getAttribute("LoginUser");
				if (loginUser.getTMSUserLevel() >= 5) {
					List<TMSBasicVehicleDetails> vehicles = mySQLService
							.getAllBasicVehDetialsByUserId(loginUser.getUserId());
					
					session_vehId = new ArrayList<>();
					for (TMSBasicVehicleDetails veh : vehicles) {
						session_vehId.add(veh.getVehId());
					}
				} else {
					List<TMSBasicVehicleDetails> vehicles = mySQLService.getVehicles();
					session_vehId = new ArrayList<>();
					for (TMSBasicVehicleDetails veh : vehicles) {
						session_vehId.add(veh.getVehId());
					}
				}				
			}
			if (null != vehIds){
				// Assign vehicle ids into the user session
				for(long vehId : vehIds){
					session_vehId.add(vehId);
				}
			}
			session.setAttribute("vehIds", session_vehId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return session_vehId;
	}

}
