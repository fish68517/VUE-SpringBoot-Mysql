/**
 * Form validation utilities
 * Provides validation rules and error handling for forms
 */

/**
 * Validation rules
 */
export const validationRules = {
  required: (value) => {
    if (typeof value === 'string') {
      return value.trim().length > 0 || 'This field is required'
    }
    return value !== null && value !== undefined || 'This field is required'
  },

  email: (value) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return emailRegex.test(value) || 'Please enter a valid email address'
  },

  phone: (value) => {
    const phoneRegex = /^1[3-9]\d{9}$/
    return phoneRegex.test(value) || 'Please enter a valid phone number'
  },

  studentId: (value) => {
    const studentIdRegex = /^\d{8,12}$/
    return studentIdRegex.test(value) || 'Please enter a valid student ID'
  },

  password: (value) => {
    if (value.length < 6) {
      return 'Password must be at least 6 characters'
    }
    return true
  },

  passwordStrength: (value) => {
    const hasUpperCase = /[A-Z]/.test(value)
    const hasLowerCase = /[a-z]/.test(value)
    const hasNumbers = /\d/.test(value)
    const hasSpecialChar = /[!@#$%^&*]/.test(value)

    if (value.length < 8) {
      return 'Password must be at least 8 characters'
    }

    if (!hasUpperCase || !hasLowerCase || !hasNumbers) {
      return 'Password must contain uppercase, lowercase, and numbers'
    }

    return true
  },

  minLength: (min) => (value) => {
    return value.length >= min || `Minimum length is ${min} characters`
  },

  maxLength: (max) => (value) => {
    return value.length <= max || `Maximum length is ${max} characters`
  },

  min: (min) => (value) => {
    return Number(value) >= min || `Value must be at least ${min}`
  },

  max: (max) => (value) => {
    return Number(value) <= max || `Value must be at most ${max}`
  },

  match: (fieldValue) => (value) => {
    return value === fieldValue || 'Fields do not match'
  },

  url: (value) => {
    try {
      new URL(value)
      return true
    } catch {
      return 'Please enter a valid URL'
    }
  },

  number: (value) => {
    return !isNaN(value) && value !== '' || 'Please enter a valid number'
  },

  integer: (value) => {
    return Number.isInteger(Number(value)) || 'Please enter a valid integer'
  }
}

/**
 * Validate a single field
 * @param {string} value - Field value
 * @param {Array|Function} rules - Validation rules
 * @returns {string|true} - Error message or true if valid
 */
export function validateField(value, rules) {
  if (!rules) return true

  const ruleArray = Array.isArray(rules) ? rules : [rules]

  for (const rule of ruleArray) {
    const result = typeof rule === 'function' ? rule(value) : true
    if (result !== true) {
      return result
    }
  }

  return true
}

/**
 * Validate multiple fields
 * @param {Object} formData - Form data object
 * @param {Object} validationSchema - Validation schema
 * @returns {Object} - Errors object
 */
export function validateForm(formData, validationSchema) {
  const errors = {}

  for (const [field, rules] of Object.entries(validationSchema)) {
    const value = formData[field]
    const error = validateField(value, rules)

    if (error !== true) {
      errors[field] = error
    }
  }

  return errors
}

/**
 * Check if form has errors
 * @param {Object} errors - Errors object
 * @returns {boolean}
 */
export function hasErrors(errors) {
  return Object.keys(errors).length > 0
}

/**
 * Get first error message
 * @param {Object} errors - Errors object
 * @returns {string|null}
 */
export function getFirstError(errors) {
  const errorKeys = Object.keys(errors)
  return errorKeys.length > 0 ? errors[errorKeys[0]] : null
}

/**
 * Clear field error
 * @param {Object} errors - Errors object
 * @param {string} field - Field name
 */
export function clearFieldError(errors, field) {
  delete errors[field]
}

/**
 * Clear all errors
 * @param {Object} errors - Errors object
 */
export function clearAllErrors(errors) {
  Object.keys(errors).forEach(key => {
    delete errors[key]
  })
}
