package com.tms.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
    
public class NocacheFilter implements Filter {
    
    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        
    	HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);
        
	    String clientOrigin = httpRequest.getHeader("Origin");
	    httpResponse.setHeader("Access-Control-Allow-Origin", clientOrigin);
	    
	    httpResponse.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS");
	    
	    String allowHeaders = httpResponse.getHeader("Access-Control-Request-Headers");
        if (allowHeaders == null) {
            allowHeaders = "Content-Type, Accept, Origin, Pragma, X-Requested-With, withCredentials";
        }
        httpResponse.setHeader("Access-Control-Allow-Headers", allowHeaders);
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        
        httpResponse.setHeader("Access-Control-Max-Age", "86400");
        httpResponse.setHeader("SET-COOKIE", "JSESSIONID=" + httpRequest.getSession().getId());// + "; secure" + "; HttpOnly");
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains");
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        chain.doFilter(request, response);
    }
    
    public void destroy() {}
    public void init(FilterConfig fConfig) throws ServletException {}
}
