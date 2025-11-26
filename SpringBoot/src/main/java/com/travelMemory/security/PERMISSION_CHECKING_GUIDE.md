# Permission Checking and Access Control Guide

## Overview

This guide explains how to implement permission checking and access control in the Travel Memory System. The system provides multiple layers of permission verification to ensure users can only access and modify resources they own.

## Components

### 1. PermissionService
Located in: `com.travelMemory.security.PermissionService`

The core service for permission checking. It provides methods to:
- Get the current authenticated user ID
- Check if a user is the owner of a resource
- Check if a user has access to a resource (owner or public)
- Verify ownership and access with exceptions

**Key Methods:**
```java
// Get current user ID
Long getCurrentUserId()

// Check ownership
boolean isResourceOwner(Long resourceOwnerId)

// Check access (owner or public)
boolean hasAccessToResource(Long resourceOwnerId, boolean isPublic)

// Verify with exceptions
void verifyResourceOwner(Long resourceOwnerId)
void verifyResourceAccess(Long resourceOwnerId, boolean isPublic)
```

### 2. PermissionUtils
Located in: `com.travelMemory.utils.PermissionUtils`

Utility class providing domain-specific permission checking methods:
- `isTravelRecordOwner(Long recordOwnerId)`
- `canViewTravelRecord(Long recordOwnerId, boolean isPublic)`
- `canEditTravelRecord(Long recordOwnerId)`
- `canDeleteTravelRecord(Long recordOwnerId)`
- `isTravelPlanOwner(Long planOwnerId)`
- `canEditTravelPlan(Long planOwnerId)`
- `canDeleteTravelPlan(Long planOwnerId)`
- `canDeleteComment(Long commentAuthorId)`

### 3. RequireResourceOwner Annotation
Located in: `com.travelMemory.security.RequireResourceOwner`

Custom annotation for method-level permission checking:
```java
@RequireResourceOwner(paramName = "userId")
public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long userId, ...) {
    // Method body
}
```

### 4. PermissionCheckAspect
Located in: `com.travelMemory.security.PermissionCheckAspect`

AOP aspect that intercepts methods annotated with `@RequireResourceOwner` and performs permission verification before method execution.

### 5. AccessDeniedException
Located in: `com.travelMemory.exception.AccessDeniedException`

Custom exception thrown when a user attempts to access a resource they don't have permission to access.

## Usage Patterns

### Pattern 1: Direct Permission Verification in Controller

```java
@PutMapping("/{id}")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<ApiResponse<UserResponse>> updateUser(
        @PathVariable Long id,
        @Valid @RequestBody UpdateUserRequest updateRequest) {
    try {
        // Verify that the current user is updating their own profile
        permissionService.verifyResourceOwner(id);
        
        UserResponse userResponse = userService.updateUser(id, updateRequest);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    } catch (AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, e.getMessage()));
    }
}
```

### Pattern 2: Using @RequireResourceOwner Annotation

```java
@PutMapping("/{id}")
@PreAuthorize("hasRole('USER')")
@RequireResourceOwner(paramName = "id")
public ResponseEntity<ApiResponse<UserResponse>> updateUser(
        @PathVariable Long id,
        @Valid @RequestBody UpdateUserRequest updateRequest) {
    // Permission check is automatically performed by the aspect
    UserResponse userResponse = userService.updateUser(id, updateRequest);
    return ResponseEntity.ok(ApiResponse.success(userResponse));
}
```

### Pattern 3: Using PermissionUtils in Service Layer

```java
@Service
public class TravelService {
    
    @Autowired
    private PermissionUtils permissionUtils;
    
    public TravelRecordResponse updateTravelRecord(Long recordId, UpdateTravelRecordRequest request) {
        TravelRecord record = travelRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));
        
        // Verify ownership
        permissionUtils.verifyOwnership(record.getUserId());
        
        // Update record
        record.setTitle(request.getTitle());
        record.setDescription(request.getDescription());
        
        return convertToResponse(travelRecordRepository.save(record));
    }
}
```

