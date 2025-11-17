import request from '../utils/request';

/**
 * Get comments for a dynamic post
 * @param {number} dynamicId - Dynamic post ID
 * @returns {Promise} List of comments
 */
export function getComments(dynamicId) {
  return request({
    url: `/api/dynamics/${dynamicId}/comments`,
    method: 'get'
  });
}

/**
 * Create new comment
 * @param {Object} data - Comment data
 * @param {string} data.content - Comment content
 * @param {number} data.dynamicId - Dynamic post ID
 * @returns {Promise} Created comment
 */
export function createComment(data) {
  return request({
    url: '/api/comments',
    method: 'post',
    data
  });
}

/**
 * Delete comment
 * @param {number} id - Comment ID
 * @returns {Promise} Delete response
 */
export function deleteComment(id) {
  return request({
    url: `/api/comments/${id}`,
    method: 'delete'
  });
}
