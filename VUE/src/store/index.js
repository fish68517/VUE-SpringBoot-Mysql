import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || null)

  const isLoggedIn = computed(() => !!token.value)

  const setUser = (userData) => {
    user.value = userData
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
  }

  return {
    user,
    token,
    isLoggedIn,
    setUser,
    setToken,
    logout
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
