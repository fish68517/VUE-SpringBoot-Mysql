import api from './api'

/**
 * Plan service - encapsulates all travel plan-related API calls
 */
export const planService = {
  /**
   * Create a new travel plan
   * @param {Object} data - Travel plan data (title, destination, startDate, endDate, budget, description)
   * @returns {Promise} Response containing created travel plan
   */
  createTravelPlan(data) {
    return api.post('/plans', data)
  },

  /**
   * Get user's travel plans with pagination
   * @param {number} page - The page number (0-indexed)
   * @param {number} size - The page size
   * @returns {Promise} Response containing paginated travel plans
   */
  getUserTravelPlans(page = 0, size = 10) {
    return api.get('/plans', {
      params: {
        page,
        size
      }
    })
  },

  /**
   * Get a travel plan by ID
   * @param {number} planId - The plan ID
   * @returns {Promise} Response containing travel plan details
   */
  getTravelPlanById(planId) {
    return api.get(`/plans/${planId}`)
  },

  /**
   * Update a travel plan
   * @param {number} planId - The plan ID
   * @param {Object} data - Updated travel plan data (title, destination, startDate, endDate, budget, description)
   * @returns {Promise} Response containing updated travel plan
   */
  updateTravelPlan(planId, data) {
    return api.put(`/plans/${planId}`, data)
  },

  /**
   * Delete a travel plan
   * @param {number} planId - The plan ID
   * @returns {Promise} Response confirming deletion
   */
  deleteTravelPlan(planId) {
    return api.delete(`/plans/${planId}`)
  }
}
