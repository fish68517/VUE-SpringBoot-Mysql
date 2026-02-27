import request from './request'

export function getSeatList() {
  return request.get('/seat/list')
}

export function getSeatsByArea(area) {
  return request.get(`/seat/area/${area}`)
}

export function getAvailableSeats(date, startTime, endTime) {
  return request.get('/seat/available', {
    params: { date, startTime, endTime }
  })
}

export function updateSeatStatus(seatId, status) {
  return request.put(`/seat/status/${seatId}?status=${status}`)
}

export function addSeat(data) {
  return request.post('/seat/add', data)
}

export function deleteSeat(seatId) {
  return request.delete(`/seat/${seatId}`)
}
