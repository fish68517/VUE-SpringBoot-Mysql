import request from '../utils/request';

/**
 * Get all users with optional filters
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @param {string} params.role - Role filter (user, coach, admin)
 * @param {string} params.username - Username search filter
 * @returns {Promise} Users list with pagination
 */
export function getUsers(params) {
  return request({
    url: '/api/admin/users',
    method: 'get',
    params
  });
}

/**
 * Get user by ID
 * @param {number} id - User ID
 * @returns {Promise} User details
 */
export function getUserById(id) {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'get'
  });
}

/**
 * Update user information
 * @param {number} id - User ID
 * @param {Object} data - Updated user data
 * @returns {Promise} Updated user
 */
export function updateUser(id, data) {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'put',
    data
  });
}

/**
 * Delete user account
 * @param {number} id - User ID
 * @returns {Promise} Delete response
 */
export function deleteUser(id) {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'delete'
  });
}

/**
 * Get system statistics
 * @returns {Promise} System-wide statistics
 */
export function getStatistics() {
  return request({
    url: '/api/admin/statistics',
    method: 'get'
  });
}

/**
 * Get content moderation queue
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @param {string} params.status - Status filter (pending, approved, rejected)
 * @returns {Promise} Moderation queue with pagination
 */
export function getModerationQueue(params) {
  return request({
    url: '/api/admin/moderation',
    method: 'get',
    params
  });
}

/**
 * Approve content
 * @param {number} id - Dynamic post ID
 * @returns {Promise} Approval response
 */
export function approveContent(id) {
  return request({
    url: `/api/admin/moderation/${id}/approve`,
    method: 'post'
  });
}

/**
 * Reject content
 * @param {number} id - Dynamic post ID
 * @param {string} reason - Rejection reason
 * @returns {Promise} Rejection response
 */
export function rejectContent(id, reason) {
  return request({
    url: `/api/admin/moderation/${id}/reject`,
    method: 'post',
    data: { reason }
  });
}
