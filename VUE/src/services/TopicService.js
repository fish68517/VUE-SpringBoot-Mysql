import apiClient from './api'

export default {
  // 获取话题列表
  getTopics(params) {
    return apiClient.get('/topics', { params })
  },

  // 获取话题详情
  getTopicDetail(id) {
    return apiClient.get(`/topics/${id}`)
  },

  // 创建话题
  createTopic(topicData) {
    return apiClient.post('/topics', topicData)
  },

  // 编辑话题
  updateTopic(id, topicData) {
    return apiClient.put(`/topics/${id}`, topicData)
  },

  // 删除话题
  deleteTopic(id) {
    return apiClient.delete(`/topics/${id}`)
  },

  // 置顶话题
  pinTopic(id) {
    return apiClient.post(`/topics/${id}/pin`)
  },
}
