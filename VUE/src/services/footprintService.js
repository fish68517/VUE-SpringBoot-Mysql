import api from './api'

/**
 * Footprint service - encapsulates all map footprint-related API calls
 */
export const footprintService = {
  /**
   * Add a map footprint to a travel record
   * @param {Object} data - Footprint data (travelRecordId, locationName, latitude, longitude, visitDate)
   * @returns {Promise} Response containing created footprint
   */
  addFootprint(data) {
    return api.post(`/api/travels/${data.travelRecordId}/footprints`, data)
  },

  /**
   * Get all map footprints for a travel record
   * @param {number} travelRecordId - The travel record ID
   * @returns {Promise} Response containing list of footprints
   */
  getFootprints(travelRecordId) {
    return api.get(`/api/travels/${travelRecordId}/footprints`)
  },

  /**
   * Delete a map footprint
   * @param {number} footprintId - The footprint ID
   * @returns {Promise} Response confirming deletion
   */
  deleteFootprint(footprintId) {
    return api.delete(`/api/travels/footprints/${footprintId}`)
  }
}
