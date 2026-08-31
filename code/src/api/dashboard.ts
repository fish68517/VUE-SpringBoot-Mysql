import request from '@/utils/request'
import type { ResponseResult } from '@/types/api'
import type { DashboardData } from '@/types/dashboard'

export const getDashboardApi = () =>
  request.get('/api/web/v1/dashboard/overview') as unknown as Promise<ResponseResult<DashboardData>>
