# Frontend Security Implementation Guide

This document describes the security measures implemented in the Vue.js frontend to protect against common web vulnerabilities.

## 1. HTTPS and Secure Communication

### Implementation
- **API Configuration**: `src/services/api.js`
- **Protocol**: All API calls use HTTPS in production
- **Certificate Validation**: Browser automatically validates SSL/TLS certificates

### Best Practices
1. Always use HTTPS in production
2. Implement certificate pinning for sensitive applications
3. Use secure WebSocket (WSS) for real-time features

## 2. CSRF (Cross-Site Request Forgery) Prevention

### JWT Token Protection
- **Storage**: Authorization header (not vulnerable to CSRF)
- **Implementation**: `src/services/api.js` - Axios interceptor adds token to headers
- **Benefit**: JWT tokens in headers are not automatically sent by browsers on cross-origin requests

### SameSite Cookie Attribute
- **Configuration**: Backend sets `SameSite=Strict` on cookies
- **Effect**: Cookies are not sent on cross-site requests
- **Fallback**: For older browsers, JWT in Authorization header provides protection

## 3. XSS (Cross-Site Scripting) Prevention

### Input Sanitization
- **Location**: `src/utils/validationUtils.js`
- **Methods**:
  - Escape HTML special characters
  - Validate input format
  - Remove dangerous patterns

### Output Encoding
- **Vue Template**: Vue automatically escapes text content
- **HTML Content**: Use `v-text` instead of `v-html` when possible
- **User-Generated Content**: Sanitize before rendering

### Content Security Policy (CSP)
- **Configured by Backend**: `SecurityHeadersConfig.java`
- **Effect**: Restricts script execution to trusted sources
- **Verification**: Check browser console for CSP violations

### Example: Safe User Input Handling
```vue
<!-- Safe - Vue escapes text automatically -->
<div>{{ userInput }}</div>

<!-- Unsafe - only use for trusted content -->
<div v-html="sanitizedContent"></div>

<!-- Safe - use text binding -->
<div v-text="userInput"></div>
```

## 4. Authentication Security

### JWT Token Management
- **Location**: `src/stores/userStore.js`
- **Storage**: Pinia store (in-memory, cleared on page refresh)
- **Transmission**: Authorization header with Bearer scheme
- **Expiration**: 24 hours

### Token Refresh
- **Location**: `src/services/api.js` - Response interceptor
- **Automatic Refresh**: Triggered on 401 Unauthorized response
- **Queue Management**: Prevents multiple simultaneous refresh requests

### Logout
- **Implementation**: `userStore.logout()`
- **Effect**: Clears token and user data from store
- **Redirect**: Redirects to login page

### Example: Secure Login Flow
```javascript
// 1. User submits credentials
const response = await authService.login(email, password)

// 2. Token is stored in Pinia store
userStore.setToken(response.token)

// 3. Token is automatically added to subsequent requests
// 4. On token expiration, automatic refresh is triggered
// 5. On logout, token is cleared
```

## 5. Authorization and Access Control

### Route Protection
- **Location**: `src/router/index.js`
- **Implementation**: Route guards check authentication status
- **Redirect**: Unauthenticated users redirected to login

### Permission Checking
- **Location**: Components check user permissions before rendering
- **Example**: Only show edit button if user is resource owner

### Example: Protected Route
```javascript
// Route guard checks authentication
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})
```

## 6. Input Validation

### Client-Side Validation
- **Location**: `src/utils/validationUtils.js`
- **Purpose**: Improve user experience and catch errors early
- **Note**: Not a security measure (can be bypassed)

### Validation Rules
- Email format validation
- Password strength requirements
- Field length limits
- Pattern matching (alphanumeric, special characters)

### Example: Form Validation
```vue
<script setup>
import { ref } from 'vue'
import { validateEmail, validatePassword } from '@/utils/validationUtils'

const email = ref('')
const emailError = ref('')

const validateEmailInput = () => {
  if (!validateEmail(email.value)) {
    emailError.value = 'Invalid email format'
  } else {
    emailError.value = ''
  }
}
</script>

<template>
  <input 
    v-model="email" 
    @blur="validateEmailInput"
    type="email"
  />
  <span v-if="emailError" class="error">{{ emailError }}</span>
</template>
```

## 7. Error Handling

### Error Display
- **Location**: `src/utils/errorHandler.js`
- **Purpose**: Show user-friendly error messages
- **Security**: Don't expose sensitive information in error messages

### Error Logging
- **Location**: `src/stores/errorStore.js`
- **Purpose**: Track errors for debugging
- **Note**: Don't log sensitive data (passwords, tokens)

