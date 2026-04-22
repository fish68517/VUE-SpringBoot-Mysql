import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const MainLayout = () => import('../views/MainLayout.vue')
const LoginView = () => import('../views/LoginView.vue')
const RegisterView = () => import('../views/RegisterView.vue')
const DashboardView = () => import('../views/DashboardView.vue')
const UsersView = () => import('../views/UsersView.vue')
const DevicesView = () => import('../views/DevicesView.vue')
const TasksView = () => import('../views/TasksView.vue')
const RecordsView = () => import('../views/RecordsView.vue')
const DefectsView = () => import('../views/DefectsView.vue')
const RepairOrdersView = () => import('../views/RepairOrdersView.vue')
const StandardsView = () => import('../views/StandardsView.vue')
const AnalyticsView = () => import('../views/AnalyticsView.vue')
const NoticesView = () => import('../views/NoticesView.vue')
const LogsView = () => import('../views/LogsView.vue')

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { guest: true }
  },
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', name: 'dashboard', component: DashboardView },
      { path: 'users', name: 'users', component: UsersView, meta: { roles: ['ADMIN'] } },
      { path: 'devices', name: 'devices', component: DevicesView, meta: { roles: ['ADMIN'] } },
      { path: 'standards', name: 'standards', component: StandardsView, meta: { roles: ['ADMIN'] } },
      { path: 'tasks', name: 'tasks', component: TasksView },
      { path: 'records', name: 'records', component: RecordsView },
      { path: 'defects', name: 'defects', component: DefectsView },
      { path: 'repair-orders', name: 'repair-orders', component: RepairOrdersView },
      { path: 'analytics', name: 'analytics', component: AnalyticsView },
      { path: 'notices', name: 'notices', component: NoticesView },
      { path: 'logs', name: 'logs', component: LogsView, meta: { roles: ['ADMIN'] } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  const isLoggedIn = !!authStore.token

  if (to.meta.guest && isLoggedIn) {
    return '/'
  }

  if (!to.meta.guest && !isLoggedIn) {
    return '/login'
  }

  if (to.meta.roles?.length && !to.meta.roles.includes(authStore.user?.role)) {
    return '/dashboard'
  }

  return true
})

export default router
