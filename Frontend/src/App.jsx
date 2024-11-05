import React, { useState, useEffect } from "react";
import './App.css';

const WeatherDetail = ({ detail, value }) => (
  <div className="weather-detail">
    <h3>{detail}</h3>
    <p>{value}</p>
  </div>
);

const TimeWeatherDetails = ({ time }) => {
  const weatherData = {
    temperature: `${time} Temp: 25°C`,
    humidity: `${time} Humidity: 60%`,
    airPressure: `${time} Air Pressure: 1013 hPa`,
    airQualityIndex: `${time} AQI: 45`,
    uvRays: `${time} UV Rays: Moderate`
  };

  return (
    <div className="weather-grid">
      <WeatherDetail detail="Temperature" value={weatherData.temperature} />
      <WeatherDetail detail="Humidity" value={weatherData.humidity} />
      <WeatherDetail detail="Air Pressure" value={weatherData.airPressure} />
      <WeatherDetail detail="Air Quality Index" value={weatherData.airQualityIndex} />
      <WeatherDetail detail="UV Rays" value={weatherData.uvRays} />
    </div>
  );
};

const PastTenDaysWeather = () => {
  const days = [
    "Day 1: Avg Temp 24°C, Humidity 55%, AQI 50",
    "Day 2: Avg Temp 22°C, Humidity 60%, AQI 45",
    "Day 3: Avg Temp 26°C, Humidity 58%, AQI 52",
    "Day 4: Avg Temp 27°C, Humidity 57%, AQI 47",
    "Day 5: Avg Temp 23°C, Humidity 59%, AQI 49",
    "Day 6: Avg Temp 25°C, Humidity 56%, AQI 53",
    "Day 7: Avg Temp 26°C, Humidity 55%, AQI 48",
    "Day 8: Avg Temp 21°C, Humidity 63%, AQI 42",
    "Day 9: Avg Temp 28°C, Humidity 54%, AQI 51",
    "Day 10: Avg Temp 27°C, Humidity 56%, AQI 50"
  ];

  return (
    <div className="weather-grid-10-days">
      {days.map((day, index) => (
        <div key={index} className="weather-box">
          <p>{day}</p>
        </div>
      ))}
    </div>
  );
};

const App = () => {
  const [currentTime, setCurrentTime] = useState(new Date());
  const [activeTab, setActiveTab] = useState("Today");
  const [activeTime, setActiveTime] = useState("Morning");

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const renderContent = () => {
    switch (activeTab) {
      case "Today":
      case "Yesterday":
        return (
          <div>
            <div className="time-weather-grid">
              <div
                className={`weather-box ${activeTime === "Morning" ? "active" : ""}`}
                onClick={() => setActiveTime("Morning")}
              >
                Morning
              </div>
              <div
                className={`weather-box ${activeTime === "Afternoon" ? "active" : ""}`}
                onClick={() => setActiveTime("Afternoon")}
              >
                Afternoon
              </div>
              <div
                className={`weather-box ${activeTime === "Evening" ? "active" : ""}`}
                onClick={() => setActiveTime("Evening")}
              >
                Evening
              </div>
              <div
                className={`weather-box ${activeTime === "Overnight" ? "active" : ""}`}
                onClick={() => setActiveTime("Overnight")}
              >
                Overnight
              </div>
            </div>
            <TimeWeatherDetails time={activeTime} />
          </div>
        );
      case "Past 10 Days":
        return <PastTenDaysWeather />;
      default:
        return null;
    }
  };

  return (
    <div className="app-container">
      <header className="header">
        <h1>Smart Weather Monitoring System</h1>
        <div className="time-location">
          <div className="time">Time - {currentTime.toLocaleTimeString()}</div>
          <button className="location-button">Location: Neemrana</button>
        </div>
      </header>

      <div className="main-content">
        <div className="weather-info">
          <h2>Weather Information</h2>
          <p>Real-time weather update for Neemrana</p>
          <div className="weather-buttons">
            <button
              className={activeTab === "Today" ? "active" : ""}
              onClick={() => setActiveTab("Today")}
            >
              Today
            </button>
            <button
              className={activeTab === "Yesterday" ? "active" : ""}
              onClick={() => setActiveTab("Yesterday")}
            >
              Yesterday
            </button>
            <button
              className={activeTab === "Past 10 Days" ? "active" : ""}
              onClick={() => setActiveTab("Past 10 Days")}
            >
              Past 10 Days
            </button>
          </div>
          {renderContent()}
        </div>
      </div>

      <footer className="footer">
        <div>
          <button>About</button>
          <button>XYZ</button>
        </div>
        <div className="social-media">
          <a href="https://www.instagram.com" target="_blank" rel="noopener noreferrer">
            <i className="fab fa-instagram"></i>
          </a>
          <a href="https://www.twitter.com" target="_blank" rel="noopener noreferrer">
            <i className="fab fa-twitter"></i>
          </a>
        </div>
      </footer>
    </div>
  );
};

export default App;
