import api from './api'

export const userService = {
  // Get users by role
  getUsersByRole(role) {
    return api.get(`/api/users/by-role/${role}`)
  },

  // Get user by id
  getUserById(userId) {
    return api.get(`/api/users/${userId}`)
  }
}
