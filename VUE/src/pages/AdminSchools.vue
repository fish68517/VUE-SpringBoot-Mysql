<template>
  <div class="admin-schools-container">
    <div class="header-section">
      <h1>School Management</h1>
      <p class="subtitle">Manage schools, majors, and exam subjects</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading">
      <p>Loading schools...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <button class="btn-retry" @click="loadSchools">Retry</button>
    </div>

    <!-- Main Content -->
    <div v-else class="schools-content">
      <!-- Add School Button -->
      <div class="action-bar">
        <button class="btn-add-school" @click="openCreateSchoolModal">
          + Add New School
        </button>
      </div>

      <!-- Empty State -->
      <div v-if="schoolList.length === 0" class="empty-state">
        <div class="empty-icon">🏫</div>
        <h2>No Schools Found</h2>
        <p>Click "Add New School" to create your first school.</p>
      </div>

      <!-- Schools List -->
      <div v-else class="schools-list">
        <div v-for="school in schoolList" :key="school.id" class="school-card">
          <div class="school-header">
            <div class="school-info">
              <h3>{{ school.name }}</h3>
              <p class="school-meta">
                <span class="tier-badge" :class="'tier-' + school.tier.toLowerCase()">{{ school.tier }}</span>
                <span class="city">📍 {{ school.city }}</span>
              </p>
            </div>
            <div class="school-actions">
              <button class="btn-action btn-edit" @click="openEditSchoolModal(school)" title="Edit school">
                Edit
              </button>
              <button class="btn-action btn-delete" @click="handleDeleteSchool(school)" title="Delete school">
                Delete
              </button>
            </div>
          </div>

          <div class="school-body">
            <p class="intro">{{ school.intro }}</p>
            <div v-if="school.website" class="website">
              <a :href="school.website" target="_blank" rel="noopener noreferrer">
                Visit Website →
              </a>
            </div>
          </div>

          <div class="school-footer">
            <button class="btn-manage" @click="openMajorsModal(school)">
              Manage Majors
            </button>
            <button class="btn-manage" @click="openSubjectsModal(school)">
              Manage Exam Subjects
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

    <!-- School Form Modal -->
    <div v-if="showSchoolModal" class="modal-overlay" @click="closeSchoolModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ editingSchool ? 'Edit School' : 'Create New School' }}</h2>
          <button class="btn-close" @click="closeSchoolModal">×</button>
        </div>

        <form @submit.prevent="handleSaveSchool" class="school-form">
          <div class="form-group">
            <label for="school-name">School Name *</label>
            <input
              id="school-name"
              v-model="schoolForm.name"
              type="text"
              placeholder="Enter school name"
              required
            >
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="school-city">City *</label>
              <input
                id="school-city"
                v-model="schoolForm.city"
                type="text"
                placeholder="Enter city"
                required
              >
            </div>

            <div class="form-group">
              <label for="school-tier">Tier *</label>
              <select id="school-tier" v-model="schoolForm.tier" required>
                <option value="">Select tier</option>
                <option value="985">985</option>
                <option value="211">211</option>
                <option value="DOUBLE_NON">Double-Non</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label for="school-website">Website</label>
            <input
              id="school-website"
              v-model="schoolForm.website"
              type="url"
              placeholder="https://example.com"
            >
          </div>

          <div class="form-group">
            <label for="school-intro">Introduction *</label>
            <textarea
              id="school-intro"
              v-model="schoolForm.intro"
              placeholder="Enter school introduction"
              rows="4"
              required
            ></textarea>
          </div>

          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="closeSchoolModal">
              Cancel
            </button>
            <button type="submit" class="btn-submit" :disabled="schoolFormLoading">
              {{ schoolFormLoading ? 'Saving...' : 'Save School' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Majors Modal -->
    <div v-if="showMajorsModal" class="modal-overlay" @click="closeMajorsModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>Manage Majors - {{ selectedSchool?.name }}</h2>
          <button class="btn-close" @click="closeMajorsModal">×</button>
        </div>

        <form @submit.prevent="handleAddMajor" class="major-form">
          <div class="form-row">
            <div class="form-group">
              <label for="major-name">Major Name *</label>
              <input
                id="major-name"
                v-model="majorForm.name"
                type="text"
                placeholder="Enter major name"
                required
              >
            </div>

            <div class="form-group">
              <label for="major-direction">Direction *</label>
              <input
                id="major-direction"
                v-model="majorForm.direction"
                type="text"
                placeholder="Enter direction"
                required
              >
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-submit" :disabled="majorFormLoading">
              {{ majorFormLoading ? 'Adding...' : 'Add Major' }}
            </button>
          </div>
        </form>

        <div class="modal-footer">
          <button class="btn-cancel" @click="closeMajorsModal">
            Close
          </button>
        </div>
      </div>
    </div>

    <!-- Exam Subjects Modal -->
    <div v-if="showSubjectsModal" class="modal-overlay" @click="closeSubjectsModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>Manage Exam Subjects - {{ selectedSchool?.name }}</h2>
          <button class="btn-close" @click="closeSubjectsModal">×</button>
        </div>

        <form @submit.prevent="handleAddSubject" class="subject-form">
          <div class="form-group">
            <label for="subject-name">Subject Name *</label>
            <input
              id="subject-name"
              v-model="subjectForm.subjectName"
              type="text"
              placeholder="Enter subject name"
              required
            >
          </div>

          <div class="form-group">
            <label for="subject-code">Subject Code *</label>
            <input
              id="subject-code"
              v-model="subjectForm.subjectCode"
              type="text"
              placeholder="Enter subject code"
              required
            >
          </div>

          <div class="form-group">
            <label for="subject-major">Major (Optional)</label>
            <input
              id="subject-major"
              v-model="subjectForm.majorId"
              type="number"
              placeholder="Enter major ID (optional)"
            >
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-submit" :disabled="subjectFormLoading">
              {{ subjectFormLoading ? 'Adding...' : 'Add Subject' }}
            </button>
          </div>
        </form>

        <div class="modal-footer">
          <button class="btn-cancel" @click="closeSubjectsModal">
            Close
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
  name: 'AdminSchools',
  data() {
    return {
      schoolList: [],
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      loading: true,
      error: '',
      successMessage: '',
      errorMessage: '',
      
      // School modal
      showSchoolModal: false,
      editingSchool: null,
      schoolForm: {
        name: '',
        city: '',
        tier: '',
        website: '',
        intro: ''
      },
      schoolFormLoading: false,

      // Majors modal
      showMajorsModal: false,
      selectedSchool: null,
      majorForm: {
        name: '',
        direction: ''
      },
      majorFormLoading: false,

      // Subjects modal
      showSubjectsModal: false,
      subjectForm: {
        subjectName: '',
        subjectCode: '',
        majorId: null
      },
      subjectFormLoading: false
    }
  },
  mounted() {
    this.loadSchools()
  },
  methods: {
    async loadSchools() {
      this.loading = true
      this.error = ''

      try {
        const response = await adminService.getSchools(this.currentPage, 20)
        const data = response.data.data

        this.schoolList = data.content || []
        this.totalElements = data.totalElements || 0
        this.totalPages = data.totalPages || 0
        this.currentPage = data.number || 0
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to load schools. Please try again.'
        this.schoolList = []
      } finally {
        this.loading = false
      }
    },

    async handleNextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++
        await this.loadSchools()
      }
    },

    async handlePreviousPage() {
      if (this.currentPage > 0) {
        this.currentPage--
        await this.loadSchools()
      }
    },

    openCreateSchoolModal() {
      this.editingSchool = null
      this.schoolForm = {
        name: '',
        city: '',
        tier: '',
        website: '',
        intro: ''
      }
      this.showSchoolModal = true
    },

    openEditSchoolModal(school) {
      this.editingSchool = school
      this.schoolForm = {
        name: school.name,
        city: school.city,
        tier: school.tier,
        website: school.website || '',
        intro: school.intro
      }
      this.showSchoolModal = true
    },

    closeSchoolModal() {
      this.showSchoolModal = false
      this.editingSchool = null
      this.schoolForm = {
        name: '',
        city: '',
        tier: '',
        website: '',
        intro: ''
      }
    },

    async handleSaveSchool() {
      this.schoolFormLoading = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        if (this.editingSchool) {
          await adminService.updateSchool(this.editingSchool.id, this.schoolForm)
          this.successMessage = 'School updated successfully'
        } else {
          await adminService.createSchool(this.schoolForm)
          this.successMessage = 'School created successfully'
        }

        this.closeSchoolModal()
        await this.loadSchools()

        setTimeout(() => {
          this.successMessage = ''
        }, 3000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Failed to save school'
        setTimeout(() => {
          this.errorMessage = ''
        }, 3000)
      } finally {
        this.schoolFormLoading = false
      }
    },

    async handleDeleteSchool(school) {
      if (!confirm(`Are you sure you want to delete "${school.name}"?`)) {
        return
      }

      this.errorMessage = ''
      this.successMessage = ''

      try {
        await adminService.deleteSchool(school.id)
        this.successMessage = 'School deleted successfully'
        await this.loadSchools()

        setTimeout(() => {
          this.successMessage = ''
        }, 3000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Failed to delete school'
        setTimeout(() => {
          this.errorMessage = ''
        }, 3000)
      }
    },

    openMajorsModal(school) {
      this.selectedSchool = school
      this.majorForm = {
        name: '',
        direction: ''
      }
      this.showMajorsModal = true
    },

    closeMajorsModal() {
      this.showMajorsModal = false
      this.selectedSchool = null
      this.majorForm = {
        name: '',
        direction: ''
      }
    },

    async handleAddMajor() {
      this.majorFormLoading = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        await adminService.createMajor(this.selectedSchool.id, this.majorForm)
        this.successMessage = 'Major added successfully'
        this.majorForm = {
          name: '',
          direction: ''
        }

        setTimeout(() => {
          this.successMessage = ''
        }, 3000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Failed to add major'
        setTimeout(() => {
          this.errorMessage = ''
        }, 3000)
      } finally {
        this.majorFormLoading = false
      }
    },

    openSubjectsModal(school) {
      this.selectedSchool = school
      this.subjectForm = {
        subjectName: '',
        subjectCode: '',
        majorId: null
      }
      this.showSubjectsModal = true
    },

    closeSubjectsModal() {
      this.showSubjectsModal = false
      this.selectedSchool = null
      this.subjectForm = {
        subjectName: '',
        subjectCode: '',
        majorId: null
      }
    },

    async handleAddSubject() {
      this.subjectFormLoading = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        const subjectData = {
          subjectName: this.subjectForm.subjectName,
          subjectCode: this.subjectForm.subjectCode
        }
        if (this.subjectForm.majorId) {
          subjectData.majorId = this.subjectForm.majorId
        }

        await adminService.createExamSubject(this.selectedSchool.id, subjectData)
        this.successMessage = 'Exam subject added successfully'
        this.subjectForm = {
          subjectName: '',
          subjectCode: '',
          majorId: null
        }

        setTimeout(() => {
          this.successMessage = ''
        }, 3000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Failed to add exam subject'
        setTimeout(() => {
          this.errorMessage = ''
        }, 3000)
      } finally {
        this.subjectFormLoading = false
      }
    }
  }
}
</script>

