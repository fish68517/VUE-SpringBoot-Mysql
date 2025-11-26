import api from './api'

/**
 * Login service - encapsulates login-related API calls
 */
export const loginService = {
  /**
   * Login user with email and password
   * @param {Object} data - Login data (email, password)
   * @returns {Promise} Response containing user info and JWT token
   */
  login(data) {
    return api.post('/auth/login', data)
  }
}
