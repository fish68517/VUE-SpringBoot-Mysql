import api from './api'

/**
 * Travel service - encapsulates all travel record-related API calls
 */
export const travelService = {
  /**
   * Create a new travel record
   * @param {Object} data - Travel record data (title, destination, startDate, endDate, description, diaryContent, isPublic)
   * @returns {Promise} Response containing created travel record
   */
  createTravelRecord(data) {
    return api.post('/travels', data)
  },

  /**
   * Get user's travel records with pagination
   * @param {number} page - The page number (0-indexed)
   * @param {number} size - The page size
   * @returns {Promise} Response containing paginated travel records
   */
  getUserTravelRecords(page = 0, size = 10) {
    return api.get('/travels', {
      params: {
        page,
        size
      }
    })
  },

  /**
   * Get a travel record by ID
   * @param {number} recordId - The record ID
   * @returns {Promise} Response containing travel record details
   */
  getTravelRecordById(recordId) {
    return api.get(`/travels/${recordId}`)
  },

  /**
   * Get public travel records with pagination (for social feed)
   * @param {number} page - The page number (0-indexed)
   * @param {number} size - The page size
   * @returns {Promise} Response containing paginated public travel records
   */
  getPublicTravelRecords(page = 0, size = 10) {
    return api.get('/travels/public/feed', {
      params: {
        page,
        size
      }
    })
  },

  /**
   * Update a travel record
   * @param {number} recordId - The record ID
   * @param {Object} data - Updated travel record data (title, destination, startDate, endDate, description, diaryContent, isPublic)
   * @returns {Promise} Response containing updated travel record
   */
  updateTravelRecord(recordId, data) {
    return api.put(`/travels/${recordId}`, data)
  },

  /**
   * Delete a travel record
   * @param {number} recordId - The record ID
   * @returns {Promise} Response confirming deletion
   */
  deleteTravelRecord(recordId) {
    return api.delete(`/travels/${recordId}`)
  }
}
