import api from './api'

export const authorService = {
  // Get author profile information
  getProfile() {
    return api.get('/api/authors/profile')
  },

  // Update author profile information
  updateProfile(profileData) {
    return api.put('/api/authors/profile', profileData)
  },

  // Change author password
  changePassword(oldPassword, newPassword) {
    return api.post('/api/authors/change-password', {
      oldPassword,
      newPassword
    })
  }
}
