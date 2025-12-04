import request from './request'

// 仪表盘统计数据接口
export interface DashboardStats {
  todayOrderCount: number
  weekOrderCount: number
  monthOrderCount: number
  userGrowth: { date: string; count: number }[]
  ticketHeatmap: { zoneName: string; remaining: number; soldRate: number }[]
  topProducts: { name: string; sales: number }[]
  taskStats: { total: number; passRate: number }
  weather: { temp: string; condition: string }
  festivalCountdown: number // 剩余天数
}

export const dashboardApi = {
  // 获取仪表盘聚合数据
  getStats: () => request.get<DashboardStats>('/admin/dashboard/stats'),
  
  // 导出报表图片 (返回文件流或图片URL)
  exportReport: () => request.get('/admin/dashboard/export', { responseType: 'blob' }),
}