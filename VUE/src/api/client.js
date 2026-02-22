import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import { performanceMonitor } from '@/utils/performanceMonitor'

const apiClient = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor to add token and track performance
apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    // Record request start time
    config.metadata = { startTime: performance.now() }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle errors and track performance
apiClient.interceptors.response.use(
  (response) => {
    // Record performance metrics
    const duration = Math.round(performance.now() - response.config.metadata.startTime)
    const apiName = response.config.url.split('/').pop() || 'unknown'
    performanceMonitor.recordAPICall(apiName, duration)

    // Handle backend Result wrapper format
    const result = response.data
    if (result && result.code === 200) {
      // Return the data field for successful responses
      return result.data !== undefined ? result.data : result
    }
    // For non-200 responses, throw error with message
    const error = new Error(result?.message || '请求失败')
    error.code = result?.code
    error.response = response
    throw error
  },
  (error) => {
    // Record performance metrics for failed requests
    if (error.config?.metadata) {
      const duration = Math.round(performance.now() - error.config.metadata.startTime)
      const apiName = error.config.url.split('/').pop() || 'unknown'
      performanceMonitor.recordAPICall(apiName, duration)
    }

    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      window.location.href = '/login'
    }
    // Preserve error message from backend
    const message = error.response?.data?.message || error.message || '请求失败'
    error.message = message
    return Promise.reject(error)
  }
)

export default apiClient
