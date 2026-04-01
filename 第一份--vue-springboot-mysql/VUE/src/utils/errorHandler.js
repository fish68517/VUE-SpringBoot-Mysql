import { ElMessage } from 'element-plus'

/**
 * Handle API errors and show appropriate messages
 * @param {Error|object} error - Error object
 * @param {string} defaultMessage - Default error message
 */
export const handleError = (error, defaultMessage = '操作失败，请重试') => {
  let message = defaultMessage

  if (error.response) {
    // Server responded with error status
    const { status, data } = error.response
    
    if (data?.message) {
      message = data.message
    } else {
      switch (status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          break
        case 403:
          message = '禁止访问'
          break
        case 404:
          message = '资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `请求失败 (${status})`
      }
    }
  } else if (error.request) {
    // Request made but no response
    message = '网络连接失败，请检查网络'
  } else if (error.message) {
    message = error.message
  }

  ElMessage.error(message)
  console.error('Error:', error)
  
  return message
}

/**
 * Show success message
 * @param {string} message - Success message
 */
export const showSuccess = (message = '操作成功') => {
  ElMessage.success(message)
}

/**
 * Show warning message
 * @param {string} message - Warning message
 */
export const showWarning = (message = '警告') => {
  ElMessage.warning(message)
}

/**
 * Show info message
 * @param {string} message - Info message
 */
export const showInfo = (message = '提示') => {
  ElMessage.info(message)
}

/**
 * Get error message from response
 * @param {Error|object} error - Error object
 * @returns {string} Error message
 */
export const getErrorMessage = (error) => {
  if (error.response?.data?.message) {
    return error.response.data.message
  }
  if (error.message) {
    return error.message
  }
  return '未知错误'
}
