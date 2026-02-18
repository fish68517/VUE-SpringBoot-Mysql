<template>
  <div class="admin-patterns">
  
    <div class="admin-container">
      
      <div class="patterns-content">
        <h1>纹样管理</h1>

        <!-- 上传纹样表单 -->
        <div class="upload-section">
          <h2>上传新纹样</h2>
          <form @submit.prevent="handleUpload" class="upload-form">
            <div class="form-group">
              <label>纹样名称 *</label>
              <input
                v-model="uploadForm.name"
                type="text"
                placeholder="输入纹样名称"
                required
              />
            </div>

            <div class="form-group">
              <label>纹样分类 *</label>
              <select v-model="uploadForm.category" required>
                <option value="">选择分类</option>
                <option value="七星纹">七星纹</option>
                <option value="东巴衍生纹">东巴衍生纹</option>
                <option value="日月纹">日月纹</option>
                <option value="云纹水纹">云纹水纹</option>
              </select>
            </div>

            <div class="form-group">
              <label>纹样描述</label>
              <textarea
                v-model="uploadForm.description"
                placeholder="输入纹样描述"
                rows="3"
              ></textarea>
            </div>

            <div class="form-group">
              <label>文化背景 *</label>
              <textarea
                v-model="uploadForm.culturalBackground"
                placeholder="输入纹样的文化背景信息"
                rows="3"
                required
              ></textarea>
            </div>

            <div class="form-group">
              <label>应用场景</label>
              <textarea
                v-model="uploadForm.applicationScenarios"
                placeholder="输入纹样的应用场景(传统服饰/现代设计/文创产品/其他)"
                rows="3"
              ></textarea>
            </div>

            <div class="form-group">
                  <label>图片 *</label>

                  <!-- 选择文件后自动上传 -->
                  <input
                    type="file"
                    accept="image/*"
                    @change="handleUploadImage"
                    :disabled="imageUploading"
                
                  />

                  <small v-if="imageUploading">图片上传中，请稍候...</small>

                  <!-- 上传成功后回填的URL（只读） -->
                  <input
                    v-model="uploadForm.imageUrl"
                    type="text"
                    placeholder="上传后自动生成图片URL"
                    readonly
                    required
                  />

                  <!-- 简单预览（可选） -->
                  <div v-if="uploadForm.imageUrl" style="margin-top:8px;">
                    <img :src="uploadForm.imageUrl" alt="预览" style="max-width: 240px; border-radius: 6px;" />
                  </div>
            </div>


            <div class="form-group" v-if="false">
              <label>下载URL</label>
              <input
                v-model="uploadForm.downloadUrl"
                type="text"
                placeholder="输入下载链接URL"
              />
            </div>

            <button type="submit" :disabled="uploading || imageUploading">
              {{ uploading ? '上传中...' : '提交' }}
            </button>

          </form>
        </div>

        <!-- 纹样列表 -->
        <div class="patterns-list-section">
          <h2>纹样列表</h2>

          <!-- 筛选和搜索 -->
          <div class="filter-bar">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索纹样名称..."
              @keyup.enter="handleSearch"
            />
            <select v-model="filterCategory">
              <option value="">全部分类</option>
              <option value="七星纹">七星纹</option>
              <option value="东巴衍生纹">东巴衍生纹</option>
              <option value="日月纹">日月纹</option>
              <option value="云纹水纹">云纹水纹</option>
            </select>
            <button @click="handleSearch" class="btn btn-secondary">搜索</button>
            <button @click="loadPatterns" class="btn btn-secondary">重置</button>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-indicator">
            <div class="spinner"></div>
            <p>加载中...</p>
          </div>

          <!-- 纹样表格 -->
          <div v-else-if="patterns.length > 0" class="patterns-table">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>名称</th>
                  <th>分类</th>
                  <th>浏览量</th>
                  <th>收藏量</th>
                  <th>创建时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="pattern in patterns" :key="pattern.id">
                  <td>{{ pattern.id }}</td>
                  <td>{{ pattern.name }}</td>
                  <td>{{ pattern.category }}</td>
                  <td>{{ pattern.viewCount }}</td>
                  <td>{{ pattern.collectionCount }}</td>
                
                  <td>{{ formatDate(pattern.createdAt) }}</td>
                  <td class="actions">
                    <button
                      @click="handleEdit(pattern)"
                      class="btn btn-small btn-info"
                    >
                      编辑
                    </button>
                    <button
                      @click="handleDelete(pattern.id)"
                      class="btn btn-small btn-danger"
                    >
                      删除
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <p>暂无纹样数据</p>
          </div>

          <!-- 分页 -->
          <div v-if="totalPages > 1" class="pagination">
            <button
              @click="currentPage--"
              :disabled="currentPage === 0"
              class="btn btn-secondary"
            >
              上一页
            </button>
            <span class="page-info">
              第 {{ currentPage + 1 }} / {{ totalPages }} 页
            </span>
            <button
              @click="currentPage++"
              :disabled="currentPage >= totalPages - 1"
              class="btn btn-secondary"
            >
              下一页
            </button>
          </div>
        </div>

        <!-- 编辑模态框 -->
        <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
          <div class="modal" @click.stop>
            <div class="modal-header">
              <h3>编辑纹样</h3>
              <button @click="closeEditModal" class="close-btn">×</button>
            </div>
            <form @submit.prevent="handleSaveEdit" class="edit-form">
              <div class="form-group">
                <label>纹样名称 *</label>
                <input
                  v-model="editForm.name"
                  type="text"
                  placeholder="输入纹样名称"
                  required
                />
              </div>

              <div class="form-group">
                <label>纹样分类 *</label>
                <select v-model="editForm.category" required>
                  <option value="七星纹">七星纹</option>
                  <option value="东巴衍生纹">东巴衍生纹</option>
                  <option value="日月纹">日月纹</option>
                  <option value="云纹水纹">云纹水纹</option>
                </select>
              </div>

              <div class="form-group">
                <label>纹样描述</label>
                <textarea
                  v-model="editForm.description"
                  placeholder="输入纹样描述"
                  rows="3"
                ></textarea>
              </div>

              <div class="form-group">
                <label>文化背景 *</label>
                <textarea
                  v-model="editForm.culturalBackground"
                  placeholder="输入纹样的文化背景信息"
                  rows="3"
                  required
                ></textarea>
              </div>

              <div class="form-group">
                <label>应用场景</label>
                <textarea
                  v-model="editForm.applicationScenarios"
                  placeholder="传统服饰/现代设计/文创产品/其他"
                  rows="30"
                ></textarea>
              </div>

              <div class="form-group" v-if="false">
                <label>图片URL *</label>
                <input
                  v-model="editForm.imageUrl"
                  type="text"
                  placeholder="输入图片URL"
                  required
                />
              </div>

              <div class="form-group" v-if="false">
                <label>下载URL</label>
                <input
                  v-model="editForm.downloadUrl"
                  type="text"
                  placeholder="输入下载链接URL"
                />
              </div>

              <div class="modal-footer">
                <button type="submit" class="btn btn-primary" :disabled="editing">
                  {{ editing ? '保存中...' : '保存' }}
                </button>
                <button
                  type="button"
                  @click="closeEditModal"
                  class="btn btn-secondary"
                >
                  取消
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { patternAPI } from '../services/api'

