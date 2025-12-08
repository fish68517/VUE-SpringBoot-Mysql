/**
 * User feedback utility functions
 * Provides consistent success, error, warning, and confirmation messages
 */

import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'

/**
 * Show success message
 * @param {string} message - Success message to display
 * @param {number} duration - Duration in milliseconds (default: 2000)
 */
export const showSuccess = (message, duration = 2000) => {
  ElMessage.success({
    message,
    duration,
    showClose: true,
    grouping: true
  })
}

/**
 * Show error message
 * @param {string} message - Error message to display
 * @param {number} duration - Duration in milliseconds (default: 3000)
 */
export const showError = (message, duration = 3000) => {
  ElMessage.error({
    message,
    duration,
    showClose: true,
    grouping: true
  })
}




/**
 * Show warning message
 * @param {string} message - Warning message to display
 * @param {number} duration - Duration in milliseconds (default: 2500)
 */
export const showWarning = (message, duration = 2500) => {
  ElMessage.warning({
    message,
    duration,
    showClose: true,
    grouping: true
  })
}

/**
 * Show info message
 * @param {string} message - Info message to display
 * @param {number} duration - Duration in milliseconds (default: 2000)
 */
export const showInfo = (message, duration = 2000) => {
  ElMessage.info({
    message,
    duration,
    showClose: true,
    grouping: true
  })
}

/**
 * Show confirmation dialog
 * @param {string} message - Confirmation message
 * @param {string} title - Dialog title (default: 'Confirm')
 * @param {object} options - Additional options
 * @returns {Promise} - Resolves if confirmed, rejects if cancelled
 */
export const showConfirm = (message, title = 'Confirm', options = {}) => {
  return ElMessageBox.confirm(
    message,
    title,
    {
      confirmButtonText: options.confirmText || 'Confirm',
      cancelButtonText: options.cancelText || 'Cancel',
      type: options.type || 'warning',
      distinguishCancelAndClose: true,
      ...options
    }
  )
}

/**
 * Show delete confirmation dialog
 * @param {string} itemName - Name of item to delete
 * @returns {Promise} - Resolves if confirmed, rejects if cancelled
 */
export const confirmDelete = (itemName = 'this item') => {
  // 打印 itemName 以便调试
  console.log('confirmDelete called with itemName:', JSON.stringify(itemName))
  return showConfirm(
    `你想删除 吗? 此操作不可撤销.`,
    '删除确认',
    {
      confirmText: '删除',
      cancelText: '取消',
      type: 'error',
      confirmButtonClass: 'el-button--danger'
    }
  )
}

/**
 * Show remove confirmation dialog
 * @param {string} itemName - Name of item to remove
 * @returns {Promise} - Resolves if confirmed, rejects if cancelled
 */
export const confirmRemove = (itemName = 'this item') => {
  return showConfirm(
    `Are you sure you want to remove ${itemName}?`,
    'Remove Confirmation',
    {
      confirmText: 'Remove',
      cancelText: 'Cancel',
      type: 'warning'
    }
  )
}

/**
 * Show discard changes confirmation dialog
 * @returns {Promise} - Resolves if confirmed, rejects if cancelled
 */
export const confirmDiscard = () => {
  return showConfirm(
    'You have unsaved changes. Are you sure you want to discard them?',
    'Discard Changes',
    {
      confirmText: 'Discard',
      cancelText: 'Keep Editing',
      type: 'warning'
    }
  )
}

/**
 * Show loading overlay
 * @param {string} text - Loading text (default: 'Loading...')
 * @returns {object} - Loading instance (call close() to dismiss)
 */
export const showLoading = (text = 'Loading...') => {
  return ElLoading.service({
    lock: true,
    text,
    background: 'rgba(0, 0, 0, 0.7)'
  })
}

/**
 * Handle async operation with loading and feedback
 * @param {Function} operation - Async operation to execute
 * @param {object} options - Options for feedback
 * @returns {Promise} - Result of the operation
 */
export const withLoading = async (operation, options = {}) => {
  const {
    loadingText = 'Loading...',
    successMessage = null,
    errorMessage = 'Operation failed',
    showSuccessMessage = true,
    showErrorMessage = true
  } = options

  const loading = showLoading(loadingText)

  try {
    const result = await operation()
    
    if (showSuccessMessage && successMessage) {
      showSuccess(successMessage)
    }
    
    return result
  } catch (error) {
    if (showErrorMessage) {
      const message = error.message || errorMessage
      showError(message)
    }
    throw error
  } finally {
    loading.close()
  }
}

/**
 * Handle form submission with validation and feedback
 * @param {object} formRef - Form reference
 * @param {Function} submitFn - Submit function
 * @param {object} options - Options for feedback
 * @returns {Promise} - Result of the submission
 */
export const handleFormSubmit = async (formRef, submitFn, options = {}) => {
  const {
    successMessage = 'Saved successfully',
    errorMessage = 'Failed to save',
    validationMessage = 'Please fill in all required fields'
  } = options

  if (!formRef) {
    showError('Form reference not found')
    return
  }

  try {
    // Validate form
    const valid = await formRef.validate().catch(() => false)
    
    if (!valid) {
      showWarning(validationMessage)
      return
    }

    // Execute submit function
    const result = await submitFn()
    
    showSuccess(successMessage)
    return result
  } catch (error) {
    // Error is already handled by request interceptor
    console.error('Form submission error:', error)
    
    // Only show additional message if it's not an HTTP error
    if (!error.response) {
      showError(error.message || errorMessage)
    }
    
    throw error
  }
}

/**
 * Handle delete operation with confirmation
 * @param {Function} deleteFn - Delete function
 * @param {string} itemName - Name of item to delete
 * @param {object} options - Options for feedback
 * @returns {Promise} - Result of the deletion
 */
export const handleDelete = async (deleteFn, itemName = 'this item', options = {}) => {
  const {
    successMessage = `${itemName} deleted successfully`,
    errorMessage = `Failed to delete ${itemName}`
  } = options

  try {
    await confirmDelete(itemName)
    
    await deleteFn()
    
    showSuccess(successMessage)
    return true
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      // User cancelled, do nothing
      return false
    }
    
    // Error is already handled by request interceptor
    console.error('Delete error:', error)
    
    // Only show additional message if it's not an HTTP error
    if (!error.response) {
      showError(error.message || errorMessage)
    }
    
    throw error
  }
}

/**
 * Common feedback messages
 */
export const messages = {
  // Success messages
  success: {
    created: 'Created successfully',
    updated: 'Updated successfully',
    deleted: 'Deleted successfully',
    saved: 'Saved successfully',
    uploaded: 'Uploaded successfully',
    submitted: 'Submitted successfully',
    sent: 'Sent successfully',
    copied: 'Copied to clipboard'
  },
  
  // Error messages
  error: {
    create: 'Failed to create',
    update: 'Failed to update',
    delete: 'Failed to delete',
    save: 'Failed to save',
    upload: 'Failed to upload',
    submit: 'Failed to submit',
    send: 'Failed to send',
    load: 'Failed to load data',
    network: 'Network error, please try again',
    unknown: 'An unexpected error occurred'
  },
  
  // Warning messages
  warning: {
    required: 'Please fill in all required fields',
    invalid: 'Please check your input',
    unsaved: 'You have unsaved changes',
    limit: 'You have reached the limit'
  },
  
  // Confirmation messages
  confirm: {
    delete: 'Are you sure you want to delete this?',
    remove: 'Are you sure you want to remove this?',
    discard: 'Are you sure you want to discard changes?',
    leave: 'Are you sure you want to leave this page?'
  }
}
