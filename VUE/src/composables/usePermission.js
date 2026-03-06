/**
 * Permission Composable
 * Provides permission checking utilities for Vue components
 */

import { computed } from 'vue'
import { useUserStore } from '../stores/userStore'
import { hasRole, hasPermission, getRolePermissions } from '../utils/permissionGuard'

export const usePermission = () => {
  const userStore = useUserStore()

  /**
   * Check if current user has a specific role
   */
  const checkRole = (requiredRoles) => {
    if (!userStore.user) return false
    return hasRole(userStore.user.role, requiredRoles)
  }

  /**
   * Check if current user has specific permissions
   */
  const checkPermission = (requiredPermissions) => {
    if (!userStore.user) return false
    return hasPermission(userStore.user.role, requiredPermissions)
  }

  /**
   * Get all permissions for current user
   */
  const currentPermissions = computed(() => {
    if (!userStore.user) return []
    return getRolePermissions(userStore.user.role)
  })

  /**
   * Check if current user is author
   */
  const isAuthor = computed(() => checkRole('AUTHOR'))

  /**
   * Check if current user is editor
   */
  const isEditor = computed(() => checkRole('EDITOR'))

  /**
   * Check if current user is reviewer
   */
  const isReviewer = computed(() => checkRole('REVIEWER'))

  /**
   * Check if current user is admin
   */
  const isAdmin = computed(() => checkRole('ADMIN'))

  return {
    checkRole,
    checkPermission,
    currentPermissions,
    isAuthor,
    isEditor,
    isReviewer,
    isAdmin
  }
}
