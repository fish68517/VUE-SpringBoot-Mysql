import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi } from '@/api/auth'
import { getToken, setToken, getUserInfo, setUserInfo, clearAuth } from '@/utils/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: getUserInfo() || null,
    token: getToken() || '',
    isAuthenticated: !!getToken()
  }),

  getters: {
    currentUser: (state) => state.user,
    userRole: (state) => state.user?.role || '',
    isAdmin: (state) => state.user?.role === 'admin',
    isCoach: (state) => state.user?.role === 'coach',
    isUser: (state) => state.user?.role === 'user'
  },

  actions: {
    async login(credentials) {
      try {
        const response = await loginApi(credentials)
        
        this.token = response.token
        this.user = response.user
        this.isAuthenticated = true
        
        setToken(response.token)
        setUserInfo(response.user)
        
        return response
      } catch (error) {
        this.logout()
        throw error
      }
    },

    async register(data) {
      try {
        const response = await registerApi(data)
        return response
      } catch (error) {
        throw error
      }
    },

    logout() {
      this.token = ''
      this.user = null
      this.isAuthenticated = false
      clearAuth()
    },

    checkAuth() {
      const token = getToken()
      const userInfo = getUserInfo()
      
      if (token && userInfo) {
        this.token = token
        this.user = userInfo
        this.isAuthenticated = true
        return true
      } else {
        this.logout()
        return false
      }
    },

    updateUser(userData) {
      this.user = { ...this.user, ...userData }
      setUserInfo(this.user)
    }
  }
})
