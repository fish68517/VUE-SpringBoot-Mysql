import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import router from '../router'

const http = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (!payload.success) {
      ElMessage.error(payload.message || '请求失败')
      return Promise.reject(new Error(payload.message || '请求失败'))
    }
    return payload.data
  },
  (error) => {
    const authStore = useAuthStore()
    const message = error.response?.data?.message || error.message || '网络异常'
    if (error.response?.status === 401) {
      authStore.logout()
      router.push('/login')
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default http
