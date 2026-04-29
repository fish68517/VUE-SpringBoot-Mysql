<template>
  <div class="notifications-container">
    <div class="notifications-header">
      <h2>通知中心</h2>
      <div class="header-actions">
        <el-button
          v-if="isAdmin"
          type="success"
          size="small"
          @click="createDialogVisible = true"
        >
          + 发布通知
        </el-button>
        
        <el-button 
          v-if="unreadCount > 0" 
          type="primary" 
          size="small"
          @click="markAllAsRead"
        >
          全部标记为已读
        </el-button>
      </div>
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

    <el-dialog v-model="createDialogVisible" title="发布系统通知" width="500px" @close="resetCreateForm">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="接收用户ID" required>
          <el-input-number v-model="createForm.userId" :min="1" placeholder="请输入接收用户ID" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="通知类型" required>
          <el-select v-model="createForm.type" placeholder="请选择通知类型" style="width: 100%;">
            <el-option label="系统通知 (SYSTEM_NOTICE)" value="SYSTEM_NOTICE" />
            <el-option label="申请结果 (APPLICATION_RESULT)" value="APPLICATION_RESULT" />
            <el-option label="任务提醒 (TASK_DEADLINE)" value="TASK_DEADLINE" />
            <el-option label="周记打回 (WEEKLY_REJECTED)" value="WEEKLY_REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知内容" required>
          <el-input
            v-model="createForm.content"
            type="textarea"
            rows="4"
            placeholder="请输入通知详细内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNotification" :loading="submitting">发布</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'
import { getNotificationList, markNotificationAsRead, createNotificationApi } from '@/api/notification'

// 1. 获取用户信息和角色判断
const userStore = useUserStore()
const isAdmin = computed(() => userStore.user?.role === 'ADMIN')

const notifications = ref([])
const loading = ref(false)

// 发布通知的表单状态
const createDialogVisible = ref(false)
const submitting = ref(false)
const createForm = ref({
  userId: null,
  type: 'SYSTEM_NOTICE',
  content: ''
})

const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.isRead).length
})

const getNotificationType = (type) => {
  const typeMap = {
    'APPLICATION_RESULT': '申请结果',
    'WEEKLY_REJECTED': '周记打回',
    'TASK_DEADLINE': '任务截止提醒',
    'SYSTEM_NOTICE': '系统通知'
  }
  return typeMap[type] || type
}

const formatTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return date.toLocaleDateString('zh-CN')
}

const loadNotifications = async () => {
  loading.value = true
  try {
    const response = await getNotificationList()
    notifications.value = response.data || []
  } catch (error) {
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

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
  }
}

const markAllAsRead = async () => {
  const unreadNotifications = notifications.value.filter(n => !n.isRead)
  try {
    await Promise.all(unreadNotifications.map(n => markNotificationAsRead(n.id)))
    notifications.value.forEach(n => n.isRead = true)
    ElMessage.success('已全部标记为已读')
  } catch (error) {
    ElMessage.error('标记失败')
  }
}

// 重置表单
const resetCreateForm = () => {
  createForm.value = {
    userId: null,
    type: 'SYSTEM_NOTICE',
    content: ''
  }
}

// 提交新通知 (仅管理员可用)
const submitNotification = async () => {
  if (!createForm.value.userId) {
    ElMessage.warning('请输入接收通知的用户ID')
    return
  }
  if (!createForm.value.content.trim()) {
    ElMessage.warning('请输入通知内容')
    return
  }

  submitting.value = true
  try {
    await createNotificationApi(createForm.value)
    ElMessage.success('通知发布成功！')
    createDialogVisible.value = false
    resetCreateForm()
    // 重新拉取最新列表
    loadNotifications()
  } catch (error) {
    ElMessage.error('发布失败: ' + (error.response?.data?.message || error.message))
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.notifications-container { padding: 20px; }
.notifications-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.notifications-header h2 { margin: 0; font-size: 20px; }
.header-actions { display: flex; gap: 10px; } /* 按钮组间距 */
.loading { padding: 20px; }
.empty-state { padding: 40px 20px; text-align: center; }
.notifications-list { display: flex; flex-direction: column; gap: 12px; }
.notification-item { display: flex; justify-content: space-between; align-items: center; padding: 16px; background-color: #f5f7fa; border-left: 4px solid #dcdfe6; border-radius: 4px; transition: all 0.3s ease; }
.notification-item:hover { background-color: #f0f2f5; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }
.notification-item.unread { background-color: #e6f7ff; border-left-color: #409eff; }
.notification-content { flex: 1; }
.notification-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.notification-type { font-weight: 600; color: #303133; font-size: 14px; }
.notification-time { color: #909399; font-size: 12px; }
.notification-message { color: #606266; font-size: 14px; line-height: 1.5; }
.notification-actions { margin-left: 16px; white-space: nowrap; }
</style>