<template>
  <AdminLayout>
    <div class="user-management-page">
      <div class="page-header">
        <h1>👥 用户管理</h1>
        <p class="subtitle">管理平台用户账户</p>
      </div>

      <div class="management-container">
        <!-- Search and Filter -->
        <div class="search-bar">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索用户（学号或昵称）"
            class="search-input"
            @keyup.enter="loadUsers"
          />
          <button class="btn-search" @click="loadUsers">🔍 搜索</button>
        </div>

        <!-- Users List Card -->
        <div class="users-list-card">
          <div class="list-header">
            <h2>用户列表</h2>
            <span class="count-badge">{{ totalCount }}</span>
          </div>

          <div v-if="loading" class="loading">
            <p>加载用户中...</p>
          </div>

          <div v-else-if="users.length === 0" class="empty-state">
            <p>暂无用户数据</p>
          </div>

          <div v-else class="users-table-wrapper">
            <table class="users-table">
              <thead>
                <tr>
                  <th>用户ID</th>
                  <th>昵称</th>
                  <th>学号</th>
                  <th>邮箱</th>
                  <th>角色</th>
                  <th>状态</th>
                  <th>注册时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in users" :key="user.id" class="user-row">
                  <td class="cell-id">{{ user.id }}</td>
                  <td class="cell-nickname">{{ user.nickname || user.username }}</td>
                  <td class="cell-username">{{ user.username }}</td>
                  <td class="cell-email">{{ user.email }}</td>
                  <td class="cell-role">
                    <span class="role-badge" :class="getRoleClass(user.role)">
                      {{ getRoleLabel(user.role) }}
                    </span>
                  </td>
                  <td class="cell-status">
                    <span class="status-badge" :class="getStatusClass(user.status)">
                      {{ getStatusLabel(user.status) }}
                    </span>
                  </td>
                  <td class="cell-date">{{ formatDate(user.createdAt) }}</td>
                  <td class="cell-actions">
                    <button
                      v-if="user.status === 'ACTIVE'"
                      class="btn-action btn-disable"
                      @click="openDisableDialog(user)"
                      :disabled="operatingId === user.id"
                    >
                      禁用
                    </button>
                    <button
                      v-else
                      class="btn-action btn-enable"
                      @click="openEnableDialog(user)"
                      :disabled="operatingId === user.id"
                    >
                      启用
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          <div v-if="totalPages > 1" class="pagination">
            <button
              v-for="page in totalPages"
              :key="page"
              class="page-btn"
              :class="{ active: currentPage === page }"
              @click="loadUsers(page - 1)"
            >
              {{ page }}
            </button>
          </div>
        </div>
      </div>

      <!-- Disable Dialog -->
      <div v-if="showDisableDialog" class="dialog-overlay" @click="closeDisableDialog">
        <div class="dialog-content" @click.stop>
          <div class="dialog-header">
            <h3>禁用用户</h3>
            <button class="close-btn" @click="closeDisableDialog">×</button>
          </div>
          <div class="dialog-body">
            <p>确定要禁用用户 <strong>{{ selectedUser?.nickname || selectedUser?.username }}</strong> 吗？</p>
            <p class="info-text">禁用后，该用户将无法登录系统。</p>
          </div>
          <div class="dialog-footer">
            <button class="btn-cancel" @click="closeDisableDialog">取消</button>
            <button class="btn-confirm" @click="submitDisable">确认禁用</button>
          </div>
        </div>
      </div>

      <!-- Enable Dialog -->
      <div v-if="showEnableDialog" class="dialog-overlay" @click="closeEnableDialog">
        <div class="dialog-content" @click.stop>
          <div class="dialog-header">
            <h3>启用用户</h3>
            <button class="close-btn" @click="closeEnableDialog">×</button>
          </div>
          <div class="dialog-body">
            <p>确定要启用用户 <strong>{{ selectedUser?.nickname || selectedUser?.username }}</strong> 吗？</p>
            <p class="info-text">启用后，该用户将可以正常登录系统。</p>
          </div>
          <div class="dialog-footer">
            <button class="btn-cancel" @click="closeEnableDialog">取消</button>
            <button class="btn-confirm" @click="submitEnable">确认启用</button>
          </div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../utils/http'
import AdminLayout from '../components/AdminLayout.vue'

const users = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const totalCount = ref(0)
const searchQuery = ref('')
const operatingId = ref(null)

const showDisableDialog = ref(false)
const showEnableDialog = ref(false)
const selectedUser = ref(null)

/**
 * Load users
 */
