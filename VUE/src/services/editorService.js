import api from './api'

export const editorService = {
  // Get editor profile information
  getProfile() {
    return api.get('/api/editors/profile')
  },

  // Update editor profile information
  updateProfile(profileData) {
    return api.put('/api/editors/profile', profileData)
  },

  // Change editor password
  changePassword(oldPassword, newPassword) {
    return api.post('/api/editors/change-password', {
      oldPassword,
      newPassword
    })
  },

  // Get pending manuscripts for initial review
  getPendingManuscripts() {
    return api.get('/api/editors/manuscripts/pending')
  },

  // Submit initial review for a manuscript
  submitInitialReview(manuscriptId, status, opinion) {
    return api.post(`/api/editors/manuscripts/${manuscriptId}/initial-review`, {
      status,
      opinion
    })
  },

  // Get all active reviewers
  getAllReviewers() {
    return api.get('/api/editors/reviewers')
  },

  // Assign reviewer to manuscript
  assignReviewer(manuscriptId, reviewerId) {
    return api.post(`/api/editors/manuscripts/${manuscriptId}/assign-reviewer`, {
      reviewerId
    })
  },

  // Get review progress for a manuscript
  getReviewProgress(manuscriptId) {
    return api.get(`/api/editors/manuscripts/${manuscriptId}/review-progress`)
  },

  // Get review opinions for a manuscript
  getReviewOpinions(manuscriptId) {
    return api.get(`/api/editors/manuscripts/${manuscriptId}/review-opinions`)
  },

  // Get accepted manuscripts for editing
  getAcceptedManuscripts() {
    return api.get('/api/editors/manuscripts/accepted')
  },

  // Edit manuscript content
  editManuscriptContent(manuscriptId, title, abstractText, content) {
    return api.put(`/api/editors/manuscripts/${manuscriptId}/edit-content`, {
      title,
      abstractText,
      content
    })
  },

  // Generate acceptance notification for a manuscript
  generateAcceptanceNotification(manuscriptId) {
    return api.post(`/api/editors/manuscripts/${manuscriptId}/generate-acceptance-notification`)
  }
}