import { ElMessage, ElMessageBox } from 'element-plus'

// State
const patterns = ref([])
const loading = ref(false)
const uploading = ref(false)
const editing = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(1)
const searchKeyword = ref('')
const filterCategory = ref('')
const showEditModal = ref(false)

// 图片上传状态
const imageUploading = ref(false)

// 上传图片到后端 /api/common/upload，成功后回填 uploadForm.imageUrl
const handleUploadImage = async (e) => {
  const file = e.target.files && e.target.files[0]
  if (!file) return


  imageUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)

    console.log('formData：', formData)
    const resp = await fetch('/api/common/upload', {
      method: 'POST',
      body: formData
    })

    if (!resp.ok) {
      throw new Error(`上传失败，HTTP ${resp.status}`)
    }

    const data = await resp.json()
    // 后端返回 { url: "/images/xxx.png" }
    if (!data.url) {
      throw new Error('后端未返回图片 url')
    }

    uploadForm.value.imageUrl = data.url
    ElMessage.success('图片上传成功')
  } catch (err) {
    ElMessage.error('图片上传失败：' + (err.message || err))
    uploadForm.value.imageUrl = ''
  } finally {
    imageUploading.value = false
    // 允许同一张图片重复选择时也触发 change
    e.target.value = ''
  }
}



// Upload form
const uploadForm = ref({
  name: '',
  category: '',
  description: '',
  culturalBackground: '',
  applicationScenarios: '',
  imageUrl: '',
  downloadUrl: ''
})

// Edit form
const editForm = ref({
  id: null,
  name: '',
  category: '',
  description: '',
  culturalBackground: '',
  applicationScenarios: '',
  imageUrl: '',
  downloadUrl: ''
})

