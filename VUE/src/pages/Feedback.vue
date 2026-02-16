<template>
  <div class="feedback-page">
    <!-- Top Navigation -->
    <TopNavigation />

    <div class="page-header">
      <h1>活动反馈</h1>
      <p class="subtitle">分享您对活动的看法和建议</p>
    </div>

    <div class="feedback-container">
      <div class="feedback-card">
        <div class="card-header">
          <h2>提交反馈</h2>
        </div>

        <form @submit.prevent="submitFeedback" class="feedback-form">
          <!-- Activity Selection -->
          <div class="form-group">
            <label>选择活动 <span class="required">*</span></label>
            <select
              v-model="formData.activityId"
              class="form-input"
              @change="validateField('activityId')"
            >
              <option value="">-- 请选择活动 --</option>
              <option v-for="activity in activities" :key="activity.id" :value="activity.id">
                {{ activity.title }}
              </option>
            </select>
            <span v-if="errors.activityId" class="error-message">{{ errors.activityId }}</span>
          </div>

          <!-- Star Rating -->
          <div class="form-group">
            <label>评分 <span class="required">*</span></label>
            <div class="star-rating">
              <button
                v-for="star in 5"
                :key="star"
                type="button"
                class="star"
                :class="{ active: formData.rating >= star }"
                @click="setRating(star)"
                @blur="validateField('rating')"
              >
                ★
              </button>
            </div>
            <span v-if="formData.rating" class="rating-text">{{ formData.rating }} 星</span>
            <span v-if="errors.rating" class="error-message">{{ errors.rating }}</span>
          </div>

          <!-- Feedback Content -->
          <div class="form-group">
            <label>反馈内容 <span class="required">*</span></label>
            <textarea
              v-model="formData.content"
              placeholder="请输入您的反馈内容（至少10个字符）"
              class="form-textarea"
              rows="6"
              @blur="validateField('content')"
            ></textarea>
            <div class="char-count">
              {{ formData.content.length }} / 500
            </div>
            <span v-if="errors.content" class="error-message">{{ errors.content }}</span>
          </div>

          <!-- Form Actions -->
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="resetForm">清空</button>
            <button type="submit" class="btn-submit" :disabled="isSubmitting">
              {{ isSubmitting ? '提交中...' : '提交反馈' }}
            </button>
          </div>
        </form>
      </div>

      <!-- My Feedback List -->
      <div class="feedback-list-card">
        <div class="card-header">
          <h2>我的反馈</h2>
        </div>

        <div v-if="loading" class="loading">
          <p>加载中...</p>
        </div>

        <div v-else-if="myFeedback.length === 0" class="empty-state">
          <p>暂无反馈记录</p>
        </div>

        <div v-else class="feedback-list">
          <div v-for="feedback in myFeedback" :key="feedback.id" class="feedback-item">
            <div class="feedback-header">
              <div class="activity-info">
                <h3>{{ feedback.activity?.title || '活动' }}</h3>
                <span class="feedback-date">{{ formatDate(feedback.createdAt) }}</span>
              </div>
              <div class="rating-display">
                <span class="stars">
                  <span v-for="i in 5" :key="i" class="star-icon" :class="{ filled: i <= feedback.rating }">★</span>
                </span>
                <span class="rating-value">{{ feedback.rating }} 星</span>
              </div>
            </div>
            <div class="feedback-content">
              {{ feedback.content }}
            </div>
            <div v-if="feedback.replyContent" class="feedback-reply">
              <div class="reply-header">
                <strong>组织者回复：</strong>
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
            @click="loadMyFeedback(page - 1)"
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

const formData = reactive({
  activityId: '',
  rating: 0,
  content: ''
})

const errors = reactive({
  activityId: '',
  rating: '',
  content: ''
})

const activities = ref([])
const myFeedback = ref([])
const isSubmitting = ref(false)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(5)
const totalPages = ref(1)

/**
 * Load user's activities for feedback
 */
const loadActivities = async () => {
  try {
    const response = await http.get('/registrations/my', {
      params: {
        page: 0,
        size: 100
      }
    })
    activities.value = response.data.content.map(reg => reg.activity).filter(Boolean)
  } catch (error) {
    console.error('Failed to load activities:', error)
  }
}

