import apiClient from './client'

export const productAPI = {
  getProducts(params) {
    return apiClient.get('/products', { params })
  },

  getProductById(id) {
    return apiClient.get(`/products/${id}`)
  },

  createProduct(data) {
    return apiClient.post('/products', data)
  },

  updateProduct(id, data) {
    return apiClient.put(`/products/${id}`, data)
  },

  deleteProduct(id) {
    return apiClient.delete(`/products/${id}`)
  },
}
