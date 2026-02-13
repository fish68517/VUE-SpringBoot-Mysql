import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // Initialize from localStorage
  const storedUser = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null
  const storedToken = localStorage.getItem('token') || null

  const user = ref(storedUser)
  const token = ref(storedToken)

  // Computed properties
  const isLoggedIn = computed(() => !!token.value && !!user.value)
  
  const isAdmin = computed(() => {
    return user.value?.role === 'admin' || user.value?.roleId === 1
  })

  // Methods
  const setUser = (userData) => {
    user.value = userData
    if (userData) {
      localStorage.setItem('user', JSON.stringify(userData))
    } else {
      localStorage.removeItem('user')
    }
  }

  const setToken = (newToken) => {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('token', newToken)
    } else {
      localStorage.removeItem('token')
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const updateUser = (userData) => {
    user.value = { ...user.value, ...userData }
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  return {
    user,
    token,
    isLoggedIn,
    isAdmin,
    setUser,
    setToken,
    logout,
    updateUser
  }
})

export const useGlobalStore = defineStore('global', () => {
  const loading = ref(false)
  const message = ref('')

  const setLoading = (value) => {
    loading.value = value
  }

  const setMessage = (msg) => {
    message.value = msg
  }

  return {
    loading,
    message,
    setLoading,
    setMessage
  }
})
