<template>
  <div class="question-list">
    <h3>提问 ({{ totalQuestions }})</h3>

    <!-- Question Input Section -->
    <div v-if="isLoggedIn" class="question-input-section">
      <div class="input-container">
        <input
          v-model="newQuestionTitle"
          placeholder="提问标题..."
          class="question-title-input"
          :disabled="submitting"
        />
        <textarea
          v-model="newQuestionContent"
          placeholder="详细描述你的问题..."
          class="question-textarea"
          :disabled="submitting"
          rows="3"
        ></textarea>
        <button
          @click="submitQuestion"
          class="submit-btn"
          :disabled="!newQuestionTitle.trim() || !newQuestionContent.trim() || submitting"
        >
          {{ submitting ? '发布中...' : '发布提问' }}
        </button>
      </div>
    </div>

    <!-- Login Prompt -->
    <div v-else class="login-prompt">
      <p>登录后可以提问</p>
      <router-link to="/login" class="login-link">立即登录</router-link>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- Questions List -->
    <div v-else-if="questions.length > 0" class="questions-container">
      <div v-for="question in questions" :key="question.id" class="question-item">
        <div class="question-header">
          <div class="user-info">
            <img
              v-if="question.user?.avatarUrl"
              :src="question.user.avatarUrl"
              :alt="question.user.username"
              class="avatar"
            />
            <div v-else class="avatar-placeholder">
              {{ question.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
            </div>
            <div class="user-details">
              <div class="username">{{ question.user?.nickname || question.user?.username || '匿名用户' }}</div>
              <div class="timestamp">{{ formatTime(question.createdAt) }}</div>
            </div>
          </div>
          <button
            v-if="canDeleteQuestion(question)"
            @click="deleteQuestion(question.id)"
            class="delete-btn"
            :disabled="deleting"
          >
            删除
          </button>
        </div>
        <div class="question-title">{{ question.title }}</div>
        <div class="question-content">
          {{ question.content }}
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
      <p>暂无提问，来提出第一个问题吧</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../store'
import { questionAPI } from '../services/api'
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
const questions = ref([])
const newQuestionTitle = ref('')
const newQuestionContent = ref('')
const loading = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalQuestions = ref(0)
const totalPages = ref(0)

// Computed
const isLoggedIn = computed(() => userStore.isLoggedIn)

// Methods
const fetchQuestions = async () => {
  loading.value = true
  try {
    const response = await questionAPI.getQuestions({
      patternId: props.patternId,
      page: currentPage.value,
      size: pageSize.value
    })

    if (response && response.data) {
      questions.value = response.data.content || []
      totalQuestions.value = response.data.totalElements || 0
      totalPages.value = response.data.totalPages || 0
    }
  } catch (error) {
    console.error('Failed to fetch questions:', error)
    ElMessage.error('加载提问失败')
  } finally {
    loading.value = false
  }
}

const submitQuestion = async () => {
  if (!newQuestionTitle.value.trim()) {
    ElMessage.warning('提问标题不能为空')
    return
  }

  if (!newQuestionContent.value.trim()) {
    ElMessage.warning('提问内容不能为空')
    return
  }

  submitting.value = true
  try {
    await questionAPI.createQuestion({
      userId: userStore.user?.id,
      patternId: props.patternId,
      title: newQuestionTitle.value.trim(),
      content: newQuestionContent.value.trim()
    })

    ElMessage.success('提问发布成功')
    newQuestionTitle.value = ''
    newQuestionContent.value = ''
    currentPage.value = 0
    
    // Record question operation
    operationLogService.recordQuestion(props.patternId, props.patternName)
    
    await fetchQuestions()
  } catch (error) {
    console.error('Failed to submit question:', error)
    ElMessage.error('发布提问失败，请重试')
  } finally {
    submitting.value = false
  }
}

const deleteQuestion = async (questionId) => {
  if (!confirm('确定要删除这个提问吗？')) {
    return
  }

  deleting.value = true
  try {
    await questionAPI.deleteQuestion(questionId)
    ElMessage.success('提问已删除')
    await fetchQuestions()
  } catch (error) {
    console.error('Failed to delete question:', error)
    ElMessage.error('删除提问失败，请重试')
  } finally {
    deleting.value = false
  }
}

const canDeleteQuestion = (question) => {
  // Allow deletion if user is admin or question owner
  return userStore.user && (userStore.user.id === question.userId || userStore.user.roleId === 1)
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
    fetchQuestions()
  }
}

const previousPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    fetchQuestions()
  }
}

// Lifecycle
onMounted(() => {
  fetchQuestions()
})
</script>

<style scoped>
.question-list {
  padding: 2rem;
  background-color: white;
  border-radius: 8px;
  margin-top: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.question-list h3 {
  font-size: 1.3rem;
  color: #333;
  margin: 0 0 1.5rem 0;
  border-bottom: 2px solid #667eea;
  padding-bottom: 0.5rem;
}

/* Question Input Section */
.question-input-section {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #eee;
}

.input-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.question-title-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  font-family: inherit;
  transition: border-color 0.3s;
}

.question-title-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.question-title-input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.question-textarea {
  width: 100%;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  font-family: inherit;
  resize: vertical;
  transition: border-color 0.3s;
}

.question-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.question-textarea:disabled {
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

/* Questions Container */
.questions-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.question-item {
  padding: 1.5rem;
  background-color: #f9f9f9;
  border-radius: 4px;
  border-left: 3px solid #667eea;
  transition: background-color 0.3s;
}

.question-item:hover {
  background-color: #f5f5f5;
}

.question-header {
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

.question-title {
  font-weight: 600;
  color: #333;
  font-size: 1.1rem;
  margin-bottom: 0.75rem;
}

.question-content {
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
  .question-list {
    padding: 1rem;
    margin-top: 1rem;
  }

  .question-list h3 {
    font-size: 1.1rem;
    margin-bottom: 1rem;
  }

  .question-input-section {
    margin-bottom: 1.5rem;
    padding-bottom: 1.5rem;
  }

  .question-title-input,
  .question-textarea {
    padding: 0.75rem;
    font-size: 0.95rem;
  }

  .submit-btn {
    padding: 0.6rem 1.2rem;
    font-size: 0.9rem;
  }

  .question-item {
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

  .question-title {
    font-size: 1rem;
    margin-bottom: 0.5rem;
  }

  .question-content {
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
  .question-list {
    padding: 0.75rem;
  }

  .question-list h3 {
    font-size: 1rem;
  }

  .question-header {
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
