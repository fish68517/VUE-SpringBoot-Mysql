<template>
  <div class="admin-feedback-container">
    <div class="header-section">
      <h1>反馈管理</h1>
      <p class="subtitle">审核和回复用户反馈</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading">
      <p>正在加载反馈...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <button class="btn-retry" @click="loadFeedback">重试</button>
    </div>

    <!-- Empty State -->
    <div v-else-if="feedbackList.length === 0" class="empty-state">
      <div class="empty-icon">📬</div>
      <h2>No Feedback Found</h2>
      <p>No feedback matches your search criteria.</p>
    </div>

    <!-- Feedback Content -->
    <div v-else class="feedback-content">
      <!-- Filter and Search Section -->
      <div class="filter-section">
        <div class="search-box">
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="按内容或用户搜索..."
            @keyup.enter="handleSearch"
          >
          <button class="btn-search" @click="handleSearch">搜索</button>
        </div>

        <div class="filter-box">
          <select v-model="statusFilter" @change="handleFilterChange">
            <option value="">All Status</option>
            <option value="NEW">New</option>
            <option value="PROCESSING">处理中</option>
            <option value="DONE">已完成</option>
          </select>
        </div>

        <div class="filter-box">
          <select v-model="typeFilter" @change="handleFilterChange">
            <option value="">All Types</option>
            <option value="BUG">Bug 报告</option>
            <option value="SUGGESTION">建议</option>
            <option value="DATA_ERROR">数据错误</option>
          </select>
        </div>
      </div>

      <!-- Results Info -->
      <div class="results-info">
        <p>反馈总数: {{ totalElements }} (第 {{ currentPage + 1 }} 页 / 共 {{ totalPages }} 页)</p>
      </div>

      <!-- Feedback Cards -->
      <div class="feedback-cards">
        <div v-for="feedback in feedbackList" :key="feedback.id" class="feedback-card">
          <div class="card-header">
            <div class="feedback-meta">
              <div class="meta-row">
                <span class="feedback-type" :class="'type-' + feedback.type.toLowerCase()">
                  {{ formatFeedbackType(feedback.type) }}
                </span>
                <span class="feedback-status" :class="'status-' + feedback.status.toLowerCase()">
                  {{ feedback.status }}
                </span>
              </div>
              <div class="user-info">
                <span class="user-id">用户 ID: {{ feedback.userId }}</span>
                <span class="feedback-date">{{ formatDate(feedback.createdAt) }}</span>
              </div>
            </div>
          </div>

          <div class="card-content">
            <div class="feedback-text">
              <h3>反馈内容</h3>
              <p>{{ feedback.content }}</p>
            </div>

            <div v-if="feedback.adminReply" class="admin-reply-display">
              <h3>您的回复</h3>
              <p>{{ feedback.adminReply }}</p>
              <div class="reply-date">
                Replied: {{ formatDate(feedback.repliedAt) }}
              </div>
            </div>
          </div>

          <div class="card-actions">
            <button
              class="btn-reply"
              @click="openReplyModal(feedback)"
              :disabled="actionLoading === feedback.id"
            >
              {{ feedback.adminReply ? '编辑回复' : '回复' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination">
        <button
          class="btn-pagination"
          :disabled="currentPage === 0"
          @click="handlePreviousPage"
        >
          Previous
        </button>

        <div class="page-info">
          Page {{ currentPage + 1 }} of {{ totalPages }}
        </div>

        <button
          class="btn-pagination"
          :disabled="currentPage >= totalPages - 1"
          @click="handleNextPage"
        >
          Next
        </button>
      </div>
    </div>

    <!-- Reply Modal -->
    <div v-if="showReplyModal" class="modal-overlay" @click="closeReplyModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>Reply to Feedback</h2>
          <button class="btn-close" @click="closeReplyModal">×</button>
        </div>

        <div class="modal-body">
          <div class="feedback-preview">
            <h3>Original Feedback</h3>
            <p class="feedback-type-badge" :class="'type-' + selectedFeedback.type.toLowerCase()">
              {{ formatFeedbackType(selectedFeedback.type) }}
            </p>
            <p class="preview-content">{{ selectedFeedback.content }}</p>
          </div>

          <div class="reply-form">
            <div class="form-group">
              <label for="status">Status</label>
              <select v-model="replyForm.status" id="status">
                <option value="NEW">New</option>
                <option value="PROCESSING">Processing</option>
                <option value="DONE">Done</option>
              </select>
            </div>

            <div class="form-group">
              <label for="reply">Reply Message</label>
              <textarea
                v-model="replyForm.adminReply"
                id="reply"
                placeholder="Enter your reply message..."
                rows="6"
              ></textarea>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="closeReplyModal">Cancel</button>
          <button
            class="btn-submit"
            @click="submitReply"
            :disabled="!replyForm.adminReply.trim() || submitting"
          >
            {{ submitting ? 'Submitting...' : 'Submit Reply' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Success Message -->
    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>

    <!-- Error Toast -->
    <div v-if="errorMessage" class="error-toast">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script>
import adminService from '../services/adminService'

export default {
  name: 'AdminFeedback',
  data() {
    return {
      feedbackList: [],
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      loading: true,
      error: '',
      searchKeyword: '',
      statusFilter: '',
      typeFilter: '',
      actionLoading: null,
      successMessage: '',
      errorMessage: '',
      showReplyModal: false,
      selectedFeedback: null,
      replyForm: {
        status: 'PROCESSING',
        adminReply: ''
      },
      submitting: false
    }
  },
  mounted() {
    this.loadFeedback()
  },
  methods: {
    async loadFeedback() {
      this.loading = true
      this.error = ''

      try {
        const response = await adminService.getFeedback(
          this.currentPage,
          20,
          this.statusFilter,
          this.typeFilter,
          this.searchKeyword
        )
        const data = response.data.data

        this.feedbackList = data.content || []
        this.totalElements = data.totalElements || 0
        this.totalPages = data.totalPages || 0
        this.currentPage = data.number || 0
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to load feedback. Please try again.'
        this.feedbackList = []
      } finally {
        this.loading = false
      }
    },

    async handleSearch() {
      this.currentPage = 0
      await this.loadFeedback()
    },

    async handleFilterChange() {
      this.currentPage = 0
      await this.loadFeedback()
    },

    async handleNextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++
        await this.loadFeedback()
      }
    },

    async handlePreviousPage() {
      if (this.currentPage > 0) {
        this.currentPage--
        await this.loadFeedback()
      }
    },

    openReplyModal(feedback) {
      this.selectedFeedback = feedback
      this.replyForm.status = feedback.status || 'PROCESSING'
      this.replyForm.adminReply = feedback.adminReply || ''
      this.showReplyModal = true
    },

    closeReplyModal() {
      this.showReplyModal = false
      this.selectedFeedback = null
      this.replyForm = {
        status: 'PROCESSING',
        adminReply: ''
      }
    },

    async submitReply() {
      if (!this.replyForm.adminReply.trim()) {
        this.errorMessage = 'Please enter a reply message'
        setTimeout(() => {
          this.errorMessage = ''
        }, 3000)
        return
      }

      this.submitting = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        await adminService.replyToFeedback(
          this.selectedFeedback.id,
          this.replyForm.status,
          this.replyForm.adminReply
        )

        this.successMessage = 'Reply submitted successfully'
        this.closeReplyModal()
        await this.loadFeedback()

        setTimeout(() => {
          this.successMessage = ''
        }, 3000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Failed to submit reply'
        setTimeout(() => {
          this.errorMessage = ''
        }, 3000)
      } finally {
        this.submitting = false
      }
    },

    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    formatFeedbackType(type) {
      const typeMap = {
        BUG: 'Bug Report',
        SUGGESTION: 'Suggestion',
        DATA_ERROR: 'Data Error'
      }
      return typeMap[type] || type
    }
  }
}
</script>

<style scoped>
.admin-feedback-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.header-section {
  max-width: 1200px;
  margin: 0 auto 40px;
  text-align: center;
  color: white;
}

.header-section h1 {
  margin: 0 0 10px 0;
  font-size: 32px;
}

.subtitle {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.loading,
.error-message,
.empty-state {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  padding: 60px 40px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.loading {
  color: #666;
  font-size: 16px;
}

.error-message {
  color: #c0392b;
}

.error-message p {
  margin: 0 0 20px 0;
  font-size: 16px;
}

.btn-retry {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-retry:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.empty-state {
  padding: 80px 40px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
}

.empty-state p {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.feedback-content {
  max-width: 1200px;
  margin: 0 auto;
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 8px 8px 0 0;
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.search-box {
  display: flex;
  gap: 10px;
  flex: 1;
  min-width: 250px;
}

.search-box input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-box input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.btn-search {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-search:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.filter-box {
  min-width: 150px;
}

.filter-box select {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background: white;
  cursor: pointer;
}

.filter-box select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.results-info {
  background: white;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  color: #666;
  font-size: 14px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.feedback-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.feedback-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
}

.feedback-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.card-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  background: #f8f9fa;
}

.feedback-meta {
  width: 100%;
}

.meta-row {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.feedback-type {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.type-bug {
  background: #fadbd8;
  color: #c0392b;
}

.type-suggestion {
  background: #d5f4e6;
  color: #27ae60;
}

.type-data_error {
  background: #fef5e7;
  color: #d68910;
}

.feedback-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.status-new {
  background: #d6eaf8;
  color: #1f618d;
}

.status-processing {
  background: #fef5e7;
  color: #d68910;
}

.status-done {
  background: #d5f4e6;
  color: #27ae60;
}

.user-info {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #999;
  flex-wrap: wrap;
}

.feedback-date {
  white-space: nowrap;
}

.card-content {
  padding: 20px;
  flex: 1;
}

.feedback-text {
  margin-bottom: 20px;
}

.feedback-text h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.feedback-text p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
}

.admin-reply-display {
  padding: 15px;
  background: #d5f4e6;
  border-radius: 4px;
  border-left: 4px solid #27ae60;
}

.admin-reply-display h3 {
  margin: 0 0 10px 0;
  color: #27ae60;
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.admin-reply-display p {
  margin: 0 0 10px 0;
  color: #155724;
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
}

.reply-date {
  color: #27ae60;
  font-size: 12px;
  margin-top: 8px;
}

.card-actions {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  background: #f8f9fa;
}

.btn-reply {
  width: 100%;
  padding: 10px 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-reply:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-reply:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.btn-pagination {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-pagination:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-pagination:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 14px;
  font-weight: 600;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

.btn-close {
  background: none;
  border: none;
  font-size: 28px;
  color: #999;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.3s;
}

.btn-close:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}

.feedback-preview {
  margin-bottom: 30px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
}

.feedback-preview h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.feedback-type-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 10px;
}

.feedback-type-badge.type-bug {
  background: #fadbd8;
  color: #c0392b;
}

.feedback-type-badge.type-suggestion {
  background: #d5f4e6;
  color: #27ae60;
}

.feedback-type-badge.type-data_error {
  background: #fef5e7;
  color: #d68910;
}

.preview-content {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
}

.reply-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #333;
  font-size: 14px;
  font-weight: 600;
}

.form-group select,
.form-group textarea {
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
}

.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 120px;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn-cancel {
  padding: 10px 24px;
  background: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-submit {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.success-message {
  position: fixed;
  top: 20px;
  right: 20px;
  background: #d5f4e6;
  color: #27ae60;
  padding: 15px 20px;
  border-radius: 4px;
  border-left: 4px solid #27ae60;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  animation: slideIn 0.3s ease-out;
}

.error-toast {
  position: fixed;
  top: 20px;
  right: 20px;
  background: #fadbd8;
  color: #c0392b;
  padding: 15px 20px;
  border-radius: 4px;
  border-left: 4px solid #c0392b;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    transform: translateX(400px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@media (max-width: 768px) {
  .admin-feedback-container {
    padding: 20px 10px;
  }

  .header-section h1 {
    font-size: 24px;
  }

  .filter-section {
    flex-direction: column;
  }

  .search-box {
    flex-direction: column;
  }

  .search-box input,
  .btn-search {
    width: 100%;
  }

  .filter-box {
    width: 100%;
  }

  .filter-box select {
    width: 100%;
  }

  .feedback-cards {
    grid-template-columns: 1fr;
  }

  .pagination {
    flex-direction: column;
    gap: 10px;
  }

  .btn-pagination {
    width: 100%;
  }

  .modal-overlay {
    padding: 10px;
  }

  .modal-content {
    max-height: 95vh;
  }

  .modal-footer {
    flex-direction: column;
  }

  .btn-cancel,
  .btn-submit {
    width: 100%;
  }
}
</style>
