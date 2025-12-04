import request from './request'

export interface AdminUser {
  id: number
  phone: string
  nickname: string
  realName: string
  idNumber: string // 后端需脱敏返回，如 2101**********1234
  points: number
  status: 'active' | 'banned'
  createdAt: string
  lastLoginTime: string
}

export interface UserQueryParams {
  page: number
  size: number
  keyword?: string // 手机号或姓名
}

export const adminUserApi = {
  // 获取用户列表（分页）
  getUsers: (params: UserQueryParams) => request.get('/admin/users', { params }),

  // 获取用户详情（包含完整实名信息，需权限）
  getUserDetail: (id: number) => request.get<AdminUser>(`/admin/users/${id}`),

  // 封禁用户
  banUser: (id: number) => request.put(`/admin/users/${id}/ban`),

  // 解封用户
  unbanUser: (id: number) => request.put(`/admin/users/${id}/unban`),

  // 调整用户积分
  adjustPoints: (id: number, points: number, reason: string) => 
    request.post(`/admin/users/${id}/points`, { points, reason }),

  // 冻结用户积分
  freezePoints: (id: number) => request.put(`/admin/users/${id}/points/freeze`),

  // 获取用户登录日志
  getLoginLogs: (id: number, params: any) => request.get(`/admin/users/${id}/logs`, { params }),
  
  // 导出用户列表
  exportUsers: (params: any) => request.get('/admin/users/export', { params, responseType: 'blob' }),
}