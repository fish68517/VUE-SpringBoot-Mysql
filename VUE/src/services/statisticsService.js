import api from './api'

/**
 * Statistics service - encapsulates all statistics-related API calls
 */
export const statisticsService = {
  /**
   * Get user statistics for dashboard
   * @returns {Promise} Response containing user statistics
   */
  getUserStatistics() {
    return api.get('/users/statistics/dashboard')
  }
}
