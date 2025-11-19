# Implementation Plan

## Backend Implementation Tasks

- [x] 1. Set up Spring Boot project structure and core configuration





  - Initialize Spring Boot Maven project with required dependencies (Spring Web, Spring Data JPA, MySQL Connector, Lombok)
  - Create package structure: config, controller, service, repository, entity, dto, vo, util, exception
  - Configure application.properties with database connection, JPA settings, and file upload paths
  - _Requirements: 1.1, 1.2, 1.3_

- [x] 2. Implement core infrastructure components





- [x] 2.1 Create unified API response wrapper and exception handling


  - Implement ApiResponse VO class with code, msg, and data fields
  - Create BusinessException, UnauthorizedException, ResourceNotFoundException, ValidationException classes
  - Implement GlobalExceptionHandler with @RestControllerAdvice for centralized error handling
  - _Requirements: All requirements - foundation for error handling_

- [x] 2.2 Implement authentication utilities and interceptor


  - Create TokenUtil class with generateToken() and extractUserId() methods
  - Implement AuthInterceptor to validate tokens and extract current user from requests
  - Configure WebMvcConfigurer to register the interceptor for protected endpoints
  - _Requirements: 1.2_

- [x] 2.3 Configure CORS and file upload settings


  - Create CorsConfig to allow frontend origin access
  - Implement FileUploadConfig with max file size settings for images (5MB) and videos (100MB)
  - Create upload directory structure configuration
  - _Requirements: 11.3, 11.4_

- [x] 3. Implement user authentication and profile management




- [x] 3.1 Create User entity and repository


  - Define User entity with id, username, password, role, avatar, gender, intro, timestamps
  - Create UserRepository interface extending JpaRepository
  - Add custom query methods for finding by username
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1_

- [x] 3.2 Implement authentication service and controller


  - Create RegisterRequest and LoginRequest DTOs with validation annotations
  - Implement AuthService with register() and login() methods (plain text password comparison)
  - Create AuthController with POST /api/auth/register and /api/auth/login endpoints
  - Return LoginResponse with token and user information
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_

- [x] 3.3 Implement user profile management


  - Create UserProfileUpdateRequest DTO and UserVO
  - Implement UserService with getProfile(), updateProfile() methods
  - Create UserController with GET /api/users/profile and PUT /api/users/profile endpoints
  - _Requirements: 2.1, 2.2_

- [x] 4. Implement file upload functionality






- [x] 4.1 Create file upload service and controller

  - Implement FileUploadService with uploadImage() and uploadVideo() methods
  - Add file validation logic for type and size constraints
  - Implement unique filename generation using timestamp and UUID
  - Create FileUploadController with POST /api/upload/image and POST /api/upload/video endpoints
  - Handle avatar upload in POST /api/users/avatar endpoint
  - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5, 2.3, 2.4_


- [x] 5. Implement fitness resource management





- [x] 5.1 Create FitnessResource entity and repository


  - Define FitnessResource entity with id, title, description, contentType, fileUrl, content, creator, viewCount, timestamps
  - Create ResourceRepository interface with pagination support
  - Add custom query methods for filtering by content type and creator
  - _Requirements: 3.1, 3.2, 3.4, 3.5, 4.1, 4.2, 14.1_

- [x] 5.2 Implement resource service and controller


  - Create ResourceCreateRequest DTO and ResourceVO
  - Implement ResourceService with create(), update(), delete(), findAll(), findById() methods
  - Create ResourceController with full CRUD endpoints (GET /api/resources, POST, PUT, DELETE)
  - Implement pagination for resource listing
  - Add role-based access control (Admin/Coach can create, Admin can delete)
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 4.1, 4.2, 14.1, 14.2, 14.3, 14.4, 14.5_

- [x] 5.3 Implement resource collection functionality


  - Create Collection entity with user-resource relationship and unique constraint
  - Create CollectionRepository interface
  - Implement collection service with addToCollection(), removeFromCollection(), getUserCollections() methods
  - Create collection endpoints (GET /api/collections, POST, DELETE)
  - _Requirements: 4.3, 4.4, 4.5_

- [x] 6. Implement training plan management






- [x] 6.1 Create TrainingPlan entity and repository

  - Define TrainingPlan entity with id, name, description, exercises (JSON), coach, student, dates, status, timestamps
  - Create TrainingPlanRepository with queries filtered by coach and student
  - _Requirements: 5.1, 5.2, 5.5_


- [x] 6.2 Implement training plan service and controller

  - Create TrainingPlanRequest DTO and TrainingPlanVO
  - Implement TrainingPlanService with create(), update(), delete(), findByCoach(), findByStudent() methods
  - Create TrainingPlanController with CRUD endpoints restricted to coaches
  - Implement role-based filtering (coaches see their plans, students see assigned plans)
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [x] 7. Implement community dynamic posts and interactions




