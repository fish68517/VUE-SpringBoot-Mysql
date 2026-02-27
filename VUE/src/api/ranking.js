import request from './request'

export function getMonthlyRanking(limit = 20) {
  return request.get('/ranking/monthly', { params: { limit } })
}

export function getTotalRanking(limit = 20) {
  return request.get('/ranking/total', { params: { limit } })
}

export function getMyRanking() {
  return request.get('/ranking/my')
}
