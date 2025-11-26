import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/userStore'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.meta.requiresAuth

  if (requiresAuth && !userStore.isAuthenticated) {
    next('/login')
  } else if (!requiresAuth && userStore.isAuthenticated && (to.path === '/login' || to.path === '/register')) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
