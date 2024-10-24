import React, { useState, useEffect } from "react";
import './App.css';

const WeatherBox = ({ time, weather }) => (
  <div className="weather-box">
    <h3>{time}</h3>
    <p>{weather}</p>
  </div>
);

const TodayWeather = () => (
  <div className="weather-grid">
    <WeatherBox time="Morning" weather="Sunny, 25°C" />
    <WeatherBox time="Afternoon" weather="Cloudy, 29°C" />
    <WeatherBox time="Evening" weather="Windy, 22°C" />
    <WeatherBox time="Overnight" weather="Clear, 19°C" />
  </div>
);

const YesterdayWeather = () => (
  <div className="weather-grid">
    <WeatherBox time="Morning" weather="Cloudy, 24°C" />
    <WeatherBox time="Afternoon" weather="Rainy, 27°C" />
    <WeatherBox time="Evening" weather="Thunderstorm, 23°C" />
    <WeatherBox time="Overnight" weather="Clear, 18°C" />
  </div>
);

const PastTenDaysWeather = () => {
  const days = [
    "Day 1: Avg. 24°C",
    "Day 2: Avg. 22°C",
    "Day 3: Avg. 26°C",
    "Day 4: Avg. 27°C",
    "Day 5: Avg. 23°C",
    "Day 6: Avg. 25°C",
    "Day 7: Avg. 26°C",
    "Day 8: Avg. 21°C",
    "Day 9: Avg. 28°C",
    "Day 10: Avg. 27°C",
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

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const renderContent = () => {
    switch (activeTab) {
      case "Today":
        return <TodayWeather />;
      case "Yesterday":
        return <YesterdayWeather />;
      case "Past 10 Days":
        return <PastTenDaysWeather />;
      default:
        return <TodayWeather />;
    }
  };

  return (
    <div className="app-container">
      <header className="header">
        <h1>Smart Weather Monitoring System</h1>
        <div className="time-location">
          <div className="time">{currentTime.toLocaleTimeString()}</div>
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
