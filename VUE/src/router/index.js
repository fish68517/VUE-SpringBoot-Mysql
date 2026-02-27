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
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart.vue'),
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


  
  // ================= 新增：管理员后台路由 =================
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    // 添加权限校验，只有 admin 角色可以进入
    meta: { requiresAuth: true, requiresAdmin: true },
    // 默认重定向到用户管理页
    redirect: '/admin/users',
    children: [
      {
        path: 'users', // 注意：子路由不需要加斜杠
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true },
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/ProductManage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true },
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/OrderManage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true },
      },
      {
        path: 'warnings',
        name: 'AdminWarnings',
        component: () => import('@/views/admin/WarningManage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true },
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// 全局路由导航守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // 1. 不管是否登录，只要访问根路径 '/'，无条件跳转到登录页
  if (to.path === '/') {
    next('/login')
    return
  }

  // 2. 访问需要登录的页面（requiresAuth），但尚未登录
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login')
  } 
  // 3. 访问需要管理员权限的页面（requiresAdmin），但当前角色不是管理员
  else if (to.meta.requiresAdmin && authStore.userRole !== 'ADMIN') {
    next('/home') // 权限不足，强制踢回前台首页
  } 
  // 4. 其他所有情况（包括直接访问 /login 或 /register），正常放行
  else {
    next()
  }
})

export default router