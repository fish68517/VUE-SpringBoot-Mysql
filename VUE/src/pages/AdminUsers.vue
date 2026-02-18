<template>
  <div class="admin-users">
    
    <div class="admin-container">
      
      <div class="users-content">
        <h1>用户管理</h1>

        <!-- 用户列表 -->
        <div class="users-list-section">
          <h2>用户列表</h2>

          <!-- 筛选和搜索 -->
          <div class="filter-bar">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索用户名或邮箱..."
              @keyup.enter="handleSearch"
            />
            <select v-model="filterStatus">
              <option value="">全部状态</option>
              <option value="ACTIVE">活跃</option>
              <option value="disabled">禁用</option>
            </select>
            <button @click="handleSearch" class="btn btn-secondary">搜索</button>
            <button @click="loadUsers" class="btn btn-secondary">重置</button>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-indicator">
            <div class="spinner"></div>
            <p>加载中...</p>
          </div>

          <!-- 用户表格 -->
          <div v-else-if="users.length > 0" class="users-table">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>用户名</th>
                  <th>邮箱</th>
                  <th>昵称</th>
                  <th>状态</th>
                  <th>注册时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in users" :key="user.id">
                  <td>{{ user.id }}</td>
                  <td>{{ user.username }}</td>
                  <td>{{ user.email }}</td>
                  <td>{{ user.nickname || '-' }}</td>
                  <td>
                    <span :class="['status-badge', user.status === 'ACTIVE' ? 'status-active' : 'status-disabled']">
                      {{ user.status === 'ACTIVE' ? '活跃' : '禁用' }}
                    </span>
                  </td>
                  <td>{{ formatDate(user.createdAt) }}</td>
                  <td class="actions">
                    <button
                      @click="handleViewDetails(user)"
                      class="btn btn-small btn-info"
                    >
                      详情
                    </button>
                    <button
                      v-if="user.status === 'ACTIVE'"
                      @click="handleDisableUser(user.id)"
                      class="btn btn-small btn-warning"
                    >
                      禁用
                    </button>
                    <button
                      v-else
                      @click="handleEnableUser(user.id)"
                      class="btn btn-small btn-success"
                    >
                      启用
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <p>暂无用户数据</p>
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

        <!-- 用户详情模态框 -->
        <div v-if="showDetailsModal" class="modal-overlay" @click="closeDetailsModal">
          <div class="modal" @click.stop>
            <div class="modal-header">
              <h3>用户详情</h3>
              <button @click="closeDetailsModal" class="close-btn">×</button>
            </div>
            <div class="modal-body">
              <!-- 用户基本信息 -->
              <div class="user-info-section">
                <h4>基本信息</h4>
                <div class="info-grid">
                  <div class="info-item">
                    <label>用户ID</label>
                    <p>{{ selectedUser.id }}</p>
                  </div>
                  <div class="info-item">
                    <label>用户名</label>
                    <p>{{ selectedUser.username }}</p>
                  </div>
                  <div class="info-item">
                    <label>邮箱</label>
                    <p>{{ selectedUser.email }}</p>
                  </div>
                  <div class="info-item">
                    <label>昵称</label>
                    <p>{{ selectedUser.nickname || '-' }}</p>
                  </div>
                  <div class="info-item">
                    <label>状态</label>
                    <p>
                      <span :class="['status-badge', selectedUser.status === 'ACTIVE' ? 'status-active' : 'status-disabled']">
                        {{ selectedUser.status === 'ACTIVE' ? '活跃' : '禁用' }}
                      </span>
                    </p>
                  </div>
                  <div class="info-item">
                    <label>注册时间</label>
                    <p>{{ formatDate(selectedUser.createdAt) }}</p>
                  </div>
                </div>
              </div>

              <!-- 操作历史 -->
              <div class="operation-history-section">
                <h4>操作历史</h4>
                <div v-if="loadingHistory" class="loading-indicator">
                  <div class="spinner"></div>
                  <p>加载中...</p>
                </div>
                <div v-else-if="operationHistory.length > 0" class="history-list">
                  <div v-for="log in operationHistory" :key="log.id" class="history-item">
                    <div class="history-header">
                      <span class="operation-type">{{ log.operationType }}</span>
                      <span class="operation-time">{{ formatDate(log.createdAt) }}</span>
                    </div>
                    <div class="history-details">
                      <p v-if="log.targetType">目标类型: {{ log.targetType }}</p>
                      <p v-if="log.details">详情: {{ log.details }}</p>
                    </div>
                  </div>
                </div>
                <div v-else class="empty-state">
                  <p>暂无操作历史</p>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button @click="closeDetailsModal" class="btn btn-secondary">
                关闭
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { adminAPI, userAPI } from '../services/api'

import { ElMessage, ElMessageBox } from 'element-plus'

// State
const users = ref([])
const loading = ref(false)
const loadingHistory = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(1)
const searchKeyword = ref('')
const filterStatus = ref('')
const showDetailsModal = ref(false)
const selectedUser = ref(null)
const operationHistory = ref([])