- [x] 7.1 Create Dynamic entity and repository


  - Define Dynamic entity with id, content, imageUrls, user, likeCount, commentCount, status, timestamps
  - Create DynamicRepository with pagination and ordering by created_at descending
  - Add queries for filtering by status (approved/pending)
  - _Requirements: 6.1, 6.2, 6.5_

- [x] 7.2 Implement dynamic post service and controller


  - Create DynamicCreateRequest DTO and DynamicVO
  - Implement DynamicService with create(), update(), delete(), findAll() methods with pagination
  - Create DynamicController with CRUD endpoints (GET /api/dynamics, POST, PUT, DELETE)
  - Set initial status to "approved" for regular posts
  - _Requirements: 6.1, 6.2_

- [x] 7.3 Implement like functionality


  - Create Like entity with user-dynamic relationship and unique constraint
  - Create LikeRepository interface
  - Implement like service with likePost(), unlikePost() methods that update Dynamic.likeCount
  - Create like endpoints (POST /api/dynamics/{id}/like, DELETE)
  - _Requirements: 6.3_

- [x] 7.4 Implement comment functionality


  - Create Comment entity with id, content, user, dynamic, timestamp
  - Create CommentRepository interface
  - Implement CommentService with create(), delete(), findByDynamic() methods
  - Create CommentController with endpoints (GET /api/dynamics/{id}/comments, POST /api/comments, DELETE)
  - Update Dynamic.commentCount when comments are added/removed
  - _Requirements: 6.4_

- [x] 8. Implement daily check-in tracking






- [x] 8.1 Create CheckIn entity and repository

  - Define CheckIn entity with id, user, checkDate, checkTime and unique constraint on (user_id, check_date)
  - Create CheckInRepository with queries for user check-in history
  - _Requirements: 7.1, 7.2, 7.3_


- [x] 8.2 Implement check-in service and controller

  - Create CheckInStatsVO with totalCount, currentStreak, longestStreak fields
  - Implement CheckInService with performCheckIn(), getHistory(), calculateStats() methods
  - Add logic to prevent duplicate check-ins on same day
  - Implement streak calculation algorithm for consecutive days
  - Create CheckInController with endpoints (POST /api/checkins, GET /api/checkins, GET /api/checkins/stats)
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_

- [x] 9. Implement diet record management






- [x] 9.1 Create DietRecord entity and repository

  - Define DietRecord entity with id, user, mealType, foodItems, calories, mealDate, timestamps
  - Create DietRecordRepository with queries filtered by user and date range
  - _Requirements: 8.1, 8.2_


- [x] 9.2 Implement diet record service and controller

  - Create DietRecordRequest DTO and DietSummaryVO
  - Implement DietRecordService with create(), update(), delete(), findByUserAndDate(), calculateDailySummary() methods
  - Create DietRecordController with full CRUD endpoints and summary endpoint
  - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_

- [-] 10. Implement coach-student relationship management


- [x] 10.1 Create CoachStudent entity and repository


  - Define CoachStudent entity with id, coach, student, createdAt timestamp and unique constraint on (coach_id, student_id)
  - Create CoachStudentRepository interface extending JpaRepository
  - Add custom query methods: findByCoachId(), findByStudentId(), existsByCoachIdAndStudentId()
  - _Requirements: 12.1, 12.2, 12.3_

- [x] 10.2 Implement coach service and controller


  - Create CoachService interface with addStudent(), removeStudent(), getMyStudents(), getAllCoaches(), getCoachProfile() methods
  - Implement CoachServiceImpl with business logic for managing coach-student relationships
  - Create CoachController with endpoints:
    - GET /api/coaches - List all coaches with their profiles
    - GET /api/coaches/{id} - Get specific coach profile with student count
    - GET /api/coaches/students - Get current coach's student list
    - POST /api/coaches/students/{userId} - Add a student (requires coach role)
    - DELETE /api/coaches/students/{userId} - Remove a student (requires coach role)
  - Add authorization checks to ensure only coaches can manage students
  - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5_

- [x] 10.3 Implement student data access for coaches





  - Extend CheckInService to add getStudentCheckIns(Long coachId, Long studentId) method
  - Extend DietRecordService to add getStudentDietRecords(Long coachId, Long studentId, LocalDate startDate, LocalDate endDate) method
  - Add validation to ensure coach-student relationship exists before allowing data access
  - Create endpoints in CoachController:
    - GET /api/coaches/students/{studentId}/checkins - View student check-in history
    - GET /api/coaches/students/{studentId}/diet-records - View student diet records
  - _Requirements: 12.5_

