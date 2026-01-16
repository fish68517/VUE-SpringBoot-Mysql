# Implementation Plan: Postgraduate School Selection System

## Phase 1: Project Setup and Core Infrastructure

- [x] 1. Set up Spring Boot project structure and dependencies





  - Create Spring Boot project with Maven/Gradle
  - Add dependencies: Spring Web, Spring Data JPA, Spring Security, JWT, MySQL Driver, Lombok
  - Configure application.properties for database connection and JWT settings
  - Set up package structure: controller, service, repository, entity, dto, config, exception, util
  - _Requirements: 1.1, 13.1_

- [x] 2. Configure database connection and create base entity classes





  - Create MySQL database and configure connection pool
  - Create BaseEntity class with common audit fields (id, createdAt, updatedAt, deleted)
  - Configure JPA entity mapping and inheritance
  - Set up database migration scripts or Flyway configuration
  - _Requirements: 14.1, 14.2, 14.3_

- [x] 3. Set up Spring Security and JWT authentication framework





  - Create JwtTokenProvider for token generation and validation
  - Configure SecurityConfig with JWT filter
  - Create AuthenticationEntryPoint and AccessDeniedHandler for error handling
  - Implement custom UserDetailsService for authentication
  - _Requirements: 1.1, 1.2, 13.1_

- [x] 4. Create global exception handling and response formatting





  - Create custom exception classes (ResourceNotFoundException, ValidationException, AuthorizationException, etc.)
  - Implement GlobalExceptionHandler with @ControllerAdvice
  - Create standardized error response DTO
  - Configure HTTP status codes for different exception types
  - _Requirements: 1.1, 1.2_

## Phase 2: User Authentication and Profile Management

- [x] 5. Create User and UserProfile entities





  - Create User entity with id, username, passwordHash, role, status, audit fields
  - Create UserProfile entity with userId, undergradTier, gpa, gpaScale, cet4Score, cet6Score, targetScore, otherScores
  - Set up one-to-one relationship between User and UserProfile
  - Create database migration for both tables
  - _Requirements: 1.1, 2.1_

- [x] 6. Implement user registration and login endpoints





  - Create AuthController with POST /api/auth/register endpoint
  - Create AuthController with POST /api/auth/login endpoint
  - Implement AuthService with registration logic (validate input, hash password, create user)
  - Implement AuthService with login logic (validate credentials, generate JWT token)
  - Create UserDTO and LoginRequest/LoginResponse DTOs
  - _Requirements: 1.1, 1.2_

- [x] 7. Implement user profile management endpoints





  - Create UserController with GET /api/me endpoint
  - Create UserController with PUT /api/me/profile endpoint
  - Implement UserService with profile retrieval and update logic
  - Create UserProfileDTO with all profile fields
  - Add validation for profile fields (GPA range, score ranges, etc.)
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

- [x] 8. Create UserRepository and UserProfileRepository





  - Create UserRepository extending JpaRepository with custom queries (findByUsername)
  - Create UserProfileRepository extending JpaRepository
  - Add custom query methods for filtering and searching
  - _Requirements: 1.1, 2.1_

## Phase 3: School Management and Search

- [x] 9. Create School, Major, ExamSubject, and SchoolRequirement entities




  - Create School entity with id, name, city, tier, website, intro, audit fields
  - Create Major entity with id, schoolId, name, direction, audit fields
  - Create ExamSubject entity with id, schoolId, majorId, subjectName, subjectCode, audit fields
  - Create SchoolRequirement entity with id, schoolId, majorId, content, updatedBy, audit fields
  - Set up relationships between entities
  - Create database migrations for all tables
  - _Requirements: 3.1, 4.1, 4.2, 4.3_

- [x] 10. Create school search and filtering endpoints





  - Create SchoolController with GET /api/schools endpoint with pagination
  - Implement filtering by region, tier, expectedScoreRange, major
  - Create SchoolService with filtering logic using JPA Criteria API or Specifications
  - Create SchoolDTO with essential fields (id, name, city, tier, intro)
  - Implement pagination with default page size of 20
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7_

- [x] 11. Create school detail endpoints





  - Create SchoolController with GET /api/schools/{id} endpoint
  - Create SchoolController with GET /api/schools/{id}/requirements endpoint
  - Create SchoolController with GET /api/schools/{id}/subjects endpoint
  - Implement SchoolService with detail retrieval logic
  - Create detailed DTOs for school information
  - _Requirements: 4.1, 4.2, 4.3, 4.4_

