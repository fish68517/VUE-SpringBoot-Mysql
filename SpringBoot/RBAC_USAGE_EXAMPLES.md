# RBAC Usage Examples

This document provides practical examples of how to use the @RequireRole annotation in your controllers.

## Example 1: Admin-Only Endpoint

```java
@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    @GetMapping
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "Get all users", description = "Admin only - retrieve all users in the system")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<UserDTO> users = userService.getAllUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @PutMapping("/{id}/status")
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "Update user status", description = "Admin only - enable or disable user account")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UpdateUserStatusRequest request) {
        
        UserDTO user = userService.updateUserStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("User status updated", user));
    }
}
```

## Example 2: Organizer-Only Endpoint

```java
@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @PostMapping
    @RequireRole(UserRole.ORGANIZER)
    @Operation(summary = "Create activity", description = "Organizer only - create a new activity")
    public ResponseEntity<ApiResponse<ActivityDTO>> createActivity(
            @Valid @RequestBody CreateActivityRequest request) {
        
        ActivityDTO activity = activityService.createActivity(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Activity created successfully", activity));
    }

    @PutMapping("/{id}")
    @RequireRole(UserRole.ORGANIZER)
    @Operation(summary = "Update activity", description = "Organizer only - update activity information")
    public ResponseEntity<ApiResponse<ActivityDTO>> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody UpdateActivityRequest request) {
        
        ActivityDTO activity = activityService.updateActivity(id, request);
        return ResponseEntity.ok(ApiResponse.success("Activity updated successfully", activity));
    }

    @DeleteMapping("/{id}")
    @RequireRole(UserRole.ORGANIZER)
    @Operation(summary = "Delete activity", description = "Organizer only - delete an activity")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable Long id) {
        
        activityService.deleteActivity(id);
        return ResponseEntity.ok(ApiResponse.success("Activity deleted successfully", null));
    }
}
```

## Example 3: Multiple Roles

```java
@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @GetMapping("/activities")
    @RequireRole({UserRole.ADMIN, UserRole.ORGANIZER})
    @Operation(summary = "Get audit activities", description = "Admin or Organizer - view activities pending audit")
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getAuditActivities() {
        
        List<ActivityDTO> activities = auditService.getPendingActivities();
        return ResponseEntity.ok(ApiResponse.success("Audit activities retrieved", activities));
    }

    @PutMapping("/activities/{id}")
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "Audit activity", description = "Admin only - approve or reject activity")
    public ResponseEntity<ApiResponse<ActivityDTO>> auditActivity(
            @PathVariable Long id,
            @Valid @RequestBody AuditRequest request) {
        
        ActivityDTO activity = auditService.auditActivity(id, request);
        return ResponseEntity.ok(ApiResponse.success("Activity audited successfully", activity));
    }
}
```

## Example 4: User-Only Endpoint

```java
@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    @PostMapping
    @RequireRole(UserRole.USER)
    @Operation(summary = "Register for activity", description = "User only - register for an activity")
    public ResponseEntity<ApiResponse<RegistrationDTO>> registerForActivity(
            @Valid @RequestBody RegisterActivityRequest request) {
        
        RegistrationDTO registration = registrationService.registerForActivity(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registered successfully", registration));
    }

    @DeleteMapping("/{id}")
    @RequireRole(UserRole.USER)
    @Operation(summary = "Cancel registration", description = "User only - cancel activity registration")
    public ResponseEntity<ApiResponse<Void>> cancelRegistration(@PathVariable Long id) {
        
        registrationService.cancelRegistration(id);
        return ResponseEntity.ok(ApiResponse.success("Registration cancelled", null));
    }
}
```

## Example 5: Public Endpoint (No Role Required)

```java
@RestController
@RequestMapping("/api/activities")
public class ActivityBrowseController {

    @GetMapping
    @Operation(summary = "Get activities", description = "Public - browse all approved activities")
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getActivities(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<ActivityDTO> activities = activityService.getApprovedActivities(type, page, size);
        return ResponseEntity.ok(ApiResponse.success("Activities retrieved", activities));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get activity details", description = "Public - view activity details")
    public ResponseEntity<ApiResponse<ActivityDTO>> getActivityDetails(@PathVariable Long id) {
        
        ActivityDTO activity = activityService.getActivityById(id);
        return ResponseEntity.ok(ApiResponse.success("Activity retrieved", activity));
    }
}
```

## Example 6: Feedback Management

