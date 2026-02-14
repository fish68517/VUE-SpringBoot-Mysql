/**
 * 路由工具函数
 */

/**
 * 获取前台路由列表
 */
export const getFrontendRoutes = () => {
  return [
    { path: '/', name: 'Home', label: '首页' },
    { path: '/artworks', name: 'Artworks', label: '作品展示' },
    { path: '/knowledge', name: 'Knowledge', label: '知识科普' },
    { path: '/community', name: 'Community', label: '互动交流' },
    { path: '/user', name: 'UserCenter', label: '用户中心' },
  ]
}

/**
 * 获取后台管理路由列表
 */
export const getAdminRoutes = () => {
  return [
    { path: '/admin/users', name: 'AdminUsers', label: '用户管理' },
    { path: '/admin/resources', name: 'AdminResources', label: '资源管理' },
    { path: '/admin/interactions', name: 'AdminInteractions', label: '互动管理' },
    { path: '/admin/system', name: 'AdminSystem', label: '系统管理' },
  ]
}

/**
 * 检查路由是否需要认证
 */
export const isAuthRequired = (route) => {
  return route.meta?.requiresAuth === true
}

/**
 * 检查路由是否需要管理员权限
 */
export const isAdminRequired = (route) => {
  return route.meta?.requiresAdmin === true
}

/**
 * 获取路由标题
 */
export const getRouteTitle = (route) => {
  return route.meta?.title || '壮族刺绣网站'
}