- [x] 10.4 Implement training progress analytics





  - Create AnalyticsVO with fields: checkInFrequency, totalCheckIns, averageCalories, activeDays, planCompletionRate
  - Create AnalyticsService with calculateStudentAnalytics(Long studentId, int days) method
  - Implement analytics calculation logic:
    - Calculate check-in frequency for last 30 days
    - Calculate average daily calories from diet records
    - Determine active days and activity trends
    - Calculate training plan completion rate
  - Add endpoint GET /api/coaches/analytics/{studentId} in CoachController
  - Add validation to ensure coach has access to student's data
  - Handle cases with insufficient data gracefully
  - _Requirements: 13.1, 13.2, 13.3, 13.4, 13.5_

- [x] 11. Implement administrator functions




- [x] 11.1 Create admin user management service and controller


  - Create AdminService interface with methods:
    - listAllUsers(String role, String username) - List/search users with optional filters
    - getUserById(Long id) - Get user details
    - updateUser(Long id, UserProfileUpdateRequest request) - Update any user's profile
    - deleteUser(Long id) - Delete user and associated data
    - getUserStatistics() - Get system-wide user statistics
  - Implement AdminServiceImpl with business logic
  - Create AdminController with endpoints:
    - GET /api/admin/users - List all users with optional search/filter parameters
    - GET /api/admin/users/{id} - Get specific user details
    - PUT /api/admin/users/{id} - Update user information
    - DELETE /api/admin/users/{id} - Delete user account
    - GET /api/admin/statistics - Get system statistics
  - Add authorization checks to ensure only admins can access these endpoints
  - For coach listings, include student count and content count statistics
  - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5_

- [x] 11.2 Implement content moderation functionality


  - Add query methods to DynamicRepository:
    - findByStatus(String status, Pageable pageable) - Get posts by moderation status
    - findByStatusOrderByCreatedAtDesc(String status, Pageable pageable) - Get moderation queue
  - Create ModerationService interface with methods:
    - getModerationQueue(int page, int size) - Get pending/flagged content
    - approveContent(Long dynamicId) - Approve a post
    - rejectContent(Long dynamicId, String reason) - Reject a post
    - getModerationHistory() - Get moderation action history
  - Implement ModerationServiceImpl with status update logic
  - Add moderation endpoints to AdminController:
    - GET /api/admin/moderation - Get moderation queue with pagination
    - POST /api/admin/moderation/{id}/approve - Approve content
    - POST /api/admin/moderation/{id}/reject - Reject content with reason
  - Update Dynamic entity status field: "pending", "approved", "rejected"
  - Add automatic flagging logic for prohibited keywords (optional enhancement)
  - _Requirements: 6.5, 15.1, 15.2, 15.3, 15.4, 15.5_

- [x] 12. Implement search functionality




- [x] 12.1 Create search DTOs and service


  - Create SearchRequest DTO with fields: query (String), type (String - optional filter: "resource", "post", "coach", "all")
  - Create SearchResultVO with fields: type, id, title, description, imageUrl, relevance
  - Create SearchService interface with searchAll(String query, String type) method
  - Implement SearchServiceImpl with logic to:
    - Search FitnessResource by title and description using LIKE queries
    - Search Dynamic posts by content using LIKE queries
    - Search User (coaches only) by username and intro using LIKE queries
    - Combine and organize results by content type
    - Apply basic relevance ranking (exact matches first, then partial matches)
  - Add custom repository methods:
    - ResourceRepository: findByTitleContainingOrDescriptionContaining()
    - DynamicRepository: findByContentContainingAndStatus()
    - UserRepository: findByRoleAndUsernameContainingOrIntroContaining()
  - _Requirements: 10.1, 10.2, 10.3, 10.4_

- [x] 12.2 Create search controller


  - Create SearchController with endpoint:
    - GET /api/search?q={query}&type={type} - Search across content types
  - Return unified response with results organized by type
  - Handle empty query and no results scenarios
  - Add pagination support for large result sets
  - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5_

- [x] 13. Create database initialization and seed data





- [x] 13.1 Create data initialization component


  - Create DataInitializer class with @Component and @PostConstruct annotations
  - Implement logic to check if initial data already exists
  - Create default admin account (username: admin, password: admin123, role: admin)
  - Add 3-5 sample fitness resources with different content types
  - Create 2-3 sample coach profiles with different specialties
  - Add sample community posts for testing
  - _Requirements: 1.1, 3.1_

- [x] 13.2 Verify database configuration


  - Ensure application.properties has correct MySQL configuration
  - Verify character set is UTF8MB4 in connection URL
  - Confirm JPA ddl-auto setting is appropriate for environment
  - Test database connection and t able creation
  - _Requirements: 1.1_

## Frontend Implementation Tasks

- [x] 14. Set up Vue 3 project structure and core configuration









- [x] 14.1 Initialize Vue 3 project with Vite



  - Run `npm create vite@latest VUE -- --template vue` to create project
  - Install dependencies: `npm install vue-router@4 pinia axios element-plus`
  - Install dev dependencies: `npm install -D @element-plus/icons-vue`
  - Configure package.json scripts for dev, build, and preview
  - _Requirements: All requirements - foundation for frontend_

