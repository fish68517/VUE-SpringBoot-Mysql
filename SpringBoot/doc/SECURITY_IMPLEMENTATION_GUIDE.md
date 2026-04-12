# Security Implementation Guide

This document describes the security measures implemented in the Travel Memory System to protect against common web vulnerabilities.

## 1. HTTPS and CORS Configuration

### HTTPS (Secure Transport)
- **Implementation**: Configured via `Strict-Transport-Security` header
- **Location**: `SecurityHeadersConfig.java`
- **Details**:
  - HSTS header enforces HTTPS for all connections
  - Max age: 31536000 seconds (1 year)
  - Includes subdomains and preload directive
  - In production, configure SSL/TLS certificates on the server

### CORS (Cross-Origin Resource Sharing)
- **Implementation**: Configured in `SecurityConfig.java`
- **Allowed Origins**: 
  - `http://localhost:5173` (Vue dev server)
  - `http://localhost:3000` (Alternative dev server)
  - `http://localhost:8080` (Backend for testing)
- **Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS, PATCH
- **Allowed Headers**: Content-Type, Authorization, X-Requested-With, Accept, Origin
- **Exposed Headers**: Authorization, Content-Type, X-Total-Count
- **Credentials**: Allowed (for JWT token transmission)
- **Max Age**: 3600 seconds (1 hour)

**Production Recommendation**: Update allowed origins to match your production domain.

## 2. CSRF (Cross-Site Request Forgery) Protection

### Implementation Strategy
- **JWT-Based API**: CSRF protection is disabled for JWT-based APIs because JWT tokens provide inherent CSRF protection
- **Reason**: JWT tokens are stored in HTTP-only cookies or Authorization headers, not in request bodies
- **Alternative**: For traditional form-based authentication, CSRF tokens should be enabled

### CSRF Token Provider
- **Class**: `CsrfTokenProvider.java`
- **Features**:
  - Generates cryptographically secure random tokens
  - Validates token format
  - Uses Base64 URL-safe encoding

### Best Practices
1. Store JWT tokens in HTTP-only cookies (not localStorage)
2. Use SameSite cookie attribute to prevent CSRF
3. Validate Origin and Referer headers on sensitive operations

## 3. XSS (Cross-Site Scripting) Prevention

### Input Sanitization
- **Class**: `SecurityUtils.java`
- **Methods**:
  - `sanitizeHtml()`: Removes dangerous HTML tags, allows safe formatting
  - `sanitizeText()`: Escapes HTML special characters
  - `sanitizeEmail()`: Validates and sanitizes email addresses
  - `sanitizeUsername()`: Validates and sanitizes usernames
  - `sanitizeSearchQuery()`: Sanitizes search parameters
  - `sanitizeFileName()`: Prevents directory traversal attacks

### HTML Policy
- **Allowed Tags**: p, br, strong, em, u, h1-h6, ul, ol, li, blockquote, a, img
- **Allowed Attributes**: href (for links), src/alt/title (for images), class (global)
- **Implementation**: Uses OWASP HTML Sanitizer library

### Content Security Policy (CSP)
- **Location**: `SecurityHeadersConfig.java`
- **Directives**:
  - `default-src 'self'`: Only allow resources from same origin
  - `script-src 'self' 'unsafe-inline' 'unsafe-eval'`: Allow scripts from same origin
  - `style-src 'self' 'unsafe-inline'`: Allow styles from same origin
  - `img-src 'self' data: https:`: Allow images from same origin, data URIs, and HTTPS
  - `connect-src 'self' https:`: Allow connections to same origin and HTTPS
  - `frame-ancestors 'none'`: Prevent embedding in iframes
  - `form-action 'self'`: Only allow form submissions to same origin

### Security Headers
- **X-Frame-Options**: DENY (prevent clickjacking)
- **X-Content-Type-Options**: nosniff (prevent MIME type sniffing)
- **X-XSS-Protection**: 1; mode=block (enable XSS protection in older browsers)
- **Referrer-Policy**: strict-origin-when-cross-origin (control referrer information)
- **Permissions-Policy**: Disable dangerous features (geolocation, microphone, camera, etc.)

## 4. SQL Injection Prevention

### Parameterized Queries
- **Implementation**: Uses Spring Data JPA with parameterized queries
- **Benefit**: All database queries use prepared statements, preventing SQL injection
- **Example**:
  ```java
  // Safe - uses parameterized query
  userRepository.findByEmail(email);
  
  // Unsafe - would concatenate strings (NOT used in this project)
  // String query = "SELECT * FROM users WHERE email = '" + email + "'";
  ```

### Input Validation
- **Location**: `InputValidationInterceptor.java`
- **Checks**:
  - Validates HTTP method
  - Checks for directory traversal patterns (`../`, `..\\`)
  - Detects null byte injection (`\0`)
  - Identifies common SQL injection patterns
  - Detects XSS patterns in URLs and query strings

