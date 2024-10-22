import './App.css';

// App.js (React)
import React from 'react';
import './App.css'; // Import the corresponding CSS file for styling

function App() {
  return (
    <div className="app-container">
      <header className="header">
        <h1>Smart Weather Monitoring System</h1>
        <div className="time-location">
          <span className="time">Time - 23:00</span>
          <button className="location-button">Location</button>
        </div>
      </header>

      <main className="main-content">
        <section className="weather-info">
          <h2>Weather Information</h2>
          <p>Real time weather update for Neemrana</p>

          <div className="weather-buttons">
            <button>Today</button>
            <button>Yesterday</button>
            <button>Past 10 Days</button>
          </div>

          <div className="weather-grid">
            <div className="weather-box">Morning</div>
            <div className="weather-box">Afternoon</div>
            <div className="weather-box">Evening</div>
            <div className="weather-box">Overnight</div>
          </div>
        </section>

        <footer className="footer">
          <button>About</button>
          <button>XYZ</button>
          <div className="social-media">
            <i className="fa fa-instagram"></i>
            <i className="fa fa-twitter"></i>
          </div>
        </footer>
      </main>
    </div>
  );
}

export default App;

