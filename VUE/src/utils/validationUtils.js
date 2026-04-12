/**
 * Validation utilities for form fields
 */

/**
 * Validate email format
 * @param {string} email - Email to validate
 * @returns {boolean} - True if valid email
 */
export const isValidEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

/**
 * Validate password strength
 * @param {string} password - Password to validate
 * @returns {object} - { isValid: boolean, strength: 'weak'|'medium'|'strong', message: string }
 */
export const validatePassword = (password) => {
  if (!password) {
    return { isValid: false, strength: 'weak', message: 'Password is required' }
  }

  if (password.length < 8) {
    return { isValid: false, strength: 'weak', message: 'Password must be at least 8 characters' }
  }

  const hasUpperCase = /[A-Z]/.test(password)
  const hasLowerCase = /[a-z]/.test(password)
  const hasNumbers = /\d/.test(password)
  const hasSpecialChar = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)

  const strengthScore = [hasUpperCase, hasLowerCase, hasNumbers, hasSpecialChar].filter(Boolean).length

  let strength = 'weak'
  if (strengthScore >= 3) {
    strength = 'strong'
  } else if (strengthScore >= 2) {
    strength = 'medium'
  }

  return {
    isValid: true,
    strength,
    message: `Password strength: ${strength}`
  }
}

/**
 * Validate username
 * @param {string} username - Username to validate
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validateUsername = (username) => {
  if (!username) {
    return { isValid: false, message: 'Username is required' }
  }

  if (username.length < 3) {
    return { isValid: false, message: 'Username must be at least 3 characters' }
  }

  if (username.length > 50) {
    return { isValid: false, message: 'Username must not exceed 50 characters' }
  }

  const usernameRegex = /^[a-zA-Z0-9_-]+$/
  if (!usernameRegex.test(username)) {
    return { isValid: false, message: 'Username can only contain letters, numbers, underscores, and hyphens' }
  }

  return { isValid: true, message: 'Username is valid' }
}

/**
 * Validate required field
 * @param {string} value - Value to validate
 * @param {string} fieldName - Field name for error message
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validateRequired = (value, fieldName = 'This field') => {
  if (!value || (typeof value === 'string' && value.trim() === '')) {
    return { isValid: false, message: `${fieldName} is required` }
  }
  return { isValid: true, message: '' }
}

/**
 * Validate date range
 * @param {Date|string} startDate - Start date
 * @param {Date|string} endDate - End date
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validateDateRange = (startDate, endDate) => {
  if (!startDate || !endDate) {
    return { isValid: false, message: 'Both start and end dates are required' }
  }

  const start = new Date(startDate)
  const end = new Date(endDate)

  if (start > end) {
    return { isValid: false, message: 'Start date must be before end date' }
  }

  return { isValid: true, message: '' }
}

/**
 * Validate number range
 * @param {number} value - Value to validate
 * @param {number} min - Minimum value
 * @param {number} max - Maximum value
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validateNumberRange = (value, min, max) => {
  if (value === null || value === undefined || value === '') {
    return { isValid: false, message: 'Value is required' }
  }

  const num = Number(value)
  if (isNaN(num)) {
    return { isValid: false, message: 'Value must be a number' }
  }

  if (num < min || num > max) {
    return { isValid: false, message: `Value must be between ${min} and ${max}` }
  }

  return { isValid: true, message: '' }
}

/**
 * Validate text length
 * @param {string} text - Text to validate
 * @param {number} minLength - Minimum length
 * @param {number} maxLength - Maximum length
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validateTextLength = (text, minLength, maxLength) => {
  if (!text) {
    return { isValid: false, message: 'Text is required' }
  }

  if (text.length < minLength) {
    return { isValid: false, message: `Text must be at least ${minLength} characters` }
  }

  if (text.length > maxLength) {
    return { isValid: false, message: `Text must not exceed ${maxLength} characters` }
  }

  return { isValid: true, message: '' }
}

/**
 * Validate file size
 * @param {File} file - File to validate
 * @param {number} maxSizeMB - Maximum file size in MB
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validateFileSize = (file, maxSizeMB = 500) => {
  if (!file) {
    return { isValid: false, message: 'File is required' }
  }

  const maxSizeBytes = maxSizeMB * 1024 * 1024
  if (file.size > maxSizeBytes) {
    return { isValid: false, message: `File size must not exceed ${maxSizeMB}MB` }
  }

  return { isValid: true, message: '' }
}

/**
 * Validate file type
 * @param {File} file - File to validate
 * @param {string[]} allowedTypes - Allowed MIME types
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validateFileType = (file, allowedTypes = ['image/jpeg', 'image/png', 'video/mp4']) => {
  if (!file) {
    return { isValid: false, message: 'File is required' }
  }

  if (!allowedTypes.includes(file.type)) {
    return { isValid: false, message: `File type must be one of: ${allowedTypes.join(', ')}` }
  }

  return { isValid: true, message: '' }
}

/**
 * Validate URL format
 * @param {string} url - URL to validate
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validateUrl = (url) => {
  if (!url) {
    return { isValid: false, message: 'URL is required' }
  }

  try {
    new URL(url)
    return { isValid: true, message: '' }
  } catch {
    return { isValid: false, message: 'Invalid URL format' }
  }
}

/**
 * Validate budget amount
 * @param {number} amount - Budget amount
 * @param {number} min - Minimum amount
 * @param {number} max - Maximum amount
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validateBudget = (amount, min = 0, max = 999999999) => {
  if (amount === null || amount === undefined || amount === '') {
    return { isValid: false, message: 'Budget is required' }
  }

  const num = Number(amount)
  if (isNaN(num)) {
    return { isValid: false, message: 'Budget must be a valid number' }
  }

  if (num < min) {
    return { isValid: false, message: `Budget must be at least ${min}` }
  }

  if (num > max) {
    return { isValid: false, message: `Budget must not exceed ${max}` }
  }

  return { isValid: true, message: '' }
}

/**
 * Validate date is not in the past
 * @param {Date|string} date - Date to validate
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validateFutureDate = (date) => {
  if (!date) {
    return { isValid: false, message: 'Date is required' }
  }

  const selectedDate = new Date(date)
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  if (selectedDate < today) {
    return { isValid: false, message: 'Date must be in the future' }
  }

  return { isValid: true, message: '' }
}

/**
 * Validate date is not in the future
 * @param {Date|string} date - Date to validate
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validatePastDate = (date) => {
  if (!date) {
    return { isValid: false, message: 'Date is required' }
  }

  const selectedDate = new Date(date)
  const today = new Date()
  today.setHours(23, 59, 59, 999)

  if (selectedDate > today) {
    return { isValid: false, message: 'Date must be in the past or today' }
  }

  return { isValid: true, message: '' }
}

/**
 * Validate that two values match (e.g., password confirmation)
 * @param {string} value1 - First value
 * @param {string} value2 - Second value
 * @param {string} fieldName - Field name for error message
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validateMatch = (value1, value2, fieldName = 'Values') => {
  if (value1 !== value2) {
    return { isValid: false, message: `${fieldName} do not match` }
  }

  return { isValid: true, message: '' }
}

/**
 * Validate that value is unique (requires async check)
 * @param {string} value - Value to validate
 * @param {Function} checkFn - Async function that returns true if unique
 * @returns {Promise<object>} - { isValid: boolean, message: string }
 */
