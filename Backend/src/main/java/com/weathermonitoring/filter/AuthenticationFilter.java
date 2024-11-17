// src/main/java/com/weathermonitoring/filter/AuthenticationFilter.java
package com.weathermonitoring.filter;

import com.weathermonitoring.util.JWTUtil;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip authentication for login and signup endpoints
        if (httpRequest.getRequestURI().contains("/api/auth/")) {
            chain.doFilter(request, response);
            return;
        }
        
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = JWTUtil.validateTokenAndGetUsername(token);
            
            if (username != null) {
                chain.doFilter(request, response);
                return;
            }
        }
        
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token");
    }
    
    @Override
    public void destroy() {}
}