/**
 * Permission Guard Utility
 * Provides role-based access control for routes and components
 */

/**
 * Check if user has required role
 * @param {string} userRole - Current user's role
 * @param {string|string[]} requiredRoles - Required role(s)
 * @returns {boolean}
 */
export const hasRole = (userRole, requiredRoles) => {
  if (!userRole) return false
  if (!requiredRoles) return true
  
  const roles = Array.isArray(requiredRoles) ? requiredRoles : [requiredRoles]
  return roles.includes(userRole)
}

/**
 * Check if user has any of the required permissions
 * @param {string} userRole - Current user's role
 * @param {string[]} requiredPermissions - Required permissions
 * @returns {boolean}
 */
export const hasPermission = (userRole, requiredPermissions) => {
  if (!userRole || !requiredPermissions || requiredPermissions.length === 0) {
    return true
  }

  const rolePermissions = getRolePermissions(userRole)
  return requiredPermissions.some(permission => rolePermissions.includes(permission))
}

/**
 * Get all permissions for a given role
 * @param {string} role - User role
 * @returns {string[]}
 */
export const getRolePermissions = (role) => {
  const permissions = {
    AUTHOR: [
      'view_own_manuscripts',
      'submit_manuscript',
      'edit_own_manuscript',
      'withdraw_manuscript',
      'view_review_progress',
      'view_messages',
      'edit_profile'
    ],
    EDITOR: [
      'view_all_manuscripts',
      'perform_initial_review',
      'assign_reviewers',
      'track_review_progress',
      'edit_accepted_manuscripts',
      'send_notifications',
      'communicate_with_authors',
      'communicate_with_reviewers',
      'edit_profile'
    ],
    REVIEWER: [
      'view_assigned_tasks',
      'accept_review_task',
      'reject_review_task',
      'view_manuscript_content',
      'submit_review_opinion',
      'view_review_history',
      'edit_profile'
    ],
    ADMIN: [
      'manage_users',
      'approve_user_registration',
      'reject_user_registration',
      'manage_categories',
      'manage_system_config',
      'view_statistics',
      'manage_notification_templates',
      'edit_profile'
    ]
  }

  return permissions[role] || []
}

/**
 * Check if user can access a route
 * @param {object} user - User object with role property
 * @param {object} routeMeta - Route meta object with roles property
 * @returns {boolean}
 */
export const canAccessRoute = (user, routeMeta) => {
  if (!routeMeta.requiresAuth) {
    return true
  }

  if (!user) {
    return false
  }

  if (routeMeta.roles && !hasRole(user.role, routeMeta.roles)) {
    return false
  }

  if (routeMeta.permissions && !hasPermission(user.role, routeMeta.permissions)) {
    return false
  }

  return true
}

/**
 * Get redirect path based on user role
 * @param {string} role - User role
 * @returns {string}
 */
export const getDefaultRedirectPath = (role) => {
  const redirects = {
    AUTHOR: '/author/dashboard',
    EDITOR: '/editor/dashboard',
    REVIEWER: '/reviewer/dashboard',
    ADMIN: '/admin/dashboard'
  }

  return redirects[role] || '/login'
}
