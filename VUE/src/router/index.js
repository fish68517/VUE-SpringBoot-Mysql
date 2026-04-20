import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/userStore'

const routes = [
  { path: '/', redirect: '/login' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/DashboardView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/records',
    name: 'RecordList',
    component: () => import('../views/RecordListView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/records/create',
    name: 'RecordCreate',
    component: () => import('../views/RecordCreateView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/records/:id',
    name: 'RecordDetail',
    component: () => import('../views/RecordDetailView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/plans',
    name: 'PlanList',
    component: () => import('../views/PlanListView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/plans/create',
    name: 'PlanCreate',
    component: () => import('../views/PlanCreateView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/plans/:id',
    name: 'PlanDetail',
    component: () => import('../views/PlanDetailView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/plans/:id/edit',
    name: 'PlanEdit',
    component: () => import('../views/PlanEditView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/social',
    name: 'SocialFeed',
    component: () => import('../views/SocialFeedView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/footprints',
    name: 'TravelFootprints',
    component: () => import('../views/TravelFootprints.vue'),
    meta: { requiresAuth: true, title: '旅行足迹' }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/AdminDashboardView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('../views/AdminUsersView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/travels',
    name: 'AdminTravels',
    component: () => import('../views/AdminTravelsView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/plans',
    name: 'AdminPlans',
    component: () => import('../views/AdminPlansView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/social',
    name: 'AdminSocial',
    component: () => import('../views/AdminSocialView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    return '/login'
  }

  if (!to.meta.requiresAuth && userStore.isAuthenticated) {
    return userStore.getDefaultRoute()
  }

  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    return userStore.getDefaultRoute()
  }

  return true
})

export default router