const loadUsers = async (page = 0) => {
  loading.value = true
  try {
    const response = await http.get('/users', {
      params: {
        page,
        size: pageSize.value,
        search: searchQuery.value || undefined
      }
    })
    users.value = response.data.content || []
    totalPages.value = response.data.totalPages || 1
    totalCount.value = response.data.totalElements || 0
    currentPage.value = page + 1
  } catch (error) {
    console.error('Failed to load users:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * Get role label
 */
const getRoleLabel = (role) => {
  const roleMap = {
    USER: '普通用户',
    ORGANIZER: '组织者',
    ADMIN: '管理员'
  }
  return roleMap[role] || '未知'
}

/**
 * Get role class
 */
const getRoleClass = (role) => {
  const classMap = {
    USER: 'role-user',
    ORGANIZER: 'role-organizer',
    ADMIN: 'role-admin'
  }
  return classMap[role] || 'role-user'
}

/**
 * Get status label
 */
const getStatusLabel = (status) => {
  const statusMap = {
    ACTIVE: '已启用',
    DISABLED: '已禁用'
  }
  return statusMap[status] || '未知'
}

/**
 * Get status class
 */
const getStatusClass = (status) => {
  const classMap = {
    ACTIVE: 'status-active',
    DISABLED: 'status-disabled'
  }
  return classMap[status] || 'status-active'
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
 * Open disable dialog
 */
const openDisableDialog = (user) => {
  selectedUser.value = user
  showDisableDialog.value = true
}

/**
 * Close disable dialog
 */
const closeDisableDialog = () => {
  showDisableDialog.value = false
  selectedUser.value = null
}

/**
 * Submit disable
 */
const submitDisable = async () => {
  if (!selectedUser.value) return

  operatingId.value = selectedUser.value.id

  try {
    await http.put(`/users/${selectedUser.value.id}/status`, {
      status: 'DISABLED'
    })

    // Update user status in list
    const userIndex = users.value.findIndex(u => u.id === selectedUser.value.id)
    if (userIndex !== -1) {
      users.value[userIndex].status = 'DISABLED'
    }

    ElMessage.success('用户已禁用')
    closeDisableDialog()
  } catch (error) {
    ElMessage.error(error.message || '禁用用户失败')
  } finally {
    operatingId.value = null
  }
}

/**
 * Open enable dialog
 */
const openEnableDialog = (user) => {
  selectedUser.value = user
  showEnableDialog.value = true
}

/**
 * Close enable dialog
 */
const closeEnableDialog = () => {
  showEnableDialog.value = false
  selectedUser.value = null
}

/**
 * Submit enable
 */
const submitEnable = async () => {
  if (!selectedUser.value) return

  operatingId.value = selectedUser.value.id

  try {
    await http.put(`/users/${selectedUser.value.id}/status`, {
      status: 'ACTIVE'
    })

    // Update user status in list
    const userIndex = users.value.findIndex(u => u.id === selectedUser.value.id)
    if (userIndex !== -1) {
      users.value[userIndex].status = 'ACTIVE'
    }

    ElMessage.success('用户已启用')
    closeEnableDialog()
  } catch (error) {
    ElMessage.error(error.message || '启用用户失败')
  } finally {
    operatingId.value = null
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-management-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.page-header {
  max-width: 1400px;
  margin: 0 auto 30px;
}

.page-header h1 {
  font-size: 28px;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.subtitle {
  color: var(--text-secondary);
  font-size: 14px;
}

.management-container {
  max-width: 1400px;
  margin: 0 auto;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.btn-search {
  padding: 10px 20px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-search:hover {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.users-list-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
}

.list-header h2 {
  margin: 0;
  font-size: 18px;
  color: var(--text-primary);
}

.count-badge {
  background-color: var(--primary-color);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.loading,
.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: var(--text-secondary);
}

.users-table-wrapper {
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.users-table thead {
  background-color: #f5f7fa;
  border-bottom: 2px solid var(--border-color);
}

.users-table th {
  padding: 12px 16px;
  text-align: left;
  font-weight: 600;
  color: var(--text-primary);
}

.users-table tbody tr {
  border-bottom: 1px solid var(--border-color);
  transition: background-color 0.3s ease;
}

.users-table tbody tr:hover {
  background-color: #f9f9f9;
}

.users-table td {
  padding: 12px 16px;
  color: var(--text-primary);
}

.cell-id {
  font-weight: 600;
  color: var(--primary-color);
}

.cell-nickname {
  font-weight: 500;
}

.cell-email {
  color: var(--text-secondary);
  word-break: break-all;
}

.role-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.role-user {
  background-color: #1890ff;
}

.role-organizer {
  background-color: #52c41a;
}

.role-admin {
  background-color: #f5222d;
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-active {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-disabled {
  background-color: #fff1f0;
  color: #f5222d;
}

.cell-actions {
  text-align: center;
}

.btn-action {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-disable {
  background-color: #f5222d;
  color: white;
}

.btn-disable:hover:not(:disabled) {
  background-color: #d9001b;
  box-shadow: 0 2px 8px rgba(245, 34, 45, 0.3);
}

.btn-enable {
  background-color: #52c41a;
  color: white;
}

.btn-enable:hover:not(:disabled) {
  background-color: #45a049;
  box-shadow: 0 2px 8px rgba(82, 196, 26, 0.3);
}

.btn-action:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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

/* Dialog Styles */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  max-width: 500px;
  width: 90%;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
}

.dialog-header h3 {
  margin: 0;
  font-size: 16px;
  color: var(--text-primary);
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.3s ease;
}

.close-btn:hover {
  color: var(--text-primary);
}

.dialog-body {
  padding: 20px;
}

.dialog-body p {
  margin: 0 0 12px 0;
  color: var(--text-primary);
  font-size: 14px;
}

.info-text {
  color: var(--text-secondary);
  font-size: 12px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid var(--border-color);
}

.btn-cancel,
.btn-confirm {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-cancel {
  background-color: #f0f0f0;
  color: var(--text-primary);
}

.btn-cancel:hover {
  background-color: #e0e0e0;
}

.btn-confirm {
  background-color: var(--primary-color);
  color: white;
}

.btn-confirm:hover {
  background-color: var(--primary-light);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

@media (max-width: 768px) {
  .user-management-page {
    padding: 12px;
  }

  .page-header h1 {
    font-size: 20px;
  }

  .search-bar {
    flex-direction: column;
  }

  .btn-search {
    width: 100%;
  }

  .users-table {
    font-size: 12px;
  }

  .users-table th,
  .users-table td {
    padding: 8px 12px;
  }

  .cell-email {
    display: none;
  }

  .dialog-content {
    width: 95%;
  }
}
</style>
