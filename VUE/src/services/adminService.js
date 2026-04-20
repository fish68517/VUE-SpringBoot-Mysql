import api from './api'

export const adminService = {
  getOverview() {
    return api.get('/admin/overview')
  },
  getUsers(page = 0, size = 10) {
    return api.get('/admin/users', { params: { page, size } })
  },
  getUser(id) {
    return api.get(`/admin/users/${id}`)
  },
  createUser(data) {
    return api.post('/admin/users', data)
  },
  updateUser(id, data) {
    return api.put(`/admin/users/${id}`, data)
  },
  deleteUser(id) {
    return api.delete(`/admin/users/${id}`)
  },
  getTravels(page = 0, size = 10) {
    return api.get('/admin/travels', { params: { page, size } })
  },
  updateTravel(id, data) {
    return api.put(`/admin/travels/${id}`, data)
  },
  deleteTravel(id) {
    return api.delete(`/admin/travels/${id}`)
  },
  getPlans(page = 0, size = 10) {
    return api.get('/admin/plans', { params: { page, size } })
  },
  updatePlan(id, data) {
    return api.put(`/admin/plans/${id}`, data)
  },
  deletePlan(id) {
    return api.delete(`/admin/plans/${id}`)
  },
  getComments(page = 0, size = 10) {
    return api.get('/admin/social/comments', { params: { page, size } })
  },
  deleteComment(id) {
    return api.delete(`/admin/social/comments/${id}`)
  },
  getLikes(page = 0, size = 10) {
    return api.get('/admin/social/likes', { params: { page, size } })
  },
  deleteLike(id) {
    return api.delete(`/admin/social/likes/${id}`)
  }
}
