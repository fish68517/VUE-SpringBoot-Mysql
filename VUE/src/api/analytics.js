import apiClient from './client'

export const analyticsAPI = {
  getOrderStats(params) {
    return apiClient.get('/analytics/orders', { params })
  },

  getWarningStats(params) {
    return apiClient.get('/analytics/warnings', { params })
  },

  getUserStats(params) {
    return apiClient.get('/analytics/users', { params })
  },

  getTopProducts(params) {
    return apiClient.get('/analytics/products/top', { params })
  },

  exportData(params) {
    return apiClient.get('/analytics/export', { params, responseType: 'blob' })
  },
}