- [x] 14.2 Create project directory structure


  - Create directories: src/api, src/views, src/components, src/router, src/store, src/utils, src/assets
  - Create subdirectories: src/views/auth, src/views/user, src/views/resource, src/views/community, src/views/coach, src/views/admin
  - Create subdirectories: src/components/common, src/components/resource, src/components/community, src/components/training, src/components/charts
  - Create subdirectories: src/assets/styles, src/assets/images
  - _Requirements: All requirements - project organization_

- [x] 14.3 Configure Vite and environment variables


  - Create vite.config.js with proxy configuration for API calls
  - Create .env.development with VITE_API_BASE_URL=http://localhost:8080
  - Create .env.production with production API URL
  - Configure build settings and asset handling
  - _Requirements: All requirements - build configuration_

- [x] 14.4 Set up Element Plus


  - Configure Element Plus auto-import in main.js
  - Import Element Plus CSS in main.js
  - Register Element Plus icons
  - Create custom theme configuration if needed
  - _Requirements: All requirements - UI framework setup_

- [x] 15. Implement API client and request utilities




- [x] 15.1 Create Axios instance with interceptors


  - Create src/utils/request.js with Axios instance configuration
  - Set baseURL from import.meta.env.VITE_API_BASE_URL
  - Implement request interceptor to attach Authorization header with token from localStorage
  - Implement response interceptor to:
    - Extract data from ApiResponse wrapper
    - Handle error responses (401, 403, 404, 500)
    - Show error messages using Element Plus ElMessage
    - Redirect to login on 401 Unauthorized
  - _Requirements: 1.2, All API interactions_

- [x] 15.2 Create authentication utilities


  - Create src/utils/auth.js with functions:
    - getToken() - Retrieve token from localStorage
    - setToken(token) - Store token in localStorage
    - removeToken() - Clear token from localStorage
    - getUserInfo() - Get user info from localStorage
    - setUserInfo(user) - Store user info in localStorage
    - clearAuth() - Clear all auth data
  - _Requirements: 1.2_

- [x] 15.3 Create API modules for authentication and user management


  - Create src/api/auth.js with register(data) and login(data) functions
  - Create src/api/user.js with getProfile(), updateProfile(data), uploadAvatar(file) functions
  - _Requirements: 1.1, 1.2, 2.1, 2.2, 2.3_

- [x] 15.4 Create API modules for resources and training


  - Create src/api/resource.js with getResources(params), getResourceById(id), createResource(data), updateResource(id, data), deleteResource(id) functions
  - Create src/api/collection.js with getCollections(), addCollection(resourceId), removeCollection(resourceId) functions
  - Create src/api/training.js with getTrainingPlans(), getTrainingPlanById(id), createTrainingPlan(data), updateTrainingPlan(id, data), deleteTrainingPlan(id) functions
  - _Requirements: 3.1-3.5, 4.1-4.5, 5.1-5.5_

- [x] 15.5 Create API modules for community features


  - Create src/api/community.js with getDynamics(params), getDynamicById(id), createDynamic(data), updateDynamic(id, data), deleteDynamic(id), likeDynamic(id), unlikeDynamic(id) functions
  - Create src/api/comment.js with getComments(dynamicId), createComment(data), deleteComment(id) functions
  - _Requirements: 6.1-6.4_

- [x] 15.6 Create API modules for tracking and coach features


  - Create src/api/checkin.js with performCheckIn(), getCheckInHistory(), getCheckInStats() functions
  - Create src/api/diet.js with getDietRecords(params), createDietRecord(data), updateDietRecord(id, data), deleteDietRecord(id), getDailySummary(date) functions
  - Create src/api/coach.js with getCoaches(), getCoachById(id), getMyStudents(), addStudent(userId), removeStudent(userId), getStudentCheckIns(studentId), getStudentDietRecords(studentId, params), getStudentAnalytics(studentId) functions
  - _Requirements: 7.1-7.5, 8.1-8.5, 12.1-12.5, 13.1-13.5_

- [x] 15.7 Create API modules for admin and utilities


  - Create src/api/admin.js with getUsers(params), getUserById(id), updateUser(id, data), deleteUser(id), getStatistics(), getModerationQueue(), approveContent(id), rejectContent(id, reason) functions
  - Create src/api/upload.js with uploadImage(file), uploadVideo(file) functions
  - Create src/api/search.js with search(query, type) function
  - _Requirements: 9.1-9.5, 10.1-10.5, 11.1-11.5, 15.1-15.5_

- [x] 16. Implement authentication and routing





