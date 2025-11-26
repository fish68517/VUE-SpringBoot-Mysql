import api from './api'

/**
 * Registration service - encapsulates registration-related API calls
 */
export const registerService = {
  /**
   * Register a new user
   * @param {Object} data - Registration data (username, email, password)
   * @returns {Promise} Response containing user info and JWT token
   */
  register(data) {
    return api.post('/auth/register', data)
  }
}
