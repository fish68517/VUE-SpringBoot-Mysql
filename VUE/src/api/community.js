import instance from './axios.js'

/**
 * Get post list with pagination
 * @param {number} page - Page number (default 1)
 * @param {number} pageSize - Page size (default 10)
 * @returns {Promise} API response with post list
 */
export const getPostList = (page = 1, pageSize = 10) => {
  return instance.get('/admin/posts', {
    params: {
      page,
      pageSize
    }
  })
}

/**
 * Get post details by id (includes comments and likes)
 * @param {number} id - Post id
 * @returns {Promise} API response with post details
 */
export const getPostById = (id) => {
  return instance.get(`/admin/posts/${id}`)
}

/**
 * Delete post by id
 * @param {number} id - Post id
 * @returns {Promise} API response
 */
export const deletePost = (id) => {
  return instance.delete(`/admin/posts/${id}`)
}

/**
 * Delete comment by id
 * @param {number} id - Comment id
 * @returns {Promise} API response
 */
export const deleteComment = (id) => {
  return instance.delete(`/admin/comments/${id}`)
}
