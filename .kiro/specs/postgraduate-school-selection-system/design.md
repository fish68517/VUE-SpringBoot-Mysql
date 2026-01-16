# Design Document: Postgraduate School Selection System

## Overview

The Postgraduate School Selection System is a full-stack web application built with Vue.js (frontend), Spring Boot (backend), and MySQL (database). The system follows a layered architecture with clear separation of concerns: presentation layer (Vue components), API layer (Spring Boot REST controllers), business logic layer (services), data access layer (repositories), and persistence layer (MySQL database).

The system implements Role-Based Access Control (RBAC) with two roles: USER and ADMIN. Users can manage profiles, search schools, maintain favorites, participate in discussions, and submit feedback. Admins manage users, schools, announcements, and feedback. A key innovation is the real-time demographic statistics feature that shows the distribution of user profiles among those who favorited each school.

## Architecture

### High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        Frontend (Vue.js)                         │
│  ┌──────────────┬──────────────┬──────────────┬──────────────┐  │
│  │   Auth       │   User       │   School     │   Admin      │  │
│  │   Pages      │   Pages      │   Pages      │   Pages      │  │
│  └──────────────┴──────────────┴──────────────┴──────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↓ HTTP/REST
┌─────────────────────────────────────────────────────────────────┐
│                    Backend (Spring Boot)                         │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              REST API Controllers                        │  │
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐   │  │
│  │  │ Auth    │ User    │ School  │ Comment │ Admin   │   │  │
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘   │  │
│  └──────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Business Logic (Services)                   │  │
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐   │  │
│  │  │ Auth    │ User    │ School  │ Comment │ Admin   │   │  │
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘   │  │
│  └──────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Data Access (Repositories)                  │  │
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐   │  │
│  │  │ User    │ School  │ Comment │ Feedback│ Announce│   │  │
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘   │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↓ JDBC
┌─────────────────────────────────────────────────────────────────┐
│                    Database (MySQL)                              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  users │ user_profile │ schools │ majors │ exam_subjects │  │
│  │  school_requirements │ favorites │ comments │ feedback    │  │
│  │  announcements                                            │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Layered Architecture

1. **Presentation Layer (Vue.js)**
   - Components for authentication, user profile, school search, favorites, discussions, feedback
   - Admin dashboard components for user, school, announcement, and feedback management
   - Routing and state management

2. **API Layer (Spring Boot Controllers)**
   - RESTful endpoints for all operations
   - Request validation and response formatting
   - Authentication and authorization checks

3. **Business Logic Layer (Services)**
   - Core business logic implementation
   - Data validation and transformation
   - Cross-entity operations (e.g., favorite statistics calculation)

4. **Data Access Layer (Repositories)**
   - JPA repositories for database operations
   - Custom queries for complex filtering and statistics
   - Transaction management

5. **Persistence Layer (MySQL)**
   - Relational database with normalized schema
   - Indexes for performance optimization
   - Audit fields (createdAt, updatedAt, deleted)

## Components and Interfaces

### 1. Authentication & Authorization

**Components:**
- `AuthController`: Handles registration, login, logout
- `AuthService`: Validates credentials, generates tokens, manages sessions
- `JwtTokenProvider`: Creates and validates JWT tokens
- `SecurityConfig`: Configures Spring Security with JWT filter

**Key Interfaces:**
```
POST /api/auth/register
  Request: { username, password }
  Response: { id, username, role, token }

POST /api/auth/login
  Request: { username, password }
  Response: { id, username, role, token }

GET /api/me
  Response: { id, username, role, profile }
```

### 2. User Profile Management

**Components:**
- `UserController`: Endpoints for profile CRUD
- `UserService`: Business logic for user operations
- `UserRepository`: Database access for users
- `UserProfileRepository`: Database access for profiles

**Key Interfaces:**
```
GET /api/me/profile
  Response: { userId, undergradTier, gpa, gpaScale, cet4Score, cet6Score, targetScore, otherScores }

PUT /api/me/profile
  Request: { undergradTier, gpa, gpaScale, cet4Score, cet6Score, targetScore, otherScores }
  Response: { userId, ... (updated fields) }
```

