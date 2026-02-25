import client from './client'

export const userApi = {
  register: (data) => client.post('/users/register', data),
  login: (data) => client.post('/users/login', data),
  getProfile: (userId) => client.get(`/users/${userId}`),
  updateProfile: (userId, data) => client.put(`/users/${userId}`, data),
  getUsers: (params) => client.get('/users', { params }),
  disableUser: (userId) => client.put(`/users/${userId}/disable`),
  enableUser: (userId) => client.put(`/users/${userId}/enable`),
  deleteUser: (userId) => client.delete(`/users/${userId}`)
}
