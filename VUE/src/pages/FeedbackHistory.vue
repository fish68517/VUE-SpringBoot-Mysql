<template>
  <div class="feedback-history-container">
    <div class="header-section">
      <h1>My Feedback History</h1>
      <p class="subtitle">Track your feedback submissions and admin responses</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading">
      <p>Loading your feedback history...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <button class="btn-retry" @click="loadFeedback">Retry</button>
    </div>

    <!-- Empty State -->
    <div v-else-if="feedbackList.length === 0" class="empty-state">
      <div class="empty-icon">📝</div>
      <h2>No Feedback Yet</h2>
      <p>You haven't submitted any feedback yet. Help us improve by sharing your thoughts.</p>
      <router-link to="/feedback-submit" class="btn-submit-feedback">
        Submit Feedback
      </router-link>
    </div>

    <!-- Feedback List -->
    <div v-else class="feedback-content">
      <!-- Results Info -->
      <div class="results-info">
        <p>You have {{ totalElements }} feedback submission{{ totalElements !== 1 ? 's' : '' }} (Page {{ currentPage + 1 }} of {{ totalPages }})</p>
      </div>

      <!-- Feedback Cards -->
      <div class="feedback-cards">
        <div v-for="feedback in feedbackList" :key="feedback.id" class="feedback-card">
          <div class="card-header">
            <div class="feedback-info">
              <div class="feedback-type-row">
                <span class="feedback-type" :class="'type-' + feedback.type.toLowerCase()">
                  {{ formatFeedbackType(feedback.type) }}
                </span>
                <span class="feedback-status" :class="'status-' + feedback.status.toLowerCase()">
                  {{ feedback.status }}
                </span>
              </div>
              <div class="feedback-date">
                Submitted: {{ formatDate(feedback.createdAt) }}
              </div>
            </div>
          </div>

          <div class="card-content">
            <div class="feedback-content-text">
              <h3>Your Feedback</h3>
              <p>{{ feedback.content }}</p>
            </div>

            <div v-if="feedback.adminReply" class="admin-reply">
              <h3>Admin Response</h3>
              <p>{{ feedback.adminReply }}</p>
              <div class="reply-date">
                Replied: {{ formatDate(feedback.repliedAt) }}
              </div>
            </div>

            <div v-else-if="feedback.status === 'NEW'" class="no-reply">
              <p>Awaiting admin review...</p>
            </div>
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

      <!-- Submit New Feedback Button -->
      <div class="submit-new-section">
        <router-link to="/feedback-submit" class="btn-submit-new">
          Submit New Feedback
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import feedbackService from '../services/feedbackService'

export default {
  name: 'FeedbackHistory',
  data() {
    return {
      feedbackList: [],
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      loading: true,
      error: ''
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
        const response = await feedbackService.getUserFeedback(this.currentPage, 20)
        const data = response.data.data

        this.feedbackList = data.content || []
        this.totalElements = data.totalElements || 0
        this.totalPages = data.totalPages || 0
        this.currentPage = data.number || 0
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to load feedback history. Please try again.'
        this.feedbackList = []
      } finally {
        this.loading = false
      }
    },

    async handleNextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++
        await this.loadPage()
      }
    },

    async handlePreviousPage() {
      if (this.currentPage > 0) {
        this.currentPage--
        await this.loadPage()
      }
    },

    async loadPage() {
      this.loading = true
      this.error = ''

      try {
        const response = await feedbackService.getUserFeedback(this.currentPage, 20)
        const data = response.data.data

        this.feedbackList = data.content || []
        this.currentPage = data.number || 0
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to load feedback history. Please try again.'
        this.feedbackList = []
      } finally {
        this.loading = false
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
.feedback-history-container {
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
  margin: 0 0 30px 0;
  color: #666;
  font-size: 16px;
}

.btn-submit-feedback {
  display: inline-block;
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-submit-feedback:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.feedback-content {
  max-width: 1200px;
  margin: 0 auto;
}

.results-info {
  background: white;
  padding: 15px 20px;
  border-radius: 8px 8px 0 0;
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

.feedback-info {
  width: 100%;
}

.feedback-type-row {
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

.feedback-date {
  color: #999;
  font-size: 12px;
}

.card-content {
  padding: 20px;
  flex: 1;
}

.feedback-content-text {
  margin-bottom: 20px;
}

.feedback-content-text h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.feedback-content-text p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
}

.admin-reply {
  padding: 15px;
  background: #d5f4e6;
  border-radius: 4px;
  border-left: 4px solid #27ae60;
}

.admin-reply h3 {
  margin: 0 0 10px 0;
  color: #27ae60;
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.admin-reply p {
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

.no-reply {
  padding: 15px;
  background: #d6eaf8;
  border-radius: 4px;
  border-left: 4px solid #1f618d;
}

.no-reply p {
  margin: 0;
  color: #1f618d;
  font-size: 14px;
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

.submit-new-section {
  text-align: center;
  margin-top: 20px;
}

.btn-submit-new {
  display: inline-block;
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-submit-new:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

@media (max-width: 768px) {
  .feedback-history-container {
    padding: 20px 10px;
  }

  .header-section h1 {
    font-size: 24px;
  }

  .loading,
  .error-message,
  .empty-state {
    padding: 40px 20px;
  }

  .empty-state {
    padding: 60px 20px;
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
}
</style>
