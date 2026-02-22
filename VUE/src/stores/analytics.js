import { defineStore } from 'pinia'
import { ref } from 'vue'
import { analyticsAPI } from '@/api/analytics'

export const useAnalyticsStore = defineStore('analytics', () => {
  const orderStats = ref(null)
  const warningStats = ref(null)
  const userStats = ref(null)
  const topProducts = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function fetchOrderStats(filters = {}) {
    loading.value = true
    error.value = null
    try {
      const response = await analyticsAPI.getOrderStats(filters)
      orderStats.value = response
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function fetchWarningStats(filters = {}) {
    loading.value = true
    error.value = null
    try {
      const response = await analyticsAPI.getWarningStats(filters)
      warningStats.value = response
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function fetchUserStats(filters = {}) {
    loading.value = true
    error.value = null
    try {
      const response = await analyticsAPI.getUserStats(filters)
      userStats.value = response
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function fetchTopProducts(limit = 10) {
    loading.value = true
    error.value = null
    try {
      const response = await analyticsAPI.getTopProducts({ limit })
      topProducts.value = Array.isArray(response) ? response : []
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function exportData(format = 'csv') {
    loading.value = true
    error.value = null
    try {
      const response = await analyticsAPI.exportData({ format })
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    orderStats,
    warningStats,
    userStats,
    topProducts,
    loading,
    error,
    fetchOrderStats,
    fetchWarningStats,
    fetchUserStats,
    fetchTopProducts,
    exportData,
  }
})
