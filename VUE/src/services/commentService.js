import api from './api'

/**
 * Comment service - encapsulates all comment-related API calls
 */
export const commentService = {
  /**
   * Create a new comment
   * @param {Object} data - Comment data (travelRecordId, content)
   * @returns {Promise} Response containing created comment
   */
  createComment(data) {
    return api.post('/comments', data)
  },

  /**
   * Get comments for a travel record with pagination
   * @param {number} travelId - The travel record ID
   * @param {number} page - The page number (0-indexed)
   * @param {number} size - The page size
   * @returns {Promise} Response containing paginated comments
   */
  getCommentsByTravelRecord(travelId, page = 0, size = 10) {
    return api.get(`/comments/travels/${travelId}`, {
      params: {
        page,
        size
      }
    })
  },

  /**
   * Delete a comment
   * @param {number} commentId - The comment ID
   * @returns {Promise} Response confirming deletion
   */
  deleteComment(commentId) {
    return api.delete(`/comments/${commentId}`)
  }
}
