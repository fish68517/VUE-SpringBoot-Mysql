import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref(null)
  const organization = ref(null)
  const isAuthenticated = computed(() => !!user.value)

  // 设置用户信息
  const setUser = (userData) => {
    user.value = userData
    // 持久化到 localStorage
    if (userData) {
      localStorage.setItem('user', JSON.stringify(userData))
    }
  }

  // 设置组织信息
  const setOrganization = (orgData) => {
    organization.value = orgData
    if (orgData) {
      localStorage.setItem('organization', JSON.stringify(orgData))
    }
  }

  // 登出
  const logout = () => {
    user.value = null
    organization.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('organization')
  }

  // 从 localStorage 恢复用户信息
  const restoreUser = () => {
    const savedUser = localStorage.getItem('user')
    const savedOrg = localStorage.getItem('organization')
    
    if (savedUser) {
      user.value = JSON.parse(savedUser)
    }
    if (savedOrg) {
      organization.value = JSON.parse(savedOrg)
    }
  }

  return {
    user,
    organization,
    isAuthenticated,
    setUser,
    setOrganization,
    logout,
    restoreUser,
  }
})
