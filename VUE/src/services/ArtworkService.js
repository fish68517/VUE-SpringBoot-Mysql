import apiClient from './api'

export default {
  // 获取作品列表
  getArtworks(params) {
    return apiClient.get('/artworks', { params })
  },

  // 获取作品详情
  getArtworkDetail(id) {
    return apiClient.get(`/artworks/${id}`)
  },

  // 获取作品分类
  getCategories() {
    return apiClient.get('/artworks/categories/all')
  },

  // 记录作品浏览
  recordView(id, userId) {
    const params = {}
    if (userId) {
      params.userId = userId
    }
    return apiClient.post(`/artworks/${id}/view`, null, { params })
  },

  // 收藏作品
  collectArtwork(id, userId) {
    return apiClient.post(`/artworks/${id}/collect`, null, { params: { userId } })
  },

  // 取消收藏
  uncollectArtwork(id, userId) {
    return apiClient.delete(`/artworks/${id}/collect`, { params: { userId } })
  },
  // 获取收藏状态
  getCollectStatus(id, userId) {
    return apiClient.get(`/artworks/${id}/collect`, { params: { userId } })
  },
}
