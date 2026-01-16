# Requirements Document: Postgraduate School Selection System

## Introduction

This is a comprehensive system for Computer Science postgraduate entrance exam candidates in Jiangsu Province to manage their school selection process. The system provides tools for candidates to maintain personal profiles, search and filter schools based on their criteria, manage favorite schools, participate in discussions, and submit feedback. Administrators can manage users, schools, announcements, and feedback. A key innovation is the collection statistics feature that shows demographic insights about users who have favorited each school.

## Glossary

- **System**: Postgraduate School Selection System
- **USER**: Regular user role with limited permissions (candidates)
- **ADMIN**: Administrator role with full system management permissions
- **School Tier**: Classification of schools (985/211/Double-Non/Other)
- **Undergraduate Tier**: Classification of user's undergraduate institution
- **CET Score**: College English Test score (CET-4 or CET-6)
- **Target Score**: Expected postgraduate entrance exam score
- **Favorite**: A school saved by a user for later reference
- **Favorite Stats**: Statistical distribution of user demographics who favorited a school
- **Comment**: User-generated discussion post about a school
- **Feedback**: User-submitted bug reports, suggestions, or data corrections
- **Announcement**: System-wide message published by administrators
- **RBAC**: Role-Based Access Control

## Requirements

### Requirement 1: User Authentication and Account Management

**User Story:** As a user, I want to register an account and log in securely, so that I can access personalized features and maintain my profile.

#### Acceptance Criteria

1. WHEN a user submits registration with username and password, THE System SHALL create a new USER account with ENABLED status
2. WHEN a user submits login credentials, THE System SHALL authenticate and return a session token if credentials are valid
3. WHEN a user is logged in, THE System SHALL allow access to personal profile and user-specific features
4. WHEN an admin disables a user account, THE System SHALL prevent that user from logging in
5. WHEN an admin resets a user password, THE System SHALL generate a temporary password and notify the user

### Requirement 2: User Profile Management

**User Story:** As a user, I want to create and update my personal profile with academic information, so that the system can provide relevant school recommendations and statistics.

#### Acceptance Criteria

1. WHEN a user accesses their profile, THE System SHALL display all profile fields including undergradTier, GPA, CET scores, and target score
2. WHEN a user updates their profile, THE System SHALL validate and persist all changes with timestamps
3. WHEN a user saves their profile, THE System SHALL store undergradTier (985/211/Double-Non/Other), GPA with scale, CET-4 score, CET-6 score, and target score
4. WHERE a user has optional fields like otherScores, THE System SHALL allow flexible storage of additional score information
5. WHEN a user views their profile, THE System SHALL display all previously saved information accurately

### Requirement 3: School Search and Filtering

**User Story:** As a user, I want to search and filter schools by multiple criteria, so that I can find schools matching my academic profile and preferences.

#### Acceptance Criteria

1. WHEN a user accesses the school search page, THE System SHALL display all schools with pagination
2. WHEN a user filters by region, THE System SHALL return only schools located in the selected Jiangsu cities
3. WHEN a user filters by school tier, THE System SHALL return only schools matching the selected tier (985/211/Double-Non/Other)
4. WHEN a user filters by expected score range, THE System SHALL return schools where the cutoff score falls within the range
5. WHEN a user filters by major or direction, THE System SHALL return schools offering the selected major
6. WHEN a user combines multiple filters, THE System SHALL apply all filters simultaneously and return matching results
7. WHEN a user views search results, THE System SHALL display school name, tier, location, and key information

### Requirement 4: School Details and Information

**User Story:** As a user, I want to view comprehensive information about a school, so that I can make informed decisions about my application.

#### Acceptance Criteria

