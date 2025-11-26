# Travel Memory System - Backend (SpringBoot)

## Project Structure

```
SpringBoot/
├── src/
│   ├── main/
│   │   ├── java/com/travelMemory/
│   │   │   ├── TravelMemoryApplication.java (Main entry point)
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java (Spring Security configuration)
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java (JWT token generation and validation)
│   │   │   │   ├── JwtAuthenticationFilter.java (JWT authentication filter)
│   │   │   │   └── JwtAuthenticationEntryPoint.java (Unauthorized entry point)
│   │   │   ├── controller/ (REST API controllers)
│   │   │   ├── service/ (Business logic layer)
│   │   │   ├── repository/ (Data access layer)
│   │   │   ├── entity/ (JPA entities)
│   │   │   ├── dto/ (Data transfer objects)
│   │   │   ├── common/
│   │   │   │   ├── ApiResponse.java (Unified API response format)
│   │   │   │   ├── GlobalExceptionHandler.java (Global exception handling)
│   │   │   │   └── exception/
│   │   │   │       ├── BusinessException.java
│   │   │   │       └── ValidationException.java
│   │   │   └── utils/ (Utility classes)
│   │   └── resources/
│   │       └── application.properties (Application configuration)
│   └── test/ (Unit and integration tests)
├── pom.xml (Maven configuration)
└── .gitignore

```

## Configuration

### Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE travel_memory_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Update `application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/travel_memory_system
spring.datasource.username=root
spring.datasource.password=your_password
```

### JWT Configuration

Update JWT secret in `application.properties`:
```properties
jwt.secret=your-secret-key-change-this-in-production
jwt.expiration=86400000
```

## Building and Running

### Build
```bash
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

Swagger UI is available at: `http://localhost:8080/swagger-ui.html`

## Dependencies

- Spring Boot 3.1.5
- Spring Security
- Spring Data JPA
- MySQL Connector
- JWT (JJWT)
- Lombok
- Validation
- OpenAPI/Swagger
