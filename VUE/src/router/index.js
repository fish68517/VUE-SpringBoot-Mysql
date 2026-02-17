import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store'

const routes = [
  {
    path: '/',
    // name: 'Home',
    redirect: '/login' // 强制重定向到登录页
    // component: () => import('../pages/Home.vue')
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

// src/router/index.js

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('user') || '{}')

  // 如果访问根路径且已登录，按角色分流
  if (to.path === '/' && token) {
    if (userInfo.roleId === 1 || userInfo.roleId === 2) {
      return next('/admin')
    }
    return next('/home') // 默认去 Home.vue
  }

  // 如果访问的是根路径且未登录，强制去登录页 (你之前的要求)
  if (to.path === '/' && !token) {
    return next('/login')
  }

  // 权限守卫：只有管理员能进 /admin 开头的页面
  if (to.meta.requiresAdmin) {
    if (token && (userInfo.roleId === 1 || userInfo.roleId === 2)) {
      next()
    } else {
      ElMessage.warning('权限不足，无法进入管理后台')
      next('/')
    }
  } else {
    next()
  }
})

export default router
