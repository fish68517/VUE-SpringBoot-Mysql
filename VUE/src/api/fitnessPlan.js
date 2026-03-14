import instance from './axios.js'

/**
 * Get fitness plan list with pagination
 * @param {number} page - Page number (default 1)
 * @param {number} pageSize - Page size (default 10)
 * @returns {Promise} API response with fitness plan list
 */
export const getFitnessPlanList = (page = 1, pageSize = 10) => {
  return instance.get('/admin/fitness-plans', {
    params: {
      page,
      pageSize
    }
  })
}

/**
 * Get fitness plan details by id
 * @param {number} id - Fitness plan id
 * @returns {Promise} API response with fitness plan details
 */
export const getFitnessPlanById = (id) => {
  return instance.get(`/admin/fitness-plans/${id}`)
}

/**
 * Update fitness plan
 * @param {number} id - Fitness plan id
 * @param {object} planData - Fitness plan data to update
 * @returns {Promise} API response
 */
export const updateFitnessPlan = (id, planData) => {
  return instance.put(`/admin/fitness-plans/${id}`, planData)
}

/**
 * Delete fitness plan by id
 * @param {number} id - Fitness plan id
 * @returns {Promise} API response
 */
export const deleteFitnessPlan = (id) => {
  return instance.delete(`/admin/fitness-plans/${id}`)
}
