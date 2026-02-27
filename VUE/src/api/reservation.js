import request from './request'

export function createReservation(data) {
  return request.post('/reservation/create', data)
}

export function cancelReservation(id) {
  return request.post(`/reservation/cancel/${id}`)
}

export function checkIn(id) {
  return request.post(`/reservation/checkin/${id}`)
}

export function checkOut(id) {
  return request.post(`/reservation/checkout/${id}`)
}

export function getMyReservations() {
  return request.get('/reservation/my')
}

export function getAllReservations(date) {
  return request.get('/reservation/list', {
    params: { date }
  })
}

export function changeReservation(id, data) {
  return request.post(`/reservation/change/${id}`, data)
}

export function getStatistics() {
  return request.get('/reservation/statistics')
}
