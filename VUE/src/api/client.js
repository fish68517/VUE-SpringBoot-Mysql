import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const client = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor
client.interceptors.request.use(
  (config) => {
    const user = localStorage.getItem('user')
    if (user) {
      const userInfo = JSON.parse(user)
      config.headers['X-User-Id'] = userInfo.id
      config.headers['X-User-Role'] = userInfo.role
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
client.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default client
