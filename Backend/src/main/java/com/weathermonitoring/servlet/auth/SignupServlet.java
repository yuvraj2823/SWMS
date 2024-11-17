// src/main/java/com/weathermonitoring/servlet/auth/SignupServlet.java
package com.weathermonitoring.servlet.auth;

import com.weathermonitoring.dao.UserDAO;
import com.weathermonitoring.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/api/auth/signup")
public class SignupServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            SignupRequest signupRequest = gson.fromJson(request.getReader(), SignupRequest.class);

            // Validate input
            if (!isValidInput(signupRequest)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input data");
                return;
            }

            // Check if username already exists
            if (userDAO.getUserByUsername(signupRequest.getUsername()) != null) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Username already exists");
                return;
            }

            // Hash password
            String hashedPassword = BCrypt.hashpw(signupRequest.getPassword(), BCrypt.gensalt());

            // Create new user
            User newUser = new User(
                0, // ID will be auto-generated
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                hashedPassword,
                "USER" // Default role
            );

            userDAO.createUser(newUser);

            // Send success response
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(new SignupResponse("User created successfully")));

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private boolean isValidInput(SignupRequest request) {
        return request.username != null && !request.username.trim().isEmpty() &&
               request.email != null && request.email.matches("^[A-Za-z0-9+_.-]+@(.+)$") &&
               request.password != null && request.password.length() >= 6;
    }

    private static class SignupRequest {
        private String username;
        private String email;
        private String password;

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }

    private static class SignupResponse {
        private String message;
        
        public SignupResponse(String message) {
            this.message = message;
        }
    }
}