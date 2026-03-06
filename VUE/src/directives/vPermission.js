/**
 * Permission Directive
 * Conditionally renders elements based on user permissions
 * Usage: v-permission="'permission-name'" or v-permission="['permission1', 'permission2']"
 */

import { useUserStore } from '../stores/userStore'
import { hasPermission } from '../utils/permissionGuard'

export const vPermission = {
  mounted(el, binding) {
    const userStore = useUserStore()
    const requiredPermissions = binding.value

    if (!userStore.user) {
      el.style.display = 'none'
      return
    }

    const hasAccess = hasPermission(userStore.user.role, requiredPermissions)
    if (!hasAccess) {
      el.style.display = 'none'
    }
  },

  updated(el, binding) {
    const userStore = useUserStore()
    const requiredPermissions = binding.value

    if (!userStore.user) {
      el.style.display = 'none'
      return
    }

    const hasAccess = hasPermission(userStore.user.role, requiredPermissions)
    if (!hasAccess) {
      el.style.display = 'none'
    } else {
      el.style.display = ''
    }
  }
}
