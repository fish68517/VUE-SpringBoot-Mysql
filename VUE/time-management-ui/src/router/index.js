import { createRouter, createWebHistory } from 'vue-router';
import Layout from '../layout/index.vue';
import AdminLayout from '../layout/AdminLayout.vue';
import { getStoredUserType, isAdminLoggedIn } from '../utils/auth';

const routes = [
  {
    path: '/user',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: '/dashboard', name: 'Dashboard', component: () => import('../views/DashboardPage.vue') },
      { path: '/task', name: 'Task', component: () => import('../views/TaskPage.vue') },
      { path: '/habit', name: 'Habit', component: () => import('../views/HabitPage.vue') },
      { path: '/achievement', name: 'Achievement', component: () => import('../views/AchievementPage.vue') },
    ],
  },
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginPage.vue'),
  },
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/AdminDashboard.vue'),
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('../views/admin/UserManagementPage.vue'),
      },
      {
        path: 'incentives',
        name: 'IncentiveManagement',
        component: () => import('../views/admin/IncentiveManagementPage.vue'),
      },
      {
        path: 'resources',
        name: 'ResourceManagement',
        component: () => import('../views/admin/ResourceManagementPage.vue'),
      },
      {
        path: 'focus',
        name: 'FocusManagement',
        component: () => import('../views/admin/FocusManagement.vue'),
      },
      {
        path: 'habits',
        name: 'HabitManagement',
        component: () => import('../views/admin/HabitManagement.vue'),
      },
      {
        path: 'user-achievements',
        name: 'UserAchievementManagement',
        component: () => import('../views/admin/UserAchievementManagement.vue'),
      },
      {
        path: 'interactions',
        name: 'UserActionManagement',
        component: () => import('../views/admin/UserActionManagement.vue'),
      },
      {
        path: 'resource-editor',
        name: 'ResourceEditor',
        component: () => import('../views/admin/ResourceContentEditor.vue'),
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const userType = getStoredUserType();

  if (to.path === '/login' && userType) {
    next(userType === 'admin' ? '/admin/dashboard' : '/dashboard');
    return;
  }

  if (to.path.startsWith('/admin') && !isAdminLoggedIn()) {
    next('/login');
    return;
  }

  next();
});

export default router;
