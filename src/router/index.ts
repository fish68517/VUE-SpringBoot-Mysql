/*
 * @Author: kaix
 * @Date: 2025-07-04 11:09:37
 * @LastEditTime: 2025-07-04 11:30:10
 * @LastEditors: kaix
 * @Description: 
 */
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Playground from '@/demo/Playground.vue'

// 懒加载的测试页面
const BusTest = () => import('@/demo/BusTest.vue');
const FireTest = () => import('@/package/BZAQ/BzFire/index.vue');
const BzRiskTest = () => import('@/demo/BzRiskTest.vue');
const AIAnalysisDiaTest = () => import('@/demo/AIAnalysisDiaTest.vue');

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: Playground,
    meta: {
      title: '组件调试playground'
    }
  },
  {
    path: '/bus',
    name: 'BusTest',
    component: BusTest,
    meta: {
      title: '全局参数-自定义事件测试页面'
    }
  },
  {
    path: '/bz-risk',
    name: 'BzRiskTest',
    component: BzRiskTest,
    meta: {
      title: 'BzRisk 风险隐患组件测试'
    }
  },
  {
    path: '/ai-analysis-dia',
    name: 'AIAnalysisDiaTest',
    component: AIAnalysisDiaTest,
    meta: {
      title: 'BzAIAnalysisDia AI分析弹窗测试'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title as string
  }
  next()
})

export default router 