import apiClient from './api'

export default {
  // 获取轮播内容
  getCarousel() {
    return apiClient.get('/home/carousel')
  },

  // 获取核心功能导航
  getFeatures() {
    return apiClient.get('/home/features')
  },

  // 获取最新活动和公告
  getAnnouncements() {
    return apiClient.get('/home/announcements')
  },

  // 获取平台访问数据
  getStatistics() {
    return apiClient.get('/home/statistics')
  },
}
