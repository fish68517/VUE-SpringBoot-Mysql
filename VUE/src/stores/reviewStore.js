import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useReviewStore = defineStore('review', () => {
  const reviews = ref([])
  const currentReview = ref(null)

  const setReviews = (data) => {
    reviews.value = data
  }

  const setCurrentReview = (data) => {
    currentReview.value = data
  }

  const clearCurrentReview = () => {
    currentReview.value = null
  }

  return {
    reviews,
    currentReview,
    setReviews,
    setCurrentReview,
    clearCurrentReview
  }
})
