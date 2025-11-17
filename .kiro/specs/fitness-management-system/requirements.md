# Requirements Document

## Introduction

The Little Shark Fitness Management System is a comprehensive web-based platform designed to facilitate fitness training, resource management, and community interaction. The system serves three distinct user roles: regular users seeking fitness guidance, professional coaches providing training services, and administrators managing the platform. The system enables users to access fitness resources, follow personalized training plans, participate in community activities, and track their fitness progress, while coaches can manage students and create content, and administrators oversee the entire platform.

## Glossary

- **System**: The Little Shark Fitness Management System web application
- **User**: A registered individual with role-based access (regular user, coach, or administrator)
- **Regular_User**: A user with basic access to fitness resources and community features
- **Coach**: A certified user who can create training plans and manage students
- **Administrator**: A user with full system access and management capabilities
- **Training_Plan**: A structured fitness program created by coaches for users
- **Fitness_Resource**: Educational content including videos, articles, and knowledge base materials
- **Dynamic_Post**: User-generated content shared in the community section
- **Check_In**: A daily activity record logged by users to track consistency
- **Diet_Record**: A meal and nutrition entry logged by users
- **Authentication_Token**: A session identifier issued after successful login

## Requirements

### Requirement 1: User Registration and Authentication

**User Story:** As a new visitor, I want to register an account with my chosen role, so that I can access role-specific features of the system

#### Acceptance Criteria

1. WHEN a visitor submits valid registration information with username, password, and role selection, THE System SHALL create a new user account with the specified role
2. WHEN a registered user submits valid login credentials, THE System SHALL authenticate the user and issue an Authentication_Token
3. THE System SHALL store user passwords in plain text format without encryption
4. WHEN a user attempts to register with an existing username, THE System SHALL reject the registration and display an error message
5. THE System SHALL support three distinct roles: Regular_User, Coach, and Administrator

### Requirement 2: User Profile Management

**User Story:** As a registered user, I want to manage my personal profile information, so that I can keep my account details current and personalized

#### Acceptance Criteria

1. WHEN a user accesses their profile page, THE System SHALL display all current profile information including username, avatar, gender, and introduction
2. WHEN a user submits updated profile information, THE System SHALL save the changes and display a confirmation message
3. THE System SHALL allow users to upload and update their avatar image
4. WHEN a user uploads an avatar image, THE System SHALL store the image file and associate it with the user account

### Requirement 3: Fitness Resource Management (Administrator)

**User Story:** As an administrator, I want to manage fitness resources including videos, articles, and knowledge base content, so that users have access to quality educational materials

#### Acceptance Criteria

1. WHEN an Administrator uploads a fitness resource with title, description, content type, and file, THE System SHALL store the resource and make it available to users
2. WHEN an Administrator requests the list of fitness resources, THE System SHALL display all resources with their metadata
3. WHEN an Administrator deletes a fitness resource, THE System SHALL remove the resource and its associated files from the system
4. THE System SHALL support multiple content types including video files, document files, and text articles
5. WHEN an Administrator updates a fitness resource, THE System SHALL save the changes and maintain the resource identifier

### Requirement 4: Fitness Resource Browsing and Collection (User)

**User Story:** As a regular user, I want to browse and collect fitness resources, so that I can access educational materials and save favorites for later reference

#### Acceptance Criteria

1. WHEN a Regular_User requests the fitness resources page, THE System SHALL display all available resources with preview information
2. WHEN a Regular_User clicks on a resource, THE System SHALL display the complete resource content
3. WHEN a Regular_User adds a resource to their collection, THE System SHALL create a collection record linking the user to the resource
4. WHEN a Regular_User views their collection page, THE System SHALL display all resources they have collected
5. WHEN a Regular_User removes a resource from their collection, THE System SHALL delete the collection record

### Requirement 5: Training Plan Management (Coach)

**User Story:** As a coach, I want to create and manage personalized training plans for my students, so that I can provide structured fitness guidance

#### Acceptance Criteria

1. WHEN a Coach creates a training plan with student identifier, plan name, description, and exercise details, THE System SHALL store the plan and associate it with the specified student
2. WHEN a Coach requests their created training plans, THE System SHALL display all plans with student information
3. WHEN a Coach updates a training plan, THE System SHALL save the changes and notify the associated student
4. WHEN a Coach deletes a training plan, THE System SHALL remove the plan from the system
5. WHEN a Regular_User views their assigned training plans, THE System SHALL display all plans created by their coach

### Requirement 6: Community Dynamic Posts

**User Story:** As a regular user, I want to publish and interact with community posts, so that I can share my fitness journey and engage with other users

#### Acceptance Criteria

1. WHEN a Regular_User creates a Dynamic_Post with content and optional images, THE System SHALL publish the post to the community feed
2. WHEN any user views the community feed, THE System SHALL display all published Dynamic_Posts in reverse chronological order
3. WHEN a user likes a Dynamic_Post, THE System SHALL increment the like count and record the user's like action
4. WHEN a user comments on a Dynamic_Post, THE System SHALL add the comment to the post and display it with the commenter's information
5. WHEN an Administrator reviews a Dynamic_Post, THE System SHALL allow approval or rejection with the ability to hide inappropriate content

### Requirement 7: Daily Check-In Tracking

**User Story:** As a regular user, I want to log my daily check-ins, so that I can track my consistency and build fitness habits

#### Acceptance Criteria

