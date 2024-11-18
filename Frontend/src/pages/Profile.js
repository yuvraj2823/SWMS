import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Profile.css';

function Profile() {
  const [userData, setUserData] = useState({
    username: '',
    email: ''
  });
  const [isEditing, setIsEditing] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProfile = async () => {
      const token = localStorage.getItem('token');
      
      if (!token) {
        navigate('/login');
        return;
      }

      try {
        const response = await fetch('/api/profile', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        const data = await response.json();

        if (data.success) {
          setUserData({
            username: data.user.username,
            email: data.user.email
          });
        } else {
          setError(data.message || 'Failed to fetch profile');
          navigate('/login');
        }
      } catch (err) {
        setError('Network error. Please try again.');
        navigate('/login');
      }
    };

    fetchProfile();
  }, [navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUserData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleSave = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('token');

    try {
      const response = await fetch('/api/profile', {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
      });

      const data = await response.json();

      if (data.success) {
        setIsEditing(false);
      } else {
        setError(data.message || 'Update failed');
      }
    } catch (err) {
      setError('Network error. Please try again.');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <div className="profile-container">
      <div className="profile-card">
        <h2>User Profile</h2>
        
        {error && <div className="error-message">{error}</div>}

        {!isEditing ? (
          <div className="profile-view">
            <div className="profile-field">
              <label>Username</label>
              <p>{userData.username}</p>
            </div>
            <div className="profile-field">
              <label>Email</label>
              <p>{userData.email}</p>
            </div>
            <div className="profile-actions">
              <button 
                className="edit-button" 
                onClick={() => setIsEditing(true)}
              >
                Edit Profile
              </button>
              <button 
                className="logout-button" 
                onClick={handleLogout}
              >
                Logout
              </button>
            </div>
          </div>
        ) : (
          <form onSubmit={handleSave} className="profile-edit">
            <div className="form-group">
              <label>Username</label>
              <input
                type="text"
                name="username"
                value={userData.username}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                name="email"
                value={userData.email}
                onChange={handleChange}
                required
              />
            </div>
            <div className="profile-actions">
              <button type="submit" className="save-button">Save Changes</button>
              <button 
                type="button" 
                className="cancel-button" 
                onClick={() => setIsEditing(false)}
              >
                Cancel
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}

export default Profile;