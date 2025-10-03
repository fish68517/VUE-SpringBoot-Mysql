import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/index.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard', // 将默认页面改为仪表盘
    children: [
      // 新增仪表盘路由
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/DashboardPage.vue')
      },
      {
        path: '/task',
        name: 'Task',
        component: () => import('../views/TaskPage.vue')
      },
      {
        path: '/habit',
        name: 'Habit',
        component: () => import('../views/HabitPage.vue')
      },
      // 新增成就徽章路由
      {
        path: '/achievement',
        name: 'Achievement',
        component: () => import('../views/AchievementPage.vue')
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
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
