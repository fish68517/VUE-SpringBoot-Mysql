import api from './api'

/**
 * Authentication service - encapsulates all authentication-related API calls
 */
export const authService = {
  /**
   * Register a new user
   * @param {Object} data - Registration data (email, password, username)
   * @returns {Promise} Response containing user info and JWT token
   */
  register(data) {
    return api.post('/auth/register', data)
  },

  /**
   * Login user with credentials
   * @param {Object} data - Login data (email, password)
   * @returns {Promise} Response containing user info and JWT token
   */
  login(data) {
    return api.post('/auth/login', data)
  },

  /**
   * Refresh JWT token
   * @returns {Promise} Response containing new JWT token
   */
  refreshToken() {
    return api.post('/auth/refresh')
  },

  /**
   * Logout user - clear stored token
   */
  logout() {
    localStorage.removeItem('token')
  }
}
