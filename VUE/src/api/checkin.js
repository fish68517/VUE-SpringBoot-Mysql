import request from '../utils/request';

/**
 * Perform daily check-in
 * @returns {Promise} Check-in response
 */
export function performCheckIn() {
  return request({
    url: '/api/checkins',
    method: 'post'
  });
}

/**
 * Get check-in history
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Check-in history list
 */
export function getCheckInHistory(params) {
  return request({
    url: '/api/checkins',
    method: 'get',
    params
  });
}

/**
 * Get check-in statistics
 * @returns {Promise} Check-in stats (total count, current streak, longest streak)
 */
export function getCheckInStats() {
  return request({
    url: '/api/checkins/stats',
    method: 'get'
  });
}
