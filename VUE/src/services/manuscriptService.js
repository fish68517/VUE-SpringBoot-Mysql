import api from './api'

export const manuscriptService = {
  // Submit a new manuscript
  submitManuscript(formData) {
    return api.post('/api/manuscripts', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // Get manuscript by id
  getManuscript(id) {
    return api.get(`/api/manuscripts/${id}`)
  },

  // Get manuscript detail with review history
  getManuscriptDetail(id) {
    return api.get(`/api/manuscripts/${id}/detail`)
  },

  // Get review history for a manuscript
  getReviewHistory(id) {
    return api.get(`/api/manuscripts/${id}/review-history`)
  },

  // Get all manuscripts for current author
  getAuthorManuscripts() {
    return api.get('/api/manuscripts/author/list')
  },

  // Get all manuscripts
  getAllManuscripts() {
    return api.get('/api/manuscripts')
  },

  // Update manuscript
  updateManuscript(id, formData) {
    return api.put(`/api/manuscripts/${id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // Delete manuscript
  deleteManuscript(id) {
    return api.delete(`/api/manuscripts/${id}`)
  }
}
