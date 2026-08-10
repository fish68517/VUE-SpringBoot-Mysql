import request from './request'

export const getRegionStatistics = params => request.get('/statistics/regions', { params })

export const getRegionTrend = params => request.get('/statistics/trend', { params })
