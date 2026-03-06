import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import { canAccessRoute, getDefaultRedirectPath } from '../utils/permissionGuard'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue')
  },
  {
    path: '/author',
    name: 'AuthorLayout',
    component: () => import('../views/layouts/AuthorLayout.vue'),
    meta: { requiresAuth: true, roles: ['AUTHOR'] },
    children: [
      {
        path: 'dashboard',
        name: 'AuthorDashboard',
        component: () => import('../views/author/Dashboard.vue')
      },
      {
        path: 'profile',
        name: 'AuthorProfile',
        component: () => import('../views/author/Profile.vue')
      },
      {
        path: 'manuscripts',
        name: 'AuthorManuscripts',
        component: () => import('../views/author/Manuscripts.vue')
      },
      {
        path: 'submit-manuscript',
        name: 'SubmitManuscript',
        component: () => import('../views/author/SubmitManuscript.vue')
      },
      {
        path: 'manuscript-detail/:id',
        name: 'ManuscriptDetail',
        component: () => import('../views/author/ManuscriptDetail.vue')
      },
      {
        path: 'edit-manuscript/:id',
        name: 'EditManuscript',
        component: () => import('../views/author/EditManuscript.vue')
      },
      {
        path: 'messages',
        name: 'AuthorMessages',
        component: () => import('../views/author/Messages.vue')
      }
    ]
  },
  {
    path: '/editor',
    name: 'EditorLayout',
    component: () => import('../views/layouts/EditorLayout.vue'),
    meta: { requiresAuth: true, roles: ['EDITOR'] },
    children: [
      {
        path: 'dashboard',
        name: 'EditorDashboard',
        component: () => import('../views/editor/Dashboard.vue')
      },
      {
        path: 'profile',
        name: 'EditorProfile',
        component: () => import('../views/editor/Profile.vue')
      },
      {
        path: 'review',
        name: 'EditorReview',
        component: () => import('../views/editor/Review.vue')
      },
      {
        path: 'initial-review',
        name: 'EditorInitialReview',
        component: () => import('../views/editor/InitialReview.vue')
      },
      {
        path: 'assign-reviewer',
        name: 'EditorAssignReviewer',
        component: () => import('../views/editor/AssignReviewer.vue')
      },
      {
        path: 'review-progress',
        name: 'EditorReviewProgress',
        component: () => import('../views/editor/ReviewProgress.vue')
      },
      {
        path: 'edit-manuscript',
        name: 'EditorEditManuscript',
        component: () => import('../views/editor/EditManuscript.vue')
      },
      {
        path: 'communication',
        name: 'EditorCommunication',
        component: () => import('../views/editor/Communication.vue')
      }
    ]
  },
  {
    path: '/reviewer',
    name: 'ReviewerLayout',
    component: () => import('../views/layouts/ReviewerLayout.vue'),
    meta: { requiresAuth: true, roles: ['REVIEWER'] },
    children: [
      {
        path: 'dashboard',
        name: 'ReviewerDashboard',
        component: () => import('../views/reviewer/Dashboard.vue')
      },
      {
        path: 'profile',
        name: 'ReviewerProfile',
        component: () => import('../views/reviewer/Profile.vue')
      },
      {
        path: 'tasks',
        name: 'ReviewerTasks',
        component: () => import('../views/reviewer/Tasks.vue')
      },
      {
        path: 'history',
        name: 'ReviewerHistory',
        component: () => import('../views/reviewer/History.vue')
      }
    ]
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('../views/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('../views/admin/Profile.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/Users.vue')
      },
      {
        path: 'user-approval',
        name: 'AdminUserApproval',
        component: () => import('../views/admin/UserApproval.vue')
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('../views/admin/Categories.vue')
      },
      {
        path: 'system-config',
        name: 'AdminSystemConfig',
        component: () => import('../views/admin/SystemConfig.vue')
      },
      {
        path: 'statistics',
        name: 'AdminStatistics',
        component: () => import('../views/admin/Statistics.vue')
      }
    ]
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  
  // Allow access to public routes (login, register)
  if (!to.meta.requiresAuth) {
    // If user is already logged in and tries to access login/register, redirect to dashboard
    if (userStore.isLoggedIn && (to.name === 'Login' || to.name === 'Register')) {
      next(getDefaultRedirectPath(userStore.user.role))
    } else {
      next()
    }
    return
  }

  // Check if user is logged in
  if (!userStore.isLoggedIn) {
    next('/login')
    return
  }

  // Check if user has permission to access the route
  if (!canAccessRoute(userStore.user, to.meta)) {
    // User doesn't have required role or permission
    next(getDefaultRedirectPath(userStore.user.role))
    return
  }

  next()
})

export default router