// Load users
const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    const response = await adminAPI.getUsers(params)
    if (response.code === 200) {
      let userList = response.data.content || []
      
      // Apply status filter if selected
      if (filterStatus.value) {
        userList = userList.filter(user => user.status === filterStatus.value)
      }
      
      users.value = userList
      totalPages.value = response.data.totalPages || 1
      searchKeyword.value = ''
    } else {
      ElMessage.error(response.message || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// Search users
const handleSearch = async () => {
  currentPage.value = 0
  if (searchKeyword.value.trim()) {
    loading.value = true
    try {
      const params = {
        page: 0,
        size: pageSize.value
      }
      const response = await adminAPI.getUsers(params)
      if (response.code === 200) {
        let userList = response.data.content || []
        
        // Filter by search keyword
        const keyword = searchKeyword.value.toLowerCase()
        userList = userList.filter(user => 
          user.username.toLowerCase().includes(keyword) || 
          user.email.toLowerCase().includes(keyword)
        )
        
        // Apply status filter if selected
        if (filterStatus.value) {
          userList = userList.filter(user => user.status === filterStatus.value)
        }
        
        users.value = userList
        totalPages.value = 1
      } else {
        ElMessage.error(response.message || '搜索失败')
      }
    } catch (error) {
      ElMessage.error('搜索失败: ' + error.message)
    } finally {
      loading.value = false
    }
  } else {
    loadUsers()
  }
}

// View user details
const handleViewDetails = async (user) => {
  selectedUser.value = user
  showDetailsModal.value = true
  
  // Load operation history
  await loadOperationHistory(user.id)
}

// Load operation history
const loadOperationHistory = async (userId) => {
  loadingHistory.value = true
  try {
    const params = {
      page: 0,
      size: 20
    }
    const response = await userAPI.getHistory(userId, params)
    if (response.code === 200) {
      operationHistory.value = response.data.content || response.data || []
    } else {
      ElMessage.error(response.message || '获取操作历史失败')
    }
  } catch (error) {
    ElMessage.error('获取操作历史失败: ' + error.message)
  } finally {
    loadingHistory.value = false
  }
}

// Close details modal
const closeDetailsModal = () => {
  showDetailsModal.value = false
  selectedUser.value = null
  operationHistory.value = []
}

// Disable user
const handleDisableUser = async (userId) => {
  try {
    await ElMessageBox.confirm('确定要禁用这个用户吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await adminAPI.updateUserStatus(userId, { status: 'disabled' })
    if (response.code === 200) {
      ElMessage.success('用户已禁用')
      loadUsers()
      if (showDetailsModal.value && selectedUser.value?.id === userId) {
        closeDetailsModal()
      }
    } else {
      ElMessage.error(response.message || '禁用失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('禁用失败: ' + error.message)
    }
  }
}

// Enable user
const handleEnableUser = async (userId) => {
  try {
    await ElMessageBox.confirm('确定要启用这个用户吗？', '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    const response = await adminAPI.updateUserStatus(userId, { status: 'ACTIVE' })
    if (response.code === 200) {
      ElMessage.success('用户已启用')
      loadUsers()
      if (showDetailsModal.value && selectedUser.value?.id === userId) {
        closeDetailsModal()
      }
    } else {
      ElMessage.error(response.message || '启用失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('启用失败: ' + error.message)
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
    loadUsers()
  }
}

// Watch current page
watch(() => currentPage.value, watchPage)

// Lifecycle
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users {
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

.users-content {
  flex: 1;
}

.users-content h1 {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #333;
}

.users-content h2 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #333;
}

.users-content h4 {
  font-size: 1.1rem;
  margin-bottom: 1rem;
  color: #333;
}

/* Users List Section */
.users-list-section {
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
.users-table {
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

/* Status Badge */
.status-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 500;
}

.status-active {
  background-color: #d4edda;
  color: #155724;
}

.status-disabled {
  background-color: #f8d7da;
  color: #721c24;
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
  max-width: 700px;
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

.modal-body {
  padding: 1.5rem;
}

.user-info-section {
  margin-bottom: 2rem;
}

.user-info-section h4 {
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #eee;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-item label {
  font-weight: 600;
  color: #666;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
}

.info-item p {
  color: #333;
  margin: 0;
}

.operation-history-section {
  margin-bottom: 1rem;
}

.operation-history-section h4 {
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #eee;
}

.history-list {
  max-height: 400px;
  overflow-y: auto;
}

.history-item {
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 0.75rem;
  background-color: #f9f9f9;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.operation-type {
  font-weight: 600;
  color: #667eea;
  font-size: 0.875rem;
}

.operation-time {
  color: #999;
  font-size: 0.75rem;
}

.history-details {
  font-size: 0.875rem;
  color: #666;
}

.history-details p {
  margin: 0.25rem 0;
}

.modal-footer {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  padding: 1.5rem;
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

.btn-warning {
  background-color: #ffc107;
  color: #333;
}

.btn-warning:hover:not(:disabled) {
  background-color: #e0a800;
}

.btn-success {
  background-color: #28a745;
  color: white;
}

.btn-success:hover:not(:disabled) {
  background-color: #218838;
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

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