### 3. School Search & Filtering

**Components:**
- `SchoolController`: Endpoints for school search and details
- `SchoolService`: Business logic for filtering and retrieval
- `SchoolRepository`: Custom queries for complex filtering
- `SchoolSpecification`: JPA Criteria API for dynamic filtering

**Key Interfaces:**
```
GET /api/schools?city=&tier=&expectedScoreMin=&expectedScoreMax=&major=&page=0&size=20
  Response: { content: [{ id, name, city, tier, intro }], totalPages, totalElements }

GET /api/schools/{id}
  Response: { id, name, city, tier, intro, majors, examSubjects, requirements }

GET /api/schools/{id}/requirements
  Response: { id, schoolId, majorId, content, updatedBy, updatedAt }

GET /api/schools/{id}/subjects
  Response: [{ id, schoolId, majorId, subjectName, subjectCode }]
```

### 4. Favorites Management

**Components:**
- `FavoriteController`: Endpoints for favorite operations
- `FavoriteService`: Business logic for favorites and statistics
- `FavoriteRepository`: Database access for favorites
- `StatisticsCalculator`: Calculates demographic distributions

**Key Interfaces:**
```
POST /api/schools/{id}/favorite
  Response: { schoolId, userId, createdAt }

DELETE /api/schools/{id}/favorite
  Response: { success: true }

GET /api/me/favorites?page=0&size=20
  Response: { content: [{ id, name, city, tier }], totalPages }

GET /api/schools/{id}/favorite-stats
  Response: {
    undergradTierDistribution: { "985": 30, "211": 40, "double-non": 25, "other": 5 },
    cet4BucketDistribution: { "<425": 10, "425-500": 25, "501-550": 40, "551+": 25 },
    totalFavorites: 100
  }
```

### 5. Comments & Discussion

**Components:**
- `CommentController`: Endpoints for comment operations
- `CommentService`: Business logic for comments
- `CommentRepository`: Database access for comments

**Key Interfaces:**
```
GET /api/schools/{id}/comments?page=0&size=20
  Response: { content: [{ id, schoolId, userId, parentId, content, status, createdAt, author }], totalPages }

POST /api/schools/{id}/comments
  Request: { content }
  Response: { id, schoolId, userId, content, status, createdAt }

POST /api/comments/{id}/reply
  Request: { content }
  Response: { id, schoolId, userId, parentId, content, status, createdAt }
```

### 6. Feedback System

**Components:**
- `FeedbackController`: Endpoints for feedback operations
- `FeedbackService`: Business logic for feedback
- `FeedbackRepository`: Database access for feedback

**Key Interfaces:**
```
POST /api/feedback
  Request: { type, content }
  Response: { id, userId, type, content, status, createdAt }

GET /api/me/feedback?page=0&size=20
  Response: { content: [{ id, type, content, status, adminReply, repliedAt }], totalPages }

GET /api/admin/feedback?status=&type=&page=0&size=20
  Response: { content: [{ id, userId, type, content, status, adminReply }], totalPages }

PATCH /api/admin/feedback/{id}
  Request: { status, adminReply }
  Response: { id, status, adminReply, repliedAt }
```

### 7. Admin User Management

**Components:**
- `AdminUserController`: Endpoints for user management
- `AdminUserService`: Business logic for admin operations
- `UserRepository`: Database access

**Key Interfaces:**
```
GET /api/admin/users?keyword=&status=&page=0&size=20
  Response: { content: [{ id, username, role, status, createdAt }], totalPages }

PATCH /api/admin/users/{id}/status
  Request: { status }
  Response: { id, status }

POST /api/admin/users/{id}/reset-password
  Response: { tempPassword }
```

### 8. Admin School Management

**Components:**
- `AdminSchoolController`: Endpoints for school management
- `AdminSchoolService`: Business logic for school operations
- `SchoolRepository`, `MajorRepository`, `ExamSubjectRepository`: Database access

