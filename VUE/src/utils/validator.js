/**
 * Form validation utility functions
 */

// Username validation
export const validateUsername = (rule, value, callback) => {
  if (!value) {
    callback(new Error('Please enter username'))
  } else if (value.length < 3) {
    callback(new Error('Username must be at least 3 characters'))
  } else if (value.length > 50) {
    callback(new Error('Username cannot exceed 50 characters'))
  } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('Username can only contain letters, numbers, and underscores'))
  } else {
    callback()
  }
}

// Password validation
export const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('Please enter password'))
  } else if (value.length < 6) {
    callback(new Error('Password must be at least 6 characters'))
  } else if (value.length > 100) {
    callback(new Error('Password cannot exceed 100 characters'))
  } else {
    callback()
  }
}

// Confirm password validation
export const validateConfirmPassword = (password) => {
  return (rule, value, callback) => {
    if (!value) {
      callback(new Error('Please confirm password'))
    } else if (value !== password) {
      callback(new Error('Passwords do not match'))
    } else {
      callback()
    }
  }
}

// Email validation (optional)
export const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback()
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('Please enter a valid email address'))
  } else {
    callback()
  }
}

// Required field validation
export const validateRequired = (fieldName) => {
  return (rule, value, callback) => {
    if (!value || (typeof value === 'string' && !value.trim())) {
      callback(new Error(`Please enter ${fieldName}`))
    } else {
      callback()
    }
  }
}

// Number range validation
export const validateNumberRange = (min, max, fieldName) => {
  return (rule, value, callback) => {
    if (value === null || value === undefined || value === '') {
      callback()
    } else if (isNaN(value)) {
      callback(new Error(`${fieldName} must be a number`))
    } else if (value < min || value > max) {
      callback(new Error(`${fieldName} must be between ${min} and ${max}`))
    } else {
      callback()
    }
  }
}

// Text length validation
export const validateTextLength = (min, max, fieldName) => {
  return (rule, value, callback) => {
    if (!value) {
      callback()
    } else if (value.length < min) {
      callback(new Error(`${fieldName} must be at least ${min} characters`))
    } else if (value.length > max) {
      callback(new Error(`${fieldName} cannot exceed ${max} characters`))
    } else {
      callback()
    }
  }
}

// URL validation
export const validateUrl = (rule, value, callback) => {
  if (!value) {
    callback()
  } else {
    try {
      new URL(value)
      callback()
    } catch {
      callback(new Error('Please enter a valid URL'))
    }
  }
}

// Date validation
export const validateDate = (rule, value, callback) => {
  if (!value) {
    callback(new Error('Please select a date'))
  } else if (!(value instanceof Date) && isNaN(Date.parse(value))) {
    callback(new Error('Please enter a valid date'))
  } else {
    callback()
  }
}

// Date range validation
export const validateDateRange = (startDate, endDate) => {
  return (rule, value, callback) => {
    if (!startDate || !endDate) {
      callback()
    } else if (new Date(startDate) > new Date(endDate)) {
      callback(new Error('Start date must be before end date'))
    } else {
      callback()
    }
  }
}

// File size validation
export const validateFileSize = (maxSizeMB) => {
  return (file) => {
    const isLtMaxSize = file.size / 1024 / 1024 < maxSizeMB
    if (!isLtMaxSize) {
      return `File size cannot exceed ${maxSizeMB}MB`
    }
    return true
  }
}

// Image file type validation
export const validateImageType = (file) => {
  const validTypes = ['image/jpeg', 'image/png', 'image/gif']
  const isValidType = validTypes.includes(file.type)
  if (!isValidType) {
    return 'Only JPG, PNG, and GIF images are allowed'
  }
  return true
}

// Video file type validation
export const validateVideoType = (file) => {
  const validTypes = ['video/mp4', 'video/avi']
  const isValidType = validTypes.includes(file.type)
  if (!isValidType) {
    return 'Only MP4 and AVI videos are allowed'
  }
  return true
}

// Common validation rules
export const commonRules = {
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' },
    { min: 3, max: 50, message: 'Username must be 3-50 characters', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: 'Username can only contain letters, numbers, and underscores', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
    { min: 6, max: 100, message: 'Password must be 6-100 characters', trigger: 'blur' }
  ],
  required: (fieldName) => [
    { required: true, message: `Please enter ${fieldName}`, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: 'Please enter a valid email address', trigger: 'blur' }
  ]
}
