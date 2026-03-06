import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const isLoggedIn = computed(() => user.value !== null)

  const setUser = (userData) => {
    user.value = userData
  }

  const clearUser = () => {
    user.value = null
  }

  return {
    user,
    isLoggedIn,
    setUser,
    clearUser
  }
})
