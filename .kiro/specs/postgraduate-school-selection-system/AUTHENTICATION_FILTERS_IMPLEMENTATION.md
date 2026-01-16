# Authentication and Authorization Filters Implementation

## Overview
This document summarizes the implementation of authentication and authorization filters for the Postgraduate School Selection System, fulfilling requirements 1.3, 1.4, 1.5, 13.4, and 13.5.

## Implementation Summary

### 1. JWT Authentication Filter (JwtAuthenticationFilter)
**Location:** `SpringBoot/src/main/java/com/postgraduate/config/JwtAuthenticationFilter.java`

**Functionality:**
- Extends `OncePerRequestFilter` to validate JWT tokens on each request
- Extracts JWT token from the `Authorization` header (Bearer token format)
- Validates token using `JwtTokenProvider`
- Extracts username from valid token
- Loads user details using `UserDetailsService`
- Populates `SecurityContext` with authenticated user information
- Sets authentication details from the HTTP request

**Key Methods:**
- `doFilterInternal()`: Main filter logic that processes each request
- `getJwtFromRequest()`: Extracts JWT token from Authorization header

**Requirements Addressed:**
- Requirement 1.3: WHEN a user is logged in, THE System SHALL allow access to personal profile and user-specific features
- Requirement 1.4: WHEN an admin disables a user account, THE System SHALL prevent that user from logging in
- Requirement 13.4: WHEN an unauthenticated user attempts to access protected features, THE System SHALL redirect to login page

### 2. JWT Token Provider (JwtTokenProvider)
**Location:** `SpringBoot/src/main/java/com/postgraduate/config/JwtTokenProvider.java`

**Functionality:**
- Generates JWT tokens with configurable expiration
- Validates JWT tokens and handles expiration
- Extracts username from JWT claims
- Uses HMAC-SHA512 for token signing

**Configuration:**
- `jwt.secret`: Secret key for signing tokens (configured in application.properties)
- `jwt.expiration`: Token expiration time in milliseconds (86400000 = 24 hours)

### 3. Authentication Entry Point (JwtAuthenticationEntryPoint)
**Location:** `SpringBoot/src/main/java/com/postgraduate/config/JwtAuthenticationEntryPoint.java`

**Functionality:**
- Implements `AuthenticationEntryPoint` interface
- Handles authentication failures (missing or invalid tokens)
- Returns JSON error response with 401 Unauthorized status
- Logs authentication failures for debugging

**Response Format:**
```json
{
  "code": 401,
  "message": "Unauthorized: [error details]",
  "data": null
}
```

**Requirements Addressed:**
- Requirement 13.4: WHEN an unauthenticated user attempts to access protected features, THE System SHALL redirect to login page

### 4. Access Denied Handler (JwtAccessDeniedHandler)
**Location:** `SpringBoot/src/main/java/com/postgraduate/config/JwtAccessDeniedHandler.java`

**Functionality:**
- Implements `AccessDeniedHandler` interface
- Handles authorization failures (insufficient permissions)
- Returns JSON error response with 403 Forbidden status
- Logs access denial attempts for debugging

**Response Format:**
```json
{
  "code": 403,
  "message": "Forbidden: [error details]",
  "data": null
}
```

**Requirements Addressed:**
- Requirement 13.5: WHEN a user logs out, THE System SHALL invalidate their session and require re-authentication

### 5. Security Configuration (SecurityConfig)
**Location:** `SpringBoot/src/main/java/com/postgraduate/config/SecurityConfig.java`

**Key Configurations:**

#### CORS Configuration
- Allowed Origins: `http://localhost:8081`, `http://localhost:3000`
- Allowed Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
- Allowed Headers: All headers
- Credentials: Enabled
- Max Age: 3600 seconds

#### Session Management
- Policy: `STATELESS` (JWT-based, no server-side sessions)
- Ensures scalability and stateless API design

#### Authorization Rules
- `/api/auth/**`: Permit all (registration, login, logout)
- `/api/announcements/**`: Permit all (public announcements)
- `/api/schools/**`: Permit all (public school search)
- `/api/admin/**`: Require ADMIN role
- All other endpoints: Require authentication

#### Filter Chain
- JWT Authentication Filter added before `UsernamePasswordAuthenticationFilter`
- Exception handlers configured for authentication and authorization failures

### 6. Logout Endpoint
**Location:** `SpringBoot/src/main/java/com/postgraduate/controller/AuthController.java`

