import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import * as notificationService from '../services/notificationService';

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref([]);
  const unreadCount = ref(0);
  const loading = ref(false);
  const error = ref(null);

  /**
   * Fetch all notifications
   */
  const fetchNotifications = async () => {
    loading.value = true;
    error.value = null;
    try {
      const response = await notificationService.getNotifications();
      if (response.code === 200) {
        notifications.value = response.data || [];
        unreadCount.value = response.unreadCount || 0;
      } else {
        error.value = response.message;
      }
    } catch (err) {
      error.value = err.message;
      console.error('Error fetching notifications:', err);
    } finally {
      loading.value = false;
    }
  };

  /**
   * Fetch unread count
   */
  const fetchUnreadCount = async () => {
    try {
      const response = await notificationService.getUnreadCount();
      if (response.code === 200) {
        unreadCount.value = response.data || 0;
      }
    } catch (err) {
      console.error('Error fetching unread count:', err);
    }
  };

  /**
   * Mark notification as read
   */
  const markAsRead = async (notificationId) => {
    try {
      const response = await notificationService.markAsRead(notificationId);
      if (response.code === 200) {
        // Update local notification
        const notification = notifications.value.find(n => n.id === notificationId);
        if (notification) {
          notification.isRead = true;
        }
        // Decrease unread count
        if (unreadCount.value > 0) {
          unreadCount.value--;
        }
      }
    } catch (err) {
      console.error('Error marking notification as read:', err);
    }
  };

  /**
   * Get notification by id
   */
  const getNotificationById = async (notificationId) => {
    try {
      const response = await notificationService.getNotificationById(notificationId);
      if (response.code === 200) {
        return response.data;
      }
    } catch (err) {
      console.error('Error fetching notification:', err);
    }
    return null;
  };

  /**
   * Clear all notifications
   */
  const clearNotifications = () => {
    notifications.value = [];
    unreadCount.value = 0;
  };

  /**
   * Get unread notifications
   */
  const unreadNotifications = computed(() => {
    return notifications.value.filter(n => !n.isRead);
  });

  /**
   * Get read notifications
   */
  const readNotifications = computed(() => {
    return notifications.value.filter(n => n.isRead);
  });

  return {
    notifications,
    unreadCount,
    loading,
    error,
    fetchNotifications,
    fetchUnreadCount,
    markAsRead,
    getNotificationById,
    clearNotifications,
    unreadNotifications,
    readNotifications
  };
});
