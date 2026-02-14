import apiClient from './api'

export default {
  // 用户注册
  register(userData) {
    return apiClient.post('/users/register', userData)
  },

  // 用户登录
  login(credentials) {
    return apiClient.post('/users/login', credentials)
  },

  // 获取用户信息
  getUserInfo(id) {
    return apiClient.get(`/users/${id}`)
  },

  // 更新用户信息
  updateUserInfo(id, userData) {
    return apiClient.put(`/users/${id}`, userData)
  },

  // 获取用户收藏列表
  getCollections(id, params) {
    return apiClient.get(`/users/${id}/collections`, { params })
  },

  // 获取用户浏览历史
  getViewHistory(id, params) {
    return apiClient.get(`/users/${id}/history`, { params })
  },
}
