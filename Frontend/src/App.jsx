import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import WeatherDashboard from './components/WeatherDashboard';
import Signup from './pages/Signup.js';
import Login from './pages/Login.js';
import Profile from './pages/Profile.js';
import PrivateRoute from './components/PrivateRoute.js';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/signup" element={<Signup />} />
        <Route path="/login" element={<Login />} />
        <Route 
          path="/dashboard" 
          element={
            <PrivateRoute>
              <WeatherDashboard />
            </PrivateRoute>
          } 
        />
        <Route 
          path="/profile" 
          element={
            <PrivateRoute>
              <Profile />
            </PrivateRoute>
          } 
        />
        <Route path="/" element={<Login />} />
      </Routes>
    </Router>
  );
}

export default App;