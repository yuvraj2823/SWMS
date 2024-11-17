
package com.weathermonitoring.dao;

import com.weathermonitoring.model.WeatherData;
import com.weathermonitoring.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WeatherDAO {
    // Create weather data
    public void createWeatherData(WeatherData data) throws SQLException {
        String sql = "INSERT INTO weather_data (date, temperature, humidity, pressure, air_quality, uv_ray_index) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(data.getDate().getTime()));
            pstmt.setDouble(2, data.getTemperature());
            pstmt.setDouble(3, data.getHumidity());
            pstmt.setDouble(4, data.getPressure());
            pstmt.setInt(5, data.getAirQuality());
            pstmt.setDouble(6, data.getUvRayIndex());
            pstmt.executeUpdate();
        }
    }

    // Get weather data by date
    public WeatherData getWeatherDataByDate(Date date) throws SQLException {
        String sql = "SELECT * FROM weather_data WHERE date = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new WeatherData(
                    rs.getInt("id"),
                    rs.getDate("date"),
                    rs.getDouble("temperature"),
                    rs.getDouble("humidity"),
                    rs.getDouble("pressure"),
                    rs.getInt("air_quality"),
                    rs.getDouble("uv_ray_index")
                );
            }
        }
        return null;
    }

    // Get last 30 days weather data
    public List<WeatherData> getLast30DaysData() throws SQLException {
        String sql = "SELECT * FROM weather_data WHERE date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) ORDER BY date DESC";
        List<WeatherData> dataList = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                dataList.add(new WeatherData(
                    rs.getInt("id"),
                    rs.getDate("date"),
                    rs.getDouble("temperature"),
                    rs.getDouble("humidity"),
                    rs.getDouble("pressure"),
                    rs.getInt("air_quality"),
                    rs.getDouble("uv_ray_index")
                ));
            }
        }
        return dataList;
    }

    // Update weather data
    public void updateWeatherData(WeatherData data) throws SQLException {
        String sql = "UPDATE weather_data SET temperature = ?, humidity = ?, pressure = ?, " +
                    "air_quality = ?, uv_ray_index = ? WHERE date = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, data.getTemperature());
            pstmt.setDouble(2, data.getHumidity());
            pstmt.setDouble(3, data.getPressure());
            pstmt.setInt(4, data.getAirQuality());
            pstmt.setDouble(5, data.getUvRayIndex());
            pstmt.setDate(6, new java.sql.Date(data.getDate().getTime()));
            pstmt.executeUpdate();
        }
    }
}