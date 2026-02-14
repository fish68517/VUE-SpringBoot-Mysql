import apiClient from './api'

export default {
  // 提交反馈
  submitFeedback(feedbackData) {
    return apiClient.post('/feedback', feedbackData)
  },

  // 获取反馈列表（管理员）
  getFeedback(params) {
    return apiClient.get('/feedback', { params })
  },
}
