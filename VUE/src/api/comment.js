import client from './client'

export const commentApi = {
  getComments: (params) => client.get('/comments', { params }),
  createComment: (data) => client.post('/comments', data),
  approveComment: (id) => client.put(`/comments/${id}/approve`),
  rejectComment: (id) => client.put(`/comments/${id}/reject`),
  pinComment: (id) => client.put(`/comments/${id}/pin`),
  unpinComment: (id) => client.put(`/comments/${id}/unpin`),
  deleteComment: (id) => client.delete(`/comments/${id}`)
}
