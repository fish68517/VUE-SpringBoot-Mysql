import request from './request'

export function createFeedback(data) {
  return request.post('/feedback/create', data)
}

export function getMyFeedbacks() {
  return request.get('/feedback/my')
}

export function getAllFeedbacks(status) {
  return request.get('/feedback/list', { params: { status } })
}

export function replyFeedback(id, reply) {
  return request.post(`/feedback/reply/${id}`, { reply })
}

export function updateFeedbackStatus(id, status) {
  return request.put(`/feedback/status/${id}?status=${status}`)
}
