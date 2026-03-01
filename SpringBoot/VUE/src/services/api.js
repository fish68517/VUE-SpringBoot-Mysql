import axios from 'axios'

// 创建 Axios 实例
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加认证令牌
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理错误
apiClient.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // 清除认证信息并重定向到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('role')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// 认证 API
export const authAPI = {
  register: (data) => apiClient.post('/auth/register', data),
  login: (data) => apiClient.post('/auth/login', data),
  doctorRegister: (data) => apiClient.post('/auth/doctor/register', data),
  doctorLogin: (data) => apiClient.post('/auth/doctor/login', data),
  adminLogin: (data) => apiClient.post('/auth/admin/login', data),
  logout: () => apiClient.post('/auth/logout')
}

// 用户 API
export const userAPI = {
  getProfile: (userId) => apiClient.get('/users/profile', { params: { userId } }),
  updateProfile: (data, userId) => apiClient.put('/users/profile', data, { params: { userId } }),
  getUsers: () => apiClient.get('/users')
}

// 健康数据 API
export const healthDataAPI = {
  submitData: (data) => apiClient.post('/health-data', data),
  getUserData: () => apiClient.get('/health-data'),
  getTrends: () => apiClient.get('/health-data/trends'),
  getDataByRange: (startDate, endDate) => 
    apiClient.get('/health-data/range', { params: { startDate, endDate } })
}

// 医师 API
export const doctorAPI = {
  getPatients: (doctorId) => apiClient.get('/doctors/patients', { params: { doctorId } }),
  getPatientRecord: (patientId, doctorId) => apiClient.get(`/doctors/patients/${patientId}`, { params: { doctorId } }),
  getDoctors: () => apiClient.get('/doctors')
}

// 咨询 API
export const consultationAPI = {
  submitConsultation: (data) => apiClient.post('/consultations', data),
  getConsultations: () => apiClient.get('/consultations'),
  answerConsultation: (id, data) => apiClient.put(`/consultations/${id}/answer`, data),
  getConsultationDetail: (id) => apiClient.get(`/consultations/${id}`)
}

// 健康建议 API
export const healthAdviceAPI = {
  createAdvice: (data, doctorId) => apiClient.post('/health-advice', data, { params: { doctorId } }),
  getPatientAdvice: (patientId) => apiClient.get(`/health-advice/patient/${patientId}`),
  getMyAdvice: () => apiClient.get('/health-advice/my-advice')
}

// 管理员 API
export const adminAPI = {
  getUsers: () => apiClient.get('/admin/users'),
  updateUserRole: (id, data) => apiClient.put(`/admin/users/${id}/role`, data),
  disableUser: (id, data) => apiClient.put(`/admin/users/${id}/status`, data),
  getDoctors: () => apiClient.get('/admin/doctors'),
  updateDoctor: (id, data) => apiClient.put(`/admin/doctors/${id}`, data),
  deleteDoctor: (id) => apiClient.delete(`/admin/doctors/${id}`),
  getStatistics: () => apiClient.get('/admin/statistics'),
  getAuditLogs: (params = {}) => apiClient.get('/admin/audit-logs', { params })
}

export default apiClient
