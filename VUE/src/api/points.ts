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
  // ==========================================
  // 管理端 API (Admin) - 原有逻辑保持不变
  // ==========================================
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

  // ==========================================
  // 用户端 API (User) - 新增：给前台 Points.vue 使用
  // ==========================================
  // 获取打卡任务列表
  getTasks: () => request.get('/points/tasks'),
  
  // 提交打卡
  checkin: (taskId: number) => request.post(`/points/tasks/${taskId}/checkin`),
  
  // 获取积分商城商品
  getMallItems: () => request.get('/points/mall'),
  
  // 兑换商品
  exchangeItem: (itemId: number, address: string) => 
    request.post(`/points/mall/${itemId}/exchange`, { address }),
    
  // 获取积分明细流水
  getHistory: () => request.get('/points/history'),
  
  // 获取当前用户积分余额
  getUserPoints: () => request.get('/points/balance')
}