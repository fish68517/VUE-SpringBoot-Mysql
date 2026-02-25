import client from './client'

export const routeApi = {
  getRoutes: (params) => client.get('/routes', { params }),
  getRouteDetail: (id) => client.get(`/routes/${id}`),
  createRoute: (data) => client.post('/routes', data),
  updateRoute: (id, data) => client.put(`/routes/${id}`, data),
  deleteRoute: (id) => client.delete(`/routes/${id}`),
  getRecommendedRoutes: (params) => client.get('/routes/recommended', { params })
}
