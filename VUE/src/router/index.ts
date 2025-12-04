import { createRouter, createWebHistory, RouteRecordRaw, NavigationGuardNext, RouteLocationNormalized } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes: RouteRecordRaw[] = [

  // 管理员后台路由
  {
    path: '/admin',
    component: () => import('../pages/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'admin' }, // 建议配合后端角色权限
    children: [
      {
        path: '', // 默认跳转
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../pages/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../pages/admin/UserManagement.vue')
      },
      {
        path: 'tickets',
        name: 'AdminTickets',
        component: () => import('../pages/admin/TicketManagement.vue')
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('../pages/admin/ProductManagement.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('../pages/admin/OrderManagement.vue')
      },
      {
        path: 'content',
        name: 'AdminContent',
        component: () => import('../pages/admin/ContentManagement.vue')
      },
      {
        path: 'tasks',
        name: 'AdminTasks',
        component: () => import('../pages/admin/TaskManagement.vue')
      },
      {
        path: 'points',
        name: 'AdminPoints',
        component: () => import('../pages/admin/PointsMallManagement.vue')
      }
    ]
  },


  {
    path: '/',
    name: 'Home',
    component: () => import('../pages/Home.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../pages/Login.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../pages/Register.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../pages/Profile.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/artist/:id',
    name: 'ArtistDetail',
    component: () => import('../pages/ArtistDetail.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/ticket-purchase',
    name: 'TicketPurchase',
    component: () => import('../pages/TicketPurchase.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/shop',
    name: 'Shop',
    component: () => import('../pages/Shop.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../pages/Cart.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('../pages/Checkout.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('../pages/Orders.vue'),
    meta: { requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach(
  (to: RouteLocationNormalized, _from: RouteLocationNormalized, next: NavigationGuardNext) => {
    const userStore = useUserStore()

    // 初始化用户信息（从 localStorage 恢复）
    if (!userStore.isLoggedIn) {
      userStore.loadFromStorage()
    }

    // 检查路由是否需要认证
    const requiresAuth = to.meta.requiresAuth

    if (requiresAuth && !userStore.isLoggedIn) {
      // 需要认证但未登录，重定向到登录页
      next('/login')
    } else if (!requiresAuth && userStore.isLoggedIn && (to.name === 'Login' || to.name === 'Register')) {
      // 已登录但访问登录/注册页，重定向到首页
      next('/')
    } else {
      next()
    }
  }
)

export default router