- [x] 12. Create SchoolRepository, MajorRepository, ExamSubjectRepository, SchoolRequirementRepository





  - Create repositories extending JpaRepository
  - Implement custom query methods for filtering and searching
  - Add methods for finding majors and subjects by school
  - _Requirements: 3.1, 4.1_

## Phase 4: Favorites and Statistics

- [x] 13. Create Favorite entity and favorite management endpoints





  - Create Favorite entity with id, userId, schoolId, createdAt, deleted fields
  - Add unique constraint on (userId, schoolId)
  - Create FavoriteRepository extending JpaRepository
  - Create FavoriteController with POST /api/schools/{id}/favorite endpoint
  - Create FavoriteController with DELETE /api/schools/{id}/favorite endpoint
  - Create FavoriteController with GET /api/me/favorites endpoint with pagination
  - Implement FavoriteService with add/remove/list logic
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [x] 14. Implement favorite statistics calculation





  - Create StatisticsCalculator service for demographic analysis
  - Implement undergradTier distribution calculation (count by tier, calculate percentages)
  - Implement CET-4 score bucket distribution (buckets: <425, 425-500, 501-550, 551+)
  - Create FavoriteController with GET /api/schools/{id}/favorite-stats endpoint
  - Create FavoriteStatsDTO with distribution data and total count
  - Handle edge case when no users have favorited a school
  - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_

## Phase 5: Comments and Discussion

- [x] 15. Create Comment entity and comment management endpoints





  - Create Comment entity with id, schoolId, userId, parentId, content, status, audit fields
  - Set up self-referencing relationship for nested comments (replies)
  - Create CommentRepository extending JpaRepository
  - Create CommentController with GET /api/schools/{id}/comments endpoint with pagination
  - Create CommentController with POST /api/schools/{id}/comments endpoint
  - Create CommentController with POST /api/comments/{id}/reply endpoint
  - Implement CommentService with CRUD logic
  - Create CommentDTO with author information and timestamps
  - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [x] 16. Implement comment deletion and filtering





  - Add soft delete functionality to CommentService (mark as DELETED instead of removing)
  - Implement filtering to show only NORMAL status comments
  - Create CommentController endpoint for admin to delete comments
  - Implement authorization check to ensure only comment author or admin can delete
  - _Requirements: 7.5, 7.6_

## Phase 6: Feedback System

- [x] 17. Create Feedback entity and feedback submission endpoints





  - Create Feedback entity with id, userId, type, content, status, adminReply, repliedAt, audit fields
  - Create FeedbackRepository extending JpaRepository
  - Create FeedbackController with POST /api/feedback endpoint
  - Create FeedbackController with GET /api/me/feedback endpoint with pagination
  - Implement FeedbackService with submission and retrieval logic
  - Create FeedbackDTO and FeedbackSubmitRequest DTOs
  - Set initial status to NEW when feedback is submitted
  - _Requirements: 8.1, 8.2, 8.3, 8.4_

- [x] 18. Implement admin feedback management endpoints





  - Create FeedbackController with GET /api/admin/feedback endpoint with filtering
  - Implement filtering by status (NEW/PROCESSING/DONE) and type (BUG/SUGGESTION/DATA_ERROR)
  - Create FeedbackController with PATCH /api/admin/feedback/{id} endpoint
  - Implement FeedbackService with reply and status update logic
  - Store admin reply and update repliedAt timestamp
  - _Requirements: 8.5, 8.6, 9.1, 9.2, 9.3, 9.4, 9.5, 9.6_

## Phase 7: Admin User Management

- [x] 19. Create admin user management endpoints





  - Create AdminUserController with GET /api/admin/users endpoint with pagination and search
  - Implement filtering by status (ENABLED/DISABLED)
  - Implement search by username or other identifying information
  - Create AdminUserController with PATCH /api/admin/users/{id}/status endpoint
  - Create AdminUserController with POST /api/admin/users/{id}/reset-password endpoint
  - Implement AdminUserService with user management logic
  - Create AdminUserDTO for displaying user information
  - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6_

## Phase 8: Admin School Management

- [x] 20. Create admin school CRUD endpoints





  - Create AdminSchoolController with POST /api/admin/schools endpoint
  - Create AdminSchoolController with PUT /api/admin/schools/{id} endpoint
  - Create AdminSchoolController with DELETE /api/admin/schools/{id} endpoint
  - Implement AdminSchoolService with school CRUD logic
  - Create SchoolCreateRequest and SchoolUpdateRequest DTOs
  - Validate school data (name, city, tier, etc.)
  - _Requirements: 10.1, 10.2, 10.3, 10.4_