1. WHEN a user views a school detail page, THE System SHALL display school name, tier, location, and introduction
2. WHEN a user views a school detail page, THE System SHALL display all exam subjects required for that school
3. WHEN a user views a school detail page, THE System SHALL display reexamination requirements and policies
4. WHEN a user views a school detail page, THE System SHALL display the favorite button and current favorite status
5. WHEN a user views a school detail page, THE System SHALL display discussion comments section
6. WHEN a user views a school detail page, THE System SHALL display favorite statistics showing demographic distribution

### Requirement 5: School Favorites Management

**User Story:** As a user, I want to save schools to my favorites list, so that I can quickly access and manage my target schools.

#### Acceptance Criteria

1. WHEN a user clicks the favorite button on a school detail page, THE System SHALL add that school to the user's favorites
2. WHEN a user clicks the favorite button again, THE System SHALL remove that school from the user's favorites
3. WHEN a user views their favorites list, THE System SHALL display all favorited schools with core information
4. WHEN a user views their favorites list, THE System SHALL display schools in the order they were favorited
5. WHEN a user removes a school from favorites, THE System SHALL immediately update the favorite statistics for that school

### Requirement 6: Favorite Statistics and Demographics

**User Story:** As a user, I want to see demographic insights about other users who favorited a school, so that I can understand the typical profile of candidates applying to that school.

#### Acceptance Criteria

1. WHEN a user views a school detail page, THE System SHALL calculate and display the distribution of undergradTier among all users who favorited that school
2. WHEN a user views a school detail page, THE System SHALL calculate and display the distribution of CET-4 scores in buckets (below 425, 425-500, 501-550, 551+) among all users who favorited that school
3. WHEN a user favorites or unfavorites a school, THE System SHALL update the statistics for that school in real-time
4. WHEN statistics are displayed, THE System SHALL show percentages or counts for each demographic category
5. WHERE no users have favorited a school, THE System SHALL display a message indicating insufficient data for statistics

### Requirement 7: School Discussion and Comments

**User Story:** As a user, I want to participate in discussions about schools, so that I can share experiences and learn from other candidates.

#### Acceptance Criteria

1. WHEN a user views a school detail page, THE System SHALL display all comments for that school in paginated format
2. WHEN a user submits a comment, THE System SHALL create a new comment associated with that school and user
3. WHEN a user submits a reply to a comment, THE System SHALL create a nested comment linked to the parent comment
4. WHEN a user views comments, THE System SHALL display comment content, author, and creation timestamp
5. WHEN an admin deletes a comment, THE System SHALL mark the comment as DELETED and hide it from display
6. WHEN a user views comments, THE System SHALL only display comments with NORMAL status

### Requirement 8: User Feedback System

**User Story:** As a user, I want to submit feedback about bugs, suggestions, or data errors, so that I can help improve the system.

#### Acceptance Criteria

1. WHEN a user accesses the feedback page, THE System SHALL display a form to submit new feedback
2. WHEN a user submits feedback, THE System SHALL store the feedback type (BUG/SUGGESTION/DATA_ERROR), content, and creation timestamp
3. WHEN a user submits feedback, THE System SHALL set the initial status to NEW
4. WHEN a user views their feedback history, THE System SHALL display all feedback submitted by that user with status and admin replies
5. WHEN an admin replies to feedback, THE System SHALL store the reply and update the status to PROCESSING or DONE
6. WHEN feedback status is updated, THE System SHALL display the updated status to the user

### Requirement 9: Administrator User Management

**User Story:** As an admin, I want to manage user accounts and permissions, so that I can maintain system security and user access control.

#### Acceptance Criteria

1. WHEN an admin accesses the user management page, THE System SHALL display all registered users with pagination and search
2. WHEN an admin searches for users, THE System SHALL filter by username or other identifying information
3. WHEN an admin filters by status, THE System SHALL display only users with the selected status (ENABLED/DISABLED)
4. WHEN an admin disables a user account, THE System SHALL set the user status to DISABLED and prevent login
5. WHEN an admin enables a user account, THE System SHALL set the user status to ENABLED and allow login
6. WHEN an admin resets a user password, THE System SHALL generate a temporary password for that user

