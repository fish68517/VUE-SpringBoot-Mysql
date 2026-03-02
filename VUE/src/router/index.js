import { createRouter, createWebHistory } from 'vue-router'
import { authService } from '../services/auth'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../components/LoginPage.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../components/RegisterPage.vue')
  },
  {
    path: '/user',
    name: 'UserLayout',
    component: () => import('../components/user/UserLayout.vue'),
    meta: { requiresAuth: true, role: 'USER' },
    children: [
      {
        path: 'profile',
        name: 'PersonalInfo',
        component: () => import('../components/user/PersonalInfo.vue')
      },
      {
        path: 'health-data',
        name: 'HealthDataInput',
        component: () => import('../components/user/HealthDataInput.vue')
      },
      {
        path: 'health-trend',
        name: 'HealthTrend',
        component: () => import('../components/user/HealthTrend.vue')
      },
      {
        path: 'health-check',
        name: 'HealthCheck',
        component: () => import('../components/user/HealthCheck.vue')
      },
      {
        path: 'gender-health',
        name: 'GenderHealth',
        component: () => import('../components/user/GenderHealth.vue')
      },
      {
        path: 'consultation',
        name: 'Consultation',
        component: () => import('../components/user/Consultation.vue')
      },
      {
        path: 'health-history',
        name: 'HealthHistory',
        component: () => import('../components/user/HealthHistory.vue')
      }
    ]
  },
  {
    path: '/doctor',
    name: 'DoctorLayout',
    component: () => import('../components/doctor/DoctorLayout.vue'),
    meta: { requiresAuth: true, role: 'DOCTOR' },
    children: [
      {
        path: 'patients',
        name: 'PatientList',
        component: () => import('../components/doctor/PatientList.vue')
      },
      {
        path: 'patient/:id',
        name: 'PatientRecord',
        component: () => import('../components/doctor/PatientRecord.vue')
      },
      {
        path: 'data-review',
        name: 'DataReview',
        component: () => import('../components/doctor/DataReview.vue')
      },
      {
        path: 'health-advice',
        name: 'HealthAdvice',
        component: () => import('../components/doctor/HealthAdvice.vue')
      }
    ]
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('../components/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('../components/admin/UserManagement.vue')
      },
      {
        path: 'doctors',
        name: 'DoctorManagement',
        component: () => import('../components/admin/DoctorManagement.vue')
      },
      {
        path: 'statistics',
        name: 'DataStatistics',
        component: () => import('../components/admin/DataStatistics.vue')
      },
      {
        path: 'audit-logs',
        name: 'AuditLog',
        component: () => import('../components/admin/AuditLog.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 检查认证和权限
router.beforeEach((to, _from, next) => {
  const authState = authService.getAuthState()
  const isAuthenticated = authState.isAuthenticated
  const userRole = authState.role

  // 如果用户已认证且尝试访问登录或注册页面，重定向到对应的主页面
  if (isAuthenticated && (to.path === '/login' || to.path === '/register')) {
    if (userRole === 'DOCTOR') {
      next('/doctor/patients')
    } else if (userRole === 'ADMIN') {
      next('/admin/users')
    } else {
      next('/user/profile')
    }
    return
  }

  // 检查需要认证的路由
  if (to.meta.requiresAuth) {
    if (!isAuthenticated) {
      // 未认证用户重定向到登录页面
      next('/login')
    } else if (to.meta.role && to.meta.role !== userRole) {
      // 权限不匹配，重定向到登录页面
      next('/login')
    } else {
      // 认证和权限都符合，允许访问
      next()
    }
  } else {
    // 不需要认证的路由，直接允许访问
    next()
  }
})

export default router
