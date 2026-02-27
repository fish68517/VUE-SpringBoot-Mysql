import request from './request'

export function addFavorite(seatId) {
  return request.post(`/favorite/add/${seatId}`)
}

export function removeFavorite(seatId) {
  return request.delete(`/favorite/remove/${seatId}`)
}

export function getMyFavorites() {
  return request.get('/favorite/list')
}

export function checkFavorite(seatId) {
  return request.get(`/favorite/check/${seatId}`)
}
