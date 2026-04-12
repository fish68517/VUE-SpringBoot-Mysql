import { ref } from 'vue'

/**
 * Composable for managing loading states
 * @param {boolean} initialState - Initial loading state (default: false)
 * @returns {object} - Loading state and methods
 */
export const useLoading = (initialState = false) => {
  const isLoading = ref(initialState)
  const loadingMessage = ref('')

  /**
   * Start loading
   * @param {string} message - Optional loading message
   */
  const startLoading = (message = '') => {
    isLoading.value = true
    loadingMessage.value = message
  }

  /**
   * Stop loading
   */
  const stopLoading = () => {
    isLoading.value = false
    loadingMessage.value = ''
  }

  /**
   * Execute async function with loading state
   * @param {function} asyncFn - Async function to execute
   * @param {string} message - Optional loading message
   * @returns {Promise} - Promise from async function
   */
  const withLoading = async (asyncFn, message = '') => {
    try {
      startLoading(message)
      return await asyncFn()
    } finally {
      stopLoading()
    }
  }

  return {
    isLoading,
    loadingMessage,
    startLoading,
    stopLoading,
    withLoading
  }
}
