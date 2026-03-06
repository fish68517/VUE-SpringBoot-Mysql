import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  withCredentials: true
})

// Request interceptor - adds session information
api.interceptors.request.use(
  (config) => {
    // Add session information to request headers if available
    const sessionId = sessionStorage.getItem('sessionId')
    if (sessionId) {
      config.headers['X-Session-Id'] = sessionId
    }

    // Add content type header for JSON requests
    if (!config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json'
    }

    return config
  },
  (error) => {
    ElMessage.error('请求配置错误')
    return Promise.reject(error)
  }
)

// Response interceptor - handles errors and processes responses
api.interceptors.response.use(
  (response) => {
    // Extract data from response
    const { data } = response

    // Check if response contains error information
    if (data && data.code && data.code !== 200 && data.code !== 0) {
      ElMessage.error(data.message || '操作失败')
      return Promise.reject(data)
    }

    return data
  },
  (error) => {
    // Handle different error scenarios
    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 400:
          ElMessage.error(data?.message || '请求参数错误')
          break
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          sessionStorage.clear()
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('您没有权限访问此资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error(data?.message || '服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || `请求失败 (${status})`)
      }

      return Promise.reject(data || error)
    } else if (error.request) {
      // Request made but no response received
      ElMessage.error('网络连接失败，请检查网络设置')
      return Promise.reject(error)
    } else {
      // Error in request setup
      ElMessage.error('请求发送失败')
      return Promise.reject(error)
    }
  }
)

export default api
