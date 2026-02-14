import apiClient from './api'

export default {
  // 获取评论列表
  getComments(params) {
    return apiClient.get('/comments', { params })
  },

  // 发布评论
  postComment(commentData) {
    return apiClient.post('/comments', commentData)
  },

  // 删除评论
  deleteComment(id) {
    return apiClient.delete(`/comments/${id}`)
  },

  // 回复评论
  replyComment(id, replyData) {
    return apiClient.post(`/comments/${id}/reply`, replyData)
  },
}
