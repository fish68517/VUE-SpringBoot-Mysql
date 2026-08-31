import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './auth'
import type { ResponseResult } from '@/types/api'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 12000,
})

request.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  config.headers['X-Client-Type'] = 'web'
  return config
})

request.interceptors.response.use(
  (response) => {
    const result = response.data as ResponseResult<unknown>
    if (result.code !== 200) {
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(new Error(result.message))
    }
    return result
  },
  (error: AxiosError<ResponseResult<unknown>>) => {
    if (error.response?.status === 401) {
      removeToken()
      if (!location.pathname.startsWith('/login')) location.href = '/login'
    }
    ElMessage.error(error.response?.data?.message || error.message || '网络请求异常')
    return Promise.reject(error)
  },
)

export default request
