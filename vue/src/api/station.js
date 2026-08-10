import request from './request'

export const getRegionTree = () => request.get('/regions/tree')

export const getStations = params => request.get('/stations', { params })

export const getStationDetail = id => request.get(`/stations/${id}`)

export const getStationPiles = id => request.get(`/stations/${id}/piles`)

export const getGuidePoints = id => request.get(`/stations/${id}/guide`)
