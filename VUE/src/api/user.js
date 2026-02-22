import apiClient from './client'

export const userAPI = {
  register(data) {
    return apiClient.post('/users/register', data)
  },

  login(data) {
    return apiClient.post('/users/login', data)
  },

  getProfile(id) {
    return apiClient.get(`/users/${id}`)
  },

  updateProfile(id, data) {
    return apiClient.put(`/users/${id}`, data)
  },

  deleteUser(id) {
    return apiClient.delete(`/users/${id}`)
  },

  changePassword(id, oldPassword, newPassword) {
    return apiClient.post(`/users/${id}/change-password`, {
      oldPassword,
      newPassword,
    })
  },

  checkUsernameExists(username) {
    return apiClient.get('/users/check/username', { params: { username } })
  },

  checkEmailExists(email) {
    return apiClient.get('/users/check/email', { params: { email } })
  },
}
