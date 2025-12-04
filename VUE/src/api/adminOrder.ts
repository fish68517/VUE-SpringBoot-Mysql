import request from './request'

export interface AdminProductOrder {
  id: number
  orderNo: string
  userPhone: string
  totalAmount: number
  status: 'paid' | 'shipped' | 'completed'
  address: string
  trackingNumber?: string
  items: any[]
  createdAt: string
}

export const adminOrderApi = {
  // 获取订单列表
  getOrders: (params: { page: number; size: number; status?: string; orderNo?: string }) => 
    request.get('/admin/orders', { params }),

  // 发货/修改物流单号
  shipOrder: (id: number, trackingNumber: string) => 
    request.put(`/admin/orders/${id}/ship`, { trackingNumber }),

  // 模拟完成订单
  completeOrder: (id: number) => request.put(`/admin/orders/${id}/complete`),

  // 导出订单
  exportOrders: (params: any) => request.get('/admin/orders/export', { params, responseType: 'blob' }),
}