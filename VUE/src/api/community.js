import request from '../utils/request';

/**
 * Get community dynamics (posts)
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @returns {Promise} Dynamics list with pagination
 */
export function getDynamics(params) {
  return request({
    url: '/api/dynamics',
    method: 'get',
    params
  });
}

/**
 * Get dynamic post by ID
 * @param {number} id - Dynamic post ID
 * @returns {Promise} Dynamic post details
 */
export function getDynamicById(id) {
  return request({
    url: `/api/dynamics/${id}`,
    method: 'get'
  });
}

/**
 * Create new dynamic post
 * @param {Object} data - Post data
 * @param {string} data.content - Post content
 * @param {string} data.imageUrls - Comma-separated image URLs
 * @returns {Promise} Created dynamic post
 */
export function createDynamic(data) {
  return request({
    url: '/api/dynamics',
    method: 'post',
    data
  });
}

/**
 * Update dynamic post
 * @param {number} id - Dynamic post ID
 * @param {Object} data - Updated post data
 * @returns {Promise} Updated dynamic post
 */
export function updateDynamic(id, data) {
  return request({
    url: `/api/dynamics/${id}`,
    method: 'put',
    data
  });
}

/**
 * Delete dynamic post
 * @param {number} id - Dynamic post ID
 * @returns {Promise} Delete response
 */
export function deleteDynamic(id) {
  return request({
    url: `/api/dynamics/${id}`,
    method: 'delete'
  });
}

/**
 * Like a dynamic post
 * @param {number} id - Dynamic post ID
 * @returns {Promise} Like response
 */
export function likeDynamic(id) {
  return request({
    url: `/api/dynamics/${id}/like`,
    method: 'post'
  });
}

/**
 * Unlike a dynamic post
 * @param {number} id - Dynamic post ID
 * @returns {Promise} Unlike response
 */
export function unlikeDynamic(id) {
  return request({
    url: `/api/dynamics/${id}/like`,
    method: 'delete'
  });
}