1. WHEN a Regular_User performs a check-in action on a given day, THE System SHALL create a Check_In record with the current date and time
2. WHEN a Regular_User views their check-in history, THE System SHALL display all Check_In records with dates and streak information
3. THE System SHALL prevent multiple check-ins on the same calendar day by the same user
4. WHEN a Regular_User checks in for consecutive days, THE System SHALL calculate and display the current streak count
5. WHEN a Regular_User views their profile, THE System SHALL display their total check-in count and longest streak

### Requirement 8: Diet Record Management

**User Story:** As a regular user, I want to log my meals and nutrition information, so that I can track my dietary habits alongside my fitness activities

#### Acceptance Criteria

1. WHEN a Regular_User creates a Diet_Record with meal type, food items, and calorie information, THE System SHALL store the record with timestamp
2. WHEN a Regular_User views their diet history, THE System SHALL display all Diet_Records organized by date
3. WHEN a Regular_User updates a Diet_Record, THE System SHALL save the changes and maintain the original creation timestamp
4. WHEN a Regular_User deletes a Diet_Record, THE System SHALL remove the record from the system
5. WHEN a Regular_User views daily summary, THE System SHALL calculate and display total calories consumed for the day

### Requirement 9: User and Coach Management (Administrator)

**User Story:** As an administrator, I want to manage all user and coach accounts, so that I can maintain platform integrity and handle account issues

#### Acceptance Criteria

1. WHEN an Administrator requests the user list, THE System SHALL display all registered users with their role, status, and registration date
2. WHEN an Administrator updates a user's information, THE System SHALL save the changes and apply them immediately
3. WHEN an Administrator deletes a user account, THE System SHALL remove the user and all associated data from the system
4. WHEN an Administrator searches for users by username or role, THE System SHALL display matching results
5. WHEN an Administrator views coach accounts, THE System SHALL display coach-specific information including student count and content statistics

### Requirement 10: Content Search and Discovery

**User Story:** As any user, I want to search for fitness resources, posts, and coaches, so that I can quickly find relevant content

#### Acceptance Criteria

1. WHEN a user enters a search query, THE System SHALL search across fitness resources, Dynamic_Posts, and coach profiles
2. WHEN search results are returned, THE System SHALL display them organized by content type with relevance ranking
3. THE System SHALL support partial text matching in titles, descriptions, and content fields
4. WHEN a user applies filters to search results, THE System SHALL refine the results based on selected criteria
5. WHEN no results match the search query, THE System SHALL display a message indicating no matches were found

### Requirement 11: File Upload Management

**User Story:** As a user or administrator, I want to upload images and videos, so that I can share visual content within the system

#### Acceptance Criteria

1. WHEN a user uploads an image file, THE System SHALL validate the file type and size before accepting the upload
2. WHEN a file upload is successful, THE System SHALL store the file and return a file URL to the uploader
3. THE System SHALL support image formats including JPG, PNG, and GIF with maximum size of 5 megabytes
4. THE System SHALL support video formats including MP4 and AVI with maximum size of 100 megabytes
5. WHEN a file upload fails validation, THE System SHALL display an error message with the specific validation failure reason

### Requirement 12: Coach Student Management

**User Story:** As a coach, I want to manage my student roster, so that I can track who I'm training and organize my coaching activities

#### Acceptance Criteria

1. WHEN a Regular_User subscribes to a Coach, THE System SHALL create a student-coach relationship record
2. WHEN a Coach views their student list, THE System SHALL display all students with their profile information and training progress
3. WHEN a Coach removes a student, THE System SHALL delete the relationship but preserve historical training plan data
4. WHEN a Regular_User views available coaches, THE System SHALL display all Coach profiles with their specialties and student count
5. WHEN a student-coach relationship exists, THE System SHALL allow the Coach to view the student's check-in and diet records

### Requirement 13: Training Progress Analytics (Coach)

**User Story:** As a coach, I want to view analytics on my students' training progress, so that I can assess effectiveness and adjust plans accordingly

#### Acceptance Criteria

1. WHEN a Coach requests progress analytics for a student, THE System SHALL calculate and display check-in frequency, plan completion rate, and activity trends
2. WHEN analytics data is displayed, THE System SHALL present it using charts and graphs for visual interpretation
3. THE System SHALL calculate progress metrics based on the most recent 30 days of activity data
4. WHEN a Coach compares multiple students, THE System SHALL display comparative analytics across selected students
5. WHEN insufficient data exists for analytics, THE System SHALL display a message indicating more data is needed

### Requirement 14: Content Creation (Coach)

**User Story:** As a coach, I want to create and publish fitness articles and videos, so that I can share my expertise with the community

#### Acceptance Criteria

1. WHEN a Coach creates content with title, description, content type, and media files, THE System SHALL publish the content to the fitness resources section
2. WHEN a Coach views their published content, THE System SHALL display all content items with view counts and engagement metrics
3. WHEN a Coach updates published content, THE System SHALL save the changes and update the last modified timestamp
4. WHEN a Coach deletes their content, THE System SHALL remove it from public view but retain it in the database for audit purposes
5. WHEN users view coach-created content, THE System SHALL display the coach's name and profile link with the content

### Requirement 15: Content Moderation (Administrator)

**User Story:** As an administrator, I want to review and moderate user-generated content, so that I can maintain community standards and remove inappropriate material

#### Acceptance Criteria

1. WHEN an Administrator accesses the moderation queue, THE System SHALL display all Dynamic_Posts and comments pending review
2. WHEN an Administrator approves content, THE System SHALL mark it as approved and keep it visible to users
3. WHEN an Administrator rejects content, THE System SHALL hide it from public view and notify the content creator
4. WHEN an Administrator views moderation history, THE System SHALL display all moderation actions with timestamps and reasons
5. THE System SHALL automatically flag content containing prohibited keywords for administrator review
