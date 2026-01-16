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

const announcementService = {
  /**
   * Get all published announcements with pagination
   * @param {number} page - The page number (0-indexed)
   * @param {number} size - The page size
   * @returns {Promise} Response containing paginated published announcements
   */
  getAnnouncements(page = 0, size = 20) {
    return apiClient.get('/announcements', {
      params: { page, size }
    })
  }
}

export default announcementService
