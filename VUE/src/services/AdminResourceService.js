import apiClient from './api'

export default {
  // ===== 作品管理 =====
  // 获取作品列表
  getArtworks(params) {
    return apiClient.get('/admin/artworks', { params })
  },

  // 上传作品
  uploadArtwork(artworkData) {
    return apiClient.post('/admin/artworks', artworkData)
  },

  // 编辑作品
  updateArtwork(id, artworkData) {
    return apiClient.put(`/admin/artworks/${id}`, artworkData)
  },

  // 删除作品
  deleteArtwork(id) {
    return apiClient.delete(`/admin/artworks/${id}`)
  },

  // 审核作品（批准）
  approveArtwork(id) {
    return apiClient.post(`/admin/artworks/${id}/approve`)
  },

  // 拒绝作品
  rejectArtwork(id, rejectData) {
    return apiClient.post(`/admin/artworks/${id}/reject`, rejectData)
  },

  // 下架作品
  offlineArtwork(id) {
    return apiClient.post(`/admin/artworks/${id}/offline`)
  },

  // ===== 资讯管理 =====
  // 获取资讯列表
  getNews(params) {
    return apiClient.get('/admin/news', { params })
  },

  // 发布资讯
  publishNews(newsData) {
    return apiClient.post('/admin/news', newsData)
  },

  // 编辑资讯
  updateNews(id, newsData) {
    return apiClient.put(`/admin/news/${id}`, newsData)
  },

  // 删除资讯
  deleteNews(id) {
    return apiClient.delete(`/admin/news/${id}`)
  },

  // ===== 活动管理 =====
  // 获取活动列表
  getActivities(params) {
    return apiClient.get('/admin/activities', { params })
  },

  // 发布活动
  publishActivity(activityData) {
    return apiClient.post('/admin/activities', activityData)
  },

  // 编辑活动
  updateActivity(id, activityData) {
    return apiClient.put(`/admin/activities/${id}`, activityData)
  },

  // 删除活动
  deleteActivity(id) {
    return apiClient.delete(`/admin/activities/${id}`)
  },
}
