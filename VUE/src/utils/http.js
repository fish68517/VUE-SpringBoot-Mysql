import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'

// Create axios instance
const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Loading instance
let loadingInstance = null
let requestCount = 0

/**
 * Show loading animation
 */
const showLoading = () => {
  if (requestCount === 0) {
    loadingInstance = ElLoading.service({
      lock: true,
      text: 'Loading...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
  }
  requestCount++
}

/**
 * Hide loading animation
 */
const hideLoading = () => {
  requestCount--
  if (requestCount === 0 && loadingInstance) {
    loadingInstance.close()
    loadingInstance = null
  }
}

/**
 * Request interceptor
 */
http.interceptors.request.use(
  (config) => {
    showLoading()

    // Add token to request header
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    hideLoading()
    return Promise.reject(error)
  }
)

/**
 * Response interceptor
 */
http.interceptors.response.use(
  (response) => {
    hideLoading()

    const { data } = response

    // Check if response follows ApiResponse format
    if (data && typeof data === 'object' && 'code' in data) {
      if (data.code === 200) {
        return data
      } else if (data.code === 401) {
        // Unauthorized - redirect to login
        localStorage.removeItem('token')
        window.location.href = '/login'
        ElMessage.error(data.message || 'Unauthorized')
        return Promise.reject(new Error(data.message))
      } else if (data.code === 403) {
        ElMessage.error(data.message || 'Access denied')
        return Promise.reject(new Error(data.message))
      } else {
        ElMessage.error(data.message || 'Request failed')
        return Promise.reject(new Error(data.message))
      }
    }

    return data
  },
  (error) => {
    hideLoading()

    if (error.response) {
      const { status, data } = error.response

      if (status === 401) {
        localStorage.removeItem('token')
        window.location.href = '/login'
        ElMessage.error('Session expired, please login again')
      } else if (status === 403) {
        ElMessage.error('Access denied')
      } else if (status === 404) {
        ElMessage.error('Resource not found')
      } else if (status === 500) {
        ElMessage.error('Server error')
      } else {
        ElMessage.error(data?.message || 'Request failed')
      }
    } else if (error.request) {
      ElMessage.error('Network error, please check your connection')
    } else {
      ElMessage.error('Request error')
    }

    return Promise.reject(error)
  }
)

export default http
