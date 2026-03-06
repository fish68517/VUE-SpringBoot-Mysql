import axios from 'axios';

const API_BASE_URL = '/api/notifications';

/**
 * Get all notifications for current user
 */
export const getNotifications = async () => {
  try {
    const response = await axios.get(API_BASE_URL);
    return response.data;
  } catch (error) {
    console.error('Error fetching notifications:', error);
    throw error;
  }
};

/**
 * Get unread notification count
 */
export const getUnreadCount = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/unread-count`);
    return response.data;
  } catch (error) {
    console.error('Error fetching unread count:', error);
    throw error;
  }
};

/**
 * Mark notification as read
 */
export const markAsRead = async (notificationId) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/${notificationId}/read`);
    return response.data;
  } catch (error) {
    console.error('Error marking notification as read:', error);
    throw error;
  }
};

/**
 * Get notification by id
 */
export const getNotificationById = async (notificationId) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/${notificationId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching notification:', error);
    throw error;
  }
};
