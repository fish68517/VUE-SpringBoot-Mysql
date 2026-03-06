import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useConfigStore = defineStore('config', () => {
  const config = ref({})
  const categories = ref([])

  const setConfig = (data) => {
    config.value = data
  }

  const setCategories = (data) => {
    categories.value = data
  }

  return {
    config,
    categories,
    setConfig,
    setCategories
  }
})
