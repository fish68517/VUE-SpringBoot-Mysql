# Frontend Error Handling Guide

This guide explains how to use the comprehensive error handling system implemented in the Travel Memory System frontend.

## Overview

The error handling system consists of:

1. **Error Store** (`stores/errorStore.js`) - Centralized state management for errors and notifications
2. **Error Handler Utility** (`utils/errorHandler.js`) - Utility functions for handling different error types
3. **Error Notification Component** (`components/ErrorNotification.vue`) - UI component for displaying notifications
4. **Error Handler Composable** (`composables/useErrorHandler.js`) - Vue composable for easy error handling in components

## Components

### 1. Error Store (Pinia)

The error store manages all error and notification state.

**Key Methods:**

- `addError(error)` - Add an error to the store
- `removeError(errorId)` - Remove an error by ID
- `clearErrors()` - Clear all errors
- `addNotification(notification)` - Add a notification
- `removeNotification(notificationId)` - Remove a notification
- `clearNotifications()` - Clear all notifications
- `showSuccess(message, duration)` - Show success notification
- `showError(message, duration)` - Show error notification
- `showWarning(message, duration)` - Show warning notification
- `showInfo(message, duration)` - Show info notification

**Example:**

```javascript
import { useErrorStore } from '@/stores/errorStore'

const errorStore = useErrorStore()

// Show a success message
errorStore.showSuccess('Operation completed successfully!')

// Show an error message
errorStore.showError('An error occurred. Please try again.')

// Show a warning message
errorStore.showWarning('This action cannot be undone.')

// Show an info message
errorStore.showInfo('Please note this important information.')
```

### 2. Error Handler Utility

The error handler utility provides functions for handling different types of errors.

**Key Methods:**

- `handleApiError(error)` - Handle API errors and show user-friendly messages
- `handleValidationError(errors)` - Handle validation errors
- `handleBusinessError(message, details)` - Handle business logic errors
- `handleAuthError(message)` - Handle authentication errors
- `handlePermissionError(message)` - Handle permission/authorization errors
- `handleNotFoundError(resource)` - Handle not found errors
- `getErrorMessage(status, data)` - Get user-friendly message for HTTP status
- `logError(error, context)` - Log error for debugging
- `normalizeError(error)` - Normalize any error type to standard format

**Example:**

```javascript
import { errorHandler } from '@/utils/errorHandler'

try {
  // Some operation
} catch (error) {
  errorHandler.handleApiError(error)
}

// Handle specific error types
errorHandler.handleValidationError({
  email: 'Invalid email format',
  password: 'Password must be at least 8 characters'
})

errorHandler.handlePermissionError('You do not have permission to delete this record')

errorHandler.handleNotFoundError('Travel Record')
```

### 3. Error Notification Component

The `ErrorNotification.vue` component displays notifications at the top-right of the screen.

**Features:**

- Auto-dismissing notifications with progress bar
- Different styles for success, error, warning, and info messages
- Manual close button
- Responsive design for mobile devices
- Smooth animations

**The component is automatically included in `App.vue`**, so you don't need to add it manually.

### 4. Error Handler Composable

The `useErrorHandler` composable provides convenient methods for error handling in Vue components.

**Key Methods:**

- `handleError(error, context)` - Handle an API error
- `handleValidationError(errors)` - Handle validation errors
- `handleBusinessError(message, details)` - Handle business errors
- `handleAuthError(message)` - Handle auth errors
- `handlePermissionError(message)` - Handle permission errors
- `handleNotFoundError(resource)` - Handle not found errors
- `showSuccess(message, duration)` - Show success notification
- `showError(message, duration)` - Show error notification
- `showWarning(message, duration)` - Show warning notification
- `showInfo(message, duration)` - Show info notification
- `withErrorHandling(fn, context)` - Wrap async function with error handling

**Example:**

```javascript
<script setup>
import { useErrorHandler } from '@/composables/useErrorHandler'

const { handleError, showSuccess, showError, withErrorHandling } = useErrorHandler()

// Handle errors in try-catch
const deleteRecord = async (id) => {
  try {
    await api.delete(`/travels/${id}`)
    showSuccess('Record deleted successfully!')
  } catch (error) {
    handleError(error, 'Delete Record')
  }
}

// Or use withErrorHandling wrapper
const updateRecord = withErrorHandling(async (id, data) => {
  await api.put(`/travels/${id}`, data)
  showSuccess('Record updated successfully!')
}, 'Update Record')
</script>
```

## Usage Patterns

### Pattern 1: Basic Error Handling in Components

```javascript
<script setup>
import { useErrorHandler } from '@/composables/useErrorHandler'
import api from '@/services/api'

const { handleError, showSuccess } = useErrorHandler()

const submitForm = async (formData) => {
  try {
    const response = await api.post('/travels', formData)
    showSuccess('Travel record created successfully!')
    // Handle success
  } catch (error) {
    handleError(error, 'Create Travel Record')
  }
}
</script>
```

### Pattern 2: Validation Error Handling

