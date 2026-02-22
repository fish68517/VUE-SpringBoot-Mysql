import apiClient from './client'

export const recommendationAPI = {
  getRecommendations(warningId, params) {
    return apiClient.get(`/recommendations/${warningId}`, { params })
  },

  createRecommendation(data) {
    return apiClient.post('/recommendations', data)
  },

  acceptRecommendation(id) {
    return apiClient.put(`/recommendations/${id}`, { status: 'accepted' })
  },

  rejectRecommendation(id) {
    return apiClient.put(`/recommendations/${id}`, { status: 'rejected' })
  },
}
