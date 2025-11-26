import { ElMessage, ElNotification } from 'element-plus'

/**
 * Show success message
 * @param {string} message - Success message text
 * @param {number} duration - Duration in milliseconds (default: 3000)
 */
export const showSuccess = (message, duration = 3000) => {
  ElMessage.success({
    message,
    duration,
    showClose: true
  })
}

/**
 * Show error message
 * @param {string} message - Error message text
 * @param {number} duration - Duration in milliseconds (default: 5000)
 */
export const showError = (message, duration = 5000) => {
  ElMessage.error({
    message,
    duration,
    showClose: true
  })
}

/**
 * Show warning message
 * @param {string} message - Warning message text
 * @param {number} duration - Duration in milliseconds (default: 4000)
 */
export const showWarning = (message, duration = 4000) => {
  ElMessage.warning({
    message,
    duration,
    showClose: true
  })
}

/**
 * Show info message
 * @param {string} message - Info message text
 * @param {number} duration - Duration in milliseconds (default: 3000)
 */
export const showInfo = (message, duration = 3000) => {
  ElMessage.info({
    message,
    duration,
    showClose: true
  })
}

/**
 * Show success notification (top-right corner)
 * @param {string} title - Notification title
 * @param {string} message - Notification message
 * @param {number} duration - Duration in milliseconds (default: 4500)
 */
export const notifySuccess = (title, message, duration = 4500) => {
  ElNotification({
    title,
    message,
    type: 'success',
    duration,
    position: 'top-right'
  })
}

/**
 * Show error notification (top-right corner)
 * @param {string} title - Notification title
 * @param {string} message - Notification message
 * @param {number} duration - Duration in milliseconds (default: 4500)
 */
export const notifyError = (title, message, duration = 4500) => {
  ElNotification({
    title,
    message,
    type: 'error',
    duration,
    position: 'top-right'
  })
}

/**
 * Show warning notification (top-right corner)
 * @param {string} title - Notification title
 * @param {string} message - Notification message
 * @param {number} duration - Duration in milliseconds (default: 4500)
 */
export const notifyWarning = (title, message, duration = 4500) => {
  ElNotification({
    title,
    message,
    type: 'warning',
    duration,
    position: 'top-right'
  })
}

/**
 * Show info notification (top-right corner)
 * @param {string} title - Notification title
 * @param {string} message - Notification message
 * @param {number} duration - Duration in milliseconds (default: 4500)
 */
export const notifyInfo = (title, message, duration = 4500) => {
  ElNotification({
    title,
    message,
    type: 'info',
    duration,
    position: 'top-right'
  })
}
