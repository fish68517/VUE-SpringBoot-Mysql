import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

// 前台页面
const Home = () => import('../pages/Home.vue')
const Artworks = () => import('../pages/Artworks.vue')
const ArtworkDetail = () => import('../pages/ArtworkDetail.vue')
const Knowledge = () => import('../pages/Knowledge.vue')
const KnowledgeDetail = () => import('../pages/KnowledgeDetail.vue')
const Community = () => import('../pages/Community.vue')
const UserCenter = () => import('../pages/UserCenter.vue')
const Login = () => import('../pages/Login.vue')
const Register = () => import('../pages/Register.vue')

// 后台页面
const AdminUsers = () => import('../pages/admin/AdminUsers.vue')
const AdminResources = () => import('../pages/admin/AdminResources.vue')
const AdminInteractions = () => import('../pages/admin/AdminInteractions.vue')
const AdminSystem = () => import('../pages/admin/AdminSystem.vue')

// 前台路由
const frontendRoutes = [
  {
    path: '/artworks',
    name: 'Artworks',
    component: Artworks,
    meta: { requiresAuth: false, title: '作品展示' },
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
    meta: { requiresAuth: true, title: '用户中心' },
  },
]

// 认证相关路由
const authRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false, title: '登录' },
  },
  { path: '/', redirect: '/login' },
   { path: '/home', name: 'Home', component: Home, meta: { requiresAuth: false, title: '首页' } },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false, title: '注册' },
  },
]

// 后台管理路由
const adminRoutes = [
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

const routes = [...frontendRoutes, ...authRoutes, ...adminRoutes]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 全局路由守卫 - 权限检查
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // 从 localStorage 加载用户信息（如果还未加载）
  if (!authStore.isLoggedIn && !authStore.user) {
    authStore.loadUserFromStorage()
  }

  // 检查是否需要认证
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    // 需要登录但未登录，重定向到登录页
    next({
      name: 'Login',
      query: { redirect: to.fullPath },
    })
    return
  }

  // 更新页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 壮族刺绣网站`
  }

  next()
})

// 路由后置钩子 - 页面加载完成后的处理
router.afterEach((to) => {
  // 可以在这里处理页面加载完成后的逻辑
  // 例如：关闭加载动画、滚动到顶部等
  window.scrollTo(0, 0)
})

export default router
