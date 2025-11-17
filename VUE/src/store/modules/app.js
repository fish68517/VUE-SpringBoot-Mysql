import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    loading: false,
    sidebarCollapsed: false
  }),

  getters: {
    isLoading: (state) => state.loading,
    isSidebarCollapsed: (state) => state.sidebarCollapsed
  },

  actions: {
    setLoading(status) {
      this.loading = status
    },

    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },

    setSidebarCollapsed(collapsed) {
      this.sidebarCollapsed = collapsed
    }
  }
})
