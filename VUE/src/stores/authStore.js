import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const user = ref(null)
  const isLoggedIn = ref(false)
  const userRole = ref('user') // 'user' 或 'admin'

  // 计算属性
  const isAdmin = computed(() => userRole.value === 'admin')

  // 方法
  const setUser = (userData) => {
    user.value = userData
    isLoggedIn.value = true
    userRole.value = userData.role || 'user'
    // 保存到 localStorage
    localStorage.setItem('user', JSON.stringify(userData))
    localStorage.setItem('userRole', userData.role || 'user')
  }

  const logout = () => {
    user.value = null
    isLoggedIn.value = false
    userRole.value = 'user'
    localStorage.removeItem('user')
    localStorage.removeItem('userRole')
  }

  const loadUserFromStorage = () => {
    const storedUser = localStorage.getItem('user')
    const storedRole = localStorage.getItem('userRole')
    if (storedUser) {
      user.value = JSON.parse(storedUser)
      isLoggedIn.value = true
      userRole.value = storedRole || 'user'
    }
  }

  return {
    user,
    isLoggedIn,
    userRole,
    isAdmin,
    setUser,
    logout,
    loadUserFromStorage,
  }
})
