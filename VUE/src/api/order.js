import client from './client'

export const orderApi = {
  getOrders: (params) => client.get('/orders', { params }),
  getOrderDetail: (id) => client.get(`/orders/${id}`),
  createOrder: (data) => client.post('/orders', data),
  cancelOrder: (id) => client.put(`/orders/${id}/cancel`),
  updateOrderStatus: (id, data) => client.put(`/orders/${id}/status`, data),
  getOrderStatistics: (params) => client.get('/orders/statistics', { params })
}
