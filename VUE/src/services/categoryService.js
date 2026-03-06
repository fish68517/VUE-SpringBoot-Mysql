import api from './api'

export const categoryService = {
  // Get all categories
  getAllCategories() {
    return api.get('/api/categories')
  },

  // Get category by id
  getCategoryById(id) {
    return api.get(`/api/categories/${id}`)
  },

  // Get category requirements by id
  getCategoryRequirements(id) {
    return api.get(`/api/categories/${id}/requirements`)
  },

  // Create a new category
  createCategory(categoryData) {
    return api.post('/api/categories', categoryData)
  },

  // Update category
  updateCategory(id, categoryData) {
    return api.put(`/api/categories/${id}`, categoryData)
  },

  // Delete category
  deleteCategory(id) {
    return api.delete(`/api/categories/${id}`)
  }
}
