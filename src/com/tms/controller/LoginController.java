package com.tms.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sun.misc.BASE64Encoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tms.beans.MyConstants;
import com.tms.beans.Response;
import com.tms.customheaders.CommonClass;
import com.tms.model.UserMaster;
import com.tms.service.MySQLService;

@Controller
@RequestMapping("/api")
public class LoginController {

	@Autowired
	private MySQLService mySQLService;

	private Response processLogin(String username, String password, String securitycode, String loginVia,
			HttpServletRequest request, HttpServletResponse response) {
		CommonClass.fixInitialHeaders(request, response);
		Response loginRes = new Response();
		loginRes.setStatus(false);
		try {
			// Enumeration headerNames = request.getHeaderNames();
			// while (headerNames.hasMoreElements()) {
			// String key = (String) headerNames.nextElement();
			// String value = request.getHeader(key);
			// System.out.println(key + ": " + value);
			// }
			if (null == username || username.equalsIgnoreCase("undefined") || username.trim().length() == 0) {
				loginRes.setDisplayMsg(MyConstants.USER_NAME_BLANK);
				loginRes.setErrorMsg(username + " - " + MyConstants.USER_NAME_BLANK);
			} else if (null == password || password.equalsIgnoreCase("undefined") || password.trim().length() == 0) {
				loginRes.setDisplayMsg(MyConstants.PASSWORD_BLANK);
				loginRes.setErrorMsg(password + " - " + MyConstants.PASSWORD_BLANK);
			} else if ((null == securitycode || securitycode.equalsIgnoreCase("undefined")
					|| securitycode.trim().length() == 0)
					&& (null == loginVia || loginVia.trim().length() == 0 || loginVia.equalsIgnoreCase("web"))) {
				loginRes.setDisplayMsg(MyConstants.SECURITY_CODE_BLANK);
				loginRes.setErrorMsg(securitycode + " - " + MyConstants.SECURITY_CODE_BLANK);
			} else {
				HttpSession session = request.getSession(true);
				if (session != null) {
					if (session.isNew() && (null == loginVia || loginVia.equalsIgnoreCase("web"))) {
						session.invalidate();
						loginRes.setDisplayMsg(MyConstants.SESSION_EXPIRED);
						loginRes.setErrorMsg(securitycode + " - " + MyConstants.SESSION_EXPIRED);
					} else if ((null == loginVia || loginVia.equalsIgnoreCase("web"))
							&& (null == session.getAttribute("dns_security_code")
									|| (!session.getAttribute("dns_security_code").equals(securitycode)))) {
						loginRes.setDisplayMsg(MyConstants.MISMATCH_SECURITY_CODE);
						loginRes.setErrorMsg(securitycode + " - " + MyConstants.MISMATCH_SECURITY_CODE);
					} else {

						UserMaster user = new UserMaster();
						user.setUserName(username);
						user.setPassword(password);
						user = mySQLService.getLoginStatus(user);
						// try {
						// if (null != request.getHeader("referer") &&
						// request.getHeader("referer").contains("SysAdmin")) {
						// System.out.println("SysAdmin logged in");
						// }
						// } catch (Exception e) {
						// e.printStackTrace();
						// }

						if (null != user && user.getUserId() > 0) {
							if (user.isTMSLoginStatus()) {
								CommonClass.fixHeaders(request, response);
								loginRes.setStatus(true);
								loginRes.setDisplayMsg(MyConstants.SUCCESS);
								loginRes.setStrResponse(user.getUserName());

								List<String> menuList = new ArrayList<>(1);
								menuList.add(getMenu(user.getTMSUserLevel()));

								loginRes.setResult(menuList);
								session.setAttribute("LoginUser", user);
							} else {
								// Need login permission for TPMS
								loginRes.setErrorMsg(MyConstants.DONT_HAVE_PERMISSION);
								loginRes.setDisplayMsg(MyConstants.DONT_HAVE_PERMISSION);
							}
						} else {
							loginRes.setErrorMsg(MyConstants.INVALID_USERNAME_PASSWORD);
							loginRes.setDisplayMsg(MyConstants.INVALID_USERNAME_PASSWORD);
						}
					}
				} else {
					loginRes.setDisplayMsg(MyConstants.SESSION_EXPIRED);
					loginRes.setErrorMsg(securitycode + " - " + MyConstants.SESSION_EXPIRED);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			loginRes.setStatus(false);
			loginRes.setErrorMsg(e.getMessage());
			loginRes.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
		}
		return loginRes;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public @ResponseBody Response loginPOST(@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam(value = "securitycode", required = false) String securitycode,
			@RequestParam(value = "loginVia", required = false) String loginVia, HttpServletRequest request,
			HttpServletResponse response) {
		Response loginRes = new Response();
		loginRes.setStatus(false);
		try {
			loginRes = processLogin(username, password, securitycode, loginVia, request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loginRes;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody Response loginGet(HttpServletRequest request, HttpServletResponse response) {
		Response loginRes = new Response();
		loginRes.setStatus(false);
		try {
			loginRes = processLogin(request.getParameter("username"), request.getParameter("password"),
					request.getParameter("securitycode"), request.getParameter("loginVia"), request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loginRes;
	}

	private Response processLogout(HttpServletRequest request, HttpServletResponse res) {
		CommonClass.fixInitialHeaders(request, res);
		Response response = new Response();
		response.setStatus(true);
		response.setDisplayMsg("User log out successfully");

		try {
			HttpSession session = request.getSession(false);
			session.invalidate();
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(false);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public @ResponseBody Response logoutGet(HttpServletRequest request, HttpServletResponse res) {
		return processLogout(request, res);
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public @ResponseBody Response logoutPost(HttpServletRequest request, HttpServletResponse res) {
		return processLogout(request, res);
	}

	@RequestMapping(value = "/captcha-image", method = RequestMethod.GET)
	public @ResponseBody Response captchaImage(HttpServletRequest request, HttpServletResponse res) {
		CommonClass.fixHeaders(request, res);

		Response response = new Response();
		response.setStatus(true);
		try {
			/*
			 * Define number characters contains the captcha image, declare
			 * global
			 */
			int iTotalChars = 6;

			/*
			 * Size image iHeight and iWidth, declare globl
			 */
			int iHeight = 30;
			int iWidth = 145;

			/*
			 * font style
			 */
			Font fntStyle1 = new Font("Arial", Font.BOLD, 20);

			/*
			 * Possible random characters in the image
			 */
			Random randChars = new Random();
			String sImageCode = (Long.toString(Math.abs(randChars.nextLong()), 36)).substring(0, iTotalChars);

			/*
			 * BufferedImage is used to create a create new image
			 */
			/*
			 * TYPE_INT_RGB - does not support transpatency, TYPE_INT_ARGB -
			 * support transpatency
			 */
			BufferedImage biImage = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2dImage = (Graphics2D) biImage.getGraphics();

			// Draw background rectangle and noisey filled round rectangles
			g2dImage.fillRect(0, 0, iWidth, iHeight);
			g2dImage.setFont(fntStyle1);
			for (int i = 0; i < iTotalChars; i++) {
				g2dImage.setColor(new Color(randChars.nextInt(255), randChars.nextInt(255), randChars.nextInt(255)));
				if (i % 2 == 0) {
					g2dImage.drawString(sImageCode.substring(i, i + 1), 25 * i, 20);
				} else {
					g2dImage.drawString(sImageCode.substring(i, i + 1), 25 * i, 25);
				}
			}
			/*
			 * create jpeg image and display on the screen
			 */
			String base64EncodedString = encodeToString(biImage, "jpeg");
			response.setStrResponse(base64EncodedString);
			/*
			 * Dispose function is used destory an image object
			 */
			// g2dImage.dispose();
			HttpSession session = request.getSession();
			session.setAttribute("dns_security_code", sImageCode);
		} catch (Exception e) {
			e.printStackTrace();
			response.setDisplayMsg(MyConstants.UNABLE_TO_PROCESS_REQUEST);
			response.setErrorMsg(e.getMessage());
		}
		return response;
	}

	public static String encodeToString(BufferedImage image, String type) {
		String imageString = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			ImageIO.write(image, type, bos);
			byte[] imageBytes = bos.toByteArray();

			BASE64Encoder encoder = new BASE64Encoder();
			imageString = encoder.encode(imageBytes);

			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (Exception e) {
			}
		}
		return imageString;
	}

	private String getMenu(long userLevel) {
		String menu_default = DASHBOARD + FITMENT + SERVICES + INSPECTIONS + TYRES;
		try {
			if (userLevel == 5) {
				// Org Admin
				menu_default = DASHBOARD + FITMENT + SERVICES + INSPECTIONS + TYRES + SENSOR + BLUETOOTH + RFID;
			} else if (userLevel < 5) {
				// Sys Admin
				menu_default = DASHBOARD + FITMENT + SERVICES + INSPECTIONS + TYRES + SENSOR + BLUETOOTH + RFID
						+ ASSIGN_VEHICLES + USER_DETAILS;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			menu_default = menu_default + LOGOUT;
		}
		return menu_default;
	}

	private static final String DASHBOARD = "<li class='nav-item' onclick=\"menuEvent('/dashboard')\" style=\"cursor: pointer;\">"
			+ "<a class='nav-link' ui-sref-active='active'  ui-sref='app.main'>"
			+ "<i class='fa fa-tachometer nav-icon' aria-hidden='true'></i> Dashboard " + "</a>" + "</li>";
	private static final String FITMENT = "<li class='nav-item' onclick=\"menuEvent('/tms-vehicles')\" style=\"cursor: pointer;\">"
			+ "<a class='nav-link' ui-sref-active='active'  ui-sref='app.tms-vehicles'>"
			+ "<i class='fa fa-wrench nav-icon' aria-hidden='true'></i> Fitment Details" + "</a>" + "</li>";
	private static final String SERVICES = "<li class='nav-item' onclick=\"menuEvent('/tmsTyreService')\" style=\"cursor: pointer;\">"
			+ "<a class='nav-link' ui-sref-active='active' ui-sref='app.tmsTyreService'>"
			+ "<i class='fa fa-cogs nav-icon' aria-hidden='true'></i> Service" + "</a>" + "</li>";
	private static final String INSPECTIONS = "<li class=\"nav-item\" onclick=\"menuEvent('/tmsTyreInspection')\" style=\"cursor: pointer;\">"
			+ "<a class=\"nav-link\" ui-sref-active=\"active\" ui-sref=\"app.tmsTyreInspection\" >"
			+ "<img src=\"img/tms/inspection.png\" alt=\"TMS\" class=\"fafont-icon\" "
			+ "width=\"27\" height=\"23\"> Inspection" + "</a>" + "</li>";
	private static final String TYRES = "<li class=\"nav-item\" onclick=\"menuEvent('/tms-tyre')\" style=\"cursor: pointer;\">"
			+ "<a class=\"nav-link\" ui-sref-active=\"active\" ui-sref=\"app.tms-tyre\" >"
			+ "<img src=\"img/tms/tyreIcon.png\" alt=\"TMS\" class=\"fafont-icon\" "
			+ "width=\"20\" height=\"15\"> Tyre Details" + "</a>" + "</li>" + "</li>";
	private static final String SENSOR = "<li class=\"nav-item\" onclick=\"menuEvent('/tms-sensor')\" style=\"cursor: pointer;\">"
			+ "<a class=\"nav-link\" ui-sref-active=\"active\" ui-sref=\"app.tms-sensor\">"
			+ "<img src=\"img/tms/sensorIcon.png\" alt=\"TMS\" class=\"fafont-icon\" width=\"25\" height=\"20\">"
			+ "<span style=\"margin-left: 6px;\"></span> Sensor" + "</a>" + "</li>";
	private static final String BLUETOOTH = "<li class=\"nav-item\" onclick=\"menuEvent('/tms-bluetooth')\" style=\"cursor: pointer;\">"
			+ "<a class=\"nav-link\" ui-sref-active=\"active\" ui-sref=\"app.tms-bluetooth\">"
			+ "<img src=\"img/tms/bluetoothIcon.png\" alt=\"TMS\" class=\"fafont-icon\" width=\"20\" height=\"15\">"
			+ "<span style=\"margin-left: 6px;\"></span> Bluetooth" + "</a>" + "</li>";
	private static final String RFID = "<li class=\"nav-item\" onclick=\"menuEvent('/tms-rfid')\" style=\"cursor: pointer;\">"
			+ "<a class=\"nav-link\" ui-sref-active=\"active\" ui-sref=\"app.tms-rfid\">"
			+ "<img src=\"img/tms/rfidIcon.png\" alt=\"TMS\" class=\"fafont-icon\" width=\"20\" height=\"15\">"
			+ "<span style=\"margin-left: 6px;\"></span> RFID" + "</a>" + "</li>";
	private static final String ASSIGN_VEHICLES = "<li class=\"nav-item\" onclick=\"menuEvent('/tms-assignVehicle')\" style=\"cursor: pointer;\">"
			+ "<a class=\"nav-link\" ui-sref-active=\"active\" ui-sref=\"app.tms-assignVehicle\">"
			+ "<img src=\"img/tms/assignIcon.png\" alt=\"TMS\" class=\"fafont-icon\" width=\"20\" height=\"15\">"
			+ "<span style=\"margin-left: 6px;\"></span> Assign Vehicle" + "</a>" + "</li>";
	private static final String USER_DETAILS = "<li class=\"nav-item\" onclick=\"menuEvent('/tms-vehicleDetails')\" style=\"cursor: pointer;\">"
			+ "<a class=\"nav-link\" ui-sref-active=\"active\"  ui-sref=\"app.tms-vehicleDetails\">"
			+ "<i class=\"fa fa-bus nav-icon\" aria-hidden=\"true\"></i> Vehicle User Details" + "</a>" + "</li>";

	private static final String LOGOUT = "<li class=\"nav-item\" onclick=\"menuEvent('logout')\" style=\"cursor: pointer;\">"
			+ "<a href=\"#\" class=\"nav-link\" ui-sref-active=\"active\" "
			+ "ng-click=\"logoutFun('Auto Logout')\"><i class=\"fa fa-sign-out nav-icon\"></i> Logout </a>" + "</li>";

}
