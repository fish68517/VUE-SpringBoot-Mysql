import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { authService } from '../services/auth'

/**
 * Composable for authentication functionality
 * Provides reactive authentication state and methods
 */
export function useAuth() {
  const router = useRouter()
  const userStore = useUserStore()

  // Computed properties
  const isLoggedIn = computed(() => userStore.isLoggedIn)
  const isAdmin = computed(() => userStore.isAdmin)
  const currentUser = computed(() => userStore.user)
  const token = computed(() => userStore.token)

  // Methods
  const login = async (username, password) => {
    const result = await authService.login(username, password)
    return result
  }

  const register = async (username, email, password) => {
    const result = await authService.register(username, email, password)
    return result
  }

  const logout = () => {
    authService.logout()
    router.push('/login')
  }

  const restoreSession = () => {
    return authService.restoreSession()
  }

  const clearSession = () => {
    authService.clearSession()
  }

  const requireAuth = () => {
    if (!isLoggedIn.value) {
      router.push({
        path: '/login',
        query: { redirect: router.currentRoute.value.fullPath }
      })
      return false
    }
    return true
  }

  const requireAdmin = () => {
    if (!isLoggedIn.value) {
      router.push({
        path: '/login',
        query: { redirect: router.currentRoute.value.fullPath }
      })
      return false
    }
    if (!isAdmin.value) {
      router.push('/')
      return false
    }
    return true
  }

  return {
    // State
    isLoggedIn,
    isAdmin,
    currentUser,
    token,
    // Methods
    login,
    register,
    logout,
    restoreSession,
    clearSession,
    requireAuth,
    requireAdmin
  }
}

export default useAuth
