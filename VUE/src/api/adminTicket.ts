import request from './request'
import { TicketSession, TicketZone } from './ticket' // 复用已有类型

export interface AdminTicketOrder {
  id: number
  orderNo: string
  buyerPhone: string
  items: Array<{
    realName: string
    idNumber: string
    zoneName: string
    price: number
  }>
  totalAmount: number
  status: string
  createdAt: string
}

export const adminTicketApi = {
  // --- 场次管理 ---
  createSession: (data: Partial<TicketSession>) => request.post('/admin/ticket/sessions', data),
  updateSession: (id: number, data: Partial<TicketSession>) => request.put(`/admin/ticket/sessions/${id}`, data),
  deleteSession: (id: number) => request.delete(`/admin/ticket/sessions/${id}`),

  // --- 分区管理 ---
  // 获取某场次的分区列表（含库存详情）
  getZonesBySession: (sessionId: number) => request.get<TicketZone[]>(`/admin/ticket/sessions/${sessionId}/zones`),
  
  createZone: (data: Partial<TicketZone>) => request.post('/admin/ticket/zones', data),
  
  // 更新分区（含库存和价格）
  updateZone: (id: number, data: Partial<TicketZone>) => request.put(`/admin/ticket/zones/${id}`, data),
  
  // 手动补票/减票 (action: 'add' | 'reduce')
  adjustStock: (zoneId: number, count: number, action: string) => 
    request.post(`/admin/ticket/zones/${zoneId}/stock`, { count, action }),

  // --- 票务订单 ---
  getOrders: (params: any) => request.get('/admin/ticket/orders', { params }),
  exportOrders: (params: any) => request.get('/admin/ticket/orders/export', { params, responseType: 'blob' }),
}