import api from './api'

export const statisticsService = {
  // Get submission statistics
  getSubmissionStats() {
    return api.get('/api/statistics/submissions')
  },

  // Get approval rate statistics
  getApprovalRateStats() {
    return api.get('/api/statistics/approval-rate')
  },

  // Get user activity statistics
  getUserActivityStats() {
    return api.get('/api/statistics/user-activity')
  },

  // Generate comprehensive statistics report
  generateReport() {
    return api.get('/api/statistics/report')
  }
}
