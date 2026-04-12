import { reactive, computed } from 'vue'
import * as validationUtils from '../utils/validationUtils'

/**
 * Composable for form validation with real-time feedback
 * @param {object} initialValues - Initial form values
 * @param {object} rules - Validation rules
 * @returns {object} - Form state and methods
 */
export const useFormValidation = (initialValues = {}, rules = {}) => {
  const formData = reactive({ ...initialValues })
  const errors = reactive({})
  const touched = reactive({})
  const validating = reactive({})

  /**
   * Validate a single field
   * @param {string} fieldName - Field name
   * @returns {Promise<boolean>} - True if valid
   */
  const validateField = async (fieldName) => {
    const rule = rules[fieldName]
    if (!rule) return true

    const value = formData[fieldName]
    const fieldErrors = []

    // Check required
    if (rule.required) {
      const validation = validationUtils.validateRequired(value, rule.label || fieldName)
      if (!validation.isValid) {
        fieldErrors.push(validation.message)
      }
    }

    // Skip other validations if field is empty and not required
    if (!value && !rule.required) {
      errors[fieldName] = ''
      return true
    }

    // Check email
    if (rule.type === 'email' && value) {
      if (!validationUtils.isValidEmail(value)) {
        fieldErrors.push(rule.message || 'Invalid email format')
      }
    }

    // Check password
    if (rule.type === 'password' && value) {
      const validation = validationUtils.validatePassword(value)
      if (!validation.isValid) {
        fieldErrors.push(validation.message)
      }
    }

    // Check username
    if (rule.type === 'username' && value) {
      const validation = validationUtils.validateUsername(value)
      if (!validation.isValid) {
        fieldErrors.push(validation.message)
      }
    }

    // Check min length
    if (rule.minLength && value && value.length < rule.minLength) {
      fieldErrors.push(rule.message || `Minimum ${rule.minLength} characters required`)
    }

    // Check max length
    if (rule.maxLength && value && value.length > rule.maxLength) {
      fieldErrors.push(rule.message || `Maximum ${rule.maxLength} characters allowed`)
    }

    // Check URL
    if (rule.type === 'url' && value) {
      const validation = validationUtils.validateUrl(value)
      if (!validation.isValid) {
        fieldErrors.push(validation.message)
      }
    }

    // Check budget
    if (rule.type === 'budget' && value !== null && value !== undefined && value !== '') {
      const validation = validationUtils.validateBudget(value, rule.min, rule.max)
      if (!validation.isValid) {
        fieldErrors.push(validation.message)
      }
    }

    // Check phone number
    if (rule.type === 'phone' && value) {
      const validation = validationUtils.validatePhoneNumber(value)
      if (!validation.isValid) {
        fieldErrors.push(validation.message)
      }
    }

    // Check postal code
    if (rule.type === 'postalCode' && value) {
      const validation = validationUtils.validatePostalCode(value)
      if (!validation.isValid) {
        fieldErrors.push(validation.message)
      }
    }

    // Check custom validator
    if (rule.validator && value) {
      try {
        validating[fieldName] = true
        const validation = await Promise.resolve(rule.validator(value))
        if (!validation.isValid) {
          fieldErrors.push(validation.message)
        }
      } catch (error) {
        fieldErrors.push(error.message || 'Validation error')
      } finally {
        validating[fieldName] = false
      }
    }

    errors[fieldName] = fieldErrors.length > 0 ? fieldErrors[0] : ''
    return fieldErrors.length === 0
  }

  /**
   * Validate all fields
   * @returns {Promise<boolean>} - True if all fields are valid
   */
  const validateForm = async () => {
    let isValid = true
    const validationPromises = []

    for (const fieldName in rules) {
      validationPromises.push(
        validateField(fieldName).then(result => {
          if (!result) isValid = false
        })
      )
    }

    await Promise.all(validationPromises)
    return isValid
  }

  /**
   * Mark field as touched and validate
   * @param {string} fieldName - Field name
   */
  const touchField = (fieldName) => {
    touched[fieldName] = true
    validateField(fieldName)
  }

  /**
   * Mark all fields as touched
   */
  const touchAllFields = () => {
    for (const fieldName in rules) {
      touched[fieldName] = true
    }
  }

  /**
   * Reset form to initial values
   */
  const resetForm = () => {
    Object.assign(formData, initialValues)
    Object.keys(errors).forEach(key => {
      errors[key] = ''
    })
    Object.keys(touched).forEach(key => {
      touched[key] = false
    })
    Object.keys(validating).forEach(key => {
      validating[key] = false
    })
  }

  /**
   * Set form values
   * @param {object} values - New values
   */
  const setFormValues = (values) => {
    Object.assign(formData, values)
  }

  /**
   * Get field error
   * @param {string} fieldName - Field name
   * @returns {string} - Error message
   */
  const getFieldError = (fieldName) => {
    return touched[fieldName] ? errors[fieldName] : ''
  }

  /**
   * Check if field has error
   * @param {string} fieldName - Field name
   * @returns {boolean} - True if field has error
   */
  const hasFieldError = (fieldName) => {
    return touched[fieldName] && !!errors[fieldName]
  }

  /**
   * Check if field is being validated
   * @param {string} fieldName - Field name
   * @returns {boolean} - True if field is validating
   */
  const isFieldValidating = (fieldName) => {
    return validating[fieldName] || false
  }

  const isFormValid = computed(() => {
    for (const fieldName in rules) {
      if (errors[fieldName]) {
        return false
      }
    }
    return true
  })

  const isFormTouched = computed(() => {
    return Object.values(touched).some(value => value === true)
  })

  return {
    formData,
    errors,
    touched,
    validating,
    validateField,
    validateForm,
    touchField,
    touchAllFields,
    resetForm,
    setFormValues,
    getFieldError,
    hasFieldError,
    isFieldValidating,
    isFormValid,
    isFormTouched
  }
}
