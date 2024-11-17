// src/main/java/com/weathermonitoring/servlet/auth/LoginServlet.java
package com.weathermonitoring.servlet.auth;

import com.weathermonitoring.dao.UserDAO;
import com.weathermonitoring.model.User;
import com.weathermonitoring.util.JWTUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            LoginRequest loginRequest = gson.fromJson(request.getReader(), LoginRequest.class);
            User user = userDAO.getUserByUsername(loginRequest.getUsername());

            if (user != null && BCrypt.checkpw(loginRequest.getPassword(), user.getPasswordHash())) {
                String token = JWTUtil.generateToken(user.getUsername());
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(new LoginResponse(token)));
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() { return username; }
        public String getPassword() { return password; }
    }

    private static class LoginResponse {
        private String token;
        
        public LoginResponse(String token) { this.token = token; }
    }
}
