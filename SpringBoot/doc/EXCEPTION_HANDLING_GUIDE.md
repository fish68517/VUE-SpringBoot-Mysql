# Exception Handling Guide

## Overview

The Travel Memory System implements a comprehensive global exception handling mechanism using Spring's `@ControllerAdvice` annotation. This ensures consistent error responses across all API endpoints and provides proper logging for debugging.

## Exception Hierarchy

```
RuntimeException
├── BusinessException (HTTP 400 by default, customizable)
│   ├── ValidationException (HTTP 400)
│   └── ResourceNotFoundException (HTTP 404)
├── AccessDeniedException (HTTP 403)
└── IllegalArgumentException (HTTP 400)
```

## Exception Classes

### 1. BusinessException
**Location:** `com.travelMemory.common.exception.BusinessException`

Base exception for all business logic violations. Can be customized with specific HTTP status codes.

**Usage:**
```java
// Default HTTP 400
throw new BusinessException("Invalid operation");

// Custom HTTP status code
throw new BusinessException(422, "Unprocessable entity");
```

### 2. ValidationException
**Location:** `com.travelMemory.common.exception.ValidationException`

Thrown when input validation fails. Always returns HTTP 400.

**Usage:**
```java
throw new ValidationException("Email format is invalid");
throw new ValidationException("Password must be at least 6 characters");
```

### 3. ResourceNotFoundException
**Location:** `com.travelMemory.common.exception.ResourceNotFoundException`

Thrown when a requested resource is not found. Always returns HTTP 404.

**Usage:**
```java
// With custom message
throw new ResourceNotFoundException("User not found");

// With resource type and ID
throw new ResourceNotFoundException("TravelRecord", 123L);
```

### 4. AccessDeniedException
**Location:** `com.travelMemory.exception.AccessDeniedException`

Thrown when a user lacks permission to access a resource. Always returns HTTP 403.

**Usage:**
```java
throw new AccessDeniedException("You do not have permission to access this resource");
```

### 5. IllegalArgumentException
**Location:** `java.lang.IllegalArgumentException`

Standard Java exception for invalid arguments. Handled by global exception handler and returns HTTP 400.

**Usage:**
```java
throw new IllegalArgumentException("Invalid date range");
```

## Global Exception Handler

**Location:** `com.travelMemory.common.GlobalExceptionHandler`

The `GlobalExceptionHandler` class uses Spring's `@ControllerAdvice` to intercept all exceptions thrown by controllers and services. It converts exceptions to appropriate HTTP responses with unified error format.

### Handled Exceptions

| Exception | HTTP Status | Handler Method |
|-----------|------------|-----------------|
| AccessDeniedException | 403 Forbidden | handleAccessDeniedException |
| ValidationException | 400 Bad Request | handleValidationException |
| BusinessException | 400-599 (customizable) | handleBusinessException |
| ResourceNotFoundException | 404 Not Found | handleResourceNotFoundException |
| IllegalArgumentException | 400 Bad Request | handleIllegalArgumentException |
| MethodArgumentNotValidException | 400 Bad Request | handleMethodArgumentNotValid |
| Exception (all others) | 500 Internal Server Error | handleGlobalException |

## Unified Response Format

All API responses follow a consistent JSON format:

### Success Response
```json
{
  "code": 200,
  "message": "Success",
  "data": { /* response data */ },
  "timestamp": "2024-01-01T12:00:00"
}
```

### Error Response
```json
{
  "code": 400,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Email format is invalid"
    }
  ],
  "timestamp": "2024-01-01T12:00:00"
}
```

### Not Found Response
```json
{
  "code": 404,
  "message": "User with id 123 not found",
  "timestamp": "2024-01-01T12:00:00"
}
```

### Access Denied Response
```json
{
  "code": 403,
  "message": "You do not have permission to access this resource",
  "timestamp": "2024-01-01T12:00:00"
}
```

## Logging Strategy

The exception handler implements a tiered logging approach:

- **WARN level**: For expected exceptions (validation errors, access denied, not found)
- **ERROR level**: For unexpected exceptions with full stack trace

### Log Examples

```
WARN: Access denied: You do not have permission to access this resource
WARN: Validation error: Email format is invalid
WARN: Illegal argument: Start date must be before or equal to end date
ERROR: Unexpected error occurred: java.lang.NullPointerException
```

## Best Practices

### 1. Use Specific Exceptions
```java
// Good
throw new ValidationException("Email format is invalid");

// Avoid
throw new Exception("Error");
```

### 2. Provide Clear Error Messages
```java
// Good
throw new ResourceNotFoundException("TravelRecord", recordId);

// Avoid
throw new ResourceNotFoundException("Not found");
```

### 3. Use Appropriate HTTP Status Codes
```java
// Good - 404 for missing resources
throw new ResourceNotFoundException("User not found");

// Good - 403 for permission issues
throw new AccessDeniedException("Access denied");

// Good - 400 for validation errors
throw new ValidationException("Invalid input");
```

### 4. Log Sensitive Information Carefully
```java
// Good - log user action without sensitive data
logger.warn("User {} attempted to access record {}", userId, recordId);

// Avoid - logging sensitive data
logger.warn("User {} with password {} attempted access", username, password);
```

## Integration with Services

Services should throw appropriate exceptions for different error scenarios:

```java
@Service
public class UserService {
    
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }
    
    public void updateUser(Long userId, UserUpdateRequest request) {
        User user = getUserById(userId);
        
        if (!isValidEmail(request.getEmail())) {
            throw new ValidationException("Email format is invalid");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }
        
        user.setEmail(request.getEmail());
        userRepository.save(user);
    }
}
```

## Integration with Controllers

Controllers should not catch exceptions - let them propagate to the global exception handler:

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        // Exception will be caught by GlobalExceptionHandler
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(user)));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        // Validation exceptions and business exceptions will be caught
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(user)));
    }
}
```

## Testing Exception Handling

Example unit test for exception handling:

```java
@SpringBootTest
public class ExceptionHandlingTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testResourceNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    public void testValidationError() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"invalid\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400));
    }
}
```

## Future Enhancements

1. **Custom Exception Types**: Add more specific exceptions for different domains (e.g., `PaymentException`, `FileUploadException`)
2. **Error Codes**: Implement error code system for better client-side error handling
3. **Internationalization**: Support error messages in multiple languages
4. **Error Tracking**: Integrate with error tracking services (e.g., Sentry)
5. **Rate Limiting**: Add rate limiting exceptions for API throttling
