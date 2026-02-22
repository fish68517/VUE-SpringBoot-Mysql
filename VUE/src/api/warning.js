import apiClient from './client'

export const warningAPI = {
  getWarnings(params) {
    return apiClient.get('/warnings', { params })
  },

  getWarningById(id) {
    return apiClient.get(`/warnings/${id}`)
  },

  createWarning(data) {
    return apiClient.post('/warnings', data)
  },

  updateWarning(id, data) {
    return apiClient.put(`/warnings/${id}`, data)
  },

  deleteWarning(id) {
    return apiClient.delete(`/warnings/${id}`)
  },
}
