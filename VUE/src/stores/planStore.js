import { defineStore } from 'pinia'
import { ref } from 'vue'

export const usePlanStore = defineStore('plan', () => {
  const plans = ref([])
  const currentPlan = ref(null)

  const setPlans = (data) => {
    plans.value = data
  }

  const setCurrentPlan = (plan) => {
    currentPlan.value = plan
  }

  const addPlan = (plan) => {
    plans.value.push(plan)
  }

  const updatePlan = (id, updatedPlan) => {
    const index = plans.value.findIndex(p => p.id === id)
    if (index !== -1) {
      plans.value[index] = updatedPlan
    }
  }

  const deletePlan = (id) => {
    plans.value = plans.value.filter(p => p.id !== id)
  }

  return {
    plans,
    currentPlan,
    setPlans,
    setCurrentPlan,
    addPlan,
    updatePlan,
    deletePlan
  }
})
