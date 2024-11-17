
// src/main/java/com/weathermonitoring/model/WeatherData.java
package com.weathermonitoring.model;

import java.util.Date;

public class WeatherData {
    private int id;
    private Date date;
    private double temperature;
    private double humidity;
    private double pressure;
    private int airQuality;
    private double uvRayIndex;

    // Constructor
    public WeatherData(int id, Date date, double temperature, double humidity, 
                      double pressure, int airQuality, double uvRayIndex) {
        this.id = id;
        this.date = date;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.airQuality = airQuality;
        this.uvRayIndex = uvRayIndex;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public double getPressure() { return pressure; }
    public void setPressure(double pressure) { this.pressure = pressure; }
    public int getAirQuality() { return airQuality; }
    public void setAirQuality(int airQuality) { this.airQuality = airQuality; }
    public double getUvRayIndex() { return uvRayIndex; }
    public void setUvRayIndex(double uvRayIndex) { this.uvRayIndex = uvRayIndex; }
}