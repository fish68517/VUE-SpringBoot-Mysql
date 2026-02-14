import apiClient from './api'

export default {
  // 获取知识文章列表
  getKnowledge(params) {
    return apiClient.get('/knowledge', { params })
  },

  // 获取知识文章详情
  getKnowledgeDetail(id) {
    return apiClient.get(`/knowledge/${id}`)
  },

  // 获取知识分类
  getCategories() {
    return apiClient.get('/knowledge/categories/all')
  },

  // 记录知识浏览
  recordView(id) {
    return apiClient.post(`/knowledge/${id}/view`)
  },
}
