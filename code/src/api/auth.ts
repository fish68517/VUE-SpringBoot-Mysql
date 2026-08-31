import request from '@/utils/request'
import type { ResponseResult } from '@/types/api'
import type { LoginForm, LoginResult, UserInfoResult } from '@/types/auth'

export const loginApi = (data: LoginForm) =>
  request.post('/api/web/v1/auth/login', data) as unknown as Promise<ResponseResult<LoginResult>>

export const getUserInfoApi = () =>
  request.get('/api/web/v1/auth/userInfo') as unknown as Promise<ResponseResult<UserInfoResult>>

export const logoutApi = () =>
  request.post('/api/web/v1/auth/logout') as unknown as Promise<ResponseResult<null>>