**Key Interfaces:**
```
POST /api/admin/schools
  Request: { name, city, tier, website, intro }
  Response: { id, name, city, tier, website, intro, createdAt }

PUT /api/admin/schools/{id}
  Request: { name, city, tier, website, intro }
  Response: { id, name, city, tier, website, intro, updatedAt }

DELETE /api/admin/schools/{id}
  Response: { success: true }

POST /api/admin/schools/{id}/majors
  Request: { name, direction }
  Response: { id, schoolId, name, direction }

POST /api/admin/schools/{id}/subjects
  Request: { majorId, subjectName, subjectCode }
  Response: { id, schoolId, majorId, subjectName, subjectCode }
```

### 9. Admin Announcement Management

**Components:**
- `AdminAnnouncementController`: Endpoints for announcement management
- `AdminAnnouncementService`: Business logic for announcements
- `AnnouncementRepository`: Database access

**Key Interfaces:**
```
POST /api/admin/announcements
  Request: { title, content, status, sortOrder }
  Response: { id, title, content, status, sortOrder, createdAt }

PUT /api/admin/announcements/{id}
  Request: { title, content, status, sortOrder }
  Response: { id, title, content, status, sortOrder, updatedAt }

DELETE /api/admin/announcements/{id}
  Response: { success: true }

GET /api/announcements?page=0&size=20
  Response: { content: [{ id, title, content, publishAt }], totalPages }
```

## Data Models

### Entity Relationships

```
User (1) ──── (N) UserProfile
User (1) ──── (N) Favorite
User (1) ──── (N) Comment
User (1) ──── (N) Feedback

School (1) ──── (N) Major
School (1) ──── (N) ExamSubject
School (1) ──── (N) SchoolRequirement
School (1) ──── (N) Favorite
School (1) ──── (N) Comment

Major (1) ──── (N) ExamSubject
Major (1) ──── (N) SchoolRequirement

Comment (1) ──── (N) Comment (self-referencing for replies)
```

### Core Entities

**User**
- id (PK)
- username (UNIQUE)
- passwordHash
- role (USER/ADMIN)
- status (ENABLED/DISABLED)
- createdAt, updatedAt, deleted

**UserProfile**
- userId (PK/FK)
- undergradTier (985/211/DOUBLE_NON/OTHER)
- gpa (DECIMAL)
- gpaScale (VARCHAR)
- cet4Score (INT)
- cet6Score (INT)
- targetScore (INT)
- otherScores (TEXT)
- createdAt, updatedAt

**School**
- id (PK)
- name (VARCHAR)
- city (VARCHAR)
- tier (985/211/DOUBLE_NON/OTHER)
- website (VARCHAR)
- intro (TEXT)
- createdAt, updatedAt, deleted

**Major**
- id (PK)
- schoolId (FK)
- name (VARCHAR)
- direction (VARCHAR)
- createdAt, updatedAt, deleted

**ExamSubject**
- id (PK)
- schoolId (FK)
- majorId (FK, nullable)
- subjectName (VARCHAR)
- subjectCode (VARCHAR)
- createdAt, updatedAt, deleted

**SchoolRequirement**
- id (PK)
- schoolId (FK)
- majorId (FK, nullable)
- content (LONGTEXT)
- updatedBy (VARCHAR)
- createdAt, updatedAt, deleted

**Favorite**
- id (PK)
- userId (FK)
- schoolId (FK)
- createdAt, deleted
- UNIQUE(userId, schoolId)

**Comment**
- id (PK)
- schoolId (FK)
- userId (FK)
- parentId (FK, nullable)
- content (TEXT)
- status (NORMAL/DELETED)
- createdAt, updatedAt, deleted

**Feedback**
- id (PK)
- userId (FK)
- type (BUG/SUGGESTION/DATA_ERROR)
- content (TEXT)
- status (NEW/PROCESSING/DONE)
- adminReply (TEXT)
- repliedAt (DATETIME)
- createdAt, updatedAt, deleted

**Announcement**
- id (PK)
- title (VARCHAR)
- content (LONGTEXT)
- status (DRAFT/PUBLISHED)
- sortOrder (INT)
- publishAt (DATETIME)
- createdAt, updatedAt, deleted

## Error Handling

### Exception Hierarchy

