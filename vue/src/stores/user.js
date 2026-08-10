import { defineStore } from 'pinia'
import * as authApi from '../api/auth'

const STORAGE_KEY = 'charging-platform-user'

function readStoredUser() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null')
  } catch {
    localStorage.removeItem(STORAGE_KEY)
    return null
  }
}

export const useUserStore = defineStore('user', {
  state: () => ({
    user: readStoredUser()
  }),
  getters: {
    isLoggedIn: state => Boolean(state.user?.id),
    displayName: state => state.user?.nickname || state.user?.username || '访客',
    isAdmin: state => state.user?.userType === 1
  },
  actions: {
    saveUser(user) {
      this.user = user
      localStorage.setItem(STORAGE_KEY, JSON.stringify(user))
    },
    async login(form) {
      const user = await authApi.login(form)
      this.saveUser(user)
      return user
    },
    async register(form) {
      const user = await authApi.register(form)
      this.saveUser(user)
      return user
    },
    logout() {
      this.user = null
      localStorage.removeItem(STORAGE_KEY)
    }
  }
})
