import api from './api'

/**
 * Itinerary service - encapsulates all itinerary item-related API calls
 */
export const itineraryService = {
  /**
   * Create a new itinerary item for a travel plan
   * @param {number} planId - The plan ID
   * @param {Object} data - Itinerary item data (itemDate, itemType, title, description, location)
   * @returns {Promise} Response containing created itinerary item
   */
  createItineraryItem(planId, data) {
    return api.post(`/plans/${planId}/items`, data)
  },

  /**
   * Get all itinerary items for a travel plan
   * @param {number} planId - The plan ID
   * @returns {Promise} Response containing list of itinerary items
   */
  getItineraryItems(planId) {
    return api.get(`/plans/${planId}/items`)
  },

  /**
   * Update an itinerary item
   * @param {number} planId - The plan ID
   * @param {number} itemId - The item ID
   * @param {Object} data - Updated itinerary item data (itemDate, itemType, title, description, location)
   * @returns {Promise} Response containing updated itinerary item
   */
  updateItineraryItem(planId, itemId, data) {
    return api.put(`/plans/${planId}/items/${itemId}`, data)
  },

  /**
   * Delete an itinerary item
   * @param {number} planId - The plan ID
   * @param {number} itemId - The item ID
   * @returns {Promise} Response confirming deletion
   */
  deleteItineraryItem(planId, itemId) {
    return api.delete(`/plans/${planId}/items/${itemId}`)
  }
}
