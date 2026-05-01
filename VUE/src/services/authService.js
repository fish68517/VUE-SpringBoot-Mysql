import api from './api'

export const authService = {
  login(username, password) {
    return api.post('/api/auth/login', { username, password })
  },

  register(userData) {
    return api.post('/api/auth/register', userData)
  },

  logout() {
    return api.post('/api/auth/logout')
  },

  getCurrentUser() {
    return api.get('/api/auth/current-user')
  }
}
