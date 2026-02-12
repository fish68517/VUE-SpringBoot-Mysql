import axios from 'axios'
import { useUserStore } from '@/stores/userStore'

// 创建 axios 实例
const instance = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    // 从 store 获取用户信息和组织信息
    const userStore = useUserStore()
    
    // 如果有用户信息，添加到请求头
    if (userStore.user) {
      config.headers['X-User-Id'] = userStore.user.id
      config.headers['X-Organization-Id'] = userStore.user.organizationId
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    // 返回响应数据
    return response.data
  },
  (error) => {
    // 处理错误
    if (error.response) {
      const { status, data } = error.response
      
      // 处理 401 未授权
      if (status === 401) {
        const userStore = useUserStore()
        userStore.logout()
        window.location.href = '/login'
      }
      
      // 返回错误信息
      return Promise.reject(data || { message: '请求失败' })
    }
    
    return Promise.reject(error)
  }
)

export default instance
