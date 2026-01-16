import axios from 'axios'

const API_BASE_URL = '/api'

// Create axios instance with default config
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Add token to requests if available
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Handle response errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

const feedbackService = {
  /**
   * Submit new feedback
   * @param {string} type - The feedback type (BUG/SUGGESTION/DATA_ERROR)
   * @param {string} content - The feedback content
   * @returns {Promise} Response containing feedback data
   */
  submitFeedback(type, content) {
    return apiClient.post('/feedback', {
      type,
      content
    })
  },

  /**
   * Get user's feedback history
   * @param {number} page - The page number (0-indexed)
   * @param {number} size - The page size
   * @returns {Promise} Response containing paginated feedback list
   */
  getUserFeedback(page = 0, size = 20) {
    return apiClient.get('/me/feedback', {
      params: {
        page,
        size
      }
    })
  }
}

export default feedbackService
