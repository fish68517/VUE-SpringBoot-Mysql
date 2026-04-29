import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    if (userInfo?.id) {
      config.params = {
        ...config.params,
        userId: userInfo.id
      }
    }

    return config
  },
  (error) => Promise.reject(error)
)

request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    ElMessage.error(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)

export const userApi = {
  login: (data) => request.post('/users/login', data),
  logout: () => request.post('/users/logout'),
  register: (data) => request.post('/users/register', data),
  getUserInfo: () => request.get('/users/info'),
  updateUserInfo: (data) => request.put('/users/info', data),
  createUser: (data) => request.post('/users', data),
  updateUser: (id, data) => request.put(`/users/${id}`, data),
  deleteUser: (id) => request.delete(`/users/${id}`),
  getUsers: (params) => request.get('/users', { params })
}

export const recipeApi = {
  getCategories: () => request.get('/categories'),
  getRecipeList: (params) => request.get('/recipes', { params }),
  getRecipeById: (id) => request.get(`/recipes/${id}`),
  getRecipesByCategory: (categoryId) => request.get(`/recipes/category/${categoryId}`),
  searchRecipes: (keyword) => request.get('/recipes/search', { params: { keyword } }),
  createRecipe: (data) => request.post('/recipes', data),
  updateRecipe: (data) => request.put(data.id ? `/recipes/${data.id}` : '/recipes', data),
  deleteRecipe: (id) => request.delete(`/recipes/${id}`)
}

export const categoryApi = {
  getCategories: () => request.get('/categories'),
  createCategory: (data) => request.post('/categories', data),
  updateCategory: (id, data) => request.put(`/categories/${id}`, data),
  deleteCategory: (id) => request.delete(`/categories/${id}`)
}

export const cartApi = {
  getCartList: () => request.get('/cart/user'),
  addToCart: (data) => request.post('/cart', data),
  updateCartItem: (id, data) => request.put(`/cart/${id}`, data),
  removeFromCart: (id) => request.delete(`/cart/${id}`),
  clearCart: () => request.delete('/cart/user')
}

export const orderApi = {
  getOrderList: (params) => {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (userInfo.role === 'admin') return request.get('/orders/admin', { params })
    if (userInfo.role === 'merchant') return request.get('/orders/merchant', { params })
    return request.get('/orders/user', { params })
  },
  getOrderDetail: (id) => request.get(`/orders/${id}`),
  getWaitingCount: () => request.get('/orders/waiting-count'),
  createOrder: (data) => request.post('/orders', data),
  updateOrderStatus: (id, status) => request.put(`/orders/${id}/status`, { status })
}

export const reviewApi = {
  getRecipeReviews: (recipeId) => request.get(`/reviews/recipe/${recipeId}`),
  addReview: (data) => request.post('/reviews', data),
  updateReview: (id, data) => request.put(`/reviews/${id}`, data),
  deleteReview: (id) => request.delete(`/reviews/${id}`),
  getReviewList: (params) => request.get('/reviews', { params })
}

export const statsApi = {
  getDashboardStats: () => request.get('/stats/dashboard')
}

export const favoriteApi = {
  getFavorites: () => request.get('/favorites/user'),
  addFavorite: (recipeId) => request.post(`/favorites/${recipeId}`),
  removeFavorite: (recipeId) => request.delete(`/favorites/${recipeId}`),
  checkFavorite: (recipeId) => request.get(`/favorites/check/${recipeId}`)
}

export const recommendApi = {
  getRecommendedRecipes: () => request.get('/recommend'),
  getHotRecipes: () => request.get('/recommend/hot')
}

export const windowApi = {
  getWindows: () => request.get('/windows')
}

export const commentApi = {
  getCommentsByRecipe: (recipeId) => request.get(`/comments/recipe/${recipeId}`),
  addComment: (data) => request.post('/comments', {
    recipeId: data.recipeId,
    content: data.content,
    rating: data.rating
  }),
  deleteComment: (commentId) => request.delete(`/comments/${commentId}`),
  likeComment: (commentId) => request.post(`/comments/${commentId}/like`),
  unlikeComment: (commentId) => request.delete(`/comments/${commentId}/like`),
  checkCommentLike: (commentId) => request.get(`/comments/${commentId}/like`)
}