```
RuntimeException
├── ResourceNotFoundException
│   ├── UserNotFoundException
│   ├── SchoolNotFoundException
│   ├── CommentNotFoundException
│   └── FeedbackNotFoundException
├── ValidationException
│   ├── InvalidCredentialsException
│   ├── DuplicateUsernameException
│   └── InvalidFilterException
├── AuthorizationException
│   ├── UnauthorizedException
│   └── ForbiddenException
└── SystemException
    ├── DatabaseException
    └── InternalServerException
```

### Error Response Format

```json
{
  "timestamp": "2024-01-16T10:30:00Z",
  "status": 400,
  "error": "ValidationException",
  "message": "Username already exists",
  "path": "/api/auth/register"
}
```

### Common Error Scenarios

1. **Authentication Errors**
   - Invalid credentials → 401 Unauthorized
   - Token expired → 401 Unauthorized
   - Missing token → 401 Unauthorized

2. **Authorization Errors**
   - User accessing admin endpoints → 403 Forbidden
   - User accessing other user's data → 403 Forbidden

3. **Validation Errors**
   - Invalid input format → 400 Bad Request
   - Missing required fields → 400 Bad Request
   - Duplicate username → 409 Conflict

4. **Resource Errors**
   - School not found → 404 Not Found
   - User not found → 404 Not Found

## Testing Strategy

### Unit Testing

1. **Service Layer Tests**
   - Test business logic in isolation
   - Mock repositories and external dependencies
   - Cover success and failure scenarios
   - Test data validation and transformation

2. **Repository Tests**
   - Test custom query methods
   - Verify filtering and pagination
   - Test relationship loading

3. **Controller Tests**
   - Test request/response mapping
   - Verify authentication and authorization
   - Test error handling

### Integration Testing

1. **API Integration Tests**
   - Test complete request/response cycles
   - Verify database persistence
   - Test transaction management

2. **End-to-End Tests**
   - Test user workflows (registration → search → favorite → comment)
   - Test admin workflows (manage users → manage schools → manage feedback)
   - Test statistics calculation accuracy

### Test Data

- Pre-populated test database with sample users, schools, majors, and exam subjects
- Test fixtures for common scenarios
- Seed data for statistics testing

## Security Considerations

1. **Authentication**
   - JWT tokens with expiration
   - Password hashing with bcrypt
   - Secure token storage in HTTP-only cookies

2. **Authorization**
   - Role-based access control (RBAC)
   - Method-level security annotations
   - Resource-level authorization checks

3. **Data Protection**
   - Input validation and sanitization
   - SQL injection prevention through parameterized queries
   - XSS prevention through output encoding

4. **API Security**
   - CORS configuration
   - Rate limiting
   - Request validation

## Performance Considerations

1. **Database Optimization**
   - Indexes on frequently queried columns (userId, schoolId, status)
   - Composite indexes for common filter combinations
   - Query optimization for statistics calculation

2. **Caching**
   - Cache school list and details
   - Cache announcement list
   - Invalidate cache on updates

3. **Pagination**
   - Implement pagination for all list endpoints
   - Default page size of 20 items
   - Maximum page size limit

4. **Lazy Loading**
   - Load related entities on demand
   - Avoid N+1 query problems
   - Use JOIN FETCH for common relationships

## Deployment Architecture

```
┌─────────────────────────────────────────┐
│         Client Browser                  │
│         (Vue.js SPA)                    │
└────────────────┬────────────────────────┘
                 │ HTTP/HTTPS
┌────────────────▼────────────────────────┐
│      Web Server (Nginx/Apache)          │
│      - Static file serving              │
│      - Reverse proxy                    │
│      - SSL/TLS termination              │
└────────────────┬────────────────────────┘
                 │ HTTP
┌────────────────▼────────────────────────┐
│    Spring Boot Application Server       │
│    - REST API                           │
│    - Business Logic                     │
│    - Session Management                 │
└────────────────┬────────────────────────┘
                 │ JDBC
┌────────────────▼────────────────────────┐
│         MySQL Database                  │
│         - Data Persistence              │
│         - Transactions                  │
└─────────────────────────────────────────┘
```
