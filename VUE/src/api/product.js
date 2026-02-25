import client from './client'

export const productApi = {
  getProducts: (params) => client.get('/products', { params }),
  getProductDetail: (id) => client.get(`/products/${id}`),
  getDetail: (id, userId) => client.get(`/products/${id}`, { params: { userId } }),
  createProduct: (data) => client.post('/products', data),
  updateProduct: (id, data) => client.put(`/products/${id}`, data),
  deleteProduct: (id) => client.delete(`/products/${id}`)
}
