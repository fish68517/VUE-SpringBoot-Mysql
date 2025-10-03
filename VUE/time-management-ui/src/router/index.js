import { createRouter, createWebHistory } from 'vue-router';
import Layout from '../layout/index.vue';
import AdminLayout from '../layout/AdminLayout.vue'; // 引入Admin布局

const routes = [
  // ... 普通用户路由保持不变
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
        { path: '/dashboard', name: 'Dashboard', component: () => import('../views/DashboardPage.vue') },
        { path: '/task', name: 'Task', component: () => import('../views/TaskPage.vue') },
        { path: '/habit', name: 'Habit', component: () => import('../views/HabitPage.vue') },
        { path: '/achievement', name: 'Achievement', component: () => import('../views/AchievementPage.vue') }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginPage.vue')
  },
  
  // ===== 新增管理员专属路由 =====
  {
    path: '/admin',
    component: AdminLayout, // 使用 Admin 布局
    redirect: '/admin/dashboard',
    children: [
      // 新增仪表盘路由
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/AdminDashboard.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('../views/admin/UserManagementPage.vue')
      },
      {
        path: 'incentives',
        name: 'IncentiveManagement',
        component: () => import('../views/admin/IncentiveManagementPage.vue')
      },
      // 新增成就徽章路由
      {
        path: 'resources',
        name: 'ResourceManagement',
        component: () => import('../views/admin/ResourceManagementPage.vue')
      }
      // 在这里继续添加其他页面的路由
    ]
  },
  // 如果有登录页面等不需要侧边栏和头部的页面，可以在这里单独配置
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginPage.vue')
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;