### Example: Safe Error Handling
```javascript
// Good - generic message to user
catch (error) {
  errorStore.showError('An error occurred. Please try again.')
  // Log detailed error for debugging
  console.error('Detailed error:', error)
}

// Bad - exposing sensitive information
catch (error) {
  errorStore.showError(`Database error: ${error.message}`)
}
```

## 8. Secure Data Storage

### Pinia Store
- **Location**: `src/stores/`
- **Data Stored**: User info, tokens, application state
- **Persistence**: In-memory only (cleared on page refresh)
- **Security**: No sensitive data persisted to localStorage

### LocalStorage Considerations
- **Risk**: Vulnerable to XSS attacks
- **Recommendation**: Don't store sensitive data in localStorage
- **Alternative**: Use HTTP-only cookies (set by backend)

### Example: Secure Store
```javascript
// Good - store in Pinia (in-memory)
const userStore = useUserStore()
userStore.setToken(token)

// Bad - store in localStorage
localStorage.setItem('token', token)
```

## 9. API Security

### Request Interceptor
- **Location**: `src/services/api.js`
- **Purpose**: Add authentication token to requests
- **Implementation**: Axios interceptor adds Authorization header

### Response Interceptor
- **Location**: `src/services/api.js`
- **Purpose**: Handle errors and token refresh
- **Implementation**: Catches 401 errors and refreshes token

### Example: Secure API Call
```javascript
// Token is automatically added to request
const response = await api.get('/api/travels')

// On 401 error, token is automatically refreshed
// On 403 error, permission error is shown
// On 404 error, not found error is shown
```

## 10. File Upload Security

### File Validation
- **Location**: `src/components/MultimediaUpload.vue`
- **Checks**:
  - File type validation (images and videos only)
  - File size validation (max 500MB)
  - File name validation

### Secure Upload
- **Method**: Chunked upload with progress tracking
- **Validation**: Backend validates file again
- **Storage**: Files stored on server, not in browser

### Example: Safe File Upload
```vue
<script setup>
const validateFile = (file) => {
  const allowedTypes = ['image/jpeg', 'image/png', 'video/mp4']
  const maxSize = 500 * 1024 * 1024 // 500MB
  
  if (!allowedTypes.includes(file.type)) {
    throw new Error('Invalid file type')
  }
  
  if (file.size > maxSize) {
    throw new Error('File too large')
  }
}
</script>
```

## 11. Third-Party Libraries

### Security Considerations
- **Vue.js**: Automatically escapes text content
- **Element Plus**: Sanitizes user input in components
- **Axios**: Handles secure HTTP communication
- **Pinia**: Manages application state securely

### Keeping Dependencies Updated
```bash
# Check for security vulnerabilities
npm audit

# Fix vulnerabilities
npm audit fix

# Update dependencies
npm update
```

## 12. Browser Security Features

### Utilized Features
- **Same-Origin Policy**: Prevents cross-origin requests
- **Content Security Policy**: Restricts resource loading
- **X-Frame-Options**: Prevents clickjacking
- **X-Content-Type-Options**: Prevents MIME sniffing

### Browser Console
- **Security Warnings**: Check for CSP violations
- **Network Tab**: Verify HTTPS connections
- **Application Tab**: Check stored data

## 13. Development vs Production

### Development Configuration
- CORS allows localhost origins
- CSP allows unsafe-inline for development
- Detailed error messages for debugging

### Production Configuration
- CORS restricted to production domain
- CSP enforces strict policies
- Generic error messages for users
- HTTPS enforced
- Security headers enabled

## 14. Security Checklist

- [ ] All API calls use HTTPS
- [ ] JWT tokens stored securely (not in localStorage)
- [ ] Input validation on all forms
- [ ] Output encoding for user-generated content
- [ ] Error messages don't expose sensitive information
- [ ] CORS configured for production domain
- [ ] CSP headers configured
- [ ] No sensitive data in browser console logs
- [ ] Dependencies kept up to date
- [ ] Security headers verified in browser

## 15. Testing Security

### Manual Testing
1. Test login/logout flow
2. Test token refresh on expiration
3. Test unauthorized access to protected routes
4. Test input validation with malicious input
5. Test file upload with invalid files
6. Check browser console for security warnings

### Automated Testing
```bash
# Run security audit
npm audit

# Run tests
npm run test

# Build for production
npm run build
```

## 16. References

- [Vue.js Security Best Practices](https://vuejs.org/guide/best-practices/security.html)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Content Security Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [Axios Security](https://axios-http.com/docs/intro)
