import request from './request'

export const getProfile = id => request.get(`/users/${id}`)

export const updateProfile = (id, data) => request.put(`/users/${id}`, data)

export const getFavorites = params => request.get('/favorites', { params })

export const checkFavorite = params => request.get('/favorites/check', { params })

export const addFavorite = data => request.post('/favorites', data)

export const removeFavorite = id => request.delete(`/favorites/${id}`)

export const getFeedbacks = params => request.get('/feedback', { params })

export const submitFeedback = data => request.post('/feedback', data)
