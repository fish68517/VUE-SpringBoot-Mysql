import request from './request'

export function getPublishedNotices() {
  return request.get('/notice/list')
}

export function getAllNotices() {
  return request.get('/notice/all')
}

export function getNoticeById(id) {
  return request.get(`/notice/${id}`)
}

export function createNotice(data) {
  return request.post('/notice/create', data)
}

export function updateNotice(data) {
  return request.put('/notice/update', data)
}

export function deleteNotice(id) {
  return request.delete(`/notice/${id}`)
}

export function updateNoticeStatus(id, status) {
  return request.put(`/notice/status/${id}?status=${status}`)
}
