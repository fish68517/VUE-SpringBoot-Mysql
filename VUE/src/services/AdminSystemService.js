import apiClient from './api'

export default {
  // 获取系统设置
  getSettings() {
    return apiClient.get('/admin/settings')
  },

  // 更新系统设置
  updateSettings(settingsData) {
    return apiClient.put('/admin/settings', settingsData)
  },

  // 执行数据备份
  backupData() {
    return apiClient.post('/admin/backup')
  },

  // 执行数据恢复
  restoreData(restoreData) {
    return apiClient.post('/admin/restore', restoreData)
  },

  // 获取访问统计数据
  getStatistics(params) {
    return apiClient.get('/admin/statistics', { params })
  },
}