### Requirement 10: Administrator School Management

**User Story:** As an admin, I want to manage school information and associated data, so that the system maintains accurate and up-to-date school information.

#### Acceptance Criteria

1. WHEN an admin accesses the school management page, THE System SHALL display all schools with CRUD operations
2. WHEN an admin creates a school, THE System SHALL store school name, city, tier, website links, and introduction
3. WHEN an admin updates school information, THE System SHALL persist all changes with timestamps
4. WHEN an admin deletes a school, THE System SHALL remove the school from the system
5. WHEN an admin manages schools, THE System SHALL allow binding majors and exam subjects to schools
6. WHEN an admin creates a major, THE System SHALL associate it with a school and store major name and direction
7. WHEN an admin creates exam subjects, THE System SHALL associate them with schools and majors

### Requirement 11: Administrator Announcement Management

**User Story:** As an admin, I want to publish and manage system announcements, so that I can communicate important information to all users.

#### Acceptance Criteria

1. WHEN an admin accesses the announcement management page, THE System SHALL display all announcements with CRUD operations
2. WHEN an admin creates an announcement, THE System SHALL store title, rich-text content, and creation timestamp
3. WHEN an admin publishes an announcement, THE System SHALL set status to PUBLISHED and make it visible to users
4. WHEN an admin saves an announcement as draft, THE System SHALL set status to DRAFT and hide it from users
5. WHEN an admin sets announcement sort order, THE System SHALL display announcements in the specified order
6. WHEN an admin updates an announcement, THE System SHALL persist all changes with timestamps
7. WHEN a user views announcements, THE System SHALL display only PUBLISHED announcements in sort order

### Requirement 12: Administrator Feedback Management

**User Story:** As an admin, I want to manage user feedback and respond to issues, so that I can track and resolve user concerns.

#### Acceptance Criteria

1. WHEN an admin accesses the feedback management page, THE System SHALL display all feedback with pagination
2. WHEN an admin filters feedback by status, THE System SHALL display only feedback with the selected status (NEW/PROCESSING/DONE)
3. WHEN an admin filters feedback by type, THE System SHALL display only feedback with the selected type (BUG/SUGGESTION/DATA_ERROR)
4. WHEN an admin searches feedback, THE System SHALL filter by content or user information
5. WHEN an admin replies to feedback, THE System SHALL store the admin reply and update the status
6. WHEN an admin marks feedback as DONE, THE System SHALL update the status and timestamp
7. WHEN a user views their feedback, THE System SHALL display the admin reply if one exists

### Requirement 13: Role-Based Access Control

**User Story:** As a system, I want to enforce role-based access control, so that users can only access features appropriate for their role.

#### Acceptance Criteria

1. WHEN a USER attempts to access admin features, THE System SHALL deny access and redirect to user dashboard
2. WHEN an ADMIN accesses the system, THE System SHALL grant access to all admin management pages
3. WHEN a USER accesses the system, THE System SHALL grant access only to user features (profile, search, favorites, feedback)
4. WHEN an unauthenticated user attempts to access protected features, THE System SHALL redirect to login page
5. WHEN a user logs out, THE System SHALL invalidate their session and require re-authentication

### Requirement 14: Data Persistence and Audit

**User Story:** As a system, I want to maintain data integrity and track changes, so that I can ensure data consistency and provide audit trails.

#### Acceptance Criteria

1. WHEN any entity is created, THE System SHALL automatically record createdAt timestamp
2. WHEN any entity is updated, THE System SHALL automatically record updatedAt timestamp
3. WHEN data is deleted, THE System SHALL mark the record as deleted rather than permanently removing it
4. WHEN viewing data, THE System SHALL exclude soft-deleted records from display
5. WHEN an admin performs critical operations, THE System SHALL record the admin user who performed the operation