- [x] 21. Create admin major and exam subject management endpoints





  - Create AdminSchoolController with POST /api/admin/schools/{id}/majors endpoint
  - Create AdminSchoolController with POST /api/admin/schools/{id}/subjects endpoint
  - Implement AdminSchoolService with major and subject management logic
  - Create MajorCreateRequest and ExamSubjectCreateRequest DTOs
  - Validate major and subject data
  - _Requirements: 10.5, 10.6, 10.7_

## Phase 9: Admin Announcement Management

- [x] 22. Create admin announcement management endpoints





  - Create AdminAnnouncementController with POST /api/admin/announcements endpoint
  - Create AdminAnnouncementController with PUT /api/admin/announcements/{id} endpoint
  - Create AdminAnnouncementController with DELETE /api/admin/announcements/{id} endpoint
  - Create AnnouncementRepository extending JpaRepository
  - Implement AdminAnnouncementService with announcement CRUD logic
  - Create AnnouncementCreateRequest and AnnouncementUpdateRequest DTOs
  - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5, 11.6_

- [x] 23. Create public announcement retrieval endpoint





  - Create AnnouncementController with GET /api/announcements endpoint with pagination
  - Filter to show only PUBLISHED announcements
  - Sort by sortOrder
  - Create AnnouncementDTO for public display
  - _Requirements: 11.7_

## Phase 10: Authorization and Security

- [x] 24. Implement role-based access control (RBAC)





  - Add @PreAuthorize annotations to all admin endpoints requiring ADMIN role
  - Add @PreAuthorize annotations to user endpoints requiring USER role
  - Create custom authorization checks for resource-level access (e.g., user can only access own profile)
  - Implement authorization logic in services to check user permissions
  - _Requirements: 13.1, 13.2, 13.3, 13.4, 13.5_

- [x] 25. Implement authentication and authorization filters





  - Create JwtAuthenticationFilter to validate tokens on each request
  - Implement SecurityContext population with authenticated user
  - Add logout endpoint to invalidate sessions
  - Configure CORS for frontend communication
  - _Requirements: 1.3, 1.4, 1.5, 13.4, 13.5_

## Phase 11: Frontend - Authentication Pages

- [x] 26. Create Vue login and registration pages





  - Create Login.vue component with username/password form
  - Create Register.vue component with registration form
  - Implement form validation (required fields, password strength)
  - Implement API calls to /api/auth/login and /api/auth/register
  - Store JWT token in localStorage or sessionStorage
  - Redirect to dashboard on successful login
  - _Requirements: 1.1, 1.2_

- [x] 27. Create Vue authentication service and router guards





  - Create AuthService.js for API communication
  - Create router guards to protect routes requiring authentication
  - Implement automatic redirect to login for unauthenticated users
  - Create logout functionality
  - _Requirements: 1.3, 1.4, 1.5_

## Phase 12: Frontend - User Profile Pages

- [x] 28. Create Vue user profile page




  - Create Profile.vue component with profile form
  - Display all profile fields (undergradTier, GPA, CET scores, target score)
  - Implement form submission to PUT /api/me/profile
  - Add form validation for numeric fields
  - Display success/error messages
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

## Phase 13: Frontend - School Search Pages

- [x] 29. Create Vue school search and filter page





  - Create SchoolSearch.vue component with filter form
  - Implement filters for region, tier, score range, major
  - Implement pagination for search results
  - Display school list with name, tier, location, key info
  - Implement API calls to GET /api/schools with filter parameters
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7_

- [x] 30. Create Vue school detail page





  - Create SchoolDetail.vue component
  - Display school information (name, tier, location, intro)
  - Display exam subjects and requirements
  - Display favorite button with current favorite status
  - Display comments section with pagination
  - Display favorite statistics (demographic distribution)
  - Implement API calls to GET /api/schools/{id}, /api/schools/{id}/requirements, /api/schools/{id}/subjects, /api/schools/{id}/favorite-stats
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 6.1, 6.2, 6.3, 6.4, 6.5_

## Phase 14: Frontend - Favorites Pages

- [x] 31. Create Vue favorites list page





  - Create Favorites.vue component
  - Display all favorited schools with pagination
  - Implement remove from favorites functionality
  - Display schools in order of favoriting
  - Implement API calls to GET /api/me/favorites and DELETE /api/schools/{id}/favorite
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

