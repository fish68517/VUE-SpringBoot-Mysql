# Campus Activity Crowdfunding Platform - Backend

A comprehensive Spring Boot 3.x backend for the Campus Activity Crowdfunding Platform.

## Project Structure

```
SpringBoot/
├── src/
│   ├── main/
│   │   ├── java/com/campus/
│   │   │   ├── config/              # Configuration classes
│   │   │   ├── controller/          # REST API controllers
│   │   │   ├── service/             # Business logic services
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   ├── util/                # Utility classes
│   │   │   └── CampusActivityApplication.java  # Main entry point
│   │   └── resources/
│   │       ├── application.yml      # Application configuration
│   │       └── db/
│   │           └── migration/       # Database migration scripts
│   └── test/
│       └── java/com/campus/         # Unit and integration tests
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **MySQL 8.0**
- **JWT Authentication**
- **Swagger/OpenAPI 3.0**
- **Lombok**
- **Maven**

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

## Setup Instructions

### 1. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE campus_activity CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configuration

Update `src/main/resources/application.yml` with your database credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_activity
    username: your_username
    password: your_password
```

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

## Project Modules

### Config
- `GlobalExceptionHandler`: Centralized exception handling
- `SecurityConfig`: Spring Security configuration
- `WebConfig`: Web interceptor configuration
- `OpenApiConfig`: Swagger/OpenAPI configuration

### Entity
- `User`: User information
- `Activity`: Campus activity
- `Registration`: Activity registration
- `CrowdfundingSupport`: Crowdfunding support
- `Feedback`: Activity feedback
- `AuditLog`: Audit logs
- `FundProof`: Fund usage proof

### Repository
- `UserRepository`: User data access
- `ActivityRepository`: Activity data access
- `RegistrationRepository`: Registration data access
- `CrowdfundingSupportRepository`: Crowdfunding data access
- `FeedbackRepository`: Feedback data access
- `AuditLogRepository`: Audit log data access
- `FundProofRepository`: Fund proof data access

### Utility
- `JwtUtil`: JWT token generation and validation
- `ValidationUtil`: Common validation methods

## Development

### Running Tests

```bash
mvn test
```

### Building for Production

```bash
mvn clean package -DskipTests
```

## Security

- Passwords are encrypted using BCrypt
- JWT tokens are used for authentication
- CSRF protection is enabled
- SQL injection prevention through parameterized queries

## Logging

Logs are configured in `application.yml` and written to `logs/application.log`

## License

Apache License 2.0
