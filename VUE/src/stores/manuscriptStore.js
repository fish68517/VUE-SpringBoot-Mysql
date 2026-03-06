import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useManuscriptStore = defineStore('manuscript', () => {
  const manuscripts = ref([])
  const currentManuscript = ref(null)

  const setManuscripts = (data) => {
    manuscripts.value = data
  }

  const setCurrentManuscript = (data) => {
    currentManuscript.value = data
  }

  const clearCurrentManuscript = () => {
    currentManuscript.value = null
  }

  return {
    manuscripts,
    currentManuscript,
    setManuscripts,
    setCurrentManuscript,
    clearCurrentManuscript
  }
})
