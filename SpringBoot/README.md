# Shark Fitness Management System - Backend

## Project Structure

```
com.sharkfitness
├── config/          # Configuration classes (CORS, file upload, etc.)
├── controller/      # REST API controllers
├── service/         # Business logic layer
├── repository/      # Data access layer (JPA repositories)
├── entity/          # JPA entities
├── dto/             # Data Transfer Objects (request payloads)
├── vo/              # Value Objects (response payloads)
├── util/            # Utility classes
└── exception/       # Custom exceptions and global exception handler
```

## Technology Stack

- **Spring Boot 3.2.6**
- **Spring Data JPA** - Database access
- **Spring Web** - REST API
- **MySQL 8.0** - Database
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management

## Configuration

The application is configured in `src/main/resources/application.properties`:

- **Server Port**: 8080
- **Database**: MySQL (shark_fitness)
- **File Upload**: Max 100MB for videos, 5MB for images
- **Character Encoding**: UTF8MB4

## Build and Run

### Using Maven Wrapper (Recommended)

```bash
# Clean and compile
.\mvnw.cmd clean compile

# Run the application
.\mvnw.cmd spring-boot:run

# Package as JAR
.\mvnw.cmd clean package
```

### Using Maven (if installed)

```bash
# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run

# Package as JAR
mvn clean package
```

## Database Setup

Create a MySQL database named `shark_fitness`:

```sql
CREATE DATABASE shark_fitness CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

The application will automatically create tables on startup using JPA's `ddl-auto=update` setting.

## Next Steps

1. Implement core infrastructure components (API response wrapper, exception handling)
2. Create entity classes
3. Implement authentication and authorization
4. Build REST API endpoints
