<template>
  <div class="communication-management">
    <h1>沟通管理</h1>
    
    <!-- Communication Type Tabs -->
    <el-card class="communication-card">
      <template #header>
        <div class="card-header">
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="与作者沟通" name="author">
              <template #label>
                <span>与作者沟通</span>
                <el-badge :value="authorUnreadCount" v-if="authorUnreadCount > 0" class="badge-margin" />
              </template>
            </el-tab-pane>
            <el-tab-pane label="与审稿人沟通" name="reviewer">
              <template #label>
                <span>与审稿人沟通</span>
                <el-badge :value="reviewerUnreadCount" v-if="reviewerUnreadCount > 0" class="badge-margin" />
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>
      </template>

      <!-- Author Communication Tab -->
      <div v-if="activeTab === 'author'" class="tab-content">
        <!-- Author Selection -->
        <div class="selection-section">
          <el-select
            v-model="selectedAuthorId"
            placeholder="选择作者"
            @change="loadAuthorMessages"
            clearable
            filterable
          >
            <el-option
              v-for="author in authorList"
              :key="author.id"
              :label="author.username"
              :value="author.id"
            />
          </el-select>
        </div>

        <!-- Messages Display -->
        <div v-if="selectedAuthorId" class="messages-section">
          <!-- Message List -->
          <div class="message-list">
            <div
              v-for="message in authorMessages"
              :key="message.id"
              class="message-item"
              :class="{ 'sent': message.senderId === currentUserId, 'received': message.senderId !== currentUserId }"
            >
              <div class="message-header">
                <span class="sender-name">{{ message.senderName }}</span>
                <span class="message-time">{{ formatTime(message.createdAt) }}</span>
              </div>
              <div class="message-content">
                {{ message.content }}
              </div>
            </div>
          </div>

          <!-- Message Input -->
          <div class="message-input-section">
            <el-input
              v-model="authorMessageContent"
              type="textarea"
              :rows="3"
              placeholder="输入消息内容..."
              @keyup.ctrl.enter="sendAuthorMessage"
            />
            <el-button
              type="primary"
              @click="sendAuthorMessage"
              :loading="sendingAuthorMessage"
              style="margin-top: 10px"
            >
              发送消息
            </el-button>
          </div>
        </div>

        <!-- Empty State -->
        <el-empty v-else description="请选择作者" />
      </div>

      <!-- Reviewer Communication Tab -->
      <div v-if="activeTab === 'reviewer'" class="tab-content">
        <!-- Reviewer Selection -->
        <div class="selection-section">
          <el-select
            v-model="selectedReviewerId"
            placeholder="选择审稿人"
            @change="loadReviewerMessages"
            clearable
            filterable
          >
            <el-option
              v-for="reviewer in reviewerList"
              :key="reviewer.id"
              :label="reviewer.username"
              :value="reviewer.id"
            />
          </el-select>
        </div>

        <!-- Messages Display -->
        <div v-if="selectedReviewerId" class="messages-section">
          <!-- Message List -->
          <div class="message-list">
            <div
              v-for="message in reviewerMessages"
              :key="message.id"
              class="message-item"
              :class="{ 'sent': message.senderId === currentUserId, 'received': message.senderId !== currentUserId }"
            >
              <div class="message-header">
                <span class="sender-name">{{ message.senderName }}</span>
                <span class="message-time">{{ formatTime(message.createdAt) }}</span>
              </div>
              <div class="message-content">
                {{ message.content }}
              </div>
            </div>
          </div>

          <!-- Message Input -->
          <div class="message-input-section">
            <el-input
              v-model="reviewerMessageContent"
              type="textarea"
              :rows="3"
              placeholder="输入消息内容..."
              @keyup.ctrl.enter="sendReviewerMessage"
            />
            <el-button
              type="primary"
              @click="sendReviewerMessage"
              :loading="sendingReviewerMessage"
              style="margin-top: 10px"
            >
              发送消息
            </el-button>
          </div>
        </div>

        <!-- Empty State -->
        <el-empty v-else description="请选择审稿人" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { messageService } from '../../services/messageService'
import { editorService } from '../../services/editorService'
import { useUserStore } from '../../stores/userStore'

const userStore = useUserStore()
const currentUserId = computed(() => userStore.user?.id)

const activeTab = ref('author')
const selectedAuthorId = ref(null)
const selectedReviewerId = ref(null)
const authorList = ref([])
const reviewerList = ref([])
const authorMessages = ref([])
const reviewerMessages = ref([])
const authorMessageContent = ref('')
const reviewerMessageContent = ref('')
const sendingAuthorMessage = ref(false)
const sendingReviewerMessage = ref(false)
const authorUnreadCount = ref(0)
const reviewerUnreadCount = ref(0)

// Load initial data
onMounted(async () => {
  await loadAuthorList()
  await loadReviewerList()
})

// Load list of authors - get from all users with AUTHOR role
const loadAuthorList = async () => {
  try {
    // 假设你在 editorService 中有一个 getAllAuthors 方法
    // 如果没有，请在 src/services/editorService.js 中加上: 
    // getAllAuthors() { return api.get('/api/editors/authors') } // 替换为真实路径
    const response = await editorService.getAllAuthors() 
    
    // 修复数据层级解析
    if (response.data && response.data.code === 200) {
      authorList.value = response.data.data || []
    }
  } catch (error) {
    console.error('Failed to load authors:', error)
  }
}

