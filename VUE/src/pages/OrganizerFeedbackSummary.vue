<template>
  <div class="feedback-summary-page">
    <!-- Top Navigation -->
    <TopNavigation />

    <div class="page-header">
      <h1>反馈汇总</h1>
      <p class="subtitle">查看和回复活动反馈</p>
    </div>

    <div class="feedback-summary-container">
      <!-- Activity Selection -->
      <div class="filter-section">
        <div class="filter-group">
          <label>选择活动</label>
          <select v-model="selectedActivityId" class="filter-select" @change="loadFeedback">
            <option value="">-- 请选择活动 --</option>
            <option v-for="activity in activities" :key="activity.id" :value="activity.id">
              {{ activity.title }}
            </option>
          </select>
        </div>
      </div>

      <!-- Feedback List -->
      <div class="feedback-list-card">
        <div v-if="loading" class="loading">
          <p>加载反馈中...</p>
        </div>

        <div v-else-if="!selectedActivityId" class="empty-state">
          <p>请选择一个活动查看反馈</p>
        </div>

        <div v-else-if="feedbackList.length === 0" class="empty-state">
          <p>该活动暂无反馈</p>
        </div>

        <div v-else class="feedback-list">
          <div v-for="feedback in feedbackList" :key="feedback.id" class="feedback-item">
            <!-- Feedback Header -->
            <div class="feedback-header">
              <div class="user-info">
                <span class="user-name">{{ feedback.user?.nickname || '用户' }}</span>
                <span class="feedback-date">{{ formatDate(feedback.createdAt) }}</span>
              </div>
              <div class="rating-display">
                <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= feedback.rating }">★</span>
                <span class="rating-value">{{ feedback.rating }} 星</span>
              </div>
            </div>

            <!-- Feedback Content -->
            <div class="feedback-content">
              {{ feedback.content }}
            </div>

            <!-- Reply Section -->
            <div v-if="!feedback.replyContent" class="reply-form">
              <textarea
                v-model="replyForms[feedback.id]"
                placeholder="输入回复内容（最多1000字）"
                class="reply-textarea"
                rows="3"
                @keydown.ctrl.enter="submitReply(feedback.id)"
              ></textarea>
              <div class="reply-actions">
                <button
                  class="btn-reply"
                  @click="submitReply(feedback.id)"
                  :disabled="!replyForms[feedback.id] || submittingReply[feedback.id]"
                >
                  {{ submittingReply[feedback.id] ? '提交中...' : '提交回复' }}
                </button>
              </div>
            </div>

            <!-- Existing Reply -->
            <div v-else class="feedback-reply">
              <div class="reply-header">
                <strong>您的回复：</strong>
              </div>
              <div class="reply-content">
                {{ feedback.replyContent }}
              </div>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="pagination">
          <button
            v-for="page in totalPages"
            :key="page"
            class="page-btn"
            :class="{ active: currentPage === page }"
            @click="loadFeedback(page - 1)"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import TopNavigation from '../components/TopNavigation.vue'
import http from '../utils/http'

const activities = ref([])
const feedbackList = ref([])
const selectedActivityId = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const replyForms = reactive({})
const submittingReply = reactive({})

/**
 * Load organizer's activities
 */
const loadActivities = async () => {
  try {
    const response = await http.get('/activities', {
      params: {
        page: 0,
        size: 100,
        organizerOnly: true
      }
    })
    activities.value = response.data.content || []
  } catch (error) {
    console.error('Failed to load activities:', error)
    ElMessage.error('加载活动列表失败')
  }
}

/**
 * Load feedback for selected activity
 */
const loadFeedback = async (page = 0) => {
  if (!selectedActivityId.value) {
    feedbackList.value = []
    return
  }

  loading.value = true
  try {
    const response = await http.get(`/feedback/activity/${selectedActivityId.value}`, {
      params: {
        page,
        size: pageSize.value
      }
    })
    feedbackList.value = response.data.content || []
    totalPages.value = response.data.totalPages || 1
    currentPage.value = page + 1

    // Initialize reply forms
    feedbackList.value.forEach(feedback => {
      if (!feedback.replyContent && !replyForms[feedback.id]) {
        replyForms[feedback.id] = ''
      }
    })
  } catch (error) {
    console.error('Failed to load feedback:', error)
    ElMessage.error('加载反馈失败')
  } finally {
    loading.value = false
  }
}

/**
 * Submit reply to feedback
 */
const submitReply = async (feedbackId) => {
  const replyContent = replyForms[feedbackId]

  if (!replyContent || !replyContent.trim()) {
    ElMessage.error('回复内容不能为空')
    return
  }

  if (replyContent.length > 1000) {
    ElMessage.error('回复内容不能超过1000字')
    return
  }

  submittingReply[feedbackId] = true

  try {
    const response = await http.put(`/feedback/${feedbackId}/reply`, {
      replyContent: replyContent.trim()
    })

    // Update feedback with reply
    const feedbackIndex = feedbackList.value.findIndex(f => f.id === feedbackId)
    if (feedbackIndex !== -1) {
      feedbackList.value[feedbackIndex].replyContent = response.data.replyContent
    }

    // Clear reply form
    delete replyForms[feedbackId]

    ElMessage.success('回复提交成功')
  } catch (error) {
    ElMessage.error(error.message || '回复提交失败')
  } finally {
    submittingReply[feedbackId] = false
  }
}

/**
 * Format date
 */
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

/**
 * Initialize page
 */
onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.feedback-summary-page {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.subtitle {
  color: var(--text-secondary);
  margin: 0;
  font-size: 14px;
}

.feedback-summary-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-group label {
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.filter-select {
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.3s ease;
}

.filter-select:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.feedback-list-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.feedback-list {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feedback-item {
  padding: 16px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  transition: box-shadow 0.3s ease;
}

.feedback-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}

.feedback-date {
  color: var(--text-secondary);
  font-size: 12px;
}

.rating-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.star {
  color: #ddd;
  font-size: 14px;
}

.star.filled {
  color: var(--primary-color);
}

.rating-value {
  color: var(--text-secondary);
  font-size: 12px;
}

.feedback-content {
  color: var(--text-primary);
  font-size: 13px;
  line-height: 1.6;
  margin-bottom: 12px;
  word-break: break-word;
}

.reply-form {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
}

.reply-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 13px;
  font-family: inherit;
  resize: vertical;
  transition: border-color 0.3s ease;
}

.reply-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.reply-actions {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.btn-reply {
  padding: 8px 16px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-reply:hover:not(:disabled) {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.btn-reply:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.feedback-reply {
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  border-left: 3px solid var(--primary-color);
  margin-top: 12px;
}

.reply-header {
  color: var(--text-primary);
  font-size: 12px;
  margin-bottom: 8px;
}

.reply-content {
  color: var(--text-primary);
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
}

.loading,
.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: var(--text-secondary);
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 20px;
  border-top: 1px solid var(--border-color);
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid var(--border-color);
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.page-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.page-btn.active {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

@media (max-width: 600px) {
  .feedback-summary-page {
    padding: 12px;
  }

  .page-header h1 {
    font-size: 20px;
  }

  .feedback-list {
    padding: 16px;
  }

  .feedback-header {
    flex-direction: column;
    gap: 8px;
  }

  .rating-display {
    justify-content: flex-start;
  }
}
</style>