export const validateUnique = async (value, checkFn) => {
  if (!value) {
    return { isValid: false, message: 'Value is required' }
  }

  try {
    const isUnique = await checkFn(value)
    if (!isUnique) {
      return { isValid: false, message: 'This value is already taken' }
    }
    return { isValid: true, message: '' }
  } catch (error) {
    return { isValid: false, message: 'Unable to validate uniqueness' }
  }
}

/**
 * Validate phone number format
 * @param {string} phone - Phone number to validate
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validatePhoneNumber = (phone) => {
  if (!phone) {
    return { isValid: false, message: 'Phone number is required' }
  }

  const phoneRegex = /^[\d\s\-\+\(\)]+$/
  if (!phoneRegex.test(phone) || phone.replace(/\D/g, '').length < 10) {
    return { isValid: false, message: 'Invalid phone number format' }
  }

  return { isValid: true, message: '' }
}

/**
 * Validate postal code format
 * @param {string} postalCode - Postal code to validate
 * @returns {object} - { isValid: boolean, message: string }
 */
export const validatePostalCode = (postalCode) => {
  if (!postalCode) {
    return { isValid: false, message: 'Postal code is required' }
  }

  const postalCodeRegex = /^[A-Z0-9\s\-]{3,10}$/i
  if (!postalCodeRegex.test(postalCode)) {
    return { isValid: false, message: 'Invalid postal code format' }
  }

  return { isValid: true, message: '' }
}
