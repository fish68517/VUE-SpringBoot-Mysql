# Online Submission System - Backend

Spring Boot 3.x backend for the online manuscript submission and review system.

## Project Structure

```
src/main/java/com/submission/
├── controller/         # REST API Controllers
├── service/            # Business Logic Layer
├── mapper/             # MyBatis Data Access Layer
├── entity/             # Database Entity Classes
├── dto/                # Data Transfer Objects
├── vo/                 # View Objects
├── config/             # Configuration Classes
├── exception/          # Custom Exception Classes
└── utils/              # Utility Classes
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

## Configuration

### Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE submission_system;
```

2. Update `src/main/resources/application.yml` with your database credentials:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/submission_system
    username: your_username
    password: your_password
```

3. Run the schema.sql to create tables:
```sql
source src/main/resources/db/schema.sql;
```

## Building and Running

### Build the project
```bash
mvn clean package
```

### Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

### Run tests
```bash
mvn test
```

## API Documentation

API endpoints are available at `http://localhost:8080/api`

## Dependencies

- Spring Boot 3.2.0
- Spring Web
- Spring Data JPA
- MyBatis
- MySQL Connector
- Lombok
- Jackson (JSON processing)
- Validation

## File Upload

Uploaded files are stored in the `image/` directory at the project root.

Static resources are accessible via `/image/**` endpoint.

## Development

### IDE Setup

For IntelliJ IDEA:
1. Open the project
2. Maven should automatically download dependencies
3. Configure Java SDK to version 17

### Code Style

- Use Lombok annotations to reduce boilerplate
- Follow Spring Boot conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods

## Troubleshooting

### Database Connection Issues
- Ensure MySQL is running
- Check database credentials in application.yml
- Verify database exists

### Port Already in Use
- Change the port in application.yml: `server.port: 8081`

### Maven Build Issues
- Clear Maven cache: `mvn clean`
- Update dependencies: `mvn dependency:resolve`
