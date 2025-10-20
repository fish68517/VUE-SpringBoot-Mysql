import { createRouter, createWebHistory } from 'vue-router';
import Layout from '../layout/index.vue';
import AdminLayout from '../layout/AdminLayout.vue';

const routes = [
  // --- 普通用户/操作员路由 ---
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
        { 
          path: '/dashboard', 
          name: 'Dashboard', 
          component: () => import('../views/DashboardPage.vue') 
        },
        { 
          path: '/products', 
          name: 'ProductManagement', 
          component: () => import('../views/ProductManagementPage.vue') 
        },
        { 
          path: '/inventory', 
          name: 'InventoryManagement', 
          component: () => import('../views/InventoryManagementPage.vue') 
        },
        { 
          path: '/inbound-orders', 
          name: 'InboundOrderManagement', 
          component: () => import('../views/InboundOrderManagementPage.vue') 
        },
  
    ]
  },
  
  // --- 管理员专属路由 ---
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/AdminDashboard.vue')
      },
      // ===== 新增数据统计路由 =====
      {
        path: 'statistics',
        name: 'DataStatistics',
        component: () => import('../views/admin/DataStatisticsPage.vue')
      },
      // ===========================
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('../views/admin/UserManagementPage.vue')
      },
      {
        path: 'departments',
        name: 'DepartmentManagement',
        component: () => import('../views/admin/DepartmentManagementPage.vue')
      },
            // ===== 新增管理员的入库管理路由 =====
      {
        path: 'inbound-management', // 使用不同的路径区分普通用户的入库管理
        name: 'AdminInboundManagement',
        component: () => import('../views/admin/InboundManagementPage.vue') 
      },
      // ===================================
      // ===== 新增出库管理路由 =====
      {
        path: 'outbound-management',
        name: 'AdminOutboundManagement',
        component: () => import('../views/admin/OutboundManagementPage.vue')
      },
      // ===========================
    ]
  },

  // --- 登录页 ---
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