import { useErrorStore } from '../stores/errorStore'

/**
 * Error handler utility for consistent error handling across the application
 */
export const errorHandler = {
  /**
   * Handle API errors and convert them to user-friendly messages
   * @param {Object} error - Error object from API or exception
   * @returns {Object} Processed error object
   */
  handleApiError(error) {
    const errorStore = useErrorStore()
    
    // Handle network errors
    if (!error.response) {
      const message = error.message === 'Network Error' 
        ? 'Network connection failed. Please check your internet connection.'
        : error.message || 'An unexpected error occurred'
      
      errorStore.showError(message)
      return {
        code: 0,
        message,
        type: 'network'
      }
    }

    // Handle API response errors
    const status = error.response?.status
    const data = error.response?.data

    let userMessage = this.getErrorMessage(status, data)
    
    errorStore.showError(userMessage)
    
    return {
      code: status,
      message: userMessage,
      details: data,
      type: 'api'
    }
  },

  /**
   * Handle validation errors from form submission
   * @param {Object} errors - Validation errors object
   */
  handleValidationError(errors) {
    const errorStore = useErrorStore()
    
    if (Array.isArray(errors)) {
      errors.forEach(error => {
        errorStore.showError(error.message || error)
      })
    } else if (typeof errors === 'object') {
      Object.values(errors).forEach(error => {
        errorStore.showError(error.message || error)
      })
    } else {
      errorStore.showError(errors || 'Validation failed')
    }
  },

  /**
   * Handle business logic errors
   * @param {string} message - Error message
   * @param {Object} details - Additional error details
   */
  handleBusinessError(message, details = null) {
    const errorStore = useErrorStore()
    errorStore.addError({
      code: 400,
      message,
      details,
      type: 'business'
    })
    errorStore.showError(message)
  },

  /**
   * Handle authentication errors
   * @param {string} message - Error message
   */
  handleAuthError(message = 'Authentication failed') {
    const errorStore = useErrorStore()
    errorStore.showError(message)
  },

  /**
   * Handle permission/authorization errors
   * @param {string} message - Error message
   */
  handlePermissionError(message = 'You do not have permission to perform this action') {
    const errorStore = useErrorStore()
    errorStore.showWarning(message)
  },

  /**
   * Handle not found errors
   * @param {string} resource - Resource name
   */
  handleNotFoundError(resource = 'Resource') {
    const errorStore = useErrorStore()
    const message = `${resource} not found`
    errorStore.showError(message)
  },

  /**
   * Get user-friendly error message based on status code and response data
   * @param {number} status - HTTP status code
   * @param {Object} data - Response data
   * @returns {string} User-friendly error message
   */
  getErrorMessage(status, data) {
    // If data has a message field, use it
    if (data?.message) {
      return data.message
    }

    // Map status codes to messages
    const statusMessages = {
      400: 'Invalid request. Please check your input.',
      401: 'Your session has expired. Please log in again.',
      403: 'You do not have permission to perform this action.',
      404: 'The requested resource was not found.',
      409: 'This action conflicts with existing data.',
      422: 'The provided data is invalid.',
      429: 'Too many requests. Please try again later.',
      500: 'Server error. Please try again later.',
      502: 'Bad gateway. Please try again later.',
      503: 'Service unavailable. Please try again later.',
      504: 'Gateway timeout. Please try again later.'
    }

    return statusMessages[status] || 'An error occurred. Please try again.'
  },

  /**
   * Log error for debugging
   * @param {Object} error - Error object
   * @param {string} context - Context where error occurred
   */
  logError(error, context = '') {
    const timestamp = new Date().toISOString()
    const errorInfo = {
      timestamp,
      context,
      message: error.message,
      code: error.code,
      stack: error.stack
    }
    
    console.error(`[${timestamp}] Error in ${context}:`, errorInfo)
    
    // In production, you might want to send this to a logging service
    // Example: sendToLoggingService(errorInfo)
  },

  /**
   * Create a safe error object from any error type
   * @param {*} error - Any error object
   * @returns {Object} Normalized error object
   */
  normalizeError(error) {
    if (error.response) {
      // Axios error
      return {
        code: error.response.status,
        message: error.response.data?.message || error.message,
        details: error.response.data,
        type: 'api'
      }
    } else if (error.message) {
      // Standard error
      return {
        code: error.code || 500,
        message: error.message,
        details: null,
        type: 'error'
      }
    } else {
      // Unknown error
      return {
        code: 500,
        message: String(error),
        details: null,
        type: 'unknown'
      }
    }
  }
}

export default errorHandler
