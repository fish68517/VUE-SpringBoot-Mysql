import instance from './axios'

/**
 * 获取通知列表
 */
export const getNotificationList = () => {
  return instance.get('/notifications')
}

/**
 * 标记通知为已读
 */
export const markNotificationAsRead = (notificationId) => {
  return instance.put(`/notifications/${notificationId}/read`)
}
