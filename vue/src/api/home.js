import request from './request'

export const getHomeOverview = () => request.get('/home/overview')

export const getRecommendedStations = (limit = 6) => request.get('/home/recommendations', { params: { limit } })

export const getPublishedNotices = params => request.get('/notices', { params })

export const getNoticeDetail = id => request.get(`/notices/${id}`)
