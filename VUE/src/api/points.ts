import request from './request'

export interface PointsGood {
  id?: number
  name: string
  image: string
  type: 'physical' | 'virtual' // 实体或虚拟
  pointsPrice: number
  stock: number
  isActive: boolean
}

export interface ExchangeOrder {
  id: number
  userId: number
  goodName: string
  pointsCost: number
  status: 'pending' | 'shipped' | 'completed' | 'rejected'
  shippingInfo?: string
}

export const pointsApi = {
  // --- 商品管理 ---
  getGoods: (params: any) => request.get('/admin/points/goods', { params }),
  saveGood: (data: PointsGood) => request.post('/admin/points/goods', data),
  deleteGood: (id: number) => request.delete(`/admin/points/goods/${id}`),

  // --- 兑换订单管理 ---
  getExchangeOrders: (params: any) => request.get('/admin/points/orders', { params }),
  
  // 审核/发货
  auditExchange: (id: number, status: string, shippingNo?: string) => 
    request.post(`/admin/points/orders/${id}/audit`, { status, shippingNo }),
    
  exportExchangeOrders: (params: any) => request.get('/admin/points/orders/export', { params, responseType: 'blob' }),
}