import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        
      },
      {
        path: 'reserve',
        name: 'Reserve',
        component: () => import('../views/Reserve.vue'),
        
      },
      {
        path: 'my-reservations',
        name: 'MyReservations',
        component: () => import('../views/MyReservations.vue'),
        
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('../views/Notice.vue'),
        
      },
      {
        path: 'ranking',
        name: 'Ranking',
        component: () => import('../views/Ranking.vue'),
        
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('../views/Favorites.vue'),
        
      },
      {
        path: 'feedback',
        name: 'Feedback',
        component: () => import('../views/Feedback.vue'),
        
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        
      },
      {
        path: 'admin/users',
        name: 'AdminUsers',
        component: () => import('../views/admin/Users.vue'),
        meta: { requiresAdmin: true }
      },
      {
        path: 'admin/seats',
        name: 'AdminSeats',
        component: () => import('../views/admin/Seats.vue'),
        meta: { requiresAdmin: true }
      },
      {
        path: 'admin/reservations',
        name: 'AdminReservations',
        component: () => import('../views/admin/Reservations.vue'),
        meta: { requiresAdmin: true }
      },
      {
        path: 'admin/statistics',
        name: 'AdminStatistics',
        component: () => import('../views/admin/Statistics.vue'),
        meta: { requiresAdmin: true }
      },
      {
        path: 'admin/notices',
        name: 'AdminNotices',
        component: () => import('../views/admin/Notices.vue'),
        meta: { requiresAdmin: true }
      },
      {
        path: 'admin/violations',
        name: 'AdminViolations',
        component: () => import('../views/admin/Violations.vue'),
        meta: { requiresAdmin: true }
      },
      {
        path: 'admin/feedbacks',
        name: 'AdminFeedbacks',
        component: () => import('../views/admin/Feedbacks.vue'),
        meta: { requiresAdmin: true }
      },
      {
        path: 'admin/settings',
        name: 'AdminSettings',
        component: () => import('../views/admin/Settings.vue'),
        meta: { requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 放行登录、注册
  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }

  // 未登录先去登录
  if (!userStore.token) {
    next('/login')
    return
  }

  const role = Number(userStore.user?.role)

  // 管理员页面权限控制
  if (to.meta.requiresAdmin && role !== 1) {
    next('/home')
    return
  }

  // 如果管理员访问根路径，可直接引导到管理员首页
  if (to.path === '/' && role === 1) {
    next('/admin/users')
    return
  }

  next()
})

export default router