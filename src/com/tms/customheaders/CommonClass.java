/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tms.customheaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sri Hari
 */
public class CommonClass {

    public static void fixHeaders(HttpServletRequest request, HttpServletResponse response) {
        try {
            //ssl issue
            //http://stackoverflow.com/questions/2870371/why-is-jquerys-ajax-method-not-sending-my-session-cookie
            String allowHeaders = request.getHeader("Access-Control-Request-Headers");
            //System.out.println("Test : " + request);
            if (allowHeaders == null) {
                allowHeaders = "Content-Type, Accept, Origin, Pragma, X-Requested-With, withCredentials";
            }

            String clientOrigin = request.getHeader("Origin");
            response.setHeader("Access-Control-Allow-Origin", clientOrigin);//"https://placer.in");// "http://localhost:8383");
//            response.addHeader("Access-Control-Allow-Origin", "*");
//            response.addHeader("Access-Control-Allow-Methods", "GET,POST");
            response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS");
            //response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            response.setHeader("Access-Control-Allow-Headers", allowHeaders);
            response.setHeader("Access-Control-Allow-Credentials", "true");
//            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "86400");
            response.setHeader("SET-COOKIE", "JSESSIONID=" + request.getSession().getId());// + "; secure" + "; HttpOnly");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.

            //Reference
            //https://docs.spring.io/spring-security/site/docs/current/reference/html/headers.html
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("X-Frame-Options", "DENY");
            response.setHeader("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains");
            response.setHeader("X-XSS-Protection", "1; mode=block");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void fixInitialHeaders(HttpServletRequest request, HttpServletResponse response) {
        try {
            String allowHeaders = request.getHeader("Access-Control-Request-Headers");
            //System.out.println("Test : " + request);
            if (allowHeaders == null) {
                allowHeaders = "Content-Type, Accept, Origin, X-Requested-With, Pragma, withCredentials";
            }
            String clientOrigin = request.getHeader("Origin");
            response.setHeader("Access-Control-Allow-Origin", clientOrigin);// "http://localhost:8383");
            response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", allowHeaders);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "86400");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            //Reference
            //https://docs.spring.io/spring-security/site/docs/current/reference/html/headers.html
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("X-Frame-Options", "DENY");
            response.setHeader("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains");
            response.setHeader("X-XSS-Protection", "1; mode=block");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
