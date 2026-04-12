import api from './api'

const likeService = {
  /**
   * Create a new like for a travel record
   * @param {number} travelRecordId - The travel record ID
   * @returns {Promise} - The API response
   */
  createLike(travelRecordId) {
    return api.post('/likes', {
      travelRecordId
    })
  },

  /**
   * Delete a like by ID
   * @param {number} likeId - The like ID
   * @returns {Promise} - The API response
   */
  deleteLike(likeId) {
    return api.delete(`/likes/${likeId}`)
  },

  /**
   * Delete a like by travel record ID
   * @param {number} travelRecordId - The travel record ID
   * @returns {Promise} - The API response
   */
  deleteLikeByTravelRecord(travelRecordId) {
    return api.delete(`/likes/travels/${travelRecordId}`)
  }
}

export default likeService
