<template>
  <div class="message-center">
    <h1>消息中心</h1>
    
    <!-- Message List -->
    <el-card class="message-card">
      <template #header>
        <div class="card-header">
          <span>消息列表</span>
          <el-button 
            v-if="messages.length > 0" 
            type="primary" 
            text 
            @click="markAllAsRead"
            :loading="markingAllAsRead"
          >
            全部标记为已读
          </el-button>
        </div>
      </template>

      <!-- Empty State -->
      <el-empty 
        v-if="messages.length === 0" 
        description="暂无消息"
      />

      <!-- Message List -->
      <div v-else class="message-list">
        <div 
          v-for="message in messages" 
          :key="message.id"
          class="message-item"
          :class="{ 'unread': !message.isRead }"
        >
          <div class="message-header">
            <div class="sender-info">
              <span class="sender-name">{{ message.senderName }}</span>
              <el-tag 
                :type="message.type === 'NOTIFICATION' ? 'info' : 'success'"
                size="small"
              >
                {{ message.type === 'NOTIFICATION' ? '系统通知' : '编辑消息' }}
              </el-tag>
              <span v-if="!message.isRead" class="unread-badge">未读</span>
            </div>
            <span class="message-time">{{ formatTime(message.createdAt) }}</span>
          </div>
          
          <div class="message-content">
            {{ message.content }}
          </div>

          <div class="message-actions">
            <el-button 
              v-if="!message.isRead"
              type="primary" 
              text 
              size="small"
              @click="markAsRead(message.id)"
              :loading="markingMessageId === message.id"
            >
              标记为已读
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { messageService } from '../../services/messageService'
import { useMessageStore } from '../../stores/messageStore'

const messageStore = useMessageStore()
const messages = ref([])
const markingMessageId = ref(null)
const markingAllAsRead = ref(false)

// Load messages on component mount
onMounted(async () => {
  await loadMessages()
})

// Load messages from API
const loadMessages = async () => {
  try {
    const response = await messageService.getMessages()
    if (response.code === 200) {
      messages.value = response.data
      messageStore.setMessages(response.data)
    } else {
      ElMessage.error(response.message || '加载消息失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '加载消息失败')
    console.error(error)
  }
}

// Mark a single message as read
const markAsRead = async (messageId) => {
  markingMessageId.value = messageId
  try {
    const response = await messageService.markMessageAsRead(messageId)
    if (response.code === 200) {
      // Update local message state
      const message = messages.value.find(m => m.id === messageId)
      if (message) {
        message.isRead = true
      }
      messageStore.markAsRead(messageId)
      ElMessage.success('消息已标记为已读')
    } else {
      ElMessage.error(response.message || '标记失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '标记消息失败')
    console.error(error)
  } finally {
    markingMessageId.value = null
  }
}

// Mark all messages as read
const markAllAsRead = async () => {
  markingAllAsRead.value = true
  try {
    const unreadMessages = messages.value.filter(m => !m.isRead)
    
    for (const message of unreadMessages) {
      await messageService.markMessageAsRead(message.id)
      message.isRead = true
      messageStore.markAsRead(message.id)
    }
    
    ElMessage.success('所有消息已标记为已读')
  } catch (error) {
    ElMessage.error(error.message || '标记消息失败')
    console.error(error)
  } finally {
    markingAllAsRead.value = false
  }
}

// Format timestamp to readable format
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  
  const date = new Date(timestamp)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMins < 1) {
    return '刚刚'
  } else if (diffMins < 60) {
    return `${diffMins}分钟前`
  } else if (diffHours < 24) {
    return `${diffHours}小时前`
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}
</script>

<style scoped>
.message-center {
  padding: 20px;
}

.message-center h1 {
  margin-bottom: 20px;
  color: #333;
}

.message-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  padding: 16px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background-color: #fafafa;
  transition: all 0.3s ease;
}

.message-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.message-item.unread {
  background-color: #f0f9ff;
  border-left: 4px solid #409eff;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.sender-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sender-name {
  font-weight: 600;
  color: #333;
}

.unread-badge {
  display: inline-block;
  padding: 2px 8px;
  background-color: #f56c6c;
  color: white;
  border-radius: 3px;
  font-size: 12px;
  font-weight: 600;
}

.message-time {
  color: #999;
  font-size: 12px;
}

.message-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
  word-break: break-word;
}

.message-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
