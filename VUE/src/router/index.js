import { createRouter, createWebHistory } from 'vue-router'
import { useAdminStore } from '../store/index.js'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../pages/Login.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../pages/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/users',
    name: 'UserManagement',
    component: () => import('../pages/UserManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/community',
    name: 'CommunityManagement',
    component: () => import('../pages/CommunityManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/fitness-plans',
    name: 'FitnessPlanManagement',
    component: () => import('../pages/FitnessPlanManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/exercise-data',
    name: 'ExerciseDataManagement',
    component: () => import('../pages/ExerciseDataManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/image-upload',
    name: 'ImageUploadManagement',
    component: () => import('../pages/ImageUploadManagement.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Global navigation guard for authentication
router.beforeEach((to, _from, next) => {
  const adminStore = useAdminStore()
  const isAuthenticated = adminStore.isAuthenticated()
  
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
