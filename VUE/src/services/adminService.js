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

const adminService = {
  /**
   * Get all users with pagination and filtering
   * @param {number} page - The page number (0-indexed)
   * @param {number} size - The page size
   * @param {string} keyword - Optional search keyword
   * @param {string} status - Optional status filter (ENABLED/DISABLED)
   * @returns {Promise} Response containing paginated user list
   */
  getUsers(page = 0, size = 20, keyword = '', status = '') {
    return apiClient.get('/admin/users', {
      params: {
        page,
        size,
        keyword: keyword || undefined,
        status: status || undefined
      }
    })
  },

  /**
   * Update user status
   * @param {number} userId - The user ID
   * @param {string} status - The new status (ENABLED/DISABLED)
   * @returns {Promise} Response containing updated user data
   */
  updateUserStatus(userId, status) {
    return apiClient.patch(`/admin/users/${userId}/status`, {
      status
    })
  },

  /**
   * Reset user password
   * @param {number} userId - The user ID
   * @returns {Promise} Response containing temporary password
   */
  resetUserPassword(userId) {
    return apiClient.post(`/admin/users/${userId}/reset-password`)
  },

  /**
   * Get all schools with pagination
   * @param {number} page - The page number (0-indexed)
   * @param {number} size - The page size
   * @returns {Promise} Response containing paginated school list
   */
  getSchools(page = 0, size = 20) {
    return apiClient.get('/admin/schools', {
      params: { page, size }
    })
  },

  /**
   * Create a new school
   * @param {object} schoolData - The school data
   * @returns {Promise} Response containing created school
   */
  createSchool(schoolData) {
    return apiClient.post('/admin/schools', schoolData)
  },

  /**
   * Update an existing school
   * @param {number} schoolId - The school ID
   * @param {object} schoolData - The updated school data
   * @returns {Promise} Response containing updated school
   */
  updateSchool(schoolId, schoolData) {
    return apiClient.put(`/admin/schools/${schoolId}`, schoolData)
  },

  /**
   * Delete a school
   * @param {number} schoolId - The school ID
   * @returns {Promise} Response confirming deletion
   */
  deleteSchool(schoolId) {
    return apiClient.delete(`/admin/schools/${schoolId}`)
  },

  /**
   * Create a major for a school
   * @param {number} schoolId - The school ID
   * @param {object} majorData - The major data
   * @returns {Promise} Response containing created major
   */
  createMajor(schoolId, majorData) {
    return apiClient.post(`/admin/schools/${schoolId}/majors`, majorData)
  },

  /**
   * Create an exam subject for a school
   * @param {number} schoolId - The school ID
   * @param {object} subjectData - The exam subject data
   * @returns {Promise} Response containing created exam subject
   */
  createExamSubject(schoolId, subjectData) {
    return apiClient.post(`/admin/schools/${schoolId}/subjects`, subjectData)
  },

  /**
   * Get all announcements with pagination
   * @param {number} page - The page number (0-indexed)
   * @param {number} size - The page size
   * @returns {Promise} Response containing paginated announcement list
   */
  getAnnouncements(page = 0, size = 20) {
    return apiClient.get('/admin/announcements', {
      params: { page, size }
    })
  },

  /**
   * Create a new announcement
   * @param {object} announcementData - The announcement data
   * @returns {Promise} Response containing created announcement
   */
  createAnnouncement(announcementData) {
    return apiClient.post('/admin/announcements', announcementData)
  },

  /**
   * Update an existing announcement
   * @param {number} announcementId - The announcement ID
   * @param {object} announcementData - The updated announcement data
   * @returns {Promise} Response containing updated announcement
   */
  updateAnnouncement(announcementId, announcementData) {
    return apiClient.put(`/admin/announcements/${announcementId}`, announcementData)
  },

  /**
   * Delete an announcement
   * @param {number} announcementId - The announcement ID
   * @returns {Promise} Response confirming deletion
   */
  deleteAnnouncement(announcementId) {
    return apiClient.delete(`/admin/announcements/${announcementId}`)
  },

  /**
   * Get all feedback with pagination and filtering
   * @param {number} page - The page number (0-indexed)
   * @param {number} size - The page size
   * @param {string} status - Optional status filter (NEW/PROCESSING/DONE)
   * @param {string} type - Optional type filter (BUG/SUGGESTION/DATA_ERROR)
   * @param {string} keyword - Optional search keyword
   * @returns {Promise} Response containing paginated feedback list
   */
  getFeedback(page = 0, size = 20, status = '', type = '', keyword = '') {
    return apiClient.get('/admin/feedback', {
      params: {
        page,
        size,
        status: status || undefined,
        type: type || undefined,
        keyword: keyword || undefined
      }
    })
  },

  /**
   * Reply to feedback and update status
   * @param {number} feedbackId - The feedback ID
   * @param {string} status - The new status (NEW/PROCESSING/DONE)
   * @param {string} adminReply - The admin reply message
   * @returns {Promise} Response containing updated feedback
   */
  replyToFeedback(feedbackId, status, adminReply) {
    return apiClient.patch(`/admin/feedback/${feedbackId}`, {
      status,
      adminReply
    })
  }
}

export default adminService
