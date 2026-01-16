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

const schoolService = {
  /**
   * Search schools with optional filters and pagination
   * @param {object} filters - Filter parameters
   * @param {string} filters.city - Optional city filter
   * @param {string} filters.tier - Optional school tier filter (985/211/DOUBLE_NON/OTHER)
   * @param {number} filters.expectedScoreMin - Optional minimum expected score
   * @param {number} filters.expectedScoreMax - Optional maximum expected score
   * @param {string} filters.major - Optional major filter
   * @param {number} filters.page - Page number (0-indexed, default 0)
   * @param {number} filters.size - Page size (default 20)
   * @returns {Promise} Response containing paginated school results
   */
  searchSchools(filters = {}) {
    const params = {
      page: filters.page || 0,
      size: filters.size || 20
    }

    if (filters.city) {
      params.city = filters.city
    }
    if (filters.tier) {
      params.tier = filters.tier
    }
    if (filters.expectedScoreMin !== undefined && filters.expectedScoreMin !== null) {
      params.expectedScoreMin = filters.expectedScoreMin
    }
    if (filters.expectedScoreMax !== undefined && filters.expectedScoreMax !== null) {
      params.expectedScoreMax = filters.expectedScoreMax
    }
    if (filters.major) {
      params.major = filters.major
    }

    return apiClient.get('/schools', { params })
  },

  /**
   * Get detailed information for a specific school
   * @param {number} schoolId - The school ID
   * @returns {Promise} Response containing school details
   */
  getSchoolDetail(schoolId) {
    return apiClient.get(`/schools/${schoolId}`)
  },

  /**
   * Get reexamination requirements for a specific school
   * @param {number} schoolId - The school ID
   * @returns {Promise} Response containing school requirements
   */
  getSchoolRequirements(schoolId) {
    return apiClient.get(`/schools/${schoolId}/requirements`)
  },

  /**
   * Get exam subjects for a specific school
   * @param {number} schoolId - The school ID
   * @returns {Promise} Response containing exam subjects
   */
  getSchoolExamSubjects(schoolId) {
    return apiClient.get(`/schools/${schoolId}/subjects`)
  },

  /**
   * Get favorite statistics for a specific school
   * @param {number} schoolId - The school ID
   * @returns {Promise} Response containing favorite statistics
   */
  getSchoolFavoriteStats(schoolId) {
    return apiClient.get(`/schools/${schoolId}/favorite-stats`)
  },

  /**
   * Get comments for a specific school
   * @param {number} schoolId - The school ID
   * @param {number} page - Page number (0-indexed, default 0)
   * @param {number} size - Page size (default 20)
   * @returns {Promise} Response containing paginated comments
   */
  getSchoolComments(schoolId, page = 0, size = 20) {
    return apiClient.get(`/schools/${schoolId}/comments`, {
      params: { page, size }
    })
  },

  /**
   * Add a school to favorites
   * @param {number} schoolId - The school ID
   * @returns {Promise} Response confirming favorite was added
   */
  addFavorite(schoolId) {
    return apiClient.post(`/schools/${schoolId}/favorite`)
  },

  /**
   * Remove a school from favorites
   * @param {number} schoolId - The school ID
   * @returns {Promise} Response confirming favorite was removed
   */
  removeFavorite(schoolId) {
    return apiClient.delete(`/schools/${schoolId}/favorite`)
  },

  /**
   * Get user's favorite schools
   * @param {number} page - Page number (0-indexed, default 0)
   * @param {number} size - Page size (default 20)
   * @returns {Promise} Response containing paginated favorite schools
   */
  getUserFavorites(page = 0, size = 20) {
    return apiClient.get('/me/favorites', {
      params: { page, size }
    })
  },

  /**
   * Create a new comment for a school
   * @param {number} schoolId - The school ID
   * @param {string} content - The comment content
   * @returns {Promise} Response containing the created comment
   */
  createComment(schoolId, content) {
    return apiClient.post(`/schools/${schoolId}/comments`, null, {
      params: { content }
    })
  },

  /**
   * Create a reply to an existing comment
   * @param {number} commentId - The parent comment ID
   * @param {string} content - The reply content
   * @returns {Promise} Response containing the created reply
   */
  createReply(commentId, content) {
    return apiClient.post(`/comments/${commentId}/reply`, null, {
      params: { content }
    })
  },

  /**
   * Delete a comment
   * @param {number} commentId - The comment ID to delete
   * @returns {Promise} Response confirming deletion
   */
  deleteComment(commentId) {
    return apiClient.delete(`/comments/${commentId}`)
  }
}

export default schoolService
