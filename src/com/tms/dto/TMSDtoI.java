package com.tms.dto;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.tms.service.MySQLService;

public interface TMSDtoI {
	
	public List<Long> getAndSetUserVehToSession(List<Long> vehIds,MySQLService mySQLService, HttpSession session);

}
