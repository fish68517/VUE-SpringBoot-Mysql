import request from '../utils/request';

/**
 * Get training plans
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Training plans list
 */
export function getTrainingPlans(params) {
  return request({
    url: '/api/training-plans',
    method: 'get',
    params
  });
}

/**
 * Get training plan by ID
 * @param {number} id - Training plan ID
 * @returns {Promise} Training plan details
 */
export function getTrainingPlanById(id) {
  return request({
    url: `/api/training-plans/${id}`,
    method: 'get'
  });
}

/**
 * Create new training plan
 * @param {Object} data - Training plan data
 * @param {string} data.name - Plan name
 * @param {string} data.description - Plan description
 * @param {string} data.exercises - Exercises JSON string
 * @param {number} data.studentId - Student user ID
 * @param {string} data.startDate - Start date
 * @param {string} data.endDate - End date
 * @param {string} data.status - Plan status (active, completed, cancelled)
 * @returns {Promise} Created training plan
 */
export function createTrainingPlan(data) {
  return request({
    url: '/api/training-plans',
    method: 'post',
    data
  });
}

/**
 * Update training plan
 * @param {number} id - Training plan ID
 * @param {Object} data - Updated plan data
 * @returns {Promise} Updated training plan
 */
export function updateTrainingPlan(id, data) {
  return request({
    url: `/api/training-plans/${id}`,
    method: 'put',
    data
  });
}

/**
 * Delete training plan
 * @param {number} id - Training plan ID
 * @returns {Promise} Delete response
 */
export function deleteTrainingPlan(id) {
  return request({
    url: `/api/training-plans/${id}`,
    method: 'delete'
  });
}
