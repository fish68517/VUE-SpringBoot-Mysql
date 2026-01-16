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

const userService = {
  /**
   * Get the current user's profile
   * @returns {Promise} Response containing user profile data
   */
  getUserProfile() {
    return apiClient.get('/me')
  },

  /**
   * Update the current user's profile
   * @param {object} profileData - The profile data to update
   * @returns {Promise} Response containing updated profile data
   */
  updateUserProfile(profileData) {
    return apiClient.put('/me/profile', profileData)
  }
}

export default userService
