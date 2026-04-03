import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const Home = () => import('../pages/Home.vue')
const Artworks = () => import('../pages/Artworks.vue')
const ArtworkDetail = () => import('../pages/ArtworkDetail.vue')
const Knowledge = () => import('../pages/Knowledge.vue')
const KnowledgeDetail = () => import('../pages/KnowledgeDetail.vue')
const Community = () => import('../pages/Community.vue')
const UserCenter = () => import('../pages/UserCenter.vue')
const Login = () => import('../pages/Login.vue')
const Register = () => import('../pages/Register.vue')

const AdminUsers = () => import('../pages/admin/AdminUsers.vue')
const AdminResources = () => import('../pages/admin/AdminResources.vue')
const AdminInteractions = () => import('../pages/admin/AdminInteractions.vue')
const AdminSystem = () => import('../pages/admin/AdminSystem.vue')

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: false, title: '首页' },
  },
  {
    path: '/artworks',
    name: 'Artworks',
    component: Artworks,
    meta: { requiresAuth: false, title: '刺绣展示' },
  },
  {
    path: '/artworks/:id',
    name: 'ArtworkDetail',
    component: ArtworkDetail,
    meta: { requiresAuth: false, title: '作品详情' },
  },
  {
    path: '/knowledge',
    name: 'Knowledge',
    component: Knowledge,
    meta: { requiresAuth: false, title: '知识科普' },
  },
  {
    path: '/knowledge/:id',
    name: 'KnowledgeDetail',
    component: KnowledgeDetail,
    meta: { requiresAuth: false, title: '知识详情' },
  },
  {
    path: '/community',
    name: 'Community',
    component: Community,
    meta: { requiresAuth: false, title: '互动交流' },
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: UserCenter,
    meta: { requiresAuth: false, title: '用户中心' },
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false, title: '登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false, title: '注册' },
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: AdminUsers,
    meta: { requiresAuth: true, requiresAdmin: true, title: '用户管理' },
  },
  {
    path: '/admin/resources',
    name: 'AdminResources',
    component: AdminResources,
    meta: { requiresAuth: true, requiresAdmin: true, title: '资源管理' },
  },
  {
    path: '/admin/interactions',
    name: 'AdminInteractions',
    component: AdminInteractions,
    meta: { requiresAuth: true, requiresAdmin: true, title: '互动管理' },
  },
  {
    path: '/admin/system',
    name: 'AdminSystem',
    component: AdminSystem,
    meta: { requiresAuth: true, requiresAdmin: true, title: '系统管理' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (!authStore.isLoggedIn && !authStore.user) {
    authStore.loadUserFromStorage()
  }

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next({
      name: 'Login',
      query: { redirect: to.fullPath },
    })
    return
  }

  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next({
      name: 'Home',
    })
    return
  }

  if (to.meta.title) {
    document.title = `${to.meta.title} - 文山壮族刺绣网站`
  }

  next()
})

router.afterEach(() => {
  window.scrollTo(0, 0)
})

export default router