### Pattern 4: Checking Access to Public Resources

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<TravelRecordResponse>> getTravelRecord(@PathVariable Long id) {
    TravelRecord record = travelRecordRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Record not found"));
    
    // Verify access (owner or public)
    permissionService.verifyResourceAccess(record.getUserId(), record.isPublic());
    
    return ResponseEntity.ok(ApiResponse.success(convertToResponse(record)));
}
```

## Security Configuration

The security configuration is set up in `SecurityConfig` with the following features:

1. **Method-Level Security**: `@EnableMethodSecurity(prePostEnabled = true)` enables `@PreAuthorize` annotations
2. **AspectJ Support**: `@EnableAspectJAutoProxy` enables AOP for permission checking
3. **JWT Authentication**: Stateless authentication using JWT tokens
4. **CORS Configuration**: Configured for frontend applications

## Exception Handling

All permission-related exceptions are handled by the `GlobalExceptionHandler`:

```java
@ExceptionHandler(AccessDeniedException.class)
@ResponseStatus(HttpStatus.FORBIDDEN)
public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(403, ex.getMessage()));
}
```

## Best Practices

1. **Always verify ownership before modifying resources**: Use `permissionService.verifyResourceOwner()` or the `@RequireResourceOwner` annotation
2. **Check access before reading resources**: Use `permissionService.verifyResourceAccess()` for resources that can be public
3. **Use domain-specific methods**: Use `PermissionUtils` methods for better code readability
4. **Handle exceptions appropriately**: Catch `AccessDeniedException` and return 403 Forbidden status
5. **Combine with @PreAuthorize**: Use `@PreAuthorize("hasRole('USER')")` to ensure user is authenticated before permission checks

## Implementation Checklist

When implementing permission checking for a new resource:

- [ ] Add permission verification in the controller method
- [ ] Use `@PreAuthorize` to ensure user is authenticated
- [ ] Use `permissionService.verifyResourceOwner()` for modification operations
- [ ] Use `permissionService.verifyResourceAccess()` for read operations on public resources
- [ ] Handle `AccessDeniedException` in the controller
- [ ] Add appropriate HTTP status codes (403 for forbidden)
- [ ] Test permission checks with different users

## Example: Complete Implementation

```java
@RestController
@RequestMapping("/api/travels")
@RequiredArgsConstructor
public class TravelController {
    
    private final TravelService travelService;
    private final PermissionService permissionService;
    
    // Create travel record (owner only)
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TravelRecordResponse>> createTravelRecord(
            @Valid @RequestBody CreateTravelRecordRequest request) {
        TravelRecordResponse response = travelService.createTravelRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
    
    // Get travel record (owner or public)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TravelRecordResponse>> getTravelRecord(@PathVariable Long id) {
        try {
            TravelRecordResponse response = travelService.getTravelRecord(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        }
    }
    
    // Update travel record (owner only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TravelRecordResponse>> updateTravelRecord(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTravelRecordRequest request) {
        try {
            TravelRecordResponse response = travelService.updateTravelRecord(id, request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        }
    }
    
    // Delete travel record (owner only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteTravelRecord(@PathVariable Long id) {
        try {
            travelService.deleteTravelRecord(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        }
    }
}
```

## Testing Permission Checks

When testing permission checks, ensure you:

1. Test with the resource owner (should succeed)
2. Test with a different user (should fail with 403)
3. Test with unauthenticated user (should fail with 401)
4. Test public vs private resources appropriately

Example test:
```java
@Test
public void testUpdateUserProfile_AsOwner_Success() {
    // Should succeed
}

@Test
public void testUpdateUserProfile_AsDifferentUser_Forbidden() {
    // Should return 403 Forbidden
}

@Test
public void testUpdateUserProfile_Unauthenticated_Unauthorized() {
    // Should return 401 Unauthorized
}
```
