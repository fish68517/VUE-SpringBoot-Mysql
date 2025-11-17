<template>
  <div class="comment-list">
    <div class="comment-header">
      <h3>Comments ({{ comments.length }})</h3>
    </div>

    <div v-if="comments.length > 0" class="comments">
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="comment-item"
      >
        <el-avatar :size="36" :src="comment.user?.avatar || '/default-avatar.png'">
          {{ comment.user?.username?.charAt(0).toUpperCase() }}
        </el-avatar>
        
        <div class="comment-content">
          <div class="comment-header-info">
            <span class="comment-username">{{ comment.user?.username }}</span>
            <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
          </div>
          <p class="comment-text">{{ comment.content }}</p>
        </div>

        <el-button
          v-if="isOwnComment(comment)"
          type="danger"
          text
          :icon="Delete"
          @click="handleDelete(comment.id)"
        />
      </div>
    </div>

    <el-empty
      v-else
      description="No comments yet. Be the first to comment!"
      :image-size="100"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/store/modules/auth'
import { Delete } from '@element-plus/icons-vue'
import { deleteComment } from '@/api/comment'
import { showSuccess, showError, confirmDelete } from '@/utils/feedback'

const props = defineProps({
  comments: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['refresh'])

const authStore = useAuthStore()

const isOwnComment = (comment) => {
  return authStore.currentUser?.id === comment.user?.id
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return 'Just now'
  if (minutes < 60) return `${minutes}m ago`
  if (hours < 24) return `${hours}h ago`
  if (days < 7) return `${days}d ago`
  
  return date.toLocaleDateString()
}

const handleDelete = async (commentId) => {
  try {
    await confirmDelete('this comment')
    
    await deleteComment(commentId)
    showSuccess('Comment deleted successfully')
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      showError('Failed to delete comment')
    }
  }
}
</script>

<style scoped>
.comment-list {
  margin-top: 24px;
}

.comment-header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.comment-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.comments {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  background-color: #f5f7fa;
  transition: background-color 0.2s;
}

.comment-item:hover {
  background-color: #ecf0f5;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.comment-username {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
}

@media (max-width: 768px) {
  .comment-item {
    padding: 8px;
  }

  .comment-username {
    font-size: 13px;
  }

  .comment-text {
    font-size: 13px;
  }
}
</style>
