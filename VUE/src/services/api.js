import axios from 'axios'

// 创建 Axios 实例
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 错误消息映射
const errorMessages = {
  400: '请求参数错误',
  401: '未授权，请登录',
  403: '禁止访问，权限不足',
  404: '请求的资源不存在',
  500: '服务器内部错误',
  503: '服务暂时不可用',
  timeout: '请求超时，请重试',
}

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 可以在这里添加认证令牌等
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    // 处理响应数据
    const { data } = response
    if (data.code === 200) {
      return data.data
    } else {
      // 处理业务错误
      const errorMsg = data.message || '请求失败'
      const error = new Error(errorMsg)
      error.code = data.code
      return Promise.reject(error)
    }
  },
  (error) => {
    // 处理网络错误
    let errorMsg = '请求失败'

    if (error.response) {
      const { status, data } = error.response
      errorMsg = data?.message || errorMessages[status] || `HTTP Error ${status}`
      console.error(`HTTP Error ${status}:`, errorMsg)
    } else if (error.request) {
      errorMsg = '网络连接失败，请检查网络'
      console.error('No response received:', error.request)
    } else if (error.code === 'ECONNABORTED') {
      errorMsg = errorMessages.timeout
      console.error('Request timeout:', error.message)
    } else {
      errorMsg = error.message || '请求失败'
      console.error('Error:', error.message)
    }

    const enhancedError = new Error(errorMsg)
    enhancedError.originalError = error
    return Promise.reject(enhancedError)
  }
)

export default apiClient
