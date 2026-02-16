<template>
  <div class="fund-proof-page">
    <!-- Top Navigation -->
    <TopNavigation />

    <div class="page-header">
      <h1>资金证明提交</h1>
      <p class="subtitle">提交活动众筹资金使用证明</p>
    </div>

    <div class="fund-proof-container">
      <!-- Submission Form -->
      <div class="submission-card">
        <div class="card-header">
          <h2>提交资金证明</h2>
        </div>

        <form @submit.prevent="submitFundProof" class="fund-proof-form">
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

          <!-- File Upload -->
          <div class="form-group">
            <label>上传凭证文件 <span class="required">*</span></label>
            <div class="file-upload-area" @dragover.prevent @drop.prevent="handleFileDrop">
              <input
                ref="fileInput"
                type="file"
                class="file-input"
                @change="handleFileSelect"
                accept=".pdf,.jpg,.jpeg,.png,.doc,.docx"
              />
              <div class="upload-content">
                <div class="upload-icon">📄</div>
                <p class="upload-text">
                  <span class="upload-link" @click="$refs.fileInput.click()">点击选择文件</span>
                  或拖拽文件到此处
                </p>
                <p class="upload-hint">支持 PDF、JPG、PNG、DOC、DOCX 格式，最大 10MB</p>
              </div>
            </div>
            <div v-if="formData.file" class="file-preview">
              <span class="file-name">✓ {{ formData.file.name }}</span>
              <span class="file-size">({{ formatFileSize(formData.file.size) }})</span>
              <button type="button" class="remove-file" @click="removeFile">移除</button>
            </div>
            <span v-if="errors.file" class="error-message">{{ errors.file }}</span>
          </div>

          <!-- Description -->
          <div class="form-group">
            <label>使用说明 <span class="required">*</span></label>
            <textarea
              v-model="formData.description"
              placeholder="请详细说明资金的使用情况（至少20个字符）"
              class="form-textarea"
              rows="6"
              @blur="validateField('description')"
            ></textarea>
            <div class="char-count">
              {{ formData.description.length }} / 1000
            </div>
            <span v-if="errors.description" class="error-message">{{ errors.description }}</span>
          </div>

          <!-- Form Actions -->
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="resetForm">清空</button>
            <button type="submit" class="btn-submit" :disabled="isSubmitting">
              {{ isSubmitting ? '提交中...' : '提交证明' }}
            </button>
          </div>
        </form>
      </div>

      <!-- Submission History -->
      <div class="history-card">
        <div class="card-header">
          <h2>提交历史</h2>
        </div>

        <div v-if="loading" class="loading">
          <p>加载中...</p>
        </div>

        <div v-else-if="submissions.length === 0" class="empty-state">
          <p>暂无提交记录</p>
        </div>

        <div v-else class="submissions-list">
          <div v-for="submission in submissions" :key="submission.id" class="submission-item">
            <div class="submission-header">
              <div class="activity-info">
                <h3>{{ getActivityTitle(submission.activityId) }}</h3>
                <span class="submission-date">{{ formatDate(submission.createdAt) }}</span>
              </div>
              <div class="status-badge" :class="getStatusClass(submission.status)">
                {{ getStatusText(submission.status) }}
              </div>
            </div>
            <div class="submission-content">
              <p class="description">{{ submission.description }}</p>
              <div class="file-info">
                <span class="file-icon">📎</span>
                <a :href="submission.fileUrl" target="_blank" class="file-link">查看凭证</a>
              </div>
            </div>
            <div v-if="submission.status === 'REJECTED' && submission.rejectionReason" class="rejection-reason">
              <div class="reason-header">
                <strong>驳回原因：</strong>
              </div>
              <div class="reason-content">
                {{ submission.rejectionReason }}
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
            @click="loadSubmissions(page - 1)"
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
  file: null,
  description: ''
})

const errors = reactive({
  activityId: '',
  file: '',
  description: ''
})

const fileInput = ref(null)
const activities = ref([])
const submissions = ref([])
const isSubmitting = ref(false)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(5)
const totalPages = ref(1)

/**
 * Load organizer's activities
 */
const loadActivities = async () => {
  try {
    const response = await http.get('/activities', {
      params: {
        page: 0,
        size: 100
      }
    })
    // Filter activities where current user is the organizer
    activities.value = response.data.content.filter(activity => activity.enableCrowdfunding)
  } catch (error) {
    console.error('Failed to load activities:', error)
  }
}

/**
 * Load fund proof submissions
 */
const loadSubmissions = async (page = 0) => {
  loading.value = true
  try {
    const response = await http.get('/fund-proofs/my', {
      params: {
        page,
        size: pageSize.value
      }
    })
    submissions.value = response.data.content
    totalPages.value = response.data.totalPages
    currentPage.value = page + 1
  } catch (error) {
    ElMessage.error('加载提交历史失败')
    console.error('Failed to load submissions:', error)
  } finally {
    loading.value = false
  }
}

/**
 * Handle file selection
 */
const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (file) {
    formData.file = file
    validateField('file')
  }
}

/**
 * Handle file drop
 */
const handleFileDrop = (event) => {
  const file = event.dataTransfer.files[0]
  if (file) {
    formData.file = file
    validateField('file')
  }
}

/**
 * Remove selected file
 */
const removeFile = () => {
  formData.file = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
  errors.file = ''
}

