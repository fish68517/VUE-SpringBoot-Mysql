import api from './api'

export const authService = {
  login(username, password) {
    return api.post('/auth/login', { username, password })
  },

  register(userData) {
    return api.post('/auth/register', userData)
  },

  logout() {
    return api.post('/auth/logout')
  },

  getCurrentUser() {
    return api.get('/auth/current-user')
  }
}
