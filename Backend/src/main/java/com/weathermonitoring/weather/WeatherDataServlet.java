// src/main/java/com/weathermonitoring/servlet/weather/WeatherDataServlet.java
package com.weathermonitoring.weather;

import com.weathermonitoring.dao.WeatherDAO;
import com.weathermonitoring.model.WeatherData;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/api/weather/data/*")
public class WeatherDataServlet extends HttpServlet {
    private WeatherDAO weatherDAO = new WeatherDAO();
    private Gson gson = new Gson();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Date parameter is required");
                return;
            }

            String dateStr = pathInfo.substring(1);
            Date date = dateFormat.parse(dateStr);
            
            WeatherData data = weatherDAO.getWeatherDataByDate(date);
            if (data != null) {
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(data));
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No data found for the specified date");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            WeatherData weatherData = gson.fromJson(request.getReader(), WeatherData.class);
            weatherDAO.createWeatherData(weatherData);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            WeatherData weatherData = gson.fromJson(request.getReader(), WeatherData.class);
            weatherDAO.updateWeatherData(weatherData);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}