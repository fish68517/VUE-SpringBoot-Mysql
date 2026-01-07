import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
// 请求拦截器
request.interceptors.request.use(
    config => {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
      }

      // 在请求发送前添加 userId
      config.params = {
        ...config.params,
        userId: '2' // 替换为你的默认 userId
      };

      return config;
    },
    error => {
      return Promise.reject(error);
    }
);

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    ElMessage.error(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)

// 用户相关接口
export const userApi = {
  login: (data) => request.post('/users/login', data),
  logout: () => request.post('/users/logout'),
  register: (data) => request.post('/users/register', data),
  getUserInfo: () => request.get('/users/info'),
  updateUserInfo: (data) => request.put('/users/info', data),
  createUser: (data) => request.post('/users', data),
  getUsers(params) {
        return request.get('/users', { params })
    }
}

// 菜品相关接口
export const recipeApi = {
  getCategories: () => request.get('/categories'),
  getRecipeList: (params) => request.get('/recipes', { params }),
  getRecipeById: (id) => request.get(`/recipes/${id}`),
  getRecipesByCategory: (categoryId) => request.get(`/recipes/category/${categoryId}`),
  searchRecipes: (keyword) => request.get('/recipes/search', { 
    params: { keyword }  // 使用 params 传递查询参数
  })
}

// 购物车相关接口
export const cartApi = {
  getCartList: () => request.get('/cart/user'),
  addToCart: (data) => request.post('/cart', data),
  updateCartItem: (id, data) => request.put(`/cart/${id}`, data),
  removeFromCart: (id) => request.delete(`/cart/${id}`),
  clearCart: () => request.delete('/cart/user')
}

// 订单相关接口
export const orderApi = {
  getOrderList: () => request.get('/orders/user'),
  getOrderDetail: (id) => request.get(`/orders/${id}`),
  createOrder: (data) => request.post('/orders', data),
  updateOrderStatus: (id, status) => request.put(`/orders/${id}/status`, { status })
}

// 评价相关接口
export const reviewApi = {
  getRecipeReviews: (recipeId) => request.get(`/reviews/recipe/${recipeId}`),
  addReview: (data) => request.post('/reviews', data),
  updateReview: (id, data) => request.put(`/reviews/${id}`, data),
  deleteReview: (id) => request.delete(`/reviews/${id}`),
    getReviewList(params) {
        return request.get('/reviews', { params })
    }
}

// 统计相关接口
export const statsApi = {
  getDashboardStats: () => request.get('/stats/dashboard')
}

// 收藏相关接口
export const favoriteApi = {
  getFavorites: () => request.get('/favorites/user'),
  addFavorite: (recipeId) => request.post(`/favorites/${recipeId}`),
  removeFavorite: (recipeId) => request.delete(`/favorites/${recipeId}`),
  checkFavorite: (recipeId) => request.get(`/favorites/check/${recipeId}`)
}

// 推荐相关接口
export const recommendApi = {
  getRecommendedRecipes: () => request.get('/recommend')
}

// 评论相关接口
export const commentApi = {
  // 获取菜谱评论列表
  getCommentsByRecipe: (recipeId) => {
    return request({
      url: `/api/comments/recipe/${recipeId}`,
      method: 'get'
    })
  },
  
  // 添加评论
  addComment: (data) => {
    return request({
      url: '/api/comments',
      method: 'post',
      data: {
        recipeId: data.recipeId,
        content: data.content,
        rating: data.rating
      }
    })
  },
  
  // 删除评论
  deleteComment: (commentId) => {
    return request({
      url: `/api/comments/${commentId}`,
      method: 'delete'
    })
  },
  
  // 点赞评论
  likeComment: (commentId) => {
    return request({
      url: `/api/comments/${commentId}/like`,
      method: 'post'
    })
  },
  
  // 取消点赞评论
  unlikeComment: (commentId) => {
    return request({
      url: `/api/comments/${commentId}/like`,
      method: 'delete'
    })
  },
  
  // 检查是否已点赞评论
  checkCommentLike: (commentId) => {
    return request({
      url: `/api/comments/${commentId}/like`,
      method: 'get'
    })
  }
}