- [x] 16.1 Create authentication views


  - Create src/views/auth/Login.vue with:
    - Username and password input fields using Element Plus el-form
    - Form validation rules (required fields)
    - Login button that calls login API
    - Link to registration page
    - Error message display
    - Redirect to appropriate dashboard after successful login based on role
  - Create src/views/auth/Register.vue with:
    - Username, password, confirm password fields
    - Role selection (radio buttons: user, coach)
    - Form validation (username length, password match, required fields)
    - Register button that calls register API
    - Link to login page
    - Success message and redirect to login after registration
  - _Requirements: 1.1, 1.2, 1.4, 1.5_

- [x] 16.2 Set up Vue Router with route guards


  - Create src/router/index.js with route definitions:
    - Public routes: /login, /register
    - User routes: /home, /profile, /resources, /resources/:id, /my-collections, /training-plans, /community, /community/:id, /checkin, /diet
    - Coach routes: /coach/dashboard, /coach/students, /coach/training-plans/create, /coach/analytics, /coach/content
    - Admin routes: /admin/dashboard, /admin/users, /admin/resources, /admin/moderation
  - Add meta fields to routes: requiresAuth (boolean), roles (array)
  - Implement beforeEach navigation guard:
    - Check if route requires authentication
    - Verify token exists in localStorage
    - Check if user role matches required roles
    - Redirect to /login if not authenticated
    - Redirect to /unauthorized if role doesn't match
  - _Requirements: 1.2, 1.5_

- [x] 16.3 Create Pinia stores for state management


  - Create src/store/index.js to initialize Pinia
  - Create src/store/modules/auth.js with:
    - State: user (object), token (string), isAuthenticated (boolean)
    - Actions: login(credentials), register(data), logout(), checkAuth()
    - Getters: currentUser, userRole, isAdmin, isCoach
  - Create src/store/modules/user.js with:
    - State: profile (object), checkInStats (object)
    - Actions: fetchProfile(), updateProfile(data), fetchCheckInStats()
  - Create src/store/modules/app.js with:
    - State: loading (boolean), sidebarCollapsed (boolean)
    - Actions: setLoading(status), toggleSidebar()
  - Sync auth store with localStorage on initialization
  - _Requirements: 1.2, 2.1_

- [x] 17. Implement common layout components





- [x] 17.1 Create main layout component


  - Create src/components/common/Layout.vue with:
    - Header, sidebar, and main content area structure
    - Responsive design using Element Plus el-container
    - Slot for page content
    - Integration with app store for sidebar state
  - _Requirements: General UX_

- [x] 17.2 Create header component


  - Create src/components/common/Header.vue with:
    - Logo and application name
    - Navigation menu with links based on user role
    - Search bar integration
    - User avatar dropdown with profile link and logout button
    - Notification icon (placeholder for future)
  - _Requirements: 10.1_

- [x] 17.3 Create sidebar component


  - Create src/components/common/Sidebar.vue with:
    - Role-based menu items using Element Plus el-menu
    - User menu: Home, Resources, My Collections, Training Plans, Community, Check-in, Diet Records
    - Coach menu: Dashboard, Students, Create Plan, Analytics, My Content
    - Admin menu: Dashboard, User Management, Resource Management, Moderation
    - Active route highlighting
    - Collapsible functionality
  - _Requirements: General UX_

- [x] 17.4 Create search and footer components


  - Create src/components/common/SearchBar.vue with:
    - Search input field with icon
    - Type filter dropdown (All, Resources, Posts, Coaches)
    - Search button
    - Emit search event to parent
  - Create src/components/common/Footer.vue with:
    - Copyright information
    - Links to about, contact, terms
    - Social media icons (optional)
  - _Requirements: 10.1_

- [x] 18. Implement user profile and home views





- [x] 18.1 Create user profile view


  - Create src/views/user/Profile.vue with:
    - Display mode showing username, avatar, gender, intro, registration date
    - Edit mode with form fields for avatar, gender, intro
    - Avatar upload with preview using Element Plus el-upload
    - Check-in statistics display (total, current streak, longest streak)
    - Save and cancel buttons for edit mode
    - Form validation for profile updates
    - Success/error message handling
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 7.5_

