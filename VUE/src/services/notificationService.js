import api from './api'

const API_BASE_URL = '/api/notifications'

/**
 * 获取当前用户全部通知
 */
export const getNotifications = async () => {
  return api.get(API_BASE_URL)
}

/**
 * 获取未读通知数量
 */
export const getUnreadCount = async () => {
  return api.get(`${API_BASE_URL}/unread-count`)
}

/**
 * 标记通知为已读
 */
export const markAsRead = async (notificationId) => {
  return api.post(`${API_BASE_URL}/${notificationId}/read`)
}

/**
 * 根据 ID 获取通知
 */
export const getNotificationById = async (notificationId) => {
  return api.get(`${API_BASE_URL}/${notificationId}`)
}