```java
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @PostMapping
    @RequireRole(UserRole.USER)
    @Operation(summary = "Submit feedback", description = "User only - submit feedback for an activity")
    public ResponseEntity<ApiResponse<FeedbackDTO>> submitFeedback(
            @Valid @RequestBody SubmitFeedbackRequest request) {
        
        FeedbackDTO feedback = feedbackService.submitFeedback(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Feedback submitted successfully", feedback));
    }

    @PutMapping("/{id}/reply")
    @RequireRole(UserRole.ORGANIZER)
    @Operation(summary = "Reply to feedback", description = "Organizer only - reply to user feedback")
    public ResponseEntity<ApiResponse<FeedbackDTO>> replyToFeedback(
            @PathVariable Long id,
            @Valid @RequestBody ReplyFeedbackRequest request) {
        
        FeedbackDTO feedback = feedbackService.replyToFeedback(id, request);
        return ResponseEntity.ok(ApiResponse.success("Reply submitted successfully", feedback));
    }

    @GetMapping("/activity/{activityId}")
    @Operation(summary = "Get activity feedback", description = "Public - view feedback for an activity")
    public ResponseEntity<ApiResponse<List<FeedbackDTO>>> getActivityFeedback(
            @PathVariable Long activityId) {
        
        List<FeedbackDTO> feedbacks = feedbackService.getActivityFeedback(activityId);
        return ResponseEntity.ok(ApiResponse.success("Feedback retrieved", feedbacks));
    }
}
```

## Example 7: Crowdfunding Management

```java
@RestController
@RequestMapping("/api/crowdfunding")
public class CrowdfundingController {

    @PostMapping("/support")
    @RequireRole(UserRole.USER)
    @Operation(summary = "Support crowdfunding", description = "User only - support activity crowdfunding")
    public ResponseEntity<ApiResponse<CrowdfundingSupportDTO>> supportCrowdfunding(
            @Valid @RequestBody SupportCrowdfundingRequest request) {
        
        CrowdfundingSupportDTO support = crowdfundingService.supportCrowdfunding(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Crowdfunding support recorded", support));
    }

    @GetMapping("/my")
    @RequireRole(UserRole.USER)
    @Operation(summary = "Get my crowdfunding supports", description = "User only - view my crowdfunding supports")
    public ResponseEntity<ApiResponse<List<CrowdfundingSupportDTO>>> getMySupports() {
        
        List<CrowdfundingSupportDTO> supports = crowdfundingService.getUserSupports();
        return ResponseEntity.ok(ApiResponse.success("Supports retrieved", supports));
    }

    @GetMapping("/activity/{activityId}")
    @Operation(summary = "Get crowdfunding details", description = "Public - view crowdfunding details")
    public ResponseEntity<ApiResponse<CrowdfundingDetailsDTO>> getCrowdfundingDetails(
            @PathVariable Long activityId) {
        
        CrowdfundingDetailsDTO details = crowdfundingService.getCrowdfundingDetails(activityId);
        return ResponseEntity.ok(ApiResponse.success("Crowdfunding details retrieved", details));
    }
}
```

## Example 8: Statistics and Reporting

```java
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @GetMapping("/dashboard")
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "Get dashboard statistics", description = "Admin only - view platform statistics")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats() {
        
        DashboardStatsDTO stats = statisticsService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", stats));
    }

    @GetMapping("/activities")
    @RequireRole(UserRole.ORGANIZER)
    @Operation(summary = "Get activity statistics", description = "Organizer only - view my activity statistics")
    public ResponseEntity<ApiResponse<ActivityStatsDTO>> getActivityStats() {
        
        ActivityStatsDTO stats = statisticsService.getOrganizerActivityStats();
        return ResponseEntity.ok(ApiResponse.success("Activity stats retrieved", stats));
    }

    @GetMapping("/export")
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "Export statistics", description = "Admin only - export platform statistics as Excel")
    public ResponseEntity<byte[]> exportStatistics() {
        
        byte[] excelData = statisticsService.exportStatistics();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=statistics.xlsx")
                .body(excelData);
    }
}
```

## Error Responses

### Unauthorized Access (Missing Token)

```
GET /api/users
```

Response:
```json
{
  "code": 403,
  "message": "User is not authenticated",
  "data": null
}
```

### Insufficient Permissions

```
GET /api/users
Authorization: Bearer <user_token>
```

Response (User token trying to access admin endpoint):
```json
{
  "code": 403,
  "message": "User does not have required role to access this resource",
  "data": null
}
```

## Best Practices

1. **Always use @RequireRole for protected endpoints**
   ```java
   @PostMapping
   @RequireRole(UserRole.ADMIN)  // Don't forget this!
   public ResponseEntity<ApiResponse<...>> adminOperation(...) { }
   ```

2. **Use specific roles, not generic permissions**
   ```java
   // Good
   @RequireRole(UserRole.ADMIN)
   
   // Avoid
   @RequireRole(UserRole.USER)  // Too permissive
   ```

3. **Document role requirements in Swagger**
   ```java
   @Operation(summary = "...", description = "Admin only - ...")
   @RequireRole(UserRole.ADMIN)
   ```

4. **Handle multiple roles when needed**
   ```java
   @RequireRole({UserRole.ADMIN, UserRole.ORGANIZER})
   ```

5. **Log access attempts**
   - RoleCheckAspect automatically logs all access attempts
   - Check logs for security audit trail

## Testing with cURL

```bash
# 1. Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'

# 2. Login to get token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 3. Use token to access protected endpoint
curl -X GET http://localhost:8080/api/test/user-only \
  -H "Authorization: Bearer <token_from_login>"

# 4. Try to access admin endpoint (should fail)
curl -X GET http://localhost:8080/api/test/admin-only \
  -H "Authorization: Bearer <user_token>"
```
