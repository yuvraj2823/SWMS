

// src/main/java/com/weathermonitoring/servlet/weather/Last30DaysServlet.java
package com.weathermonitoring.servlet.weather;

import com.weathermonitoring.dao.WeatherDAO;
import com.weathermonitoring.model.WeatherData;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.gson.Gson;
import java.util.List;

@WebServlet("/api/weather/last30days")
public class Last30DaysServlet extends HttpServlet {
    private WeatherDAO weatherDAO = new WeatherDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<WeatherData> dataList = weatherDAO.getLast30DaysData();
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(dataList));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}