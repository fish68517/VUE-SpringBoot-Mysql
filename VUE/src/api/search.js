import request from '../utils/request';

/**
 * Search across content types
 * @param {string} query - Search query string
 * @param {string} type - Content type filter (all, resource, post, coach)
 * @returns {Promise} Search results organized by type
 */
export function search(query, type = 'all') {
  return request({
    url: '/api/search',
    method: 'get',
    params: {
      q: query,
      type
    }
  });
}
