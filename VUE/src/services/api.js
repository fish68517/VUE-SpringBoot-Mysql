import axios from 'axios'
import { ElMessage } from 'element-plus'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
})

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      ElMessage.error('登录已过期，请重新登录')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// User APIs
export const userAPI = {
  register: (data) => api.post('/users/register', data),
  login: (data) => api.post('/users/login', data),
  getUser: (id) => api.get(`/users/${id}`),
  updateUser: (id, data) => api.put(`/users/${id}`, data),
  changePassword: (id, data) => api.put(`/users/${id}/password`, data),
  getHistory: (id) => api.get(`/users/${id}/history`),
  getCollections: (id) => api.get(`/users/${id}/collections`)
}

// Pattern APIs
export const patternAPI = {
  getPatterns: (params) => api.get('/patterns', { params }),
  getPattern: (id) => api.get(`/patterns/${id}`),
  createPattern: (data) => api.post('/patterns', data),
  updatePattern: (id, data) => api.put(`/patterns/${id}`, data),
  deletePattern: (id) => api.delete(`/patterns/${id}`),
  searchPatterns: (keyword, params) => api.get('/patterns/search', { params: { ...params, keyword } }),
  downloadPattern: (id) => api.get(`/patterns/${id}/download`)
}

// Collection APIs
export const collectionAPI = {
  addCollection: (data) => api.post('/collections', data),
  deleteCollection: (id) => api.delete(`/collections/${id}`),
  getCollections: (params) => api.get('/collections', { params })
}

// Comment APIs
export const commentAPI = {
  createComment: (data) => api.post('/comments', data),
  getComments: (params) => api.get('/comments', { params }),
  deleteComment: (id) => api.delete(`/comments/${id}`)
}

// Question APIs
export const questionAPI = {
  createQuestion: (data) => api.post('/questions', data),
  getQuestions: (params) => api.get('/questions', { params }),
  deleteQuestion: (id) => api.delete(`/questions/${id}`)
}

// Creative Work APIs
export const workAPI = {
  createWork: (data) => api.post('/works', data),
  getWorks: (params) => api.get('/works', { params }),
  getWork: (id) => api.get(`/works/${id}`),
  deleteWork: (id) => api.delete(`/works/${id}`),
  getPendingWorks: (params) => api.get('/works/pending', { params }),
  reviewWork: (id, data) => api.put(`/works/${id}/review`, data)
}

// Admin APIs
export const adminAPI = {
  getLogs: (params) => api.get('/admin/logs', { params }),
  getSettings: () => api.get('/admin/settings'),
  updateSettings: (data) => api.put('/admin/settings', data),
  getUsers: (params) => api.get('/admin/users', { params }),
  updateUserStatus: (id, data) => api.put(`/admin/users/${id}/status`, data),
  getAuditLogs: (params) => api.get('/admin/audit-logs', { params }),
  getRoles: () => api.get('/admin/roles'),
  createRole: (data) => api.post('/admin/roles', data),
  updateRole: (id, data) => api.put(`/admin/roles/${id}`, data)
}

export default api
