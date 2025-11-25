import { createRouter, createWebHistory } from 'vue-router'
import { getToken, getUserInfo } from '@/utils/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // Public routes
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/auth/Register.vue'),
      meta: { requiresAuth: false }
    },
    
    // User routes
    {
      path: '/home',
      name: 'Home',
      component: () => import('@/views/Home.vue'),
      meta: { requiresAuth: true, roles: ['user', 'coach', 'admin'] }
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('@/views/user/Profile.vue'),
      meta: { requiresAuth: true, roles: ['user', 'coach', 'admin'] }
    },
    {
      path: '/resources',
      name: 'ResourceList',
      component: () => import('@/views/resource/ResourceList.vue'),
      meta: { requiresAuth: true, roles: ['user', 'coach', 'admin'] }
    },
    {
      path: '/resources/:id',
      name: 'ResourceDetail',
      component: () => import('@/views/resource/ResourceDetail.vue'),
      meta: { requiresAuth: true, roles: ['user', 'coach', 'admin'] }
    },
    {
      path: '/my-collections',
      name: 'MyCollections',
      component: () => import('@/views/user/MyCollections.vue'),
      meta: { requiresAuth: true, roles: ['user', 'coach', 'admin'] }
    },
    {
      path: '/training-plans',
      name: 'MyTrainingPlans',
      component: () => import('@/views/user/MyTrainingPlans.vue'),
      meta: { requiresAuth: true, roles: ['user', 'coach', 'admin'] }
    },
    {
      path: '/community',
      name: 'CommunityFeed',
      // 修改这一行，加上 .catch 打印错误
      component: () => import('@/views/community/CommunityFeed.vue').catch(err => {
        console.log('严重错误：无法加载 CommunityFeed 组件！', err);
        return import('@/views/NotFound.vue'); // 或者返回一个临时组件防止白屏
      }),
      meta: { requiresAuth: false } 
    },
    {
      path: '/community/create',
      name: 'CreatePost',
      component: () => import('@/views/community/CreatePost.vue'),
      meta: { requiresAuth: true, roles: ['user', 'coach', 'admin'] }
    },
    {
      path: '/community/:id',
      name: 'PostDetail',
      component: () => import('@/views/community/PostDetail.vue'),
      meta: { requiresAuth: true, roles: ['user', 'coach', 'admin'] }
    },
    {
      path: '/checkin',
      name: 'CheckIn',
      component: () => import('@/views/user/CheckIn.vue'),
      meta: { requiresAuth: true, roles: ['user', 'coach', 'admin'] }
    },
    {
      path: '/diet',
      name: 'DietRecords',
      component: () => import('@/views/user/DietRecords.vue'),
      meta: { requiresAuth: true, roles: ['user', 'coach', 'admin'] }
    },
    
    // Coach routes
    {
      path: '/coach/dashboard',
      name: 'CoachDashboard',
      component: () => import('@/views/coach/CoachDashboard.vue'),
      meta: { requiresAuth: true, roles: ['coach'] }
    },
    {
      path: '/coach/students',
      name: 'StudentList',
      component: () => import('@/views/coach/StudentList.vue'),
      meta: { requiresAuth: true, roles: ['coach'] }
    },
    {
      path: '/coach/training-plans/create',
      name: 'CreateTrainingPlan',
      component: () => import('@/views/coach/CreateTrainingPlan.vue'),
      meta: { requiresAuth: true, roles: ['coach'] }
    },
    {
      path: '/coach/analytics',
      name: 'StudentAnalytics',
      component: () => import('@/views/coach/StudentAnalytics.vue'),
      meta: { requiresAuth: true, roles: ['coach'] }
    },
    {
      path: '/coach/content',
      name: 'ContentManagement',
      component: () => import('@/views/coach/ContentManagement.vue'),
      meta: { requiresAuth: true, roles: ['coach'] }
    },
    
    // Admin routes
    {
      path: '/admin/dashboard',
      name: 'AdminDashboard',
      component: () => import('@/views/admin/AdminDashboard.vue'),
      meta: { requiresAuth: true, roles: ['admin'] }
    },
    {
      path: '/admin/users',
      name: 'UserManagement',
      component: () => import('@/views/admin/UserManagement.vue'),
      meta: { requiresAuth: true, roles: ['admin'] }
    },
    {
      path: '/admin/resources',
      name: 'ResourceManagement',
      component: () => import('@/views/admin/ResourceManagement.vue'),
      meta: { requiresAuth: true, roles: ['admin'] }
    },
    {
      path: '/admin/moderation',
      name: 'ContentModeration',
      component: () => import('@/views/admin/ContentModeration.vue').catch(err => {
        console.log('严重错误：无法加载 ContentModeration 组件！', err);
        return import('@/views/NotFound.vue'); // 或者返回一个临时组件防止白屏
      }),
      meta: { requiresAuth: false } 
    },
    
    // Search results
    {
      path: '/search',
      name: 'SearchResults',
      component: () => import('@/views/SearchResults.vue'),
      meta: { requiresAuth: true, roles: ['user', 'coach', 'admin'] }
    },
    
    // Error pages
    {
      path: '/unauthorized',
      name: 'Unauthorized',
      component: () => import('@/views/Unauthorized.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/NotFound.vue'),
      meta: { requiresAuth: false }
    },
    
    // Default redirect
    {
      path: '/',
      redirect: '/login'
    }
  ]
})

// Navigation guard
router.beforeEach((to, _from, next) => {
  console.log('路由守卫触发 -> 去往:', to.path); // 1. 打印目标路径
  const token = getToken()
  const userInfo = getUserInfo()
  
  // Check if route requires authentication
  // if (to.meta.requiresAuth) {
  //   if (!token) {
  //     // Not authenticated, redirect to login
  //     next('/login')
  //     return
  //   }
    
  //   // Check if user role matches required roles
  //   if (to.meta.roles && userInfo) {
  //     const userRole = userInfo.role
  //     if (!to.meta.roles.includes(userRole)) {
  //       // Role doesn't match, redirect to unauthorized
  //       next('/unauthorized')
  //       return
  //     }
  //   }
  // }
  
  // If already logged in and trying to access login/register, redirect to appropriate dashboard
  // if ((to.path === '/login' || to.path === '/register') && token && userInfo) {
  //   const role = userInfo.role
  //   if (role === 'admin') {
  //     next('/admin/dashboard')
  //   } else if (role === 'coach') {
  //     next('/coach/dashboard')
  //   } else {
  //     next('/home')
  //   }
  //   return
  // }
  console.log('路由守卫放行 -> next() 执行'); // 2. 确认执行到了这里
  next()
})

export default router
