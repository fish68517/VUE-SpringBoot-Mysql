import { defineStore } from 'pinia'
import { ref } from 'vue'
import { recommendationAPI } from '@/api/recommendation'

export const useRecommendationsStore = defineStore('recommendations', () => {
  const recommendations = ref([])
  const currentRecommendation = ref(null)
  const loading = ref(false)
  const error = ref(null)

  async function fetchRecommendations(warningId) {
    loading.value = true
    error.value = null
    try {
      const response = await recommendationAPI.getRecommendations(warningId)
      recommendations.value = Array.isArray(response) ? response : []
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function generateRecommendations(recommendationData) {
    loading.value = true
    error.value = null
    try {
      const response = await recommendationAPI.createRecommendation(recommendationData)
      recommendations.value = Array.isArray(response) ? response : [response]
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function acceptRecommendation(id) {
    loading.value = true
    error.value = null
    try {
      const response = await recommendationAPI.acceptRecommendation(id)
      const index = recommendations.value.findIndex((r) => r.id === id)
      if (index !== -1) {
        recommendations.value[index] = response
      }
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function rejectRecommendation(id) {
    loading.value = true
    error.value = null
    try {
      const response = await recommendationAPI.rejectRecommendation(id)
      const index = recommendations.value.findIndex((r) => r.id === id)
      if (index !== -1) {
        recommendations.value[index] = response
      }
      return response
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    recommendations,
    currentRecommendation,
    loading,
    error,
    fetchRecommendations,
    generateRecommendations,
    acceptRecommendation,
    rejectRecommendation,
  }
})
