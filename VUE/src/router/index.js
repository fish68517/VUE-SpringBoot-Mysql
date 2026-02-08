import { createRouter, createWebHistory } from 'vue-router'
import authService from '../services/authService'
import MainLayout from '../components/MainLayout.vue'
import AdminLayout from '../components/AdminLayout.vue' // [新增] 引入管理员布局

// 页面组件引入
import Login from '../pages/Login.vue'
import Register from '../pages/Register.vue'
import Dashboard from '../pages/Dashboard.vue'
import Profile from '../pages/Profile.vue'
import SchoolSearch from '../pages/SchoolSearch.vue'
import SchoolDetail from '../pages/SchoolDetail.vue'
import Favorites from '../pages/Favorites.vue'
import Announcements from '../pages/Announcements.vue'
import FeedbackSubmit from '../pages/FeedbackSubmit.vue'
import FeedbackHistory from '../pages/FeedbackHistory.vue'

// 管理员页面组件
import AdminUsers from '../pages/AdminUsers.vue'
import AdminSchools from '../pages/AdminSchools.vue'
import AdminAnnouncements from '../pages/AdminAnnouncements.vue'
import AdminFeedback from '../pages/AdminFeedback.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false, title: 'Login' }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false, title: 'Register' }
  },
  
  // 普通用户路由 (使用 MainLayout)
  {
    path: '/app',
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: 'Dashboard' } // 用户也可以访问，或者您可以移除这里
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile,
        meta: { requiredRole: 'USER', title: 'My Profile' }
      },
      {
        path: 'schools',
        name: 'SchoolSearch',
        component: SchoolSearch,
        meta: { requiredRole: 'USER', title: 'Search Schools' }
      },
      {
        path: 'school/:id',
        name: 'SchoolDetail',
        component: SchoolDetail,
        meta: { requiredRole: 'USER', title: 'School Details' }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: Favorites,
        meta: { requiredRole: 'USER', title: 'My Favorites' }
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: Announcements,
        meta: { requiredRole: 'USER', title: 'Announcements' }
      },
      {
        path: 'feedback',
        name: 'FeedbackSubmit',
        component: FeedbackSubmit,
        meta: { requiredRole: 'USER', title: 'Submit Feedback' }
      },
      {
        path: 'feedback-history',
        name: 'FeedbackHistory',
        component: FeedbackHistory,
        meta: { requiredRole: 'USER', title: 'Feedback History' }
      }
    ]
  },

  // [新增] 管理员路由 (使用 AdminLayout)
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiredRole: 'ADMIN' }, // 整个 /admin 路径都需要 ADMIN 权限
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        // 如果您没有专门的 AdminDashboard.vue，可以暂时重定向到 users 或者复用 Dashboard
        // 这里建议您可以先重定向到 users
        redirect: '/admin/users' 
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: AdminUsers,
        meta: { title: '用户管理' }
      },
      {
        path: 'schools',
        name: 'AdminSchools',
        component: AdminSchools,
        meta: { title: '学校管理' }
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: AdminAnnouncements,
        meta: { title: '公告管理' }
      },
      {
        path: 'feedback',
        name: 'AdminFeedback',
        component: AdminFeedback,
        meta: { title: '反馈处理' }
      }
    ]
  },

  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// ... (Router Guard 代码保持不变) ...
router.beforeEach((to, _from, next) => {
  const isAuthenticated = authService.isAuthenticated()
  const userRole = authService.getUserRole()
  const requiresAuth = to.meta.requiresAuth
  const requiredRole = to.meta.requiredRole

  if (requiresAuth) {
    if (!isAuthenticated) {
      next('/login')
      return
    }
    // 权限检查
    if (requiredRole && userRole !== requiredRole) {
      // 如果是管理员访问用户页面，或者用户访问管理员页面，做重定向处理
      if (userRole === 'ADMIN') {
        next('/admin/users') // 管理员默认去这里
      } else {
        next('/app/dashboard') // 用户默认去这里
      }
      return
    }
  }

  // 已登录用户访问登录页处理
  if ((to.path === '/login' || to.path === '/register') && isAuthenticated) {
    if (userRole === 'ADMIN') {
      next('/admin/users')
    } else {
      next('/app/dashboard')
    }
    return
  }

  next()
})

router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} - 考研择校系统` : '考研择校系统'
})

export default router