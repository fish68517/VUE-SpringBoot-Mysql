import { ref } from 'vue'
import { showError } from '../utils/notificationUtils'

/**
 * Composable for handling async operations with loading and error states
 * @param {function} asyncFn - Async function to execute
 * @param {object} options - Options for the composable
 * @returns {object} - State and methods
 */
export const useAsync = (asyncFn, options = {}) => {
  const {
    immediate = false,
    showErrorNotification = true,
    onSuccess = null,
    onError = null
  } = options

  const data = ref(null)
  const error = ref(null)
  const isLoading = ref(false)

  /**
   * Execute the async function
   * @param {...any} args - Arguments to pass to the async function
   * @returns {Promise} - Promise from async function
   */
  const execute = async (...args) => {
    try {
      isLoading.value = true
      error.value = null
      const result = await asyncFn(...args)
      data.value = result
      if (onSuccess) {
        onSuccess(result)
      }
      return result
    } catch (err) {
      error.value = err
      if (showErrorNotification) {
        const errorMsg = err.message || 'An error occurred'
        showError(errorMsg)
      }
      if (onError) {
        onError(err)
      }
      throw err
    } finally {
      isLoading.value = false
    }
  }

  /**
   * Reset the state
   */
  const reset = () => {
    data.value = null
    error.value = null
    isLoading.value = false
  }

  // Execute immediately if specified
  if (immediate) {
    execute()
  }

  return {
    data,
    error,
    isLoading,
    execute,
    reset
  }
}
