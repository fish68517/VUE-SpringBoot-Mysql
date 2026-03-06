import api from './api'

export const reviewerService = {
  // Get reviewer profile information
  getProfile() {
    return api.get('/api/reviewers/profile')
  },

  // Update reviewer expertise areas and research directions
  updateProfile(profileData) {
    return api.put('/api/reviewers/profile', profileData)
  },

  // Change reviewer password
  changePassword(oldPassword, newPassword) {
    return api.post('/api/reviewers/change-password', {
      oldPassword,
      newPassword
    })
  }
}
