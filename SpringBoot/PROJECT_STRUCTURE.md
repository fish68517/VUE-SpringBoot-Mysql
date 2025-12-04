# Spring Boot Project Structure

## Overview

This document describes the complete Spring Boot 3.x project structure created for the Shenyang Music Festival Management System backend.

## Directory Structure

```
SpringBoot/
├── pom.xml                                          # Maven configuration with all dependencies
├── README.md                                        # Project documentation
├── .gitignore                                       # Git ignore rules
├── src/
│   └── main/
│       ├── java/com/shenyang/musicfestival/
│       │   ├── MusicFestivalApplication.java       # Main Spring Boot application class
│       │   ├── config/
│       │   │   └── SecurityConfig.java             # Security configuration (BCrypt password encoder)
│       │   ├── controller/
│       │   │   └── UserController.java             # REST API endpoints for user operations
│       │   ├── dto/
│       │   │   └── UserDTO.java                    # Data Transfer Object for User
│       │   ├── entity/
│       │   │   ├── BaseEntity.java                 # Base entity with common fields (id, createdAt, updatedAt)
│       │   │   └── User.java                       # User entity with JPA annotations
│       │   ├── exception/
│       │   │   └── GlobalExceptionHandler.java     # Global exception handling
│       │   ├── repository/
│       │   │   └── UserRepository.java             # JPA repository for User entity
│       │   ├── service/
│       │   │   ├── UserService.java                # Service interface for user operations
│       │   │   └── impl/
│       │   │       └── UserServiceImpl.java         # Service implementation
│       │   └── util/
│       │       ├── ApiResponse.java                # Unified API response wrapper
│       │       └── JwtUtil.java                    # JWT token generation and validation
│       └── resources/
│           └── application.yml                     # Application configuration
└── target/                                          # Build output (generated)
```

## Key Components

### 1. Configuration Layer (config/)
- **SecurityConfig.java**: Provides BCryptPasswordEncoder bean for password encryption

### 2. Entity Layer (entity/)
- **BaseEntity.java**: Abstract base class with common fields (id, createdAt, updatedAt)
- **User.java**: User entity with fields for authentication, profile, and real-name verification

### 3. Repository Layer (repository/)
- **UserRepository.java**: JPA repository with custom query methods for user lookup

### 4. Service Layer (service/)
- **UserService.java**: Interface defining user business operations
- **UserServiceImpl.java**: Implementation with password encoding, user registration, and profile management

### 5. Controller Layer (controller/)
- **UserController.java**: REST endpoints for user operations (/users/profile, /users/real-name, etc.)

### 6. DTO Layer (dto/)
- **UserDTO.java**: Data transfer object for user information exchange

### 7. Exception Handling (exception/)
- **GlobalExceptionHandler.java**: Centralized exception handling for all controllers

### 8. Utility Layer (util/)
- **JwtUtil.java**: JWT token generation, validation, and claim extraction
- **ApiResponse.java**: Unified response wrapper for all API responses

## Maven Dependencies

### Core Framework
- Spring Boot 3.2.0
- Spring Web MVC
- Spring Data JPA

### Database
- MySQL Connector 8.0.33

### Security & Authentication
- JWT (jjwt) 0.12.3
- Spring Security Crypto

### Utilities
- Lombok
- Jackson (JSON processing)
- Apache Commons Lang3

### PDF & QR Code
- iText PDF 5.5.13.3
- ZXing (QR Code) 3.5.1

### API Documentation
- Springdoc OpenAPI 2.0.2 (Swagger)

### Testing
- JUnit 5
- Mockito
- Spring Boot Test

## Configuration (application.yml)

### Database
- MySQL 8 connection with UTF-8 support
- JPA/Hibernate auto-update schema
- Connection pooling and batch processing

### Server
- Port: 8080
- Context path: /api
- Gzip compression enabled

### JWT
- Secret key for token signing
- Access token expiration: 24 hours
- Refresh token expiration: 7 days

### Logging
- Root level: INFO
- Application level: DEBUG
- File output: logs/application.log

### File Upload
- Max size: 10MB
- Allowed extensions: jpg, jpeg, png, gif, pdf, doc, docx

## Layered Architecture

```
┌─────────────────────────────────────────┐
│         REST API Layer                  │
│      (UserController)                   │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│      Service Layer                      │
│   (UserService/UserServiceImpl)          │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│    Repository Layer                     │
│    (UserRepository)                     │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│    Database Layer                       │
│    (MySQL 8)                            │
└─────────────────────────────────────────┘
```

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

### Access
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html

## Next Steps

This foundation is ready for:
1. Adding more entities (Ticket, Product, Order, etc.)
2. Implementing additional services and controllers
3. Creating database migration scripts
4. Adding authentication interceptors
5. Implementing business logic for all features
