<template>
  <div class="comment-list">
    <h3>评论 ({{ totalComments }})</h3>

    <!-- Comment Input Section -->
    <div v-if="isLoggedIn" class="comment-input-section">
      <div class="input-container">
        <textarea
          v-model="newComment"
          placeholder="发表你的评论..."
          class="comment-textarea"
          :disabled="submitting"
          rows="3"
        ></textarea>
        <button
          @click="submitComment"
          class="submit-btn"
          :disabled="!newComment.trim() || submitting"
        >
          {{ submitting ? '发布中...' : '发布评论' }}
        </button>
      </div>
    </div>

    <!-- Login Prompt -->
    <div v-else class="login-prompt">
      <p>登录后可以发表评论</p>
      <router-link to="/login" class="login-link">立即登录</router-link>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- Comments List -->
    <div v-else-if="comments.length > 0" class="comments-container">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <div class="user-info">
            <img
              v-if="comment.user?.avatarUrl"
              :src="comment.user.avatarUrl"
              :alt="comment.user.username"
              class="avatar"
            />
            <div v-else class="avatar-placeholder">
              {{ comment.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
            </div>
            <div class="user-details">
              <div class="username">{{ comment.user?.nickname || comment.user?.username || '匿名用户' }}</div>
              <div class="timestamp">{{ formatTime(comment.createdAt) }}</div>
            </div>
          </div>
          <button
            v-if="canDeleteComment(comment)"
            @click="deleteComment(comment.id)"
            class="delete-btn"
            :disabled="deleting"
          >
            删除
          </button>
        </div>
        <div class="comment-content">
          {{ comment.content }}
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination">
        <button
          @click="previousPage"
          :disabled="currentPage === 0"
          class="page-btn"
        >
          上一页
        </button>
        <span class="page-info">
          第 {{ currentPage + 1 }} / {{ totalPages }} 页
        </span>
        <button
          @click="nextPage"
          :disabled="currentPage >= totalPages - 1"
          class="page-btn"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="empty-state">
      <p>暂无评论，来发表第一条评论吧</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../store'
import { commentAPI } from '../services/api'
import { operationLogService } from '../services/operationLog'
import { ElMessage } from 'element-plus'

const props = defineProps({
  patternId: {
    type: Number,
    required: true
  },
  patternName: {
    type: String,
    default: '纹样'
  }
})

const userStore = useUserStore()

// State
const comments = ref([])
const newComment = ref('')
const loading = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalComments = ref(0)
const totalPages = ref(0)

// Computed
const isLoggedIn = computed(() => userStore.isLoggedIn)

// Methods
const fetchComments = async () => {
  loading.value = true
  try {
    const response = await commentAPI.getComments({
      patternId: props.patternId,
      page: currentPage.value,
      size: pageSize.value
    })

    if (response && response.data) {
      comments.value = response.data.content || []
      totalComments.value = response.data.totalElements || 0
      totalPages.value = response.data.totalPages || 0
    }
  } catch (error) {
    console.error('Failed to fetch comments:', error)
    ElMessage.error('加载评论失败')
  } finally {
    loading.value = false
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  submitting.value = true
  try {
    await commentAPI.createComment({
      userId: userStore.user?.id,
      patternId: props.patternId,
      content: newComment.value.trim()
    })

    ElMessage.success('评论发布成功')
    newComment.value = ''
    currentPage.value = 0
    
    // Record comment operation
    operationLogService.recordComment(props.patternId, props.patternName)
    
    await fetchComments()
  } catch (error) {
    console.error('Failed to submit comment:', error)
    ElMessage.error('发布评论失败，请重试')
  } finally {
    submitting.value = false
  }
}

const deleteComment = async (commentId) => {
  if (!confirm('确定要删除这条评论吗？')) {
    return
  }

  deleting.value = true
  try {
    await commentAPI.deleteComment(commentId)
    ElMessage.success('评论已删除')
    await fetchComments()
  } catch (error) {
    console.error('Failed to delete comment:', error)
    ElMessage.error('删除评论失败，请重试')
  } finally {
    deleting.value = false
  }
}

const canDeleteComment = (comment) => {
  // Allow deletion if user is admin or comment owner
  return userStore.user && (userStore.user.id === comment.userId || userStore.user.roleId === 1)
}

const formatTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date

  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (seconds < 60) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`

  return date.toLocaleDateString('zh-CN')
}

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    fetchComments()
  }
}

const previousPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    fetchComments()
  }
}

// Lifecycle
onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.comment-list {
  padding: 2rem;
  background-color: white;
  border-radius: 8px;
  margin-top: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.comment-list h3 {
  font-size: 1.3rem;
  color: #333;
  margin: 0 0 1.5rem 0;
  border-bottom: 2px solid #667eea;
  padding-bottom: 0.5rem;
}

/* Comment Input Section */
.comment-input-section {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #eee;
}

.input-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment-textarea {
  width: 100%;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  font-family: inherit;
  resize: vertical;
  transition: border-color 0.3s;
}

.comment-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.comment-textarea:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.submit-btn {
  align-self: flex-end;
  padding: 0.75rem 1.5rem;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background-color: #5568d3;
}

.submit-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* Login Prompt */
.login-prompt {
  text-align: center;
  padding: 2rem;
  background-color: #f9f9f9;
  border-radius: 4px;
  margin-bottom: 2rem;
}

.login-prompt p {
  color: #666;
  margin: 0 0 1rem 0;
}

.login-link {
  display: inline-block;
  background-color: #667eea;
  color: white;
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.3s;
}

.login-link:hover {
  background-color: #5568d3;
}

/* Loading State */
.loading-state {
  padding: 1rem 0;
}

/* Comments Container */
.comments-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.comment-item {
  padding: 1.5rem;
  background-color: #f9f9f9;
  border-radius: 4px;
  border-left: 3px solid #667eea;
  transition: background-color 0.3s;
}

.comment-item:hover {
  background-color: #f5f5f5;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.user-info {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
  flex: 1;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #667eea;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
}

.user-details {
  flex: 1;
}

.username {
  font-weight: 600;
  color: #333;
  margin-bottom: 0.3rem;
}

.timestamp {
  font-size: 0.85rem;
  color: #999;
}

.delete-btn {
  padding: 0.4rem 0.8rem;
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: background-color 0.3s;
  white-space: nowrap;
}

.delete-btn:hover:not(:disabled) {
  background-color: #ff5252;
}

.delete-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.comment-content {
  color: #555;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

.page-btn {
  padding: 0.5rem 1rem;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  background-color: #e0e0e0;
  border-color: #999;
}

.page-btn:disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 0.95rem;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 2rem;
  color: #999;
}

/* Responsive Design */
@media (max-width: 768px) {
  .comment-list {
    padding: 1rem;
    margin-top: 1rem;
  }

  .comment-list h3 {
    font-size: 1.1rem;
    margin-bottom: 1rem;
  }

  .comment-input-section {
    margin-bottom: 1.5rem;
    padding-bottom: 1.5rem;
  }

  .comment-textarea {
    padding: 0.75rem;
    font-size: 0.95rem;
  }

  .submit-btn {
    padding: 0.6rem 1.2rem;
    font-size: 0.9rem;
  }

  .comment-item {
    padding: 1rem;
  }

  .user-info {
    gap: 0.75rem;
  }

  .avatar,
  .avatar-placeholder {
    width: 36px;
    height: 36px;
  }

  .username {
    font-size: 0.95rem;
  }

  .timestamp {
    font-size: 0.8rem;
  }

  .delete-btn {
    padding: 0.35rem 0.7rem;
    font-size: 0.8rem;
  }

  .comment-content {
    font-size: 0.95rem;
  }

  .pagination {
    gap: 0.5rem;
    flex-wrap: wrap;
  }

  .page-btn {
    padding: 0.4rem 0.8rem;
    font-size: 0.9rem;
  }

  .page-info {
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .comment-list {
    padding: 0.75rem;
  }

  .comment-list h3 {
    font-size: 1rem;
  }

  .comment-header {
    flex-direction: column;
    gap: 0.75rem;
  }

  .delete-btn {
    align-self: flex-start;
  }

  .pagination {
    flex-direction: column;
    gap: 0.75rem;
  }

  .page-btn {
    width: 100%;
  }
}
</style>
