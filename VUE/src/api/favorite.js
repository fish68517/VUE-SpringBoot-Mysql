import client from './client'

export const favoriteApi = {
  getFavorites: (params) => client.get('/favorites', { params }),
  addFavorite: (data) => client.post('/favorites', data),
  removeFavorite: (id) => client.delete(`/favorites/${id}`),
  checkFavorite: (targetType, targetId) => client.get(`/favorites/check?targetType=${targetType}&targetId=${targetId}`)
}
