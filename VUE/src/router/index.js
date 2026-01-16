import { createRouter, createWebHistory } from 'vue-router'
import authService from '../services/authService'
import MainLayout from '../components/MainLayout.vue'
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
  {
    path: '/app',
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { requiredRole: 'USER', title: 'Dashboard' }
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
      },
      {
        path: 'admin/users',
        name: 'AdminUsers',
        component: AdminUsers,
        meta: { requiredRole: 'ADMIN', title: 'User Management' }
      },
      {
        path: 'admin/schools',
        name: 'AdminSchools',
        component: AdminSchools,
        meta: { requiredRole: 'ADMIN', title: 'School Management' }
      },
      {
        path: 'admin/announcements',
        name: 'AdminAnnouncements',
        component: AdminAnnouncements,
        meta: { requiredRole: 'ADMIN', title: 'Announcement Management' }
      },
      {
        path: 'admin/feedback',
        name: 'AdminFeedback',
        component: AdminFeedback,
        meta: { requiredRole: 'ADMIN', title: 'Feedback Management' }
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

/**
 * Global navigation guard to handle authentication and authorization
 * Checks if user is authenticated and has required role for the route
 */
router.beforeEach((to, _from, next) => {
  const isAuthenticated = authService.isAuthenticated()
  const userRole = authService.getUserRole()
  const requiresAuth = to.meta.requiresAuth
  const requiredRole = to.meta.requiredRole

  // If route requires authentication
  if (requiresAuth) {
    if (!isAuthenticated) {
      // Redirect unauthenticated users to login
      next('/login')
      return
    }

    // If route requires a specific role
    if (requiredRole && userRole !== requiredRole) {
      // Redirect users without required role to dashboard
      next('/app/dashboard')
      return
    }
  }

  // If user is authenticated and trying to access login/register
  if ((to.path === '/login' || to.path === '/register') && isAuthenticated) {
    // Redirect authenticated users to dashboard
    next('/app/dashboard')
    return
  }

  // Allow navigation
  next()
})

/**
 * After navigation hook to update page title
 */
router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} - Postgraduate School Selection` : 'Postgraduate School Selection'
})

export default router
