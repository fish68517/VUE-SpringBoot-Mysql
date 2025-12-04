# Shenyang Music Festival Management System - Backend

Spring Boot 3.x backend for the Shenyang Music Festival Management System.

## Project Structure

```
src/main/java/com/shenyang/musicfestival/
├── config/              # Configuration classes
├── controller/          # REST API controllers
├── dto/                 # Data Transfer Objects
├── entity/              # JPA entities
├── exception/           # Exception handlers
├── repository/          # Data access layer (JPA repositories)
├── service/             # Business logic layer
│   └── impl/            # Service implementations
└── util/                # Utility classes

src/main/resources/
├── application.yml      # Application configuration
└── db/                  # Database scripts
```

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

## Setup

### 1. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE shenyang_music_festival CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Configure Database Connection

Edit `src/main/resources/application.yml` and update the database credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shenyang_music_festival
    username: root
    password: your_password
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/api/swagger-ui.html
```

## Key Features

- **User Authentication**: JWT-based authentication
- **User Management**: Registration, login, profile management
- **Real Name Verification**: Identity verification system
- **Ticket Management**: Purchase and manage event tickets
- **Product Management**: Manage merchandise and products
- **Points System**: Accumulate and redeem points
- **Check-in Tasks**: Location-based check-in system
- **Admin Dashboard**: Comprehensive management interface

## Configuration

### JWT Configuration

Configure JWT settings in `application.yml`:

```yaml
jwt:
  secret: your-secret-key
  expiration: 86400000  # 24 hours
  refresh-expiration: 604800000  # 7 days
```

### Database Configuration

Configure database connection in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shenyang_music_festival
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
```

## Development

### Running Tests

```bash
mvn test
```

### Building for Production

```bash
mvn clean package -DskipTests
```

## Logging

Logs are configured in `application.yml` and output to:
- Console: INFO level
- File: `logs/application.log`

## Dependencies

- Spring Boot 3.2.0
- Spring Data JPA
- MySQL Connector
- JWT (jjwt)
- Lombok
- Springdoc OpenAPI (Swagger)
- iText PDF
- ZXing (QR Code)

## License

This project is part of the Shenyang Music Festival Management System.
