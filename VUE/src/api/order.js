import apiClient from './client'

export const orderAPI = {
  getOrders(params) {
    return apiClient.get('/orders', { params })
  },

  getOrderById(id) {
    return apiClient.get(`/orders/${id}`)
  },

  createOrder(data) {
    return apiClient.post('/orders', data)
  },

  updateOrderStatus(id, status) {
    return apiClient.put(`/orders/${id}/status`, { status })
  },

  payOrder(id) {
    return apiClient.post(`/orders/${id}/pay`)
  },
}