/**
 * Load user's feedback
 */
const loadMyFeedback = async (page = 0) => {
  loading.value = true
  try {
    const response = await http.get('/feedback/my', {
      params: {
        page,
        size: pageSize.value
      }
    })
    myFeedback.value = response.data.content
    totalPages.value = response.data.totalPages
    currentPage.value = page + 1
  } catch (error) {
    ElMessage.error('加载反馈失败')
    console.error('Failed to load feedback:', error)
  } finally {
    loading.value = false
  }
}

/**
 * Set rating
 */
const setRating = (star) => {
  formData.rating = formData.rating === star ? 0 : star
}

/**
 * Validate a single field
 */
const validateField = (fieldName) => {
  errors[fieldName] = ''

  if (fieldName === 'activityId') {
    if (!formData.activityId) {
      errors.activityId = '请选择活动'
    }
  } else if (fieldName === 'rating') {
    if (formData.rating === 0) {
      errors.rating = '请选择评分'
    }
  } else if (fieldName === 'content') {
    if (!formData.content.trim()) {
      errors.content = '反馈内容不能为空'
    } else if (formData.content.trim().length < 10) {
      errors.content = '反馈内容至少需要10个字符'
    } else if (formData.content.length > 500) {
      errors.content = '反馈内容不能超过500个字符'
    }
  }
}

/**
 * Validate all fields
 */
const validateForm = () => {
  validateField('activityId')
  validateField('rating')
  validateField('content')

  return !errors.activityId && !errors.rating && !errors.content
}

/**
 * Submit feedback
 */
const submitFeedback = async () => {
  if (!validateForm()) {
    ElMessage.error('请填写完整的反馈信息')
    return
  }

  isSubmitting.value = true

  try {
    const feedbackData = {
      activityId: parseInt(formData.activityId),
      rating: formData.rating,
      content: formData.content
    }

    await http.post('/feedback', feedbackData)
    ElMessage.success('反馈提交成功')
    resetForm()
    loadMyFeedback(0)
  } catch (error) {
    ElMessage.error(error.message || '反馈提交失败，请重试')
  } finally {
    isSubmitting.value = false
  }
}

/**
 * Reset form
 */
const resetForm = () => {
  formData.activityId = ''
  formData.rating = 0
  formData.content = ''
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
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
  loadMyFeedback(0)
})
</script>

<style scoped>
.feedback-page {
  padding: 20px;
  max-width: 1200px;
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

.feedback-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.feedback-card,
.feedback-list-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  background-color: #fafafa;
}

.card-header h2 {
  margin: 0;
  font-size: 16px;
  color: var(--text-primary);
}

.feedback-form {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.required {
  color: #f56c6c;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.3s ease;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 120px;
}

.star-rating {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.star {
  background: none;
  border: none;
  font-size: 32px;
  color: #ddd;
  cursor: pointer;
  padding: 0;
  transition: color 0.2s ease;
}

.star:hover,
.star.active {
  color: var(--primary-color);
}

.rating-text {
  display: block;
  color: var(--text-secondary);
  font-size: 12px;
  margin-top: 4px;
}

.char-count {
  text-align: right;
  color: var(--text-secondary);
  font-size: 12px;
  margin-top: 4px;
}

.error-message {
  display: block;
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.btn-cancel,
.btn-submit {
  flex: 1;
  padding: 10px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel {
  background-color: #f5f7fa;
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-cancel:hover {
  background-color: #eff2f5;
}

.btn-submit {
  background-color: var(--primary-color);
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.feedback-list {
  padding: 20px;
}

.feedback-item {
  padding: 16px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  margin-bottom: 12px;
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

.activity-info h3 {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: var(--text-primary);
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

.stars {
  display: flex;
  gap: 2px;
}

.star-icon {
  color: #ddd;
  font-size: 14px;
}

.star-icon.filled {
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

.feedback-reply {
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  border-left: 3px solid var(--primary-color);
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

@media (max-width: 1024px) {
  .feedback-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .feedback-page {
    padding: 12px;
  }

  .page-header h1 {
    font-size: 20px;
  }

  .feedback-form,
  .feedback-list {
    padding: 16px;
  }

  .form-actions {
    flex-direction: column;
  }

  .star {
    font-size: 24px;
  }
}
</style>
