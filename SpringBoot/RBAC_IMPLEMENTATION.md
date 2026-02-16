# Role-Based Access Control (RBAC) Implementation

## Overview

This document describes the implementation of Role-Based Access Control (RBAC) in the Campus Activity Crowdfunding Platform. The RBAC system provides fine-grained access control based on user roles.

## Architecture

### Components

1. **@RequireRole Annotation** (`annotation/RequireRole.java`)
   - Custom annotation for marking methods that require specific roles
   - Can be applied to controller methods
   - Supports multiple roles (user must have at least one)

2. **RoleCheckAspect** (`aspect/RoleCheckAspect.java`)
   - AspectJ aspect that intercepts methods annotated with @RequireRole
   - Validates user permissions before method execution
   - Throws SecurityException if user lacks required role

3. **JwtAuthenticationFilter** (`config/JwtAuthenticationFilter.java`)
   - Extracts JWT token from Authorization header
   - Validates token and extracts user role
   - Sets authentication in SecurityContext with role as authority

4. **SecurityConfig** (`config/SecurityConfig.java`)
   - Configures Spring Security
   - Enables AspectJ auto-proxy for @RequireRole support
   - Enables method-level security
   - Configures JWT filter

5. **GlobalExceptionHandler** (`config/GlobalExceptionHandler.java`)
   - Handles SecurityException thrown by RoleCheckAspect
   - Returns 403 Forbidden response with error message

## User Roles

The system supports three user roles:

1. **USER** - Regular user
   - Can browse activities
   - Can register for activities
   - Can provide feedback
   - Can support crowdfunding

2. **ORGANIZER** - Activity organizer
   - Can create and manage activities
   - Can view activity statistics
   - Can manage crowdfunding
   - Can reply to feedback

3. **ADMIN** - Administrator
   - Can audit activities
   - Can audit crowdfunding
   - Can manage users
   - Can view platform statistics

## Usage

### Basic Usage

```java
@GetMapping("/admin-only")
@RequireRole(UserRole.ADMIN)
public ResponseEntity<ApiResponse<String>> adminOnly() {
    return ResponseEntity.ok(ApiResponse.success("Admin access granted", "..."));
}
```

### Multiple Roles

```java
@GetMapping("/admin-or-organizer")
@RequireRole({UserRole.ADMIN, UserRole.ORGANIZER})
public ResponseEntity<ApiResponse<String>> adminOrOrganizer() {
    return ResponseEntity.ok(ApiResponse.success("Access granted", "..."));
}
```

### No Role Requirement

```java
@GetMapping("/public")
public ResponseEntity<ApiResponse<String>> publicEndpoint() {
    return ResponseEntity.ok(ApiResponse.success("Public access", "..."));
}
```

## Authentication Flow

1. User logs in with username and password
2. AuthService validates credentials and generates JWT token
3. JWT token includes user role as a claim
4. Client includes token in Authorization header: `Bearer <token>`
5. JwtAuthenticationFilter extracts and validates token
6. User role is set as authority in SecurityContext
7. When accessing @RequireRole endpoint, RoleCheckAspect checks authority
8. If user has required role, method executes; otherwise SecurityException is thrown

## JWT Token Structure

```json
{
  "sub": "username",
  "role": "ADMIN",
  "iat": 1234567890,
  "exp": 1234654290
}
```

## Error Handling

### Unauthorized Access

When a user attempts to access a resource without required role:

**Request:**
```
GET /api/test/admin-only
Authorization: Bearer <user_token>
```

**Response (403 Forbidden):**
```json
{
  "code": 403,
  "message": "User does not have required role to access this resource",
  "data": null
}
```

### Missing Authentication

When accessing protected endpoint without token:

**Request:**
```
GET /api/test/admin-only
```

**Response (403 Forbidden):**
```json
{
  "code": 403,
  "message": "User is not authenticated",
  "data": null
}
```

## Testing

### Test Endpoints

The `RoleTestController` provides test endpoints to verify RBAC functionality:

1. **GET /api/test/admin-only** - Requires ADMIN role
2. **GET /api/test/organizer-only** - Requires ORGANIZER role
3. **GET /api/test/user-only** - Requires USER role
4. **GET /api/test/admin-or-organizer** - Requires ADMIN or ORGANIZER role
5. **GET /api/test/authenticated-users** - Requires authentication (any role)

### Testing Steps

1. Register a user (default role: USER)
2. Login to get JWT token
3. Test endpoints with different roles:
   - USER token should access /user-only and /authenticated-users
   - ORGANIZER token should access /organizer-only and /admin-or-organizer
   - ADMIN token should access all endpoints

## Security Considerations

1. **Token Validation**: All tokens are validated before processing
2. **Role Extraction**: Role is extracted from JWT claims
3. **Authority Mapping**: Role is mapped to Spring Security authority with "ROLE_" prefix
4. **Exception Handling**: Unauthorized access throws SecurityException with descriptive message
5. **Logging**: All access attempts are logged for audit purposes

## Configuration

### JWT Configuration (application.yml)

```yaml
jwt:
  secret: your-secret-key-change-this-in-production-environment-with-a-strong-key
  expiration: 86400000  # 24 hours in milliseconds
```

### Security Configuration

- CSRF protection: Disabled (stateless JWT authentication)
- Session management: Stateless (SessionCreationPolicy.STATELESS)
- Authentication filter: JwtAuthenticationFilter
- Method security: Enabled with @RequireRole support

## Future Enhancements

1. **Permission-based Access Control**: Extend to support fine-grained permissions
2. **Dynamic Role Management**: Allow runtime role creation and modification
3. **Role Hierarchy**: Implement role inheritance (e.g., ADMIN inherits ORGANIZER permissions)
4. **Audit Logging**: Enhanced logging of all access control decisions
5. **Rate Limiting**: Add rate limiting for sensitive operations

## References

- Spring Security Documentation: https://spring.io/projects/spring-security
- AspectJ Documentation: https://www.eclipse.org/aspectj/
- JWT (JSON Web Tokens): https://jwt.io/
