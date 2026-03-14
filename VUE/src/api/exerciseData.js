import instance from './axios.js'

/**
 * Get exercise data list with pagination
 * @param {number} page - Page number (default 1)
 * @param {number} pageSize - Page size (default 10)
 * @returns {Promise} API response with exercise data list
 */
export const getExerciseDataList = (page = 1, pageSize = 10) => {
  return instance.get('/admin/exercise-data', {
    params: {
      page,
      pageSize
    }
  })
}

/**
 * Get exercise data details by id
 * @param {number} id - Exercise data id
 * @returns {Promise} API response with exercise data details
 */
export const getExerciseDataById = (id) => {
  return instance.get(`/admin/exercise-data/${id}`)
}

/**
 * Delete exercise data by id
 * @param {number} id - Exercise data id
 * @returns {Promise} API response
 */
export const deleteExerciseData = (id) => {
  return instance.delete(`/admin/exercise-data/${id}`)
}
