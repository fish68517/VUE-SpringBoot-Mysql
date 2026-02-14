import apiClient from './api'

export default {
  // 获取用户列表
  getUsers(params) {
    return apiClient.get('/admin/users', { params })
  },

  // 创建用户
  createUser(userData) {
    return apiClient.post('/admin/users', userData)
  },

  // 修改用户信息
  updateUser(id, userData) {
    return apiClient.put(`/admin/users/${id}`, userData)
  },

  // 删除用户
  deleteUser(id) {
    return apiClient.delete(`/admin/users/${id}`)
  },

  // 获取用户行为日志
  getUserLogs(id, params) {
    return apiClient.get(`/admin/users/${id}/logs`, { params })
  },

  // 发送消息推送
  sendMessage(messageData) {
    return apiClient.post('/admin/messages', messageData)
  },
}
