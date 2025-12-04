import request from './request'

export interface CheckInTask {
  id?: number
  name: string
  description: string
  coverImage: string
  pointsReward: number
  startTime: string
  endTime: string
  location: {
    latitude: number
    longitude: number
    radius: number // 地理围栏半径
    addressName: string
  }
  isActive: boolean
}

export interface UserTaskRecord {
  id: number
  userId: number
  userName: string
  taskId: number
  taskName: string
  checkInImage: string
  checkInTime: string
  status: 'pending' | 'approved' | 'rejected'
}

export const taskApi = {
  // --- 任务配置 ---
  getTasks: (params: any) => request.get('/admin/tasks', { params }),
  createTask: (data: CheckInTask) => request.post('/admin/tasks', data),
  updateTask: (id: number, data: Partial<CheckInTask>) => request.put(`/admin/tasks/${id}`, data),
  deleteTask: (id: number) => request.delete(`/admin/tasks/${id}`),
  updateTaskStatus: (id: number, isActive: boolean) => request.put(`/admin/tasks/${id}/status`, { isActive }),

  // --- 打卡记录审核 ---
  getRecords: (params: { page: number; size: number; status?: string }) => 
    request.get('/admin/tasks/records', { params }),
  
  // 审核 (pass 或 reject)
  auditRecord: (recordIds: number[], status: 'approved' | 'rejected', reason?: string) => 
    request.post('/admin/tasks/records/audit', { recordIds, status, reason }),
}