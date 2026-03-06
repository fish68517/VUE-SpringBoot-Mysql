<template>
  <div class="notification-bell">
    <!-- Notification Bell Icon -->
    <el-badge :value="unreadCount" class="notification-badge">
      <el-button 
        type="primary" 
        icon="Bell" 
        circle 
        @click="toggleNotificationPanel"
      />
    </el-badge>

    <!-- Notification Panel -->
    <el-drawer
      v-model="showPanel"
      title="消息中心"
      direction="rtl"
      size="400px"
    >
      <div class="notification-panel">
        <!-- Tabs for filtering -->
        <el-tabs v-model="activeTab">
          <el-tab-pane label="全部消息" name="all">
            <div v-if="loading" class="loading">
              <el-skeleton :rows="3" animated />
            </div>
            <div v-else-if="notifications.length === 0" class="empty">
              <el-empty description="暂无消息" />
            </div>
            <div v-else class="notification-list">
              <div
                v-for="notification in notifications"
                :key="notification.id"
                class="notification-item"
                :class="{ unread: !notification.isRead }"
                @click="handleNotificationClick(notification)"
              >
                <div class="notification-header">
                  <span class="notification-type">{{ getNotificationType(notification.type) }}</span>
                  <span class="notification-time">{{ formatTime(notification.createdAt) }}</span>
                </div>
                <div class="notification-content">
                  {{ notification.content }}
                </div>
                <div v-if="!notification.isRead" class="unread-indicator" />
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="未读消息" name="unread">
            <div v-if="unreadNotifications.length === 0" class="empty">
              <el-empty description="暂无未读消息" />
            </div>
            <div v-else class="notification-list">
              <div
                v-for="notification in unreadNotifications"
                :key="notification.id"
                class="notification-item unread"
                @click="handleNotificationClick(notification)"
              >
                <div class="notification-header">
                  <span class="notification-type">{{ getNotificationType(notification.type) }}</span>
                  <span class="notification-time">{{ formatTime(notification.createdAt) }}</span>
                </div>
                <div class="notification-content">
                  {{ notification.content }}
                </div>
                <div class="unread-indicator" />
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useNotificationStore } from '../stores/notificationStore';
import { ElMessage } from 'element-plus';

const notificationStore = useNotificationStore();
const showPanel = ref(false);
const activeTab = ref('all');

const notifications = computed(() => notificationStore.notifications);
const unreadCount = computed(() => notificationStore.unreadCount);
const loading = computed(() => notificationStore.loading);
const unreadNotifications = computed(() => notificationStore.unreadNotifications);

/**
 * Toggle notification panel
 */
const toggleNotificationPanel = () => {
  showPanel.value = !showPanel.value;
  if (showPanel.value) {
    loadNotifications();
  }
};

/**
 * Load notifications
 */
const loadNotifications = async () => {
  await notificationStore.fetchNotifications();
};

/**
 * Handle notification click
 */
const handleNotificationClick = async (notification) => {
  if (!notification.isRead) {
    await notificationStore.markAsRead(notification.id);
  }
};

/**
 * Get notification type display name
 */
const getNotificationType = (type) => {
  const typeMap = {
    'NOTIFICATION': '系统通知',
    'COMMUNICATION': '编辑沟通'
  };
  return typeMap[type] || type;
};

/**
 * Format time
 */
const formatTime = (dateTime) => {
  if (!dateTime) return '';
  const date = new Date(dateTime);
  const now = new Date();
  const diff = now - date;
  
  // Less than 1 minute
  if (diff < 60000) {
    return '刚刚';
  }
  
  // Less than 1 hour
  if (diff < 3600000) {
    return Math.floor(diff / 60000) + '分钟前';
  }
  
  // Less than 1 day
  if (diff < 86400000) {
    return Math.floor(diff / 3600000) + '小时前';
  }
  
  // Format as date
  return date.toLocaleDateString('zh-CN');
};

/**
 * Initialize
 */
onMounted(() => {
  loadNotifications();
  // Refresh notifications every 30 seconds
  setInterval(() => {
    notificationStore.fetchUnreadCount();
  }, 30000);
});
</script>

<style scoped>
.notification-bell {
  position: relative;
}

.notification-badge {
  cursor: pointer;
}

.notification-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.notification-list {
  flex: 1;
  overflow-y: auto;
}

.notification-item {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
  position: relative;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-item.unread {
  background-color: #f0f9ff;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.notification-type {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
}

.notification-time {
  font-size: 12px;
  color: #c0c4cc;
}

.notification-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
  max-height: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.unread-indicator {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 8px;
  height: 8px;
  background-color: #409eff;
  border-radius: 50%;
}

.empty {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.loading {
  padding: 16px;
}
</style>
