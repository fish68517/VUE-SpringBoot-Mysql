# Little Shark Fitness Management System

A comprehensive web-based fitness management platform that enables users to access fitness resources, follow personalized training plans, participate in community activities, and track their fitness progress.

## Overview

The Little Shark Fitness Management System is designed to serve three distinct user roles:
- **Regular Users**: Access fitness resources, follow training plans, track progress, and participate in community
- **Coaches**: Create training plans, manage students, publish fitness content, and view student analytics
- **Administrators**: Manage users, moderate content, and oversee platform operations

## Features

### For Regular Users
- User registration and profile management
- Browse and collect fitness resources (videos, articles, documents)
- Follow personalized training plans from coaches
- Daily check-in tracking with streak calculations
- Diet record logging and daily summaries
- Community participation (posts, likes, comments)
- Search across resources, posts, and coaches

### For Coaches
- Manage student roster
- Create and assign personalized training plans
- Publish fitness resources and articles
- View student analytics and progress
- Track student check-ins and diet records

### For Administrators
- User and coach account management
- Fitness resource management
- Content moderation and approval workflow
- System statistics and monitoring

## Technology Stack

### Backend
- **Framework**: Spring Boot 2.7+
- **Database**: MySQL 8.0
- **Build Tool**: Maven
- **Language**: Java

### Frontend
- **Framework**: Vue 3 with Composition API
- **State Management**: Pinia
- **Routing**: Vue Router 4
- **HTTP Client**: Axios
- **UI Framework**: Element Plus

## Project Structure

```
.
├── SpringBoot/                 # Backend application
│   ├── src/
│   │   ├── main/java/com/sharkfitness/
│   │   │   ├── config/        # Configuration classes
│   │   │   ├── controller/    # REST API endpoints
│   │   │   ├── service/       # Business logic
│   │   │   ├── repository/    # Data access layer
│   │   │   ├── entity/        # JPA entities
│   │   │   ├── dto/           # Data transfer objects
│   │   │   ├── vo/            # View objects
│   │   │   ├── util/          # Utility classes
│   │   │   └── exception/     # Exception handling
│   │   └── resources/
│   │       └── application.properties
│   └── pom.xml
├── VUE/                        # Frontend application
│   ├── src/
│   │   ├── api/               # API client modules
│   │   ├── views/             # Page components
│   │   ├── components/        # Reusable components
│   │   ├── router/            # Route definitions
│   │   ├── store/             # Pinia stores
│   │   ├── utils/             # Utility functions
│   │   ├── assets/            # Static assets
│   │   └── App.vue
│   ├── package.json
│   └── vite.config.js
└── doc/                        # Documentation
```

## Getting Started

### Prerequisites

- Java 8 or higher
- Node.js 14+ and npm
- MySQL 8.0
- Git

### Backend Setup

1. **Navigate to backend directory**
   ```bash
   cd SpringBoot
   ```

2. **Configure database connection**
   - Edit `src/main/resources/application.properties`
   - Update database URL, username, and password:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/sharkfitness?useUnicode=true&characterEncoding=utf8mb4
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Build the application**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd VUE
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure API endpoint**
   - Edit `.env.development`:
   ```
   VITE_API_BASE_URL=http://localhost:8080
   ```

4. **Start development server**
   ```bash
   npm run dev
   ```

   The frontend will be available at `http://localhost:5173`

5. **Build for production**
   ```bash
   npm run build
   ```

## Default Credentials

For testing purposes, the system includes a default admin account:
- **Username**: admin
- **Password**: admin123
- **Role**: Administrator

## API Documentation

For detailed API endpoint documentation, see [API.md](./API.md)

## User Guides

- [Regular User Guide](./docs/USER_GUIDE.md) - How to use the system as a regular user
- [Coach Manual](./docs/COACH_MANUAL.md) - Guide for coaches managing students and content
- [Administrator Guide](./docs/ADMIN_GUIDE.md) - System administration and moderation

## Database Schema

The system uses the following main entities:
- **User**: User accounts with role-based access
- **FitnessResource**: Educational content (videos, articles, documents)
- **TrainingPlan**: Personalized fitness programs
- **Dynamic**: Community posts
- **Comment**: Comments on posts
- **CheckIn**: Daily activity tracking
- **DietRecord**: Meal and nutrition logging
- **Collection**: User's saved resources
- **Like**: Post likes
- **CoachStudent**: Coach-student relationships

## Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/sharkfitness
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# File Upload
file.upload.path=/uploads
file.max-size.image=5242880
file.max-size.video=104857600

# CORS
cors.allowed-origins=http://localhost:5173
```

## Troubleshooting

### Backend Issues

**Database Connection Error**
- Verify MySQL is running
- Check database credentials in application.properties
- Ensure database `sharkfitness` exists

**Port Already in Use**
- Change `server.port` in application.properties
- Or kill the process using port 8080

### Frontend Issues

**API Connection Error**
- Verify backend is running on port 8080
- Check `VITE_API_BASE_URL` in .env.development
- Check browser console for CORS errors

**Module Not Found**
- Run `npm install` to ensure all dependencies are installed
- Clear node_modules and reinstall: `rm -rf node_modules && npm install`

## Support

For issues or questions:
1. Check the relevant user guide (USER_GUIDE.md, COACH_MANUAL.md, ADMIN_GUIDE.md)
2. Review API documentation in API.md
3. Check application logs for error messages

## License

This project is part of an academic fitness management system.

## Version

Current Version: 1.0.0
Last Updated: November 2025
