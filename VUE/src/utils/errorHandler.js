import { ElMessage, ElMessageBox } from 'element-plus'

/**
 * Handle API errors with user-friendly messages
 */
export function handleError(error, customMessage = null) {
  if (customMessage) {
    showError(customMessage)
    return
  }

  if (error.response) {
    const { status, data } = error.response

    switch (status) {
      case 400:
        showError(data?.message || 'Invalid request')
        break
      case 401:
        showError('Session expired, please login again')
        localStorage.removeItem('token')
        setTimeout(() => {
          window.location.href = '/login'
        }, 1500)
        break
      case 403:
        showError('Access denied')
        break
      case 404:
        showError('Resource not found')
        break
      case 409:
        showError(data?.message || 'Operation conflict')
        break
      case 500:
        showError('Server error, please try again later')
        break
      default:
        showError(data?.message || 'Request failed')
    }
  } else if (error.request) {
    showError('Network error, please check your connection')
  } else {
    showError('An error occurred')
  }
}

/**
 * Show error message using Toast or ElMessage
 */
export function showError(message) {
  if (window.$toast) {
    window.$toast.error(message)
  } else {
    ElMessage.error(message)
  }
}

/**
 * Show confirmation dialog
 */
export function showConfirm(title, message, confirmText = 'Confirm', cancelText = 'Cancel') {
  return ElMessageBox.confirm(message, title, {
    confirmButtonText: confirmText,
    cancelButtonText: cancelText,
    type: 'warning'
  })
}

/**
 * Show success message
 */
export function showSuccess(message = 'Operation successful') {
  if (window.$toast) {
    window.$toast.success(message)
  } else {
    ElMessage.success(message)
  }
}

/**
 * Show warning message
 */
export function showWarning(message) {
  if (window.$toast) {
    window.$toast.warning(message)
  } else {
    ElMessage.warning(message)
  }
}

/**
 * Show info message
 */
export function showInfo(message) {
  if (window.$toast) {
    window.$toast.info(message)
  } else {
    ElMessage.info(message)
  }
}
