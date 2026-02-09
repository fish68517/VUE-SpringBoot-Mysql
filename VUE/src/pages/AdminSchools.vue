<template>
  <div class="admin-schools-container">
    <div class="header-section">
      <h1>学校管理</h1>
      <p class="subtitle">管理学校、专业与考试科目</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading">
      <p>正在加载学校列表...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <button class="btn-retry" @click="loadSchools">重试</button>
    </div>

    <!-- Main Content -->
    <div v-else class="schools-content">
      <!-- Add School Button -->
      <div class="action-bar">
        <button class="btn-add-school" @click="openCreateSchoolModal">
          + 新增学校
        </button>
      </div>

      <!-- Empty State -->
      <div v-if="schoolList.length === 0" class="empty-state">
        <div class="empty-icon">🏫</div>
        <h2>暂无学校数据</h2>
        <p>点击“新增学校”创建你的第一所学校。</p>
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
              <button class="btn-action btn-edit" @click="openEditSchoolModal(school)" title="编辑学校">
                编辑
              </button>
              <button class="btn-action btn-delete" @click="handleDeleteSchool(school)" title="删除学校">
                删除
              </button>
            </div>
          </div>

          <div class="school-body">
            <p class="intro">{{ school.intro }}</p>
            <div v-if="school.website" class="website">
              <a :href="school.website" target="_blank" rel="noopener noreferrer">
                访问官网 →
              </a>
            </div>
          </div>

          <div class="school-footer">
            <button class="btn-manage" @click="openMajorsModal(school)">
              管理专业
            </button>
            <button class="btn-manage" @click="openSubjectsModal(school)">
              管理考试科目
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
          上一页
        </button>

        <div class="page-info">
          第 {{ currentPage + 1 }} 页 / 共 {{ totalPages }} 页
        </div>

        <button
          class="btn-pagination"
          :disabled="currentPage >= totalPages - 1"
          @click="handleNextPage"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- School Form Modal -->
    <div v-if="showSchoolModal" class="modal-overlay" @click="closeSchoolModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ editingSchool ? '编辑学校' : '新增学校' }}</h2>
          <button class="btn-close" @click="closeSchoolModal">×</button>
        </div>

        <form @submit.prevent="handleSaveSchool" class="school-form">
          <div class="form-group">
            <label for="school-name">学校名称 *</label>
            <input
              id="school-name"
              v-model="schoolForm.name"
              type="text"
              placeholder="请输入学校名称"
              required
            >
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="school-city">城市 *</label>
              <input
                id="school-city"
                v-model="schoolForm.city"
                type="text"
                placeholder="请输入城市"
                required
              >
            </div>

            <div class="form-group">
              <label for="school-tier">层次 *</label>
              <select id="school-tier" v-model="schoolForm.tier" required>
                <option value="">请选择层次</option>
                <option value="985">985</option>
                <option value="211">211</option>
                <option value="DOUBLE_NON">双非</option>
                <option value="OTHER">其他</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label for="school-website">官网（可选）</label>
            <input
              id="school-website"
              v-model="schoolForm.website"
              type="url"
              placeholder="例如：https://example.com"
            >
          </div>

          <div class="form-group">
            <label for="school-intro">简介 *</label>
            <textarea
              id="school-intro"
              v-model="schoolForm.intro"
              placeholder="请输入学校简介"
              rows="4"
              required
            ></textarea>
          </div>

          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="closeSchoolModal">
              取消
            </button>
            <button type="submit" class="btn-submit" :disabled="schoolFormLoading">
              {{ schoolFormLoading ? '正在保存...' : '保存学校' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Majors Modal -->
    <div v-if="showMajorsModal" class="modal-overlay" @click="closeMajorsModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>管理专业 - {{ selectedSchool?.name }}</h2>
          <button class="btn-close" @click="closeMajorsModal">×</button>
        </div>

        <form @submit.prevent="handleAddMajor" class="major-form">
          <div class="form-row">
            <div class="form-group">
              <label for="major-name">专业名称 *</label>
              <input
                id="major-name"
                v-model="majorForm.name"
                type="text"
                placeholder="请输入专业名称"
                required
              >
            </div>

            <div class="form-group">
              <label for="major-direction">方向 *</label>
              <input
                id="major-direction"
                v-model="majorForm.direction"
                type="text"
                placeholder="请输入方向"
                required
              >
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-submit" :disabled="majorFormLoading">
              {{ majorFormLoading ? '正在添加...' : '添加专业' }}
            </button>
          </div>
        </form>

        <div class="modal-footer">
          <button class="btn-cancel" @click="closeMajorsModal">
            关闭
          </button>
        </div>
      </div>
    </div>

    <!-- Exam Subjects Modal -->
    <div v-if="showSubjectsModal" class="modal-overlay" @click="closeSubjectsModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>管理考试科目 - {{ selectedSchool?.name }}</h2>
          <button class="btn-close" @click="closeSubjectsModal">×</button>
        </div>

        <form @submit.prevent="handleAddSubject" class="subject-form">
          <div class="form-group">
            <label for="subject-name">科目名称 *</label>
            <input
              id="subject-name"
              v-model="subjectForm.subjectName"
              type="text"
              placeholder="请输入科目名称"
              required
            >
          </div>

          <div class="form-group">
            <label for="subject-code">科目代码 *</label>
            <input
              id="subject-code"
              v-model="subjectForm.subjectCode"
              type="text"
              placeholder="请输入科目代码"
              required
            >
          </div>

          <div class="form-group">
            <label for="subject-major">专业ID（可选）</label>
            <input
              id="subject-major"
              v-model="subjectForm.majorId"
              type="number"
              placeholder="请输入专业ID（可不填）"
            >
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-submit" :disabled="subjectFormLoading">
              {{ subjectFormLoading ? '正在添加...' : '添加科目' }}
            </button>
          </div>
        </form>

        <div class="modal-footer">
          <button class="btn-cancel" @click="closeSubjectsModal">
            关闭
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
        this.error = error.response?.data?.message || '加载学校列表失败，请重试。'
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
          this.successMessage = '学校更新成功'
        } else {
          await adminService.createSchool(this.schoolForm)
          this.successMessage = '学校创建成功'
        }

        this.closeSchoolModal()
        await this.loadSchools()

        setTimeout(() => {
          this.successMessage = ''
        }, 3000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || '保存学校失败'
        setTimeout(() => {
          this.errorMessage = ''
        }, 3000)
      } finally {
        this.schoolFormLoading = false
      }
    },

    async handleDeleteSchool(school) {
      if (!confirm(`确认要删除“${school.name}”吗？`)) {
        return
      }

      this.errorMessage = ''
      this.successMessage = ''

      try {
        await adminService.deleteSchool(school.id)
        this.successMessage = '学校删除成功'
        await this.loadSchools()

        setTimeout(() => {
          this.successMessage = ''
        }, 3000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || '删除学校失败'
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
        this.successMessage = '专业添加成功'
        this.majorForm = {
          name: '',
          direction: ''
        }

        setTimeout(() => {
          this.successMessage = ''
        }, 3000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || '添加专业失败'
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
        this.successMessage = '考试科目添加成功'
        this.subjectForm = {
          subjectName: '',
          subjectCode: '',
          majorId: null
        }

        setTimeout(() => {
          this.successMessage = ''
        }, 3000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || '添加考试科目失败'
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
