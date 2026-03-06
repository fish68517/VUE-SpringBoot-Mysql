import { ElMessage, ElMessageBox } from 'element-plus'

/**
 * Unified message helper for consistent UI feedback
 */

export const messageHelper = {
  /**
   * Show success message
   */
  success(message = '操作成功', duration = 2000) {
    return ElMessage.success({
      message,
      duration,
      showClose: true
    })
  },

  /**
   * Show error message
   */
  error(message = '操作失败', duration = 3000) {
    return ElMessage.error({
      message,
      duration,
      showClose: true
    })
  },

  /**
   * Show warning message
   */
  warning(message = '警告', duration = 3000) {
    return ElMessage.warning({
      message,
      duration,
      showClose: true
    })
  },

  /**
   * Show info message
   */
  info(message = '提示', duration = 2000) {
    return ElMessage.info({
      message,
      duration,
      showClose: true
    })
  },

  /**
   * Show confirmation dialog
   */
  confirm(title, message, options = {}) {
    return ElMessageBox.confirm(message, title, {
      confirmButtonText: options.confirmText || '确定',
      cancelButtonText: options.cancelText || '取消',
      type: options.type || 'warning',
      ...options
    })
  },

  /**
   * Show alert dialog
   */
  alert(title, message, options = {}) {
    return ElMessageBox.alert(message, title, {
      confirmButtonText: options.confirmText || '确定',
      type: options.type || 'info',
      ...options
    })
  },

  /**
   * Show prompt dialog
   */
  prompt(title, message, options = {}) {
    return ElMessageBox.prompt(message, title, {
      confirmButtonText: options.confirmText || '确定',
      cancelButtonText: options.cancelText || '取消',
      inputType: options.inputType || 'text',
      inputPlaceholder: options.placeholder || '',
      ...options
    })
  },

  /**
   * Format validation error message
   */
  formatValidationError(errors) {
    if (Array.isArray(errors)) {
      return errors.map(e => e.message).join('; ')
    }
    return errors?.message || '验证失败'
  },

  /**
   * Format API error message
   */
  formatApiError(error) {
    if (error.response?.data?.message) {
      return error.response.data.message
    }
    if (error.message) {
      return error.message
    }
    return '请求失败，请稍后重试'
  }
}

export default messageHelper
