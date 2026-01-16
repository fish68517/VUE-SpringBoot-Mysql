# Role-Based Access Control (RBAC) Implementation Summary

## Overview

This document summarizes the implementation of Role-Based Access Control (RBAC) for the Postgraduate School Selection System. The system enforces authorization at multiple levels: HTTP request level, method level, and service level.

## Authorization Architecture

### 1. Security Configuration (SecurityConfig.java)

The Spring Security configuration is set up with the following features:

- **JWT Authentication**: Stateless session management using JWT tokens
- **Method-Level Security**: `@EnableGlobalMethodSecurity(prePostEnabled = true)` enables `@PreAuthorize` annotations
- **CORS Configuration**: Allows requests from frontend applications
- **HTTP Authorization Rules**:
  - `/api/auth/**` - Public (no authentication required)
  - `/api/announcements/**` - Public (no authentication required)
  - `/api/schools/**` - Public (no authentication required)
  - `/api/admin/**` - Requires ADMIN role
  - All other endpoints - Require authentication

### 2. JWT Authentication Filter (JwtAuthenticationFilter.java)

- Extracts JWT token from Authorization header
- Validates token and loads user details
- Populates SecurityContext with authenticated user information
- Runs on every request to maintain authentication state

### 3. Authorization Utility (AuthorizationUtil.java)

A centralized utility component for authorization checks:

```java
public class AuthorizationUtil {
    public User getCurrentUser()                    // Get authenticated user
    public boolean isAdmin()                        // Check if user is admin
    public boolean isUser()                         // Check if user is regular user
    public void requireAdmin()                      // Enforce admin role
    public void requireResourceOwnership(Long id)   // Enforce resource ownership
    public boolean canAccessResource(Long id)       // Check resource access
}
```

## Endpoint Authorization

### Public Endpoints (No Authentication Required)

#### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

#### School Search
- `GET /api/schools` - Search schools with filters
- `GET /api/schools/{id}` - Get school details
- `GET /api/schools/{id}/requirements` - Get school requirements
- `GET /api/schools/{id}/subjects` - Get exam subjects
- `GET /api/schools/{id}/comments` - Get school comments
- `GET /api/schools/{id}/favorite-stats` - Get favorite statistics

#### Announcements
- `GET /api/announcements` - Get published announcements

### User Endpoints (Requires USER or ADMIN Role)

#### User Profile
- `GET /api/me` - Get current user profile
- `PUT /api/me/profile` - Update user profile

#### Favorites
- `POST /api/schools/{id}/favorite` - Add school to favorites
- `DELETE /api/schools/{id}/favorite` - Remove school from favorites
- `GET /api/me/favorites` - Get user's favorites

#### Comments
- `POST /api/schools/{id}/comments` - Create comment
- `POST /api/comments/{id}/reply` - Reply to comment
- `DELETE /api/comments/{id}` - Delete comment (with authorization check)

#### Feedback
- `POST /api/feedback` - Submit feedback
- `GET /api/me/feedback` - Get user's feedback

### Admin Endpoints (Requires ADMIN Role)

#### User Management
- `GET /api/admin/users` - List all users
- `PATCH /api/admin/users/{id}/status` - Update user status
- `POST /api/admin/users/{id}/reset-password` - Reset user password

#### School Management
- `POST /api/admin/schools` - Create school
- `PUT /api/admin/schools/{id}` - Update school
- `DELETE /api/admin/schools/{id}` - Delete school
- `POST /api/admin/schools/{id}/majors` - Create major
- `POST /api/admin/schools/{id}/subjects` - Create exam subject

#### Announcement Management
- `POST /api/admin/announcements` - Create announcement
- `PUT /api/admin/announcements/{id}` - Update announcement
- `DELETE /api/admin/announcements/{id}` - Delete announcement

#### Feedback Management
- `GET /api/admin/feedback` - List all feedback
- `PATCH /api/admin/feedback/{id}` - Reply to feedback

## Authorization Implementation Details

### 1. Method-Level Authorization (@PreAuthorize)

All controllers use `@PreAuthorize` annotations to enforce role-based access:

```java
// User endpoints
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public ResponseEntity<ApiResponse<UserProfileDTO>> getUserProfile() { ... }

// Admin endpoints
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ApiResponse<Page<AdminUserDTO>>> getAllUsers() { ... }
```

### 2. Resource-Level Authorization

Services implement resource-level authorization checks for operations that require ownership verification:

#### Comment Deletion (CommentService)
```java
public void deleteCommentWithAuthorization(Long commentId) {
    User currentUser = authorizationUtil.getCurrentUser();
    Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
        .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    
    // Check if user is the comment author or an admin
    boolean isAuthor = comment.getUserId().equals(currentUser.getId());
    boolean isAdmin = currentUser.getRole() == User.UserRole.ADMIN;
    
    if (!isAuthor && !isAdmin) {
        throw new AuthorizationException("You are not authorized to delete this comment");
    }
    
    // Proceed with deletion
    comment.setStatus(Comment.CommentStatus.DELETED);
    comment.setDeleted(true);
    commentRepository.save(comment);
}
```

