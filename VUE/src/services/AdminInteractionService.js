import apiClient from './api'

export default {
  // ===== 评论管理 =====
  // 获取评论列表
  getComments(params) {
    return apiClient.get('/admin/comments', { params })
  },

  // 删除评论
  deleteComment(id) {
    return apiClient.delete(`/admin/comments/${id}`)
  },

  // 回复评论
  replyComment(id, replyData) {
    return apiClient.post(`/admin/comments/${id}/reply`, replyData)
  },

  // ===== 话题管理 =====
  // 获取话题列表
  getTopics(params) {
    return apiClient.get('/admin/topics', { params })
  },

  // 置顶话题
  pinTopic(id) {
    return apiClient.post(`/admin/topics/${id}/pin`)
  },

  // 删除话题
  deleteTopic(id) {
    return apiClient.delete(`/admin/topics/${id}`)
  },

  // ===== 投票统计 =====
  // 获取投票列表
  getVotes(params) {
    return apiClient.get('/admin/votes', { params })
  },

  // 获取投票统计
  getVoteStats(id) {
    return apiClient.get(`/admin/votes/${id}/stats`)
  },

  // ===== 反馈处理 =====
  // 获取反馈列表
  getFeedback(params) {
    return apiClient.get('/admin/feedback', { params })
  },
}
