import api from './api'

/**
 * User service - encapsulates all user-related API calls
 */
export const userService = {
  /**
   * Get user information by ID
   * @param {number} userId - The user ID
   * @returns {Promise} Response containing user information
   */
  getUserById(userId) {
    return api.get(`/users/${userId}`)
  },

  /**
   * Update user information
   * @param {number} userId - The user ID
   * @param {Object} data - Update data (username, avatarUrl, bio)
   * @returns {Promise} Response containing updated user information
   */
  updateUser(userId, data) {
    return api.put(`/users/${userId}`, data)
  }
}
