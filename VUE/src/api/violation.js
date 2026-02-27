import request from './request'

export function getMyViolations() {
  return request.get('/violation/my')
}

export function getMyViolationCount() {
  return request.get('/violation/count')
}

export function getAllViolations() {
  return request.get('/violation/list')
}

export function handleViolation(id, status) {
  return request.put(`/violation/handle/${id}?status=${status}`)
}