### 3. Service-Level Authorization

All services use `AuthorizationUtil` to get the current authenticated user and enforce authorization:

#### UserService
- `getUserProfile()` - Returns profile for current user only
- `updateUserProfile()` - Updates profile for current user only

#### FavoriteService
- `addFavorite()` - Adds favorite for current user only
- `removeFavorite()` - Removes favorite for current user only
- `getUserFavorites()` - Returns favorites for current user only

#### CommentService
- `createComment()` - Creates comment for current user
- `createReply()` - Creates reply for current user
- `deleteCommentWithAuthorization()` - Enforces ownership or admin role

#### FeedbackService
- `submitFeedback()` - Submits feedback for current user
- `getUserFeedback()` - Returns feedback for current user only
- `getAdminFeedback()` - Returns all feedback (admin only via @PreAuthorize)
- `replyToFeedback()` - Updates feedback (admin only via @PreAuthorize)

## User Roles

### USER Role
- Can manage own profile
- Can search and view schools
- Can add/remove favorites
- Can view favorite statistics
- Can create and reply to comments
- Can delete own comments
- Can submit and view own feedback

### ADMIN Role
- All USER permissions
- Can manage all users (enable/disable, reset passwords)
- Can manage schools (create, update, delete)
- Can manage majors and exam subjects
- Can manage announcements
- Can manage all feedback
- Can delete any comment

## Security Best Practices Implemented

1. **Stateless Authentication**: JWT tokens eliminate server-side session storage
2. **Password Security**: Passwords are hashed using BCrypt
3. **Token Validation**: JWT tokens are validated on every request
4. **Role-Based Access**: All endpoints enforce role requirements
5. **Resource Ownership**: Services verify user ownership before allowing modifications
6. **Soft Deletes**: Deleted records are marked rather than permanently removed
7. **Audit Trail**: All operations include logging for security auditing
8. **CORS Configuration**: Restricts cross-origin requests to trusted domains

## Testing Authorization

### Authorization Test Scenarios

1. **Unauthenticated Access**
   - Accessing protected endpoints without token → 401 Unauthorized

2. **Insufficient Permissions**
   - USER accessing admin endpoints → 403 Forbidden
   - USER accessing other user's profile → 403 Forbidden

3. **Valid Authorization**
   - USER accessing own profile → 200 OK
   - ADMIN accessing admin endpoints → 200 OK
   - ADMIN accessing user endpoints → 200 OK

4. **Resource Ownership**
   - USER deleting own comment → 200 OK
   - USER deleting other user's comment → 403 Forbidden
   - ADMIN deleting any comment → 200 OK

## Error Handling

Authorization failures are handled through custom exception handlers:

- `AuthorizationException` → 403 Forbidden
- `ResourceNotFoundException` → 404 Not Found
- `ValidationException` → 400 Bad Request

## Requirements Coverage

This implementation satisfies the following requirements:

- **Requirement 13.1**: USER accessing admin features → Denied with 403 Forbidden
- **Requirement 13.2**: ADMIN accessing admin pages → Granted with 200 OK
- **Requirement 13.3**: USER accessing user features → Granted with 200 OK
- **Requirement 13.4**: Unauthenticated users → Redirected to login (401 Unauthorized)
- **Requirement 13.5**: User logout → Session invalidated (handled by JWT expiration)

## Implementation Files

### New Files Created
- `AuthorizationUtil.java` - Centralized authorization utility

### Modified Files
- `UserService.java` - Uses AuthorizationUtil for current user retrieval
- `FavoriteService.java` - Uses AuthorizationUtil for current user retrieval
- `CommentService.java` - Uses AuthorizationUtil and implements resource-level authorization
- `FeedbackService.java` - Uses AuthorizationUtil for current user retrieval

### Existing Files (Already Configured)
- `SecurityConfig.java` - Spring Security configuration with method-level security
- `JwtAuthenticationFilter.java` - JWT token validation
- `UserController.java` - @PreAuthorize annotations on endpoints
- `AdminUserController.java` - @PreAuthorize("hasRole('ADMIN')") on all endpoints
- `AdminSchoolController.java` - @PreAuthorize("hasRole('ADMIN')") on all endpoints
- `AdminAnnouncementController.java` - @PreAuthorize("hasRole('ADMIN')") on all endpoints
- `FavoriteController.java` - @PreAuthorize annotations on user endpoints
- `CommentController.java` - @PreAuthorize annotations on user endpoints
- `FeedbackController.java` - @PreAuthorize annotations on user and admin endpoints

## Conclusion

The RBAC implementation provides comprehensive authorization at multiple levels:
1. HTTP request level (SecurityConfig)
2. Method level (@PreAuthorize annotations)
3. Service level (AuthorizationUtil and resource ownership checks)

This multi-layered approach ensures that users can only access resources and perform operations appropriate for their role.
