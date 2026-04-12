import axios from 'axios'
import { useUserStore } from '../stores/userStore'
import { useErrorStore } from '../stores/errorStore'
import { errorHandler } from '../utils/errorHandler'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Flag to prevent multiple token refresh requests
let isRefreshing = false
let failedQueue = []

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  
  isRefreshing = false
  failedQueue = []
}

// Request interceptor - automatically add JWT token to request headers
api.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    errorHandler.logError(error, 'API Request')
    return Promise.reject(error)
  }
)

// Response interceptor - handle errors and token expiration
api.interceptors.response.use(
  (response) => {
    // Return the data from the unified response format
    return response.data
  },
  (error) => {
    const originalRequest = error.config
    const errorStore = useErrorStore()

    // Handle 401 Unauthorized - attempt token refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        }).then(token => {
          originalRequest.headers.Authorization = `Bearer ${token}`
          return api(originalRequest)
        }).catch(err => {
          return Promise.reject(err)
        })
      }

      originalRequest._retry = true
      isRefreshing = true

      const userStore = useUserStore()
      const token = userStore.token

      if (!token) {
        userStore.logout()
        errorStore.showError('Your session has expired. Please log in again.')
        window.location.href = '/login'
        return Promise.reject(error.response?.data || error)
      }

      // Attempt to refresh token
      return api.post('/auth/refresh', { token })
        .then(response => {
          const newToken = response.data.token
          userStore.setToken(newToken)
          userStore.setUser(response.data.user)
          
          originalRequest.headers.Authorization = `Bearer ${newToken}`
          processQueue(null, newToken)
          
          return api(originalRequest)
        })
        .catch(err => {
          processQueue(err, null)
          userStore.logout()
          errorStore.showError('Your session has expired. Please log in again.')
          window.location.href = '/login'
          errorHandler.logError(err, 'Token Refresh')
          return Promise.reject(err.data || err)
        })
    }

    // Handle 403 Forbidden - permission denied
    if (error.response?.status === 403) {
      errorHandler.handlePermissionError(
        error.response?.data?.message || 'You do not have permission to perform this action'
      )
    }

    // Handle 404 Not Found
    if (error.response?.status === 404) {
      errorHandler.handleNotFoundError(
        error.response?.data?.message || 'Resource not found'
      )
    }

    // Log error for debugging
    errorHandler.logError(error, 'API Response')
    
    // Return error response in unified format or raw error
    const errorData = error.response?.data || {
      code: error.response?.status || 500,
      message: error.message || 'An error occurred',
      data: null
    }
    
    return Promise.reject(errorData)
  }
)

export default api
