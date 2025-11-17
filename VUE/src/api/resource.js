import request from '../utils/request';

/**
 * Get fitness resources list
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number
 * @param {number} params.size - Page size
 * @param {string} params.contentType - Content type filter (video, article, document)
 * @returns {Promise} Resources list with pagination
 */
export function getResources(params) {
  return request({
    url: '/api/resources',
    method: 'get',
    params
  });
}

/**
 * Get resource by ID
 * @param {number} id - Resource ID
 * @returns {Promise} Resource details
 */
export function getResourceById(id) {
  return request({
    url: `/api/resources/${id}`,
    method: 'get'
  });
}

/**
 * Create new fitness resource
 * @param {Object} data - Resource data
 * @param {string} data.title - Resource title
 * @param {string} data.description - Resource description
 * @param {string} data.contentType - Content type (video, article, document)
 * @param {string} data.fileUrl - File URL (for video/document)
 * @param {string} data.content - Text content (for article)
 * @returns {Promise} Created resource
 */
export function createResource(data) {
  return request({
    url: '/api/resources',
    method: 'post',
    data
  });
}

/**
 * Update fitness resource
 * @param {number} id - Resource ID
 * @param {Object} data - Updated resource data
 * @returns {Promise} Updated resource
 */
export function updateResource(id, data) {
  return request({
    url: `/api/resources/${id}`,
    method: 'put',
    data
  });
}

/**
 * Delete fitness resource
 * @param {number} id - Resource ID
 * @returns {Promise} Delete response
 */
export function deleteResource(id) {
  return request({
    url: `/api/resources/${id}`,
    method: 'delete'
  });
}