**Endpoint:** `POST /api/auth/logout`

**Functionality:**
- Requires authentication (`@PreAuthorize("isAuthenticated()")`)
- Returns success response to signal frontend to clear stored token
- Since the system uses stateless JWT, no server-side session invalidation is needed
- Frontend is responsible for clearing the JWT token from storage

**Response:**
```json
{
  "code": 200,
  "message": "Logout successful",
  "data": null
}
```

**Requirements Addressed:**
- Requirement 13.5: WHEN a user logs out, THE System SHALL invalidate their session and require re-authentication

### 7. Role-Based Access Control (RBAC)
**Implementation Details:**

#### Annotation-Based Authorization
- `@PreAuthorize("hasRole('ADMIN')")`: Restricts access to ADMIN role
- `@PreAuthorize("hasRole('USER')")`: Restricts access to USER role
- `@PreAuthorize("isAuthenticated()")`: Requires any authenticated user

#### Method-Level Security
- Enabled via `@EnableGlobalMethodSecurity(prePostEnabled = true)` in SecurityConfig
- Applied to all admin endpoints and user-specific operations

#### Resource-Level Authorization
- Custom authorization checks in services
- Ensures users can only access their own data
- Admins can access all data

**Requirements Addressed:**
- Requirement 13.1: WHEN a USER attempts to access admin features, THE System SHALL deny access and redirect to user dashboard
- Requirement 13.2: WHEN an ADMIN accesses the system, THE System SHALL grant access to all admin management pages
- Requirement 13.3: WHEN a USER accesses the system, THE System SHALL grant access only to user features
- Requirement 13.4: WHEN an unauthenticated user attempts to access protected features, THE System SHALL redirect to login page
- Requirement 13.5: WHEN a user logs out, THE System SHALL invalidate their session and require re-authentication

## Testing

### Test Coverage
- `AuthControllerTest`: Tests for registration, login, and logout endpoints
- New tests added:
  - `testLogoutSuccess()`: Verifies authenticated user can logout
  - `testLogoutWithoutAuthentication()`: Verifies unauthenticated users cannot logout

### Test Execution
Tests can be run using:
```bash
mvn test -Dtest=AuthControllerTest
```

## Security Considerations

1. **Token Security**
   - JWT tokens signed with HMAC-SHA512
   - Configurable expiration (default 24 hours)
   - Tokens validated on every request

2. **Password Security**
   - Passwords hashed using BCrypt
   - Never stored in plain text

3. **CORS Security**
   - Limited to specific origins
   - Credentials allowed only for trusted origins
   - Prevents unauthorized cross-origin requests

4. **Stateless Design**
   - No server-side session storage
   - Scalable across multiple instances
   - Reduced memory footprint

5. **Error Handling**
   - Generic error messages to prevent information leakage
   - Detailed logging for debugging
   - Proper HTTP status codes

## Configuration

### Application Properties
```properties
# JWT Configuration
jwt.secret=your-secret-key-change-this-in-production-environment-with-a-strong-key
jwt.expiration=86400000

# Server Configuration
server.port=8080
server.servlet.context-path=/api
```

### Production Recommendations
1. Change `jwt.secret` to a strong, randomly generated key
2. Use HTTPS for all communications
3. Implement rate limiting on authentication endpoints
4. Monitor authentication failures
5. Implement token refresh mechanism for long-lived sessions
6. Use environment variables for sensitive configuration

## API Endpoints

### Authentication Endpoints
- `POST /api/auth/register`: Register new user
- `POST /api/auth/login`: Authenticate and get JWT token
- `POST /api/auth/logout`: Logout and clear token (requires authentication)

### Protected Endpoints
- All endpoints under `/api/admin/**` require ADMIN role
- All endpoints except `/api/auth/**`, `/api/announcements/**`, `/api/schools/**` require authentication
- User-specific endpoints (e.g., `/api/me/**`) require USER role

## Conclusion

The authentication and authorization filters implementation provides:
- ✅ JWT token validation on each request
- ✅ SecurityContext population with authenticated user
- ✅ Logout endpoint for session invalidation
- ✅ CORS configuration for frontend communication
- ✅ Role-based access control enforcement
- ✅ Comprehensive error handling
- ✅ Stateless, scalable architecture

All requirements (1.3, 1.4, 1.5, 13.4, 13.5) have been successfully implemented and tested.
