import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { authService } from '../auth'

describe('认证服务 (authService)', () => {
  beforeEach(() => {
    // 清空 localStorage
    localStorage.clear()
  })

  afterEach(() => {
    localStorage.clear()
  })

  describe('setAuth', () => {
    it('应该保存认证信息到 localStorage', () => {
      const token = 'test-token-123'
      const user = { id: 1, username: 'testuser', email: 'test@example.com' }
      const role = 'USER'

      authService.setAuth(token, user, role)

      expect(localStorage.getItem('token')).toBe(token)
      expect(localStorage.getItem('role')).toBe(role)
      expect(localStorage.getItem('userId')).toBe('1')
      expect(JSON.parse(localStorage.getItem('user'))).toEqual(user)
    })

    it('应该处理没有用户ID的情况', () => {
      const token = 'test-token'
      const user = { username: 'testuser' }
      const role = 'USER'

      authService.setAuth(token, user, role)

      expect(localStorage.getItem('token')).toBe(token)
      expect(localStorage.getItem('role')).toBe(role)
    })
  })

  describe('getAuthState', () => {
    it('应该返回完整的认证状态', () => {
      const token = 'test-token'
      const user = { id: 1, username: 'testuser' }
      const role = 'USER'

      authService.setAuth(token, user, role)
      const state = authService.getAuthState()

      expect(state.isAuthenticated).toBe(true)
      expect(state.token).toBe(token)
      expect(state.user).toEqual(user)
      expect(state.role).toBe(role)
      expect(state.userId).toBe(1)
    })

    it('应该返回未认证状态', () => {
      const state = authService.getAuthState()

      expect(state.isAuthenticated).toBe(false)
      expect(state.token).toBeNull()
      expect(state.user).toBeNull()
      expect(state.role).toBeNull()
      expect(state.userId).toBeNull()
    })
  })

  describe('getCurrentUser', () => {
    it('应该返回当前用户信息', () => {
      const user = { id: 1, username: 'testuser', email: 'test@example.com' }
      authService.setAuth('token', user, 'USER')

      const currentUser = authService.getCurrentUser()

      expect(currentUser).toEqual(user)
    })

    it('应该在没有用户时返回 null', () => {
      const currentUser = authService.getCurrentUser()

      expect(currentUser).toBeNull()
    })
  })

  describe('getUser', () => {
    it('应该返回当前用户信息（别名方法）', () => {
      const user = { id: 1, username: 'testuser' }
      authService.setAuth('token', user, 'USER')

      const result = authService.getUser()

      expect(result).toEqual(user)
    })
  })

  describe('getCurrentUserId', () => {
    it('应该返回当前用户ID', () => {
      const user = { id: 42, username: 'testuser' }
      authService.setAuth('token', user, 'USER')

      const userId = authService.getCurrentUserId()

      expect(userId).toBe(42)
    })

    it('应该在没有用户时返回 null', () => {
      const userId = authService.getCurrentUserId()

      expect(userId).toBeNull()
    })
  })

  describe('getCurrentRole', () => {
    it('应该返回当前用户角色', () => {
      authService.setAuth('token', { id: 1 }, 'DOCTOR')

      const role = authService.getCurrentRole()

      expect(role).toBe('DOCTOR')
    })

    it('应该在没有角色时返回 null', () => {
      const role = authService.getCurrentRole()

      expect(role).toBeNull()
    })
  })

  describe('getUserRole', () => {
    it('应该返回当前用户角色（别名方法）', () => {
      authService.setAuth('token', { id: 1 }, 'ADMIN')

      const role = authService.getUserRole()

      expect(role).toBe('ADMIN')
    })
  })

  describe('getToken', () => {
    it('应该返回认证令牌', () => {
      const token = 'my-token-123'
      authService.setAuth(token, { id: 1 }, 'USER')

      const result = authService.getToken()

      expect(result).toBe(token)
    })

    it('应该在没有令牌时返回 null', () => {
      const result = authService.getToken()

      expect(result).toBeNull()
    })
  })

  describe('isAuthenticated', () => {
    it('应该在有令牌时返回 true', () => {
      authService.setAuth('token', { id: 1 }, 'USER')

      expect(authService.isAuthenticated()).toBe(true)
    })

    it('应该在没有令牌时返回 false', () => {
      expect(authService.isAuthenticated()).toBe(false)
    })
  })

  describe('hasRole', () => {
    it('应该检查用户是否具有特定角色', () => {
      authService.setAuth('token', { id: 1 }, 'DOCTOR')

      expect(authService.hasRole('DOCTOR')).toBe(true)
      expect(authService.hasRole('USER')).toBe(false)
      expect(authService.hasRole('ADMIN')).toBe(false)
    })

    it('应该在没有角色时返回 false', () => {
      expect(authService.hasRole('USER')).toBe(false)
    })
  })

  describe('updateUser', () => {
    it('应该更新用户信息', () => {
      const originalUser = { id: 1, username: 'oldname' }
      authService.setAuth('token', originalUser, 'USER')

      const updatedUser = { id: 1, username: 'newname', email: 'new@example.com' }
      authService.updateUser(updatedUser)

      const currentUser = authService.getCurrentUser()

      expect(currentUser).toEqual(updatedUser)
      expect(currentUser.username).toBe('newname')
    })

    it('应该更新用户ID', () => {
      authService.setAuth('token', { id: 1 }, 'USER')

      const updatedUser = { id: 99, username: 'user' }
      authService.updateUser(updatedUser)

      expect(authService.getCurrentUserId()).toBe(99)
    })
  })

  describe('logout', () => {
    it('应该清除所有认证信息', () => {
      authService.setAuth('token', { id: 1, username: 'user' }, 'USER')

      authService.logout()

      expect(authService.isAuthenticated()).toBe(false)
      expect(authService.getCurrentUser()).toBeNull()
      expect(authService.getCurrentRole()).toBeNull()
      expect(authService.getCurrentUserId()).toBeNull()
    })
  })

  describe('clearAuth', () => {
    it('应该清除认证信息（别名方法）', () => {
      authService.setAuth('token', { id: 1 }, 'USER')

      authService.clearAuth()

      expect(authService.isAuthenticated()).toBe(false)
    })
  })
})
