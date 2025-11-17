import request from '../utils/request';

/**
 * Get diet records
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @param {string} params.startDate - Start date filter (YYYY-MM-DD)
 * @param {string} params.endDate - End date filter (YYYY-MM-DD)
 * @returns {Promise} Diet records list
 */
export function getDietRecords(params) {
  return request({
    url: '/api/diet-records',
    method: 'get',
    params
  });
}

/**
 * Create new diet record
 * @param {Object} data - Diet record data
 * @param {string} data.mealType - Meal type (breakfast, lunch, dinner, snack)
 * @param {string} data.foodItems - Food items description
 * @param {number} data.calories - Calorie count
 * @param {string} data.mealDate - Meal date (YYYY-MM-DD)
 * @returns {Promise} Created diet record
 */
export function createDietRecord(data) {
  return request({
    url: '/api/diet-records',
    method: 'post',
    data
  });
}

/**
 * Update diet record
 * @param {number} id - Diet record ID
 * @param {Object} data - Updated diet record data
 * @returns {Promise} Updated diet record
 */
export function updateDietRecord(id, data) {
  return request({
    url: `/api/diet-records/${id}`,
    method: 'put',
    data
  });
}

/**
 * Delete diet record
 * @param {number} id - Diet record ID
 * @returns {Promise} Delete response
 */
export function deleteDietRecord(id) {
  return request({
    url: `/api/diet-records/${id}`,
    method: 'delete'
  });
}

/**
 * Get daily diet summary
 * @param {string} date - Date (YYYY-MM-DD)
 * @returns {Promise} Daily summary with total calories
 */
export function getDailySummary(date) {
  return request({
    url: '/api/diet-records/summary',
    method: 'get',
    params: { date }
  });
}
