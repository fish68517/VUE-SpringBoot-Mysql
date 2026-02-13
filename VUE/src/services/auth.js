import { useUserStore } from '../store'
import { userAPI } from './api'
import { ElMessage } from 'element-plus'

/**
 * Authentication service for handling login, logout, and session management
 */
export const authService = {
  /**
   * Login user with credentials
   * @param {string} username - User username
   * @param {string} password - User password
   * @returns {Promise<Object>} User data and token
   */
  async login(username, password) {
    try {
      const response = await userAPI.login({
        username,
        password
      })

      if (response.code === 200) {
        const user = response.data
        const userStore = useUserStore()
        
        // Generate token (in production, backend would return JWT)
        const token = `token_${user.id}_${Date.now()}`
        
        // Store user and token
        userStore.setUser(user)
        userStore.setToken(token)
        
        return { success: true, user, token }
      } else {
        return { success: false, message: response.message || '登录失败' }
      }
    } catch (error) {
      console.error('Login error:', error)
      const message = error.response?.data?.message || error.message || '登录失败，请稍后重试'
      return { success: false, message }
    }
  },

  /**
   * Register new user
   * @param {string} username - User username
   * @param {string} email - User email
   * @param {string} password - User password
   * @returns {Promise<Object>} Registration result
   */
  async register(username, email, password) {
    try {
      const response = await userAPI.register({
        username,
        email,
        password
      })

      if (response.code === 200) {
        return { success: true, message: '注册成功' }
      } else {
        return { success: false, message: response.message || '注册失败' }
      }
    } catch (error) {
      console.error('Registration error:', error)
      const message = error.response?.data?.message || error.message || '注册失败，请稍后重试'
      return { success: false, message }
    }
  },

  /**
   * Logout current user
   */
  logout() {
    const userStore = useUserStore()
    userStore.logout()
  },

  /**
   * Check if user is authenticated
   * @returns {boolean} True if user is logged in
   */
  isAuthenticated() {
    const userStore = useUserStore()
    return userStore.isLoggedIn
  },

  /**
   * Check if user is admin
   * @returns {boolean} True if user is admin
   */
  isAdmin() {
    const userStore = useUserStore()
    return userStore.isAdmin
  },

  /**
   * Get current user
   * @returns {Object|null} Current user object or null
   */
  getCurrentUser() {
    const userStore = useUserStore()
    return userStore.user
  },

  /**
   * Get current token
   * @returns {string|null} Current token or null
   */
  getToken() {
    const userStore = useUserStore()
    return userStore.token
  },

  /**
   * Restore session from localStorage
   * @returns {boolean} True if session was restored
   */
  restoreSession() {
    const userStore = useUserStore()
    const token = localStorage.getItem('token')
    const user = localStorage.getItem('user')

    if (token && user) {
      try {
        userStore.setToken(token)
        userStore.setUser(JSON.parse(user))
        return true
      } catch (error) {
        console.error('Failed to restore session:', error)
        userStore.logout()
        return false
      }
    }
    return false
  },

  /**
   * Clear session
   */
  clearSession() {
    const userStore = useUserStore()
    userStore.logout()
  }
}

export default authService
