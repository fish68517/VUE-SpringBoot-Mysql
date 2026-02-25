import client from './client'

export const attractionApi = {
  getAttractions: (params) => client.get('/attractions', { params }),
  getAttractionDetail: (id) => client.get(`/attractions/${id}`),
  getDetail: (id, userId) => client.get(`/attractions/${id}`, { params: { userId } }),
  createAttraction: (data) => client.post('/attractions', data),
  updateAttraction: (id, data) => client.put(`/attractions/${id}`, data),
  deleteAttraction: (id) => client.delete(`/attractions/${id}`),
  getAttractionTags: (attractionId) => client.get(`/attractions/${attractionId}/tags`),
  addAttractionTag: (attractionId, data) => client.post(`/attractions/${attractionId}/tags`, data),
  removeAttractionTag: (attractionId, tagId) => client.delete(`/attractions/${attractionId}/tags/${tagId}`)
}
