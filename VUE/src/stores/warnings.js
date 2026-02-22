import { defineStore } from 'pinia'
import { ref } from 'vue'
import { warningAPI } from '@/api/warning'

export const useWarningsStore = defineStore('warnings', () => {
  const warnings = ref([])
  const currentWarning = ref(null)
  const loading = ref(false)
  const error = ref(null)

  async function fetchWarnings(filters = {}) {
    loading.value = true
    error.value = null
    try {
      const response = await warningAPI.getWarnings(filters)
      warnings.value = Array.isArray(response) ? response : []
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function fetchWarningById(id) {
    loading.value = true
    error.value = null
    try {
      const response = await warningAPI.getWarningById(id)
      currentWarning.value = response
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function createWarning(warningData) {
    loading.value = true
    error.value = null
    try {
      const response = await warningAPI.createWarning(warningData)
      warnings.value.push(response)
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateWarning(id, warningData) {
    loading.value = true
    error.value = null
    try {
      const response = await warningAPI.updateWarning(id, warningData)
      const index = warnings.value.findIndex((w) => w.id === id)
      if (index !== -1) {
        warnings.value[index] = response
      }
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteWarning(id) {
    loading.value = true
    error.value = null
    try {
      await warningAPI.deleteWarning(id)
      warnings.value = warnings.value.filter((w) => w.id !== id)
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    warnings,
    currentWarning,
    loading,
    error,
    fetchWarnings,
    fetchWarningById,
    createWarning,
    updateWarning,
    deleteWarning,
  }
})