<style scoped>
.admin-schools-container {
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

.schools-content {
  max-width: 1200px;
  margin: 0 auto;
}

.action-bar {
  margin-bottom: 30px;
  display: flex;
  justify-content: flex-end;
}

.btn-add-school {
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

.btn-add-school:hover {
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

.schools-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.school-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
}

.school-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.school-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 15px;
}

.school-info {
  flex: 1;
}

.school-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.school-meta {
  margin: 0;
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  font-size: 13px;
  color: #666;
}

.tier-badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.tier-985 {
  background: #fadbd8;
  color: #c0392b;
}

.tier-211 {
  background: #fdebd0;
  color: #d68910;
}

.tier-double_non {
  background: #d5f4e6;
  color: #27ae60;
}

.tier-other {
  background: #d6eaf8;
  color: #1f618d;
}

.city {
  white-space: nowrap;
}

.school-actions {
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

.school-body {
  padding: 20px;
  flex: 1;
}

.intro {
  margin: 0 0 15px 0;
  color: #555;
  font-size: 14px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.website {
  margin: 0;
}

.website a {
  color: #667eea;
  text-decoration: none;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.3s;
}

.website a:hover {
  color: #764ba2;
}

.school-footer {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
}

.btn-manage {
  flex: 1;
  padding: 8px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-manage:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
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
  max-width: 500px;
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

.school-form,
.major-form,
.subject-form {
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

.modal-footer {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
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
  .admin-schools-container {
    padding: 20px 10px;
  }

  .header-section h1 {
    font-size: 24px;
  }

  .schools-list {
    grid-template-columns: 1fr;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .school-header {
    flex-direction: column;
  }

  .school-actions {
    width: 100%;
  }

  .btn-action {
    flex: 1;
  }

  .school-footer {
    flex-direction: column;
  }

  .btn-manage {
    width: 100%;
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
}
</style>
