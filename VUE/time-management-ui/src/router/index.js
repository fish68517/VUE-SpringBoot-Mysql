import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/index.vue'

const routes = [
  {
    path: '/',
    component: Layout, // 所有主要页面都使用这个布局
    redirect: '/task', // 默认重定向到任务页面
    children: [
      {
        path: '/task',
        name: 'Task',
        component: () => import('../views/TaskPage.vue') // 懒加载
      },
      {
        path: '/habit',
        name: 'Habit',
        component: () => import('../views/HabitPage.vue')
      },
      {
        path: '/stats',
        name: 'Stats',
        component: () => import('../views/StatsPage.vue')
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
