# Backend Directory Structure

## Source Code (`/src`)
### `com.weather` Package
#### Data Access Objects (`dao`)
- `UserDAO.java` - Data Access Object for User CRUD operations
- `WeatherDataDAO.java` - Data Access Object for Weather data operations

#### Models (`model`)
- `User.java` - Model class representing User
- `WeatherData.java` - Model class representing Weather data

#### Utilities (`util`)
- `DatabaseConnection.java` - Utility for managing DB connection

#### Application Entry
- `Main.java` - Main application file to run backend logic

## Libraries (`/lib`)
- `mysql-connector-java-8.x.x.jar` - MySQL connector JAR for database connection

## Configuration (`/config`)
- `db.properties` - Database properties for connection

## SQL Scripts (`/sql`)
- `WeatherMonitoringSystem.sql` - SQL script for database schema and initial data