/**
 * Format file size
 */
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
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
  } else if (fieldName === 'file') {
    if (!formData.file) {
      errors.file = '请选择文件'
    } else if (formData.file.size > 10 * 1024 * 1024) {
      errors.file = '文件大小不能超过 10MB'
      formData.file = null
    } else {
      const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']
      if (!allowedTypes.includes(formData.file.type)) {
        errors.file = '不支持的文件格式'
        formData.file = null
      }
    }
  } else if (fieldName === 'description') {
    if (!formData.description.trim()) {
      errors.description = '使用说明不能为空'
    } else if (formData.description.trim().length < 20) {
      errors.description = '使用说明至少需要 20 个字符'
    } else if (formData.description.length > 1000) {
      errors.description = '使用说明不能超过 1000 个字符'
    }
  }
}

/**
 * Validate all fields
 */
const validateForm = () => {
  validateField('activityId')
  validateField('file')
  validateField('description')

  return !errors.activityId && !errors.file && !errors.description
}

/**
 * Submit fund proof
 */
const submitFundProof = async () => {
  if (!validateForm()) {
    ElMessage.error('请填写完整的资金证明信息')
    return
  }

  isSubmitting.value = true

  try {
    const formDataObj = new FormData()
    formDataObj.append('activityId', formData.activityId)
    formDataObj.append('file', formData.file)
    formDataObj.append('description', formData.description)

    await http.post('/fund-proofs', formDataObj, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    ElMessage.success('资金证明提交成功，等待管理员审核')
    resetForm()
    loadSubmissions(0)
  } catch (error) {
    ElMessage.error(error.message || '资金证明提交失败，请重试')
  } finally {
    isSubmitting.value = false
  }
}

/**
 * Reset form
 */
const resetForm = () => {
  formData.activityId = ''
  formData.file = null
  formData.description = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
}

/**
 * Get activity title by ID
 */
const getActivityTitle = (activityId) => {
  const activity = activities.value.find(a => a.id === activityId)
  return activity ? activity.title : '未知活动'
}

/**
 * Get status text
 */
const getStatusText = (status) => {
  const statusMap = {
    PENDING_AUDIT: '待审核',
    APPROVED: '已通过',
    REJECTED: '已驳回'
  }
  return statusMap[status] || status
}

/**
 * Get status class
 */
const getStatusClass = (status) => {
  const classMap = {
    PENDING_AUDIT: 'status-pending',
    APPROVED: 'status-approved',
    REJECTED: 'status-rejected'
  }
  return classMap[status] || ''
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
  loadSubmissions(0)
})
</script>

<style scoped>
.fund-proof-page {
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

.fund-proof-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.submission-card,
.history-card {
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

.fund-proof-form {
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

.file-upload-area {
  border: 2px dashed var(--border-color);
  border-radius: 4px;
  padding: 30px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #fafafa;
}

.file-upload-area:hover {
  border-color: var(--primary-color);
  background-color: #f0f5ff;
}

.file-input {
  display: none;
}

.upload-content {
  pointer-events: none;
}

.upload-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.upload-text {
  margin: 0 0 8px 0;
  color: var(--text-primary);
  font-size: 14px;
}

.upload-link {
  color: var(--primary-color);
  cursor: pointer;
  text-decoration: underline;
  pointer-events: auto;
}

.upload-hint {
  margin: 0;
  color: var(--text-secondary);
  font-size: 12px;
}

.file-preview {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background-color: #f0f5ff;
  border-radius: 4px;
  margin-top: 12px;
  font-size: 13px;
}

.file-name {
  color: var(--primary-color);
  flex: 1;
}

.file-size {
  color: var(--text-secondary);
  font-size: 12px;
}

.remove-file {
  background: none;
  border: none;
  color: #f56c6c;
  cursor: pointer;
  font-size: 12px;
  text-decoration: underline;
  padding: 0;
}

.remove-file:hover {
  color: #dd001b;
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

.submissions-list {
  padding: 20px;
}

.submission-item {
  padding: 16px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  margin-bottom: 12px;
  transition: box-shadow 0.3s ease;
}

.submission-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.submission-header {
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

.submission-date {
  color: var(--text-secondary);
  font-size: 12px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.status-pending {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.status-approved {
  background-color: #f0f9ff;
  color: #67c23a;
}

.status-rejected {
  background-color: #fef0f0;
  color: #f56c6c;
}

.submission-content {
  margin-bottom: 12px;
}

.description {
  color: var(--text-primary);
  font-size: 13px;
  line-height: 1.6;
  margin: 0 0 12px 0;
  word-break: break-word;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.file-icon {
  font-size: 14px;
}

.file-link {
  color: var(--primary-color);
  text-decoration: none;
}

.file-link:hover {
  text-decoration: underline;
}

.rejection-reason {
  background-color: #fef0f0;
  padding: 12px;
  border-radius: 4px;
  border-left: 3px solid #f56c6c;
}

.reason-header {
  color: var(--text-primary);
  font-size: 12px;
  margin-bottom: 8px;
}

.reason-content {
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
  .fund-proof-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .fund-proof-page {
    padding: 12px;
  }

  .page-header h1 {
    font-size: 20px;
  }

  .fund-proof-form,
  .submissions-list {
    padding: 16px;
  }

  .form-actions {
    flex-direction: column;
  }

  .submission-header {
    flex-direction: column;
    gap: 8px;
  }

  .status-badge {
    align-self: flex-start;
  }
}
</style>