// Load list of reviewers
const loadReviewerList = async () => {
  try {
    const response = await editorService.getAllReviewers()
    // 修复数据层级解析：从 response.code 改为 response.data.code
    if (response.data && response.code === 200) {
      reviewerList.value = response.data || []
    }
  } catch (error) {
    console.error('Failed to load reviewers:', error)
  }
}

// Load messages with selected author
const loadAuthorMessages = async () => {
  if (!selectedAuthorId.value) {
    authorMessages.value = []
    return
  }

  try {
    const response = await messageService.getMessagesWithAuthor(selectedAuthorId.value)
    // 修复数据解析
    if (response.data && response.data.code === 200) {
      authorMessages.value = response.data.data || []
      // Scroll to bottom
      setTimeout(() => {
        const messageList = document.querySelector('.message-list')
        if (messageList) {
          messageList.scrollTop = messageList.scrollHeight
        }
      }, 100)
    }
  } catch (error) {
    ElMessage.error('加载消息失败')
    console.error(error)
  }
}

// Load messages with selected reviewer
const loadReviewerMessages = async () => {
  if (!selectedReviewerId.value) {
    reviewerMessages.value = []
    return
  }

  try {
    const response = await messageService.getMessagesWithReviewer(selectedReviewerId.value)
    // 修复数据解析
    if (response.data && response.data.code === 200) {
      reviewerMessages.value = response.data.data || []
      // Scroll to bottom
      setTimeout(() => {
        const messageList = document.querySelector('.message-list')
        if (messageList) {
          messageList.scrollTop = messageList.scrollHeight
        }
      }, 100)
    }
  } catch (error) {
    ElMessage.error('加载消息失败')
    console.error(error)
  }
}

// Send message to author
const sendAuthorMessage = async () => {
  if (!authorMessageContent.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  sendingAuthorMessage.value = true
  try {
    const response = await messageService.sendMessage({
      recipientId: selectedAuthorId.value,
      content: authorMessageContent.value,
      type: 'COMMUNICATION'
    })

    // 修复数据解析
    if (response.data && response.data.code === 200) {
      authorMessages.value.push(response.data.data) // 将返回的新消息推入数组
      authorMessageContent.value = ''
      ElMessage.success('消息已发送')
      // Scroll to bottom
      setTimeout(() => {
        const messageList = document.querySelector('.message-list')
        if (messageList) {
          messageList.scrollTop = messageList.scrollHeight
        }
      }, 100)
    } else {
      ElMessage.error(response.data?.message || '发送失败')
    }
  } catch (error) {
    ElMessage.error('发送消息失败')
    console.error(error)
  } finally {
    sendingAuthorMessage.value = false
  }
}

// Send message to reviewer
const sendReviewerMessage = async () => {
  if (!reviewerMessageContent.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  sendingReviewerMessage.value = true
  try {
    const response = await messageService.sendMessage({
      recipientId: selectedReviewerId.value,
      content: reviewerMessageContent.value,
      type: 'COMMUNICATION'
    })

    // 修复数据解析
    if (response.data && response.data.code === 200) {
      reviewerMessages.value.push(response.data.data) // 将返回的新消息推入数组
      reviewerMessageContent.value = ''
      ElMessage.success('消息已发送')
      // Scroll to bottom
      setTimeout(() => {
        const messageList = document.querySelector('.message-list')
        if (messageList) {
          messageList.scrollTop = messageList.scrollHeight
        }
      }, 100)
    } else {
      ElMessage.error(response.data?.message || '发送失败')
    }
  } catch (error) {
    ElMessage.error('发送消息失败')
    console.error(error)
  } finally {
    sendingReviewerMessage.value = false
  }
}

// Handle tab change
const handleTabChange = (tabName) => {
  if (tabName === 'author') {
    selectedAuthorId.value = null
    authorMessages.value = []
  } else if (tabName === 'reviewer') {
    selectedReviewerId.value = null
    reviewerMessages.value = []
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
.communication-management {
  padding: 20px;
}

.communication-management h1 {
  margin-bottom: 20px;
  color: #333;
}

.communication-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.badge-margin {
  margin-left: 8px;
}

.tab-content {
  padding: 20px 0;
}

.selection-section {
  margin-bottom: 20px;
}

.selection-section :deep(.el-select) {
  width: 300px;
}

.messages-section {
  display: flex;
  flex-direction: column;
  height: 500px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background-color: #fafafa;
}

.message-item {
  display: flex;
  flex-direction: column;
  max-width: 70%;
  padding: 12px;
  border-radius: 4px;
  word-break: break-word;
}

.message-item.sent {
  align-self: flex-end;
  background-color: #409eff;
  color: white;
}

.message-item.received {
  align-self: flex-start;
  background-color: #e0e0e0;
  color: #333;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
}

.sender-name {
  font-weight: 600;
}

.message-time {
  opacity: 0.7;
  margin-left: 8px;
}

.message-content {
  line-height: 1.6;
}

.message-input-section {
  padding: 16px;
  border-top: 1px solid #e0e0e0;
  background-color: white;
}

.message-input-section :deep(.el-textarea) {
  margin-bottom: 10px;
}
</style>
