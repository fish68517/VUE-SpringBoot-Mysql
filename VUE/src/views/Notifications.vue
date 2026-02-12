<template>
  <div class="notifications-container">
    <div class="notifications-header">
      <h2>通知中心</h2>
      <el-button 
        v-if="unreadCount > 0" 
        type="primary" 
        size="small"
        @click="markAllAsRead"
      >
        全部标记为已读
      </el-button>
    </div>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="notifications.length === 0" class="empty-state">
      <el-empty description="暂无通知" />
    </div>

    <div v-else class="notifications-list">
      <div
        v-for="notification in notifications"
        :key="notification.id"
        class="notification-item"
        :class="{ unread: !notification.isRead }"
      >
        <div class="notification-content">
          <div class="notification-header">
            <span class="notification-type">{{ getNotificationType(notification.type) }}</span>
            <span class="notification-time">{{ formatTime(notification.createdAt) }}</span>
          </div>
          <div class="notification-message">{{ notification.content }}</div>
        </div>
        <div class="notification-actions">
          <el-button
            v-if="!notification.isRead"
            type="text"
            size="small"
            @click="handleMarkAsRead(notification.id)"
          >
            标记已读
          </el-button>
          <el-tag v-else type="success" size="small">已读</el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNotificationList, markNotificationAsRead } from '@/api/notification'

const notifications = ref([])
const loading = ref(false)

const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.isRead).length
})

// 获取通知类型的中文描述
const getNotificationType = (type) => {
  const typeMap = {
    'APPLICATION_RESULT': '申请结果',
    'WEEKLY_REJECTED': '周记打回',
    'TASK_DEADLINE': '任务截止提醒',
  }
  return typeMap[type] || type
}

// 格式化时间
const formatTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const now = new Date()
  const diff = now - date

  // 一分钟内
  if (diff < 60000) {
    return '刚刚'
  }
  // 一小时内
  if (diff < 3600000) {
    return Math.floor(diff / 60000) + '分钟前'
  }
  // 一天内
  if (diff < 86400000) {
    return Math.floor(diff / 3600000) + '小时前'
  }
  // 一周内
  if (diff < 604800000) {
    return Math.floor(diff / 86400000) + '天前'
  }

  // 显示具体日期
  return date.toLocaleDateString('zh-CN')
}

// 加载通知列表
const loadNotifications = async () => {
  loading.value = true
  try {
    const response = await getNotificationList()
    notifications.value = response.data || []
  } catch (error) {
    ElMessage.error('加载通知失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 标记单个通知为已读
const handleMarkAsRead = async (notificationId) => {
  try {
    await markNotificationAsRead(notificationId)
    const notification = notifications.value.find(n => n.id === notificationId)
    if (notification) {
      notification.isRead = true
    }
    ElMessage.success('已标记为已读')
  } catch (error) {
    ElMessage.error('标记失败')
    console.error(error)
  }
}

// 全部标记为已读
const markAllAsRead = async () => {
  const unreadNotifications = notifications.value.filter(n => !n.isRead)
  try {
    await Promise.all(
      unreadNotifications.map(n => markNotificationAsRead(n.id))
    )
    notifications.value.forEach(n => {
      n.isRead = true
    })
    ElMessage.success('已全部标记为已读')
  } catch (error) {
    ElMessage.error('标记失败')
    console.error(error)
  }
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.notifications-container {
  padding: 20px;
}

.notifications-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.notifications-header h2 {
  margin: 0;
  font-size: 20px;
}

.loading {
  padding: 20px;
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
}

.notifications-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #f5f7fa;
  border-left: 4px solid #dcdfe6;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.notification-item:hover {
  background-color: #f0f2f5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.notification-item.unread {
  background-color: #e6f7ff;
  border-left-color: #409eff;
}

.notification-content {
  flex: 1;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notification-type {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.notification-time {
  color: #909399;
  font-size: 12px;
}

.notification-message {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.notification-actions {
  margin-left: 16px;
  white-space: nowrap;
}
</style>