// Load patterns
const loadPatterns = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    if (filterCategory.value) {
      params.category = filterCategory.value
    }
    const response = await patternAPI.getPatterns(params)
    if (response.code === 200) {
      patterns.value = response.data.content || []
      totalPages.value = response.data.totalPages || 1
      searchKeyword.value = ''
    } else {
      ElMessage.error(response.message || '获取纹样列表失败')
    }
  } catch (error) {
    ElMessage.error('获取纹样列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// Search patterns
const handleSearch = async () => {
  currentPage.value = 0
  if (searchKeyword.value.trim()) {
    loading.value = true
    try {
      const params = {
        keyword: searchKeyword.value,
        page: 0,
        size: pageSize.value
      }
      if (filterCategory.value) {
        params.category = filterCategory.value
      }
      const response = await patternAPI.searchPatterns(searchKeyword.value, params)
      if (response.code === 200) {
        patterns.value = response.data.content || []
        totalPages.value = response.data.totalPages || 1
      } else {
        ElMessage.error(response.message || '搜索失败')
      }
    } catch (error) {
      ElMessage.error('搜索失败: ' + error.message)
    } finally {
      loading.value = false
    }
  } else {
    loadPatterns()
  }
}

// Upload pattern
const handleUpload = async () => {
  if (!uploadForm.value.imageUrl) {
    ElMessage.error('请先上传图片')
    return
  }
  if (imageUploading.value) {
    ElMessage.warning('图片正在上传，请稍候再提交')
    return
  }


  console.log('上传表单数据：', uploadForm.value)
  uploading.value = true
  try {
    const response = await patternAPI.createPattern(uploadForm.value)
    if (response.code === 200) {
      ElMessage.success('纹样上传成功')
      uploadForm.value = {
        name: '',
        category: '',
        description: '',
        culturalBackground: '',
        applicationScenarios: '',
        imageUrl: '',
        downloadUrl: ''
      }
      currentPage.value = 0
      loadPatterns()
    } else {
      ElMessage.error(response.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败: ' + error.message)
  } finally {
    uploading.value = false
  }
}

// Edit pattern
const handleEdit = (pattern) => {
  editForm.value = { ...pattern }
  showEditModal.value = true
}

// Save edit
const handleSaveEdit = async () => {
  editing.value = true
  try {
    const response = await patternAPI.updatePattern(editForm.value.id, editForm.value)
    if (response.code === 200) {
      ElMessage.success('纹样更新成功')
      showEditModal.value = false
      loadPatterns()
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败: ' + error.message)
  } finally {
    editing.value = false
  }
}

// Close edit modal
const closeEditModal = () => {
  showEditModal.value = false
}

// Delete pattern
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个纹样吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await patternAPI.deletePattern(id)
    if (response.code === 200) {
      ElMessage.success('纹样删除成功')
      loadPatterns()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + error.message)
    }
  }
}

// Format date
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN')
}

// Watch page changes
const watchPage = () => {
  if (searchKeyword.value.trim()) {
    handleSearch()
  } else {
    loadPatterns()
  }
}

// Watch current page
watch(() => currentPage.value, watchPage)

// Lifecycle
onMounted(() => {
  loadPatterns()
})
</script>

<style scoped>
.admin-patterns {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-container {
  display: flex;
  flex: 1;
  max-width: 14000px;
  margin: 0 auto;
  width: 100%;
  gap: 2rem;
  padding: 2rem;
}

.patterns-content {
  flex: 1;
}

.patterns-content h1 {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #333;
}

.patterns-content h2 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #333;
}

/* Upload Section */
.upload-section {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.upload-form {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: #333;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
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

.upload-form button {
  grid-column: 1 / -1;
  align-self: flex-start;
}

/* Patterns List Section */
.patterns-list-section {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-bar {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.filter-bar input,
.filter-bar select {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.filter-bar input {
  flex: 1;
  min-width: 200px;
}

.filter-bar select {
  min-width: 150px;
}

/* Loading */
.loading-indicator {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  gap: 1rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Table */
.patterns-table {
  overflow-x: auto;
  margin-bottom: 1.5rem;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background-color: #f5f5f5;
}

th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #333;
  border-bottom: 2px solid #ddd;
}

td {
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

tbody tr:hover {
  background-color: #f9f9f9;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 3rem;
  color: #999;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 1.5rem;
}

.page-info {
  color: #666;
  font-size: 0.875rem;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
}

.close-btn:hover {
  color: #333;
}

.edit-form {
  padding: 1.5rem;
  display: grid;
  grid-template-columns: 1fr;
  gap: 1rem;
}

.modal-footer {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

/* Buttons */
.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #667eea;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #5568d3;
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #333;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.btn-info {
  background-color: #17a2b8;
  color: white;
}

.btn-info:hover:not(:disabled) {
  background-color: #138496;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c82333;
}

.btn-small {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

/* Responsive */
@media (max-width: 768px) {
  .admin-container {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .upload-form {
    grid-template-columns: 1fr;
  }

  .filter-bar {
    flex-direction: column;
  }

  .filter-bar input,
  .filter-bar select {
    width: 100%;
  }

  table {
    font-size: 0.875rem;
  }

  th,
  td {
    padding: 0.75rem 0.5rem;
  }

  .actions {
    flex-direction: column;
  }

  .modal {
    width: 95%;
  }
}
</style>
