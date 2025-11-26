import { useErrorStore } from '../stores/errorStore'
import { errorHandler } from '../utils/errorHandler'

/**
 * Composable for error handling in Vue components
 * Provides convenient methods for showing errors, warnings, and success messages
 */
export const useErrorHandler = () => {
  const errorStore = useErrorStore()

  /**
   * Handle an API error
   * @param {Object} error - Error object from API
   * @param {string} context - Context for logging
   */
  const handleError = (error, context = '') => {
    if (context) {
      errorHandler.logError(error, context)
    }
    return errorHandler.handleApiError(error)
  }

  /**
   * Handle validation errors
   * @param {Object|Array} errors - Validation errors
   */
  const handleValidationError = (errors) => {
    errorHandler.handleValidationError(errors)
  }

  /**
   * Handle business logic errors
   * @param {string} message - Error message
   * @param {Object} details - Additional details
   */
  const handleBusinessError = (message, details = null) => {
    errorHandler.handleBusinessError(message, details)
  }

  /**
   * Handle authentication errors
   * @param {string} message - Error message
   */
  const handleAuthError = (message = 'Authentication failed') => {
    errorHandler.handleAuthError(message)
  }

  /**
   * Handle permission errors
   * @param {string} message - Error message
   */
  const handlePermissionError = (message = 'You do not have permission to perform this action') => {
    errorHandler.handlePermissionError(message)
  }

  /**
   * Handle not found errors
   * @param {string} resource - Resource name
   */
  const handleNotFoundError = (resource = 'Resource') => {
    errorHandler.handleNotFoundError(resource)
  }

  /**
   * Show success notification
   * @param {string} message - Success message
   * @param {number} duration - Duration in milliseconds
   */
  const showSuccess = (message, duration = 3000) => {
    return errorStore.showSuccess(message, duration)
  }

  /**
   * Show error notification
   * @param {string} message - Error message
   * @param {number} duration - Duration in milliseconds
   */
  const showError = (message, duration = 5000) => {
    return errorStore.showError(message, duration)
  }

  /**
   * Show warning notification
   * @param {string} message - Warning message
   * @param {number} duration - Duration in milliseconds
   */
  const showWarning = (message, duration = 4000) => {
    return errorStore.showWarning(message, duration)
  }

  /**
   * Show info notification
   * @param {string} message - Info message
   * @param {number} duration - Duration in milliseconds
   */
  const showInfo = (message, duration = 3000) => {
    return errorStore.showInfo(message, duration)
  }

  /**
   * Clear all notifications
   */
  const clearNotifications = () => {
    errorStore.clearNotifications()
  }

  /**
   * Clear all errors
   */
  const clearErrors = () => {
    errorStore.clearErrors()
  }

  /**
   * Wrap an async function with error handling
   * @param {Function} fn - Async function to wrap
   * @param {string} context - Context for logging
   * @returns {Function} Wrapped function
   */
  const withErrorHandling = (fn, context = '') => {
    return async (...args) => {
      try {
        return await fn(...args)
      } catch (error) {
        handleError(error, context)
        throw error
      }
    }
  }

  return {
    handleError,
    handleValidationError,
    handleBusinessError,
    handleAuthError,
    handlePermissionError,
    handleNotFoundError,
    showSuccess,
    showError,
    showWarning,
    showInfo,
    clearNotifications,
    clearErrors,
    withErrorHandling
  }
}

export default useErrorHandler
