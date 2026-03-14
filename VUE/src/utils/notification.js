import { ElMessage, ElNotification } from 'element-plus'

/**
 * Show success notification
 */
export const showSuccess = (message, title = '成功') => {
  ElNotification({
    title,
    message,
    type: 'success',
    duration: 3000,
    position: 'top-right'
  })
}

/**
 * Show error notification
 */
export const showError = (message, title = '错误') => {
  ElNotification({
    title,
    message,
    type: 'error',
    duration: 4000,
    position: 'top-right'
  })
}

/**
 * Show warning notification
 */
export const showWarning = (message, title = '警告') => {
  ElNotification({
    title,
    message,
    type: 'warning',
    duration: 3000,
    position: 'top-right'
  })
}

/**
 * Show info notification
 */
export const showInfo = (message, title = '提示') => {
  ElNotification({
    title,
    message,
    type: 'info',
    duration: 3000,
    position: 'top-right'
  })
}

/**
 * Show quick message (shorter duration)
 */
export const showMessage = (message, type = 'success') => {
  ElMessage({
    message,
    type,
    duration: 2000
  })
}
