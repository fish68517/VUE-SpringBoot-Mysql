import type { MockMethod } from 'vite-plugin-mock'

const permissions = [
  '*:*:*',
  'system:user:list', 'system:user:add', 'system:user:update', 'system:user:delete',
  'system:role:list', 'system:role:add', 'system:role:update', 'system:role:delete',
  'access:client:list', 'access:client:add', 'access:client:update', 'access:client:delete',
  'event:event:list', 'event:event:add', 'event:event:update', 'event:event:delete',
]

const user = { id: 1, username: 'admin', realName: '系统管理员', deptName: '系统管理部' }

export default [
  {
    url: '/api/web/v1/auth/login',
    method: 'post',
    response: ({ body }: { body: { username?: string; password?: string } }) => {
      if (body?.username === 'admin' && body?.password === '123456') {
        return { code: 200, message: 'success', data: { accessToken: 'mock-jwt-token-admin', user, permissions } }
      }
      return { code: 401, message: '账号或密码错误，请使用 admin / 123456', data: null }
    },
  },
  {
    url: '/api/web/v1/auth/userInfo',
    method: 'get',
    response: () => ({ code: 200, message: 'success', data: { user, permissions } }),
  },
  {
    url: '/api/web/v1/auth/logout',
    method: 'post',
    response: () => ({ code: 200, message: 'success', data: null }),
  },
] as MockMethod[]