- [x] 18.2 Create home landing page


  - Create src/views/Home.vue with:
    - Welcome banner with user's name
    - Featured fitness resources section (top 6 by view count)
    - Recent community posts section (latest 5)
    - Quick access cards for main features (Resources, Training Plans, Community, Check-in)
    - Statistics overview (user's check-ins, collections, posts)
  - _Requirements: General user experience_

- [x] 19. Implement fitness resource views






- [x] 19.1 Create resource card component

  - Create src/components/resource/ResourceCard.vue with:
    - Resource thumbnail/icon based on content type
    - Title and description (truncated)
    - Content type badge
    - View count display
    - Creator name (if coach-created)
    - Click handler to navigate to detail page
  - _Requirements: 4.1, 4.2_


- [x] 19.2 Create resource listing view

  - Create src/views/resource/ResourceList.vue with:
    - Filter dropdown for content type (All, Video, Article, Document)
    - Grid layout of ResourceCard components
    - Pagination using Element Plus el-pagination
    - Loading state
    - Empty state when no resources found
  - _Requirements: 4.1, 4.2_


- [x] 19.3 Create resource detail view

  - Create src/views/resource/ResourceDetail.vue with:
    - Full resource information display
    - Video player for video content
    - Document viewer or download link for documents
    - Article content rendering for text articles
    - Collection button (add/remove from collection)
    - View count display
    - Creator information with link to coach profile
    - Back button to resource list
  - _Requirements: 4.1, 4.2, 4.3_


- [x] 19.4 Create user collection view

  - Create src/views/user/MyCollections.vue with:
    - Grid of collected resources using ResourceCard
    - Collection date display
    - Remove from collection button
    - Empty state when no collections
    - Link to browse more resources
  - _Requirements: 4.3, 4.4, 4.5_

- [x] 20. Implement training plan views for users




- [x] 20.1 Create training plan components


  - Create src/components/training/PlanCard.vue with:
    - Plan name and description
    - Coach name
    - Start and end dates
    - Status badge (active, completed, cancelled)
    - Click handler to view details
  - Create src/components/training/ExerciseList.vue with:
    - List of exercises from plan
    - Exercise name, sets, reps, duration
    - Completion checkbox (optional)
  - _Requirements: 5.5_

- [x] 20.2 Create training plan list view


  - Create src/views/user/MyTrainingPlans.vue with:
    - List of assigned training plans using PlanCard
    - Filter by status (All, Active, Completed)
    - Plan detail modal or expandable section showing ExerciseList
    - Empty state when no plans assigned
    - Message to contact a coach if no plans
  - _Requirements: 5.5_

- [x] 21. Implement community features





- [x] 21.1 Create community post components


  - Create src/components/community/PostCard.vue with:
    - User avatar and username
    - Post content with "read more" for long text
    - Image gallery for multiple images
    - Like button with count
    - Comment count
    - Post timestamp
    - Edit/delete buttons for own posts
    - Click handler to view full post
  - Create src/components/community/LikeButton.vue with:
    - Heart icon (filled if liked, outline if not)
    - Like count display
    - Toggle like/unlike on click
    - Optimistic UI update
  - _Requirements: 6.1, 6.2, 6.3_

- [x] 21.2 Create community feed view


  - Create src/views/community/CommunityFeed.vue with:
    - Create post button (opens modal or navigates to create page)
    - List of PostCard components
    - Pagination or infinite scroll
    - Loading state
    - Empty state for no posts
    - Pull to refresh (optional)
  - _Requirements: 6.1, 6.2_

- [x] 21.3 Create post detail and comment views


  - Create src/components/community/CommentList.vue with:
    - List of comments with user info and timestamp
    - Delete button for own comments
    - Empty state for no comments
  - Create src/views/community/PostDetail.vue with:
    - Full post display with all images
    - Like button
    - CommentList component
    - Comment input form at bottom
    - Submit comment button
    - Real-time count updates after like/comment actions
  - _Requirements: 6.3, 6.4_

- [x] 21.4 Create post creation view


  - Create src/views/community/CreatePost.vue with:
    - Textarea for post content
    - Image upload component (multiple images)
    - Image preview with remove option
    - Character count display
    - Submit button
    - Cancel button
    - Success message and redirect to feed after creation
  - _Requirements: 6.1_

- [x] 22. Implement check-in and diet tracking views





- [x] 22.1 Create check-in view


  - Create src/views/user/CheckIn.vue with:
    - Large check-in button (disabled if already checked in today)
    - Current streak display with fire icon
    - Total check-in count
    - Longest streak record
    - Calendar view showing check-in dates (using Element Plus el-calendar)
    - Success animation after check-in
    - Message if already checked in today
  - Create src/components/charts/CheckInChart.vue (optional) with:
    - Line chart showing check-in frequency over time
    - Use a chart library like Chart.js or ECharts
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_

- [x] 22.2 Create diet record views


  - Create src/views/user/DietRecords.vue with:
    - Date picker to filter records
    - Add diet record button (opens modal)
    - Daily summary card showing total calories for selected date
    - List of diet records grouped by meal type
    - Edit and delete buttons for each record
  - Create diet record form modal with:
    - Meal type selector (Breakfast, Lunch, Dinner, Snack)
    - Food items textarea
    - Calories number input
    - Meal date picker
    - Save and cancel buttons
  - Create src/components/charts/CalorieChart.vue (optional) with:
    - Bar chart showing daily calorie intake over a week
    - Use a chart library like Chart.js or ECharts
  - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_

- [x] 23. Implement coach-specific views





- [x] 23.1 Create coach dashboard


  - Create src/views/coach/CoachDashboard.vue with:
    - Statistics cards (total students, total content, total plans)
    - Recent students list with quick links
    - Recent training plans created
    - Quick action buttons (Add Student, Create Plan, Create Content)
  - _Requirements: 12.1, 12.4_

- [x] 23.2 Create student management view


  - Create src/views/coach/StudentList.vue with:
    - Table of students with avatar, username, join date
    - View student details button (shows check-ins, diet records)
    - Remove student button with confirmation dialog
    - Add student button (search and add by username)
    - Empty state when no students
  - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5_

- [x] 23.3 Create training plan creation view


  - Create src/views/coach/CreateTrainingPlan.vue with:
    - Student selector dropdown (from coach's students)
    - Plan name and description inputs
    - Start and end date pickers
    - Exercise builder section:
      - Add exercise button
      - Exercise form fields (name, sets, reps, duration, notes)
      - Remove exercise button
      - Reorder exercises functionality
    - Status selector (Active, Completed, Cancelled)
    - Save and cancel buttons
    - Form validation for required fields
    - Edit mode for existing plans (load plan data)
  - _Requirements: 5.1, 5.2, 5.3, 5.4_

- [x] 23.4 Create student analytics view


  - Create src/views/coach/StudentAnalytics.vue with:
    - Student selector dropdown
    - Time period selector (7 days, 30 days, 90 days)
    - Statistics cards (total check-ins, check-in frequency, average calories, active days)
    - ProgressChart component showing activity trends
    - Plan completion rate display
    - Message when insufficient data available
  - Create src/components/charts/ProgressChart.vue with:
    - Line chart for check-in trends over time
    - Bar chart for calorie intake
    - Use Chart.js or ECharts
  - _Requirements: 13.1, 13.2, 13.3, 13.4, 13.5, 12.5_

- [x] 23.5 Create content management view for coaches


  - Create src/views/coach/ContentManagement.vue with:
    - Create content button (opens form modal)
    - Table of published content with title, type, view count, created date
    - Edit and delete buttons for each content item
    - Content creation/edit form modal:
      - Title and description inputs
      - Content type selector (Video, Article, Document)
      - File upload for video/document
      - Rich text editor for article content
      - Save and cancel buttons
  - _Requirements: 14.1, 14.2, 14.3, 14.4, 14.5_

- [x] 24. Implement admin-specific views




- [x] 24.1 Create admin dashboard


  - Create src/views/admin/AdminDashboard.vue with:
    - Statistics cards (total users, total coaches, total resources, pending moderation)
    - User count by role chart
    - Recent registrations list
    - System activity overview
    - Quick action buttons (User Management, Moderation Queue)
  - _Requirements: 9.1, 9.5_

- [x] 24.2 Create user management view


  - Create src/views/admin/UserManagement.vue with:
    - Search bar for username
    - Role filter dropdown (All, User, Coach, Admin)
    - Table of users with columns: avatar, username, role, registration date, actions
    - Edit user button (opens modal with profile edit form)
    - Delete user button with confirmation dialog
    - For coaches, display student count and content count in table
    - Pagination for user list
  - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5_

- [x] 24.3 Create resource management view


  - Create src/views/admin/ResourceManagement.vue with:
    - Create resource button (opens form modal)
    - Table of resources with title, type, creator, view count, created date
    - Edit and delete buttons for each resource
    - Resource creation/edit form modal:
      - Title and description inputs
      - Content type selector
      - File upload component
      - Text editor for article content
      - Save and cancel buttons
    - Confirmation dialog for deletion
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_

- [x] 24.4 Create content moderation view


  - Create src/views/admin/ContentModeration.vue with:
    - Tabs for Pending, Approved, Rejected posts
    - Table of posts with content preview, author, created date, status
    - Approve button (changes status to approved)
    - Reject button (opens dialog for rejection reason)
    - View full post button (opens modal)
    - Moderation history section showing past actions
    - Filter by date range
    - Pagination for moderation queue
  - _Requirements: 15.1, 15.2, 15.3, 15.4, 15.5_

- [x] 25. Implement search functionality




- [x] 25.1 Create search results view


  - Create src/views/SearchResults.vue with:
    - Search query display at top
    - Type filter tabs (All, Resources, Posts, Coaches)
    - Results organized by type in sections
    - Resource results using ResourceCard component
    - Post results using PostCard component
    - Coach results with avatar, name, intro, student count
    - Result count display for each type
    - "No results found" message when appropriate
    - Link back to previous page
  - Integrate SearchBar component in Header to navigate to SearchResults
  - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5_

- [x] 26. Implement file upload components






- [x] 26.1 Create reusable upload components




  - Create src/components/common/ImageUpload.vue with:
    - Element Plus el-upload component
    - Image preview after selection
    - File type validation (JPG, PNG, GIF)
    - File size validation (max 5MB)
    - Drag-and-drop support
    - Remove image button
    - Upload progress indicator
    - Error message display for validation failures
  - Create src/components/common/VideoUpload.vue with:
    - Element Plus el-upload component
    - Video file type validation (MP4, AVI)
    - File size validation (max 100MB)
    - Upload progress bar
    - Cancel upload button
    - Error message display
  - Both components should emit uploaded file URL to parent
  - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5_

- [x] 27. Add responsive design and styling






- [x] 27.1 Create global styles and theme


  - Create src/assets/styles/variables.css with CSS custom properties for colors, spacing, typography
  - Create src/assets/styles/global.css with base styles and resets
  - Customize Element Plus theme variables if needed
  - Import global styles in main.js
  - _Requirements: General user experience_

- [x] 27.2 Implement responsive layouts


  - Add media queries for mobile (< 768px), tablet (768px - 1024px), desktop (> 1024px)
  - Make sidebar collapsible on mobile
  - Adjust grid layouts for different screen sizes
  - Ensure forms are mobile-friendly
  - Test all views on different screen sizes
  - _Requirements: General user experience_

- [x] 27.3 Add loading states and animations


  - Create src/components/common/Loading.vue component with spinner
  - Add skeleton screens for content loading (optional)
  - Add transitions for route changes
  - Add hover effects on buttons and cards
  - Add success animations for actions (check-in, like, etc.)
  - _Requirements: General user experience_

- [ ] 28. Implement error handling and user feedback





- [x] 28.1 Enhance error handling


  - Ensure all API calls have try-catch blocks
  - Display user-friendly error messages using Element Plus ElMessage
  - Add form validation with clear error messages
  - Create src/components/common/ErrorBoundary.vue for catching component errors
  - Add 404 Not Found page
  - Add 403 Unauthorized page
  - _Requirements: All requirements - user experience_


- [x] 28.2 Add user feedback mechanisms










  - Use ElMessage for success notifications (green)
  - Use ElMessage for error notifications (red)
  - Use ElMessage for warnings (yellow)
  - Add loading indicators for all async operations
  - Add confirmation dialogs for destructive actions (delete, remove)
  - Show success messages after create/update/delete operations
  - _Requirements: All requirements - user experience_

## Integration and Testing Tasks

- [ ]* 29. Backend integration testing
- [ ]* 29.1 Test authentication and user management
  - Write integration tests for registration and login endpoints
  - Test token generation and validation
  - Test profile update functionality
  - Test role-based access control
  - _Requirements: 1.1, 1.2, 2.1, 2.2_

- [ ]* 29.2 Test core features
  - Test resource CRUD operations with different roles
  - Test training plan creation and assignment
  - Test community post interactions (create, like, comment)
  - Test check-in functionality and streak calculations
  - Test diet record CRUD operations
  - _Requirements: 3.1-8.5_

- [ ]* 29.3 Test coach and admin features
  - Test coach-student relationship management
  - Test student analytics calculations
  - Test admin user management
  - Test content moderation workflow
  - Test search functionality across content types
  - _Requirements: 9.1-15.5_

- [ ]* 30. Frontend component testing
- [ ]* 30.1 Test critical components
  - Write unit tests for authentication forms (Login, Register)
  - Test form validation logic
  - Test API call mocking with axios-mock-adapter
  - Test route guards and navigation
  - Test Pinia store actions and state mutations
  - _Requirements: All frontend requirements_

- [ ]* 31. Manual end-to-end testing
- [ ]* 31.1 Test critical user flows
  - Test complete user registration and login flow
  - Test resource browsing and collection workflow
  - Test training plan creation and viewing
  - Test community post creation and interaction
  - Test check-in and diet tracking workflows
  - Test coach student management and analytics
  - Test admin moderation workflow
  - _Requirements: Critical user flows_

## Deployment and Documentation Tasks

- [x] 32. Prepare for deployment






- [x]* 32.1 Configure production environment


  - Update application.properties with production database connection
  - Set up file storage directory with proper permissions on server
  - Create production environment configuration files (.env.production)
  - Configure CORS to allow production frontend domain
  - Set up proper logging configuration
  - _Requirements: Deployment readiness_

- [x]* 32.2 Build and deploy application


  - Build frontend production bundle using `npm run build`
  - Package backend as JAR file using `mvn clean package`
  - Deploy backend JAR to server
  - Deploy frontend dist folder to web server or CDN
  - Configure reverse proxy (Nginx) if needed
  - Test production deployment
  - _Requirements: Deployment readiness_

- [x] 33. Create user documentation






- [x]* 33.1 Write user guides


  - Create README.md with project overview and setup instructions
  - Write user guide for regular users (registration, resources, community, tracking)
  - Create coach manual for training plan management and student analytics
  - Write admin guide for system management and moderation
  - Document API endpoints in API.md for future reference
  - _Requirements: User support and maintenance_
