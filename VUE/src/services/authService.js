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
    // If token is invalid or expired, clear auth and redirect to login
    if (error.response?.status === 401) {
      authService.logout()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

const authService = {
  /**
   * Register a new user account
   * @param {string} username - The username for the new account
   * @param {string} password - The password for the new account
   * @returns {Promise} Response containing user data and token
   */
  register(username, password) {
    return apiClient.post('/auth/register', {
      username,
      password
    })
  },

  /**
   * Login with username and password
   * @param {string} username - The username
   * @param {string} password - The password
   * @returns {Promise} Response containing user data and token
   */
  login(username, password) {
    return apiClient.post('/auth/login', {
      username,
      password
    })
  },

  /**
   * Logout the current user
   * Clears all authentication data from storage
   */
  logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    // Clear authorization header
    delete apiClient.defaults.headers.common['Authorization']
    // Clear sessionStorage in case it's being used
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('user')
  },

  /**
   * Store JWT token in localStorage and set it in request headers
   * @param {string} token - The JWT token
   */
  setToken(token) {
    localStorage.setItem('token', token)
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`
  },

  /**
   * Retrieve JWT token from localStorage
   * @returns {string|null} The JWT token or null if not found
   */
  getToken() {
    return localStorage.getItem('token')
  },

  /**
   * Store user information in localStorage
   * @param {object} user - The user object containing id, username, role, etc.
   */
  setUser(user) {
    localStorage.setItem('user', JSON.stringify(user))
  },

  /**
   * Retrieve user information from localStorage
   * @returns {object|null} The user object or null if not found
   */
  getUser() {
    const user = localStorage.getItem('user')
    return user ? JSON.parse(user) : null
  },

  /**
   * Check if user is currently authenticated
   * @returns {boolean} True if user has a valid token, false otherwise
   */
  isAuthenticated() {
    return !!this.getToken()
  },

  /**
   * Get the current user's role
   * @returns {string|null} The user's role (USER or ADMIN) or null if not authenticated
   */
  getUserRole() {
    const user = this.getUser()
    return user ? user.role : null
  },

  /**
   * Check if current user is an admin
   * @returns {boolean} True if user is an admin, false otherwise
   */
  isAdmin() {
    return this.getUserRole() === 'ADMIN'
  },

  /**
   * Check if current user is a regular user
   * @returns {boolean} True if user is a regular user, false otherwise
   */
  isUser() {
    return this.getUserRole() === 'USER'
  },

  /**
   * Ensure that user is logged out if the token is invalid or expired
   */
  clearInvalidToken() {
    const token = this.getToken()
    if (!token) {
      this.logout()
    }
  }
}

export default authService
