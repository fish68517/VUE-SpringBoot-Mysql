import request from '../utils/request';

/**
 * Get user's resource collections
 * @returns {Promise} List of collected resources
 */
export function getCollections() {
  return request({
    url: '/api/collections',
    method: 'get'
  });
}

/**
 * Add resource to collection
 * @param {number} resourceId - Resource ID to collect
 * @returns {Promise} Collection response
 */
export function addCollection(resourceId) {
  return request({
    url: '/api/collections',
    method: 'post',
    data: { resourceId }
  });
}

/**
 * Remove resource from collection
 * @param {number} resourceId - Resource ID to remove
 * @returns {Promise} Remove response
 */
export function removeCollection(resourceId) {
  return request({
    url: `/api/collections/${resourceId}`,
    method: 'delete'
  });
}
