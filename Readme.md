File Structure for Backend:

Backend/
├── src/
│   ├── com/
│   │   ├── weather/
│   │   │   ├── dao/
│   │   │   │   ├── UserDAO.java             # Data Access Object for User CRUD operations
│   │   │   │   ├── WeatherDataDAO.java      # Data Access Object for Weather data operations
│   │   │   ├── model/
│   │   │   │   ├── User.java                # Model class representing User
│   │   │   │   ├── WeatherData.java         # Model class representing Weather data
│   │   │   ├── util/
│   │   │   │   ├── DatabaseConnection.java  # Utility for managing DB connection
│   │   │   ├── Main.java                    # Main application file to run backend logic
├── lib/
│   ├── mysql-connector-java-8.x.x.jar       # MySQL connector JAR for database connection
├── config/
│   ├── db.properties                        # Database properties for connection
├── sql/
│   ├── WeatherMonitoringSystem.sql          # SQL script for database schema and initial data
