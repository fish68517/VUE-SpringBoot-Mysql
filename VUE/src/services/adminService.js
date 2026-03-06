import api from './api'

export const adminService = {
  // Get admin profile information
  getProfile() {
    return api.get('/api/admins/profile')
  },

  // Update admin profile information
  updateProfile(profileData) {
    return api.put('/api/admins/profile', profileData)
  },

  // Change admin password
  changePassword(oldPassword, newPassword) {
    return api.post('/api/admins/change-password', {
      oldPassword,
      newPassword
    })
  },

  // Get pending users awaiting approval
  getPendingUsers() {
    return api.get('/api/admins/users/pending')
  },

  // Approve user registration
  approveUser(userId) {
    return api.post(`/api/admins/users/${userId}/approve`)
  },

  // Reject user registration
  rejectUser(userId) {
    return api.post(`/api/admins/users/${userId}/reject`)
  },

  // Get all users with optional filtering
  getAllUsers(params) {
    return api.get('/api/admins/users', { params })
  },

  // Update user information
  updateUser(userId, userData) {
    return api.put(`/api/admins/users/${userId}`, userData)
  },

  // Delete user
  deleteUser(userId) {
    return api.delete(`/api/admins/users/${userId}`)
  },

  // Assign user role
  assignUserRole(userId, role) {
    return api.post(`/api/admins/users/${userId}/assign-role`, { role })
  }
}
