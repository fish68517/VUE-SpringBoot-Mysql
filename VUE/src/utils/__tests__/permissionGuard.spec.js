import { describe, it, expect } from 'vitest'
import {
  hasRole,
  hasPermission,
  getRolePermissions,
  canAccessRoute,
  getDefaultRedirectPath
} from '../permissionGuard'

describe('Permission Guard Utilities', () => {
  describe('hasRole', () => {
    it('should return true when user has required role', () => {
      expect(hasRole('AUTHOR', 'AUTHOR')).toBe(true)
    })

    it('should return true when user has one of multiple required roles', () => {
      expect(hasRole('AUTHOR', ['AUTHOR', 'EDITOR'])).toBe(true)
      expect(hasRole('EDITOR', ['AUTHOR', 'EDITOR'])).toBe(true)
    })

    it('should return false when user does not have required role', () => {
      expect(hasRole('AUTHOR', 'EDITOR')).toBe(false)
    })

    it('should return false when user role is null', () => {
      expect(hasRole(null, 'AUTHOR')).toBe(false)
    })

    it('should return true when no roles are required', () => {
      expect(hasRole('AUTHOR', null)).toBe(true)
    })
  })

  describe('hasPermission', () => {
    it('should return true when user has required permission', () => {
      expect(hasPermission('AUTHOR', ['submit_manuscript'])).toBe(true)
    })

    it('should return true when user has one of multiple required permissions', () => {
      expect(hasPermission('AUTHOR', ['submit_manuscript', 'manage_users'])).toBe(true)
    })

    it('should return false when user does not have required permission', () => {
      expect(hasPermission('AUTHOR', ['manage_users'])).toBe(false)
    })

    it('should return true when no permissions are required', () => {
      expect(hasPermission('AUTHOR', [])).toBe(true)
    })

    it('should return true when permissions is null', () => {
      expect(hasPermission('AUTHOR', null)).toBe(true)
    })
  })

  describe('getRolePermissions', () => {
    it('should return author permissions', () => {
      const permissions = getRolePermissions('AUTHOR')
      expect(permissions).toContain('submit_manuscript')
      expect(permissions).toContain('view_own_manuscripts')
      expect(permissions).not.toContain('manage_users')
    })

    it('should return editor permissions', () => {
      const permissions = getRolePermissions('EDITOR')
      expect(permissions).toContain('perform_initial_review')
      expect(permissions).toContain('assign_reviewers')
      expect(permissions).not.toContain('submit_manuscript')
    })

    it('should return reviewer permissions', () => {
      const permissions = getRolePermissions('REVIEWER')
      expect(permissions).toContain('accept_review_task')
      expect(permissions).toContain('submit_review_opinion')
      expect(permissions).not.toContain('manage_users')
    })

    it('should return admin permissions', () => {
      const permissions = getRolePermissions('ADMIN')
      expect(permissions).toContain('manage_users')
      expect(permissions).toContain('manage_categories')
      expect(permissions).not.toContain('submit_manuscript')
    })

    it('should return empty array for unknown role', () => {
      const permissions = getRolePermissions('UNKNOWN')
      expect(permissions).toEqual([])
    })
  })

  describe('canAccessRoute', () => {
    it('should allow access to public routes', () => {
      const user = { role: 'AUTHOR' }
      const routeMeta = { requiresAuth: false }
      expect(canAccessRoute(user, routeMeta)).toBe(true)
    })

    it('should deny access when user is not logged in', () => {
      const user = null
      const routeMeta = { requiresAuth: true, roles: ['AUTHOR'] }
      expect(canAccessRoute(user, routeMeta)).toBe(false)
    })

    it('should allow access when user has required role', () => {
      const user = { role: 'AUTHOR' }
      const routeMeta = { requiresAuth: true, roles: ['AUTHOR'] }
      expect(canAccessRoute(user, routeMeta)).toBe(true)
    })

    it('should deny access when user does not have required role', () => {
      const user = { role: 'AUTHOR' }
      const routeMeta = { requiresAuth: true, roles: ['EDITOR'] }
      expect(canAccessRoute(user, routeMeta)).toBe(false)
    })

    it('should allow access when user has one of multiple required roles', () => {
      const user = { role: 'AUTHOR' }
      const routeMeta = { requiresAuth: true, roles: ['AUTHOR', 'EDITOR'] }
      expect(canAccessRoute(user, routeMeta)).toBe(true)
    })

    it('should allow access when user has required permission', () => {
      const user = { role: 'AUTHOR' }
      const routeMeta = { requiresAuth: true, permissions: ['submit_manuscript'] }
      expect(canAccessRoute(user, routeMeta)).toBe(true)
    })

    it('should deny access when user does not have required permission', () => {
      const user = { role: 'AUTHOR' }
      const routeMeta = { requiresAuth: true, permissions: ['manage_users'] }
      expect(canAccessRoute(user, routeMeta)).toBe(false)
    })
  })

  describe('getDefaultRedirectPath', () => {
    it('should return author dashboard path', () => {
      expect(getDefaultRedirectPath('AUTHOR')).toBe('/author/dashboard')
    })

    it('should return editor dashboard path', () => {
      expect(getDefaultRedirectPath('EDITOR')).toBe('/editor/dashboard')
    })

    it('should return reviewer dashboard path', () => {
      expect(getDefaultRedirectPath('REVIEWER')).toBe('/reviewer/dashboard')
    })

    it('should return admin dashboard path', () => {
      expect(getDefaultRedirectPath('ADMIN')).toBe('/admin/dashboard')
    })

    it('should return login path for unknown role', () => {
      expect(getDefaultRedirectPath('UNKNOWN')).toBe('/login')
    })
  })
})
