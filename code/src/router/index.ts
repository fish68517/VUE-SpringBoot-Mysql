import { createRouter, createWebHistory } from 'vue-router'
import { routes } from './routes'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach(async (to) => {
  const token = getToken()
  if (to.meta.public) return token && to.path === '/login' ? '/dashboard' : true
  if (!token) return { path: '/login', query: { redirect: to.fullPath } }
  const userStore = useUserStore()
  if (!userStore.loaded) {
    try {
      await userStore.loadUser()
    } catch {
      userStore.reset()
      return { path: '/login', query: { redirect: to.fullPath } }
    }
  }
  return true
})

router.afterEach((to) => {
  document.title = `${to.meta.title || '工作台'} - ${import.meta.env.VITE_APP_TITLE}`
})

export default router
