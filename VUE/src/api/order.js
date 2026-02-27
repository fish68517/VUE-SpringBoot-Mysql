import apiClient from './client'

export const orderAPI = {
  // 创建订单
  createOrder(data) {
    return apiClient.post('/orders', data)
  },

  // 支付订单
  payOrder(orderId, data) {
    return apiClient.post(`/orders/${orderId}/pay`, data)
  },

  // 获取用户订单列表
  getOrdersByUserId(userId) {
    return apiClient.get(`/orders/user/${userId}`)
  },
  // 👇 补上缺失的：更新订单状态接口
  updateOrderStatus(orderId, status) {
    // 后端接口要求 status 是通过 @RequestParam 传递的
    return apiClient.put(`/orders/${orderId}/status`, null, { 
      params: { status: status.toUpperCase() } 
    })
  }
}