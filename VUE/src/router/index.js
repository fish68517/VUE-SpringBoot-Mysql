import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    // 无论什么状态，只要访问根路径 / 就重定向到 /login
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
  },
  // 如果你还需要原来的主页，可以把它移到一个新的路径下，例如 /home，并加上权限验证
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true }, 
  },
  {
    path: '/weather',
    name: 'Weather',
    component: () => import('@/views/Weather.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/warnings',
    name: 'Warnings',
    component: () => import('@/views/Warnings.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/products',
    name: 'Products',
    component: () => import('@/views/Products.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/recommendations',
    name: 'Recommendations',
    component: () => import('@/views/Recommendations.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/Orders.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/analytics',
    name: 'Analytics',
    component: () => import('@/views/Analytics.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// Navigation guard for authentication
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && authStore.userRole !== 'admin') {
    // 注意：原本非 admin 访问 analytics 会被踢回 '/'
    // 因为现在 '/' 会重定向到 '/login'，所以非管理员访问也会被踢回登录页
    // 如果你想让他们去首页，应该改成 next('/home')
    next('/home') 
  } else {
    next()
  }
})

export default router