## Phase 15: Frontend - Comments and Discussion

- [x] 32. Create Vue comments section component





  - Create Comments.vue component for displaying comments
  - Implement comment list with pagination
  - Display comment author, content, and timestamp
  - Implement reply functionality with nested display
  - Implement API calls to GET /api/schools/{id}/comments
  - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [x] 33. Create Vue comment submission component





  - Create CommentForm.vue component for submitting comments and replies
  - Implement form validation
  - Implement API calls to POST /api/schools/{id}/comments and POST /api/comments/{id}/reply
  - Display success/error messages
  - _Requirements: 7.2, 7.3_

## Phase 16: Frontend - Feedback Pages

- [x] 34. Create Vue feedback submission page





  - Create FeedbackSubmit.vue component with feedback form
  - Implement form fields for type (BUG/SUGGESTION/DATA_ERROR) and content
  - Implement form validation
  - Implement API call to POST /api/feedback
  - Display success message after submission
  - _Requirements: 8.1, 8.2, 8.3_

- [x] 35. Create Vue user feedback history page





  - Create FeedbackHistory.vue component
  - Display all feedback submitted by user with pagination
  - Display feedback type, content, status, and admin reply
  - Implement API call to GET /api/me/feedback
  - _Requirements: 8.4, 8.5, 8.6_

## Phase 17: Frontend - Admin Pages

- [x] 36. Create Vue admin user management page





  - Create AdminUsers.vue component
  - Implement user list with pagination and search
  - Implement filtering by status
  - Implement disable/enable user functionality
  - Implement password reset functionality
  - Implement API calls to GET /api/admin/users, PATCH /api/admin/users/{id}/status, POST /api/admin/users/{id}/reset-password
  - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6_

- [x] 37. Create Vue admin school management page





  - Create AdminSchools.vue component
  - Implement school list with CRUD operations
  - Implement form for creating/editing schools
  - Implement major and exam subject management
  - Implement API calls to POST/PUT/DELETE /api/admin/schools and related endpoints
  - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5, 10.6, 10.7_

- [x] 38. Create Vue admin announcement management page





  - Create AdminAnnouncements.vue component
  - Implement announcement list with CRUD operations
  - Implement form for creating/editing announcements with rich text editor
  - Implement status and sort order management
  - Implement API calls to POST/PUT/DELETE /api/admin/announcements
  - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5, 11.6_

- [x] 39. Create Vue admin feedback management page





  - Create AdminFeedback.vue component
  - Implement feedback list with pagination
  - Implement filtering by status and type
  - Implement search functionality
  - Implement reply and status update functionality
  - Implement API calls to GET /api/admin/feedback and PATCH /api/admin/feedback/{id}
  - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6, 12.7_

## Phase 18: Frontend - Public Pages

- [x] 40. Create Vue announcements page





  - Create Announcements.vue component
  - Display all published announcements with pagination
  - Display announcements in sort order
  - Implement API call to GET /api/announcements
  - _Requirements: 11.7_

- [x] 41. Create Vue main navigation and layout





  - Create main layout component with navigation
  - Implement role-based navigation (show admin links only for admins)
  - Implement user menu with profile and logout
  - Create routing configuration for all pages
  - _Requirements: 13.1, 13.2, 13.3_

## Phase 19: Integration and Testing

- [ ] 42. Integrate frontend and backend
  - Configure API base URL in Vue application
  - Test all API endpoints with frontend
  - Verify authentication flow end-to-end
  - Verify authorization for admin endpoints
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_

- [ ] 43. Test complete user workflows
  - Test user registration and login flow
  - Test profile creation and update
  - Test school search and filtering
  - Test favorite management and statistics
  - Test comment submission and viewing
  - Test feedback submission and viewing
  - _Requirements: 1.1, 2.1, 3.1, 5.1, 7.1, 8.1_

- [ ] 44. Test complete admin workflows
  - Test admin user management
  - Test admin school management
  - Test admin announcement management
  - Test admin feedback management
  - _Requirements: 9.1, 10.1, 11.1, 12.1_

- [ ] 45. Verify data persistence and audit fields
  - Verify createdAt and updatedAt timestamps are recorded
  - Verify soft delete functionality (deleted flag)
  - Verify audit trail for admin operations
  - _Requirements: 14.1, 14.2, 14.3, 14.4, 14.5_
