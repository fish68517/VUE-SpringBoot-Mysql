import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useErrorStore = defineStore('error', () => {
  const errors = ref([])
  const notifications = ref([])

  /**
   * Add an error to the error store
   * @param {Object} error - Error object with code, message, details
   */
  const addError = (error) => {
    const errorId = Date.now()
    const errorObj = {
      id: errorId,
      code: error.code || 500,
      message: error.message || 'An error occurred',
      details: error.details || null,
      timestamp: new Date(),
      type: error.type || 'error'
    }
    errors.value.push(errorObj)
    return errorId
  }

  /**
   * Remove an error by ID
   * @param {number} errorId - The error ID to remove
   */
  const removeError = (errorId) => {
    errors.value = errors.value.filter(e => e.id !== errorId)
  }

  /**
   * Clear all errors
   */
  const clearErrors = () => {
    errors.value = []
  }

  /**
   * Add a notification (for user-friendly messages)
   * @param {Object} notification - Notification object
   */
  const addNotification = (notification) => {
    const notificationId = Date.now()
    const notificationObj = {
      id: notificationId,
      message: notification.message,
      type: notification.type || 'info', // 'success', 'error', 'warning', 'info'
      duration: notification.duration || 3000,
      timestamp: new Date()
    }
    notifications.value.push(notificationObj)
    
    // Auto-remove notification after duration
    if (notificationObj.duration > 0) {
      setTimeout(() => {
        removeNotification(notificationId)
      }, notificationObj.duration)
    }
    
    return notificationId
  }

  /**
   * Remove a notification by ID
   * @param {number} notificationId - The notification ID to remove
   */
  const removeNotification = (notificationId) => {
    notifications.value = notifications.value.filter(n => n.id !== notificationId)
  }

  /**
   * Clear all notifications
   */
  const clearNotifications = () => {
    notifications.value = []
  }

  /**
   * Show success notification
   * @param {string} message - Success message
   * @param {number} duration - Duration in milliseconds
   */
  const showSuccess = (message, duration = 3000) => {
    return addNotification({
      message,
      type: 'success',
      duration
    })
  }

  /**
   * Show error notification
   * @param {string} message - Error message
   * @param {number} duration - Duration in milliseconds
   */
  const showError = (message, duration = 5000) => {
    return addNotification({
      message,
      type: 'error',
      duration
    })
  }

  /**
   * Show warning notification
   * @param {string} message - Warning message
   * @param {number} duration - Duration in milliseconds
   */
  const showWarning = (message, duration = 4000) => {
    return addNotification({
      message,
      type: 'warning',
      duration
    })
  }

  /**
   * Show info notification
   * @param {string} message - Info message
   * @param {number} duration - Duration in milliseconds
   */
  const showInfo = (message, duration = 3000) => {
    return addNotification({
      message,
      type: 'info',
      duration
    })
  }

  return {
    errors,
    notifications,
    addError,
    removeError,
    clearErrors,
    addNotification,
    removeNotification,
    clearNotifications,
    showSuccess,
    showError,
    showWarning,
    showInfo
  }
})
