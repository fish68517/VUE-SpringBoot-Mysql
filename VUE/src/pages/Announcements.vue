<template>
  <div class="announcements-container">
    <div class="header-section">
      <h1>System Announcements</h1>
      <p class="subtitle">Stay updated with the latest news and information</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading">
      <p>Loading announcements...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <button class="btn-retry" @click="loadAnnouncements">Retry</button>
    </div>

    <!-- Main Content -->
    <div v-else class="announcements-content">
      <!-- Empty State -->
      <div v-if="announcementList.length === 0" class="empty-state">
        <div class="empty-icon">📢</div>
        <h2>No Announcements</h2>
        <p>There are currently no announcements. Please check back later.</p>
      </div>

      <!-- Announcements List -->
      <div v-else class="announcements-list">
        <div v-for="announcement in announcementList" :key="announcement.id" class="announcement-card">
          <div class="announcement-header">
            <h3>{{ announcement.title }}</h3>
            <span class="published-date">{{ formatDate(announcement.createdAt) }}</span>
          </div>

          <div class="announcement-body">
            <p class="content">{{ announcement.content }}</p>
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
  </div>
</template>

<script>
import announcementService from '../services/announcementService'

export default {
  name: 'Announcements',
  data() {
    return {
      announcementList: [],
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      loading: true,
      error: ''
    }
  },
  mounted() {
    this.loadAnnouncements()
  },
  methods: {
    async loadAnnouncements() {
      this.loading = true
      this.error = ''

      try {
        const response = await announcementService.getAnnouncements(this.currentPage, 20)
        const data = response.data.data

        this.announcementList = data.content || []
        this.totalElements = data.totalElements || 0
        this.totalPages = data.totalPages || 0
        this.currentPage = data.number || 0
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to load announcements. Please try again.'
        this.announcementList = []
      } finally {
        this.loading = false
      }
    },

    async handleNextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++
        await this.loadAnnouncements()
      }
    },

    async handlePreviousPage() {
      if (this.currentPage > 0) {
        this.currentPage--
        await this.loadAnnouncements()
      }
    },

    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.announcements-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.header-section {
  max-width: 1000px;
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
.error-message {
  max-width: 1000px;
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

.announcements-content {
  max-width: 1000px;
  margin: 0 auto;
}

.empty-state {
  background: white;
  padding: 80px 40px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
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

.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 30px;
}

.announcement-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s;
}

.announcement-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.announcement-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
}

.announcement-header h3 {
  margin: 0;
  font-size: 20px;
  color: #333;
  flex: 1;
}

.published-date {
  font-size: 13px;
  color: #999;
  white-space: nowrap;
}

.announcement-body {
  padding: 20px;
}

.content {
  margin: 0;
  color: #555;
  font-size: 15px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
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

@media (max-width: 768px) {
  .announcements-container {
    padding: 20px 10px;
  }

  .header-section h1 {
    font-size: 24px;
  }

  .announcement-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .published-date {
    align-self: flex-start;
  }

  .pagination {
    flex-wrap: wrap;
  }

  .btn-pagination {
    flex: 1;
    min-width: 100px;
  }
}
</style>
