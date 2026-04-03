import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/patterns',
    name: 'PatternLibrary',
    component: () => import('../pages/PatternLibrary.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../pages/Home.vue')
  },
  {
    path: '/patterns/:id',
    name: 'PatternDetail',
    component: () => import('../pages/PatternDetail.vue')
  },
  {
    path: '/science',
    name: 'CulturalScience',
    component: () => import('../pages/CulturalScience.vue')
  },
  {
    path: '/works',
    name: 'CreativeWorks',
    component: () => import('../pages/CreativeWorks.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../pages/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../pages/Register.vue')
  },
  {
    path: '/user-center',
    name: 'UserCenter',
    component: () => import('../pages/UserCenter.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user-center/my-works',
    name: 'MyWorks',
    component: () => import('../pages/MyWorks.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../pages/AdminDashboard.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/patterns',
    name: 'AdminPatterns',
    component: () => import('../pages/AdminPatterns.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('../pages/AdminUsers.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/review',
    name: 'AdminReview',
    component: () => import('../pages/AdminReview.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/logs',
    name: 'AdminLogs',
    component: () => import('../pages/AdminLogs.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/settings',
    name: 'AdminSettings',
    component: () => import('../pages/AdminSettings.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/permissions',
    name: 'AdminPermissions',
    component: () => import('../pages/AdminPermissions.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
  const isAdmin = userInfo.roleId === 1 || userInfo.roleId === 2

  if (to.meta.requiresAdmin) {
    if (!token) {
      ElMessage.warning('此功能需要登录才能访问')
      return next(from.fullPath && from.fullPath !== to.fullPath ? from.fullPath : '/home')
    }

    if (!isAdmin) {
      ElMessage.warning('权限不足，无法进入管理后台')
      return next(from.fullPath && from.fullPath !== to.fullPath ? from.fullPath : '/home')
    }
  }

  if (to.meta.requiresAuth && !token) {
    ElMessage.warning('此功能需要登录才能访问')
    return next(from.fullPath && from.fullPath !== to.fullPath ? from.fullPath : '/home')
  }

  next()
})

export default router