### Validation in Services
- **Location**: `SecurityUtils.java`
- **Methods**:
  - `validateNumericId()`: Ensures IDs are positive numbers
  - `containsSqlInjectionPattern()`: Detects SQL keywords in input
  - `sanitizeSearchQuery()`: Removes dangerous characters from search queries

## 5. Authentication and Authorization

### JWT (JSON Web Token)
- **Implementation**: `JwtTokenProvider.java`
- **Token Expiration**: 24 hours
- **Storage**: HTTP-only cookies or Authorization header
- **Validation**: Verified on every protected request

### Password Security
- **Hashing Algorithm**: BCrypt
- **Implementation**: `SecurityConfig.java` - `BCryptPasswordEncoder`
- **Strength**: Automatically handles salt and iterations

### Authorization
- **Method**: Spring Security `@PreAuthorize` annotations
- **Ownership Verification**: Services verify user ownership before allowing modifications
- **Permission Checking**: `PermissionService.java` validates access rights

## 6. File Upload Security

### File Validation
- **Location**: `FileService.java`
- **Checks**:
  - File type validation (only images and videos allowed)
  - File size validation (max 500MB per file)
  - File name sanitization (prevents directory traversal)

### File Storage
- **Location**: Server file system (configurable path)
- **Access Control**: Files are served through authenticated endpoints
- **Cleanup**: Files are deleted when records are deleted

## 7. Request/Response Security

### Request Interceptor
- **Location**: `InputValidationInterceptor.java`
- **Purpose**: Validates all incoming requests before processing
- **Checks**: HTTP method, request path, query parameters

### Response Headers
- **Location**: `SecurityHeadersInterceptor.java`
- **Headers Added**:
  - Security headers (X-Frame-Options, X-Content-Type-Options, etc.)
  - CSP headers
  - HSTS header
  - Permissions-Policy header

### Error Handling
- **Location**: `GlobalExceptionHandler.java`
- **Purpose**: Prevents information disclosure through error messages
- **Details**: Generic error messages for users, detailed logs for developers

## 8. Configuration for Production

### Environment Variables
Update `application.properties` for production:

```properties
# HTTPS Configuration
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your-password
server.ssl.key-store-type=PKCS12

# JWT Configuration
jwt.secret=your-very-long-random-secret-key-min-32-chars
jwt.expiration=86400000

# CORS Configuration
cors.allowed-origins=https://yourdomain.com,https://www.yourdomain.com

# Database
spring.datasource.url=jdbc:mysql://your-db-host:3306/travel_memory_system?useSSL=true&serverTimezone=UTC
spring.datasource.username=your-db-user
spring.datasource.password=your-db-password
```

### Security Checklist
- [ ] Generate strong JWT secret (minimum 32 characters)
- [ ] Configure SSL/TLS certificates
- [ ] Update CORS allowed origins to production domain
- [ ] Enable HTTPS only
- [ ] Set secure cookie flags (HttpOnly, Secure, SameSite)
- [ ] Configure database connection with SSL
- [ ] Enable logging and monitoring
- [ ] Regular security updates for dependencies
- [ ] Implement rate limiting for authentication endpoints
- [ ] Set up Web Application Firewall (WAF)

## 9. Testing Security

### Unit Tests
- Test input sanitization functions
- Test validation logic
- Test permission checking

### Integration Tests
- Test authentication flow
- Test authorization on protected endpoints
- Test CORS headers
- Test error handling

### Security Testing
- Use OWASP ZAP or Burp Suite for vulnerability scanning
- Test for SQL injection vulnerabilities
- Test for XSS vulnerabilities
- Test for CSRF vulnerabilities
- Test for authentication bypass

## 10. Monitoring and Logging

### Security Events to Log
- Failed login attempts
- Unauthorized access attempts
- Input validation failures
- File upload failures
- Permission denied errors

### Log Location
- Application logs: `logs/` directory
- Database logs: MySQL error log
- Access logs: Web server access log

### Recommended Monitoring
- Set up alerts for multiple failed login attempts
- Monitor for unusual file upload patterns
- Track authorization failures
- Monitor for suspicious input patterns

## 11. Dependencies

### Security-Related Dependencies
- `spring-boot-starter-security`: Spring Security framework
- `jjwt`: JWT token generation and validation
- `owasp-java-html-sanitizer`: HTML sanitization
- `spring-boot-starter-validation`: Input validation

### Keeping Dependencies Updated
```bash
# Check for dependency updates
mvn versions:display-dependency-updates

# Update dependencies
mvn versions:use-latest-versions
```

## 12. References

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [Content Security Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP)
- [CORS Specification](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
