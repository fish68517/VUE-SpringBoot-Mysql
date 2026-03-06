/**
 * Role Directive
 * Conditionally renders elements based on user role
 * Usage: v-role="'AUTHOR'" or v-role="['AUTHOR', 'EDITOR']"
 */

import { useUserStore } from '../stores/userStore'
import { hasRole } from '../utils/permissionGuard'

export const vRole = {
  mounted(el, binding) {
    const userStore = useUserStore()
    const requiredRoles = binding.value

    if (!userStore.user) {
      el.style.display = 'none'
      return
    }

    const hasAccess = hasRole(userStore.user.role, requiredRoles)
    if (!hasAccess) {
      el.style.display = 'none'
    }
  },

  updated(el, binding) {
    const userStore = useUserStore()
    const requiredRoles = binding.value

    if (!userStore.user) {
      el.style.display = 'none'
      return
    }

    const hasAccess = hasRole(userStore.user.role, requiredRoles)
    if (!hasAccess) {
      el.style.display = 'none'
    } else {
      el.style.display = ''
    }
  }
}