```javascript
<script setup>
import { useErrorHandler } from '@/composables/useErrorHandler'

const { handleValidationError } = useErrorHandler()

const validateForm = (formData) => {
  const errors = {}
  
  if (!formData.email) {
    errors.email = 'Email is required'
  }
  
  if (!formData.password || formData.password.length < 8) {
    errors.password = 'Password must be at least 8 characters'
  }
  
  if (Object.keys(errors).length > 0) {
    handleValidationError(errors)
    return false
  }
  
  return true
}
</script>
```

### Pattern 3: Async Function Wrapper

```javascript
<script setup>
import { useErrorHandler } from '@/composables/useErrorHandler'
import api from '@/services/api'

const { withErrorHandling, showSuccess } = useErrorHandler()

// Wrap async function with automatic error handling
const fetchRecords = withErrorHandling(async () => {
  const response = await api.get('/travels')
  return response.data
}, 'Fetch Travel Records')

// Use it
const records = await fetchRecords()
</script>
```

### Pattern 4: Permission Error Handling

```javascript
<script setup>
import { useErrorHandler } from '@/composables/useErrorHandler'
import api from '@/services/api'

const { handlePermissionError, showSuccess } = useErrorHandler()

const deleteRecord = async (id) => {
  try {
    await api.delete(`/travels/${id}`)
    showSuccess('Record deleted successfully!')
  } catch (error) {
    if (error.code === 403) {
      handlePermissionError('You cannot delete this record')
    } else {
      handleError(error, 'Delete Record')
    }
  }
}
</script>
```

## API Error Handling

The API service (`services/api.js`) automatically handles errors and shows notifications:

1. **Network Errors** - Shows "Network connection failed" message
2. **401 Unauthorized** - Attempts token refresh, redirects to login if failed
3. **403 Forbidden** - Shows permission denied message
4. **404 Not Found** - Shows resource not found message
5. **Other Errors** - Shows appropriate error message based on status code

**Error Response Format:**

```javascript
{
  code: 400,
  message: "Invalid request. Please check your input.",
  details: {
    // Additional error details from server
  }
}
```

## Best Practices

1. **Always use try-catch for API calls:**
   ```javascript
   try {
     await api.post('/travels', data)
   } catch (error) {
     handleError(error, 'Create Travel')
   }
   ```

2. **Provide context when logging errors:**
   ```javascript
   handleError(error, 'Update User Profile')
   ```

3. **Use specific error handlers for different scenarios:**
   ```javascript
   if (error.code === 403) {
     handlePermissionError()
   } else if (error.code === 404) {
     handleNotFoundError('Travel Record')
   } else {
     handleError(error)
   }
   ```

4. **Show success messages for important operations:**
   ```javascript
   await api.post('/travels', data)
   showSuccess('Travel record created successfully!')
   ```

5. **Use the composable in components for consistency:**
   ```javascript
   import { useErrorHandler } from '@/composables/useErrorHandler'
   const { handleError, showSuccess } = useErrorHandler()
   ```

6. **Wrap async operations with error handling:**
   ```javascript
   const operation = withErrorHandling(async () => {
     // Your async code
   }, 'Operation Name')
   ```

## Notification Duration

Default durations for notifications:
- Success: 3000ms
- Error: 5000ms
- Warning: 4000ms
- Info: 3000ms

You can customize the duration:
```javascript
showSuccess('Message', 5000) // 5 seconds
showError('Message', 10000) // 10 seconds
```

## Debugging

Enable error logging in development:

```javascript
// In main.js or any component
import { errorHandler } from '@/utils/errorHandler'

errorHandler.logError(error, 'My Context')
```

Errors are logged to the browser console with:
- Timestamp
- Context
- Error message
- Error code
- Stack trace

## Testing Error Handling

Example test for error handling:

```javascript
import { describe, it, expect } from 'vitest'
import { useErrorHandler } from '@/composables/useErrorHandler'

describe('Error Handler', () => {
  it('should show error notification', () => {
    const { showError } = useErrorHandler()
    const notificationId = showError('Test error')
    expect(notificationId).toBeDefined()
  })

  it('should handle API errors', () => {
    const { handleError } = useErrorHandler()
    const error = {
      response: {
        status: 400,
        data: { message: 'Invalid input' }
      }
    }
    handleError(error, 'Test')
  })
})
```

## Troubleshooting

### Notifications not showing
- Ensure `ErrorNotification` component is included in `App.vue`
- Check browser console for errors
- Verify error store is properly initialized

### Error messages not user-friendly
- Check if error has a `message` field in response
- Add custom error message mapping in `errorHandler.getErrorMessage()`

### Token refresh not working
- Verify JWT token is stored in localStorage
- Check if `/api/auth/refresh` endpoint is implemented
- Ensure token refresh logic in `api.js` is correct

## Related Files

- `stores/errorStore.js` - Error state management
- `utils/errorHandler.js` - Error handling utilities
- `components/ErrorNotification.vue` - Notification UI
- `composables/useErrorHandler.js` - Vue composable
- `services/api.js` - API service with error interceptors
- `main.js` - Global error handler setup
