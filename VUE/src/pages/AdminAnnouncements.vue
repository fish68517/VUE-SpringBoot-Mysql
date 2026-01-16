<template>
  <div class="admin-announcements-container">
    <div class="header-section">
      <h1>Announcement Management</h1>
      <p class="subtitle">Create, edit, and manage system announcements</p>
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
      <!-- Add Announcement Button -->
      <div class="action-bar">
        <button class="btn-add-announcement" @click="openCreateAnnouncementModal">
          + Add New Announcement
        </button>
      </div>

      <!-- Empty State -->
      <div v-if="announcementList.length === 0" class="empty-state">
        <div class="empty-icon">📢</div>
        <h2>No Announcements Found</h2>
        <p>Click "Add New Announcement" to create your first announcement.</p>
      </div>

      <!-- Announcements List -->
      <div v-else class="announcements-list">
        <div v-for="announcement in announcementList" :key="announcement.id" class="announcement-card">
          <div class="announcement-header">
            <div class="announcement-info">
              <h3>{{ announcement.title }}</h3>
              <p class="announcement-meta">
                <span class="status-badge" :class="'status-' + announcement.status.toLowerCase()">
                  {{ announcement.status }}
                </span>
                <span class="sort-order">Sort: {{ announcement.sortOrder }}</span>
              </p>
            </div>
            <div class="announcement-actions">
              <button class="btn-action btn-edit" @click="openEditAnnouncementModal(announcement)" title="Edit announcement">
                Edit
              </button>
              <button class="btn-action btn-delete" @click="handleDeleteAnnouncement(announcement)" title="Delete announcement">
                Delete
              </button>
            </div>
          </div>

          <div class="announcement-body">
            <p class="content">{{ truncateContent(announcement.content, 150) }}</p>
          </div>

          <div class="announcement-footer">
            <span class="created-at">Created: {{ formatDate(announcement.createdAt) }}</span>
            <span v-if="announcement.updatedAt" class="updated-at">Updated: {{ formatDate(announcement.updatedAt) }}</span>
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

    <!-- Announcement Form Modal -->
    <div v-if="showAnnouncementModal" class="modal-overlay" @click="closeAnnouncementModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ editingAnnouncement ? 'Edit Announcement' : 'Create New Announcement' }}</h2>
          <button class="btn-close" @click="closeAnnouncementModal">×</button>
        </div>

        <form @submit.prevent="handleSaveAnnouncement" class="announcement-form">
          <div class="form-group">
            <label for="announcement-title">Title *</label>
            <input
              id="announcement-title"
              v-model="announcementForm.title"
              type="text"
              placeholder="Enter announcement title"
              required
            >
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="announcement-status">Status *</label>
              <select id="announcement-status" v-model="announcementForm.status" required>
                <option value="">Select status</option>
                <option value="DRAFT">Draft</option>
                <option value="PUBLISHED">Published</option>
              </select>
            </div>

            <div class="form-group">
              <label for="announcement-sort-order">Sort Order *</label>
              <input
                id="announcement-sort-order"
                v-model.number="announcementForm.sortOrder"
                type="number"
                placeholder="Enter sort order"
                required
              >
            </div>
          </div>

          <div class="form-group">
            <label for="announcement-content">Content *</label>
            <div class="editor-toolbar">
              <button type="button" class="toolbar-btn" @click="insertMarkdown('**', '**')" title="Bold">
                <strong>B</strong>
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('*', '*')" title="Italic">
                <em>I</em>
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('- ', '')" title="Bullet list">
                • List
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('# ', '')" title="Heading">
                H1
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('[', '](url)')" title="Link">
                🔗 Link
              </button>
            </div>
            <textarea
              id="announcement-content"
              ref="contentEditor"
              v-model="announcementForm.content"
              placeholder="Enter announcement content (supports markdown)"
              rows="8"
              required
            ></textarea>
            <div class="editor-hint">
              💡 Tip: You can use markdown formatting like **bold**, *italic*, - bullet points, # headings
            </div>
          </div>

          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="closeAnnouncementModal">
              Cancel
            </button>
            <button type="submit" class="btn-submit" :disabled="announcementFormLoading">
              {{ announcementFormLoading ? 'Saving...' : 'Save Announcement' }}
            </button>
          </div>
        </form>
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
  name: 'AdminAnnouncements',
  data() {
    return {
      announcementList: [],
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      loading: true,
      error: '',
      successMessage: '',
      errorMessage: '',
      
      // Announcement modal
      showAnnouncementModal: false,
      editingAnnouncement: null,
      announcementForm: {
        title: '',
        content: '',
        status: '',
        sortOrder: 0
      },
      announcementFormLoading: false
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
        const response = await adminService.getAnnouncements(this.currentPage, 20)
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

    openCreateAnnouncementModal() {
      this.editingAnnouncement = null
      this.announcementForm = {
        title: '',
        content: '',
        status: '',
        sortOrder: 0
      }
      this.showAnnouncementModal = true
    },

    openEditAnnouncementModal(announcement) {
      this.editingAnnouncement = announcement
      this.announcementForm = {
        title: announcement.title,
        content: announcement.content,
        status: announcement.status,
        sortOrder: announcement.sortOrder
      }
      this.showAnnouncementModal = true
    },

    closeAnnouncementModal() {
      this.showAnnouncementModal = false
      this.editingAnnouncement = null
      this.announcementForm = {
        title: '',
        content: '',
        status: '',
        sortOrder: 0
      }
    },

    async handleSaveAnnouncement() {
      this.announcementFormLoading = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        if (this.editingAnnouncement) {
          await adminService.updateAnnouncement(this.editingAnnouncement.id, this.announcementForm)
          this.successMessage = 'Announcement updated successfully'
        } else {
          await adminService.createAnnouncement(this.announcementForm)
          this.successMessage = 'Announcement created successfully'
        }

        this.closeAnnouncementModal()
        await this.loadAnnouncements()

        setTimeout(() => {
          this.successMessage = ''
        }, 3000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Failed to save announcement'
        setTimeout(() => {
          this.errorMessage = ''
        }, 3000)
      } finally {
        this.announcementFormLoading = false
      }
    },

    async handleDeleteAnnouncement(announcement) {
      if (!confirm(`Are you sure you want to delete "${announcement.title}"?`)) {
        return
      }

      this.errorMessage = ''
      this.successMessage = ''

      try {
        await adminService.deleteAnnouncement(announcement.id)
        this.successMessage = 'Announcement deleted successfully'
        await this.loadAnnouncements()

        setTimeout(() => {
          this.successMessage = ''
        }, 3000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Failed to delete announcement'
        setTimeout(() => {
          this.errorMessage = ''
        }, 3000)
      }
    },

    insertMarkdown(before, after) {
      const textarea = this.$refs.contentEditor
      if (!textarea) return

      const start = textarea.selectionStart
      const end = textarea.selectionEnd
      const selectedText = this.announcementForm.content.substring(start, end)
      const beforeText = this.announcementForm.content.substring(0, start)
      const afterText = this.announcementForm.content.substring(end)

      this.announcementForm.content = beforeText + before + selectedText + after + afterText

      this.$nextTick(() => {
        textarea.focus()
        textarea.selectionStart = start + before.length
        textarea.selectionEnd = start + before.length + selectedText.length
      })
    },

    truncateContent(content, maxLength) {
      if (!content) return ''
      if (content.length <= maxLength) return content
      return content.substring(0, maxLength) + '...'
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
    }
  }
}
</script>

<style scoped>
.admin-announcements-container {
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
.error-message {
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

.announcements-content {
  max-width: 1200px;
  margin: 0 auto;
}

.action-bar {
  margin-bottom: 30px;
  display: flex;
  justify-content: flex-end;
}

.btn-add-announcement {
  padding: 12px 24px;
  background: white;
  color: #667eea;
  border: 2px solid white;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-add-announcement:hover {
  background: transparent;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
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
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.announcement-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
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
  align-items: flex-start;
  gap: 15px;
}

.announcement-info {
  flex: 1;
}

.announcement-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.announcement-meta {
  margin: 0;
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  font-size: 13px;
  color: #666;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.status-draft {
  background: #fdebd0;
  color: #d68910;
}

.status-published {
  background: #d5f4e6;
  color: #27ae60;
}

.sort-order {
  white-space: nowrap;
}

.announcement-actions {
  display: flex;
  gap: 8px;
}

.btn-action {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-edit {
  background: #d6eaf8;
  color: #1f618d;
  border: 1px solid #85c1e2;
}

.btn-edit:hover {
  background: #85c1e2;
  color: white;
}

.btn-delete {
  background: #fadbd8;
  color: #c0392b;
  border: 1px solid #f5b7b1;
}

.btn-delete:hover {
  background: #f5b7b1;
  color: white;
}

.announcement-body {
  padding: 20px;
  flex: 1;
}

.content {
  margin: 0;
  color: #555;
  font-size: 14px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.announcement-footer {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

.created-at,
.updated-at {
  white-space: nowrap;
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
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  max-width: 700px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
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
  font-size: 20px;
  color: #333;
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
  transition: all 0.3s;
}

.btn-close:hover {
  color: #333;
}

.announcement-form {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 200px;
}

.editor-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.toolbar-btn {
  padding: 6px 12px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  color: #333;
}

.toolbar-btn:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.editor-hint {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
  font-style: italic;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.btn-cancel {
  padding: 10px 20px;
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

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
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
  z-index: 1001;
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
  z-index: 1001;
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
  .admin-announcements-container {
    padding: 20px 10px;
  }

  .header-section h1 {
    font-size: 24px;
  }

  .announcements-list {
    grid-template-columns: 1fr;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .announcement-header {
    flex-direction: column;
  }

  .announcement-actions {
    width: 100%;
  }

  .btn-action {
    flex: 1;
  }

  .modal-content {
    max-width: 90%;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-cancel,
  .btn-submit {
    width: 100%;
  }

  .editor-toolbar {
    flex-wrap: wrap;
  }

  .toolbar-btn {
    flex: 1;
    min-width: 60px;
  }
}
</style>
