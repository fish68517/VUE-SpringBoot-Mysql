<template>
  <div class="admin-users-container">
    <div class="header-section">
      <h1>用户管理</h1>
      <p class="subtitle">管理用户账号与权限</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <p>正在加载用户列表...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <button class="btn-retry" @click="loadUsers">重试</button>
    </div>

    <!-- 空状态 -->
    <div v-else-if="userList.length === 0" class="empty-state">
      <div class="empty-icon">👥</div>
      <h2>未找到用户</h2>
      <p>没有符合当前搜索条件的用户。</p>
    </div>

    <!-- 用户内容 -->
    <div v-else class="users-content">
      <!-- 过滤与搜索 -->
      <div class="filter-section">
        <div class="search-box">
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="按用户名搜索..."
            @keyup.enter="handleSearch"
          >
          <button class="btn-search" @click="handleSearch">搜索</button>
        </div>

        <div class="filter-box">
          <select v-model="statusFilter" @change="handleFilterChange">
            <option value="">全部状态</option>
            <option value="ENABLED">启用</option>
            <option value="DISABLED">禁用</option>
          </select>
        </div>
      </div>

      <!-- 结果信息 -->
      <div class="results-info">
        <p>用户总数：{{ totalElements }}（第 {{ currentPage + 1 }} / {{ totalPages }} 页）</p>
      </div>

      <!-- 用户表格 -->
      <div class="users-table-wrapper">
        <table class="users-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>角色</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in userList" :key="user.id">
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>
                <span class="role-badge" :class="'role-' + user.role.toLowerCase()">
                  {{ user.role }}
                </span>
              </td>
              <td>
                <span class="status-badge" :class="'status-' + user.status.toLowerCase()">
                  {{ user.status }}
                </span>
              </td>
              <td>{{ formatDate(user.createdAt) }}</td>
              <td class="actions-cell">
                <button
                  class="btn-action btn-toggle-status"
                  :title="user.status === 'ENABLED' ? '禁用用户' : '启用用户'"
                  @click="handleToggleStatus(user)"
                  :disabled="actionLoading === user.id"
                >
                  {{ user.status === 'ENABLED' ? '禁用' : '启用' }}
                </button>
                <button
                v-if="false"
                  class="btn-action btn-reset-password"
                  title="重置用户密码"
                  @click="handleResetPassword(user)"
                  :disabled="actionLoading === user.id"
                >
                  重置密码
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="pagination">
        <button
          class="btn-pagination"
          :disabled="currentPage === 0"
          @click="handlePreviousPage"
        >
          上一页
        </button>

        <div class="page-info">
          第 {{ currentPage + 1 }} / {{ totalPages }} 页
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

    <!-- 成功提示 -->
    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>

    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-toast">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script>
import adminService from '../services/adminService'

export default {
  name: 'AdminUsers',
  data() {
    return {
      userList: [],
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      loading: true,
      error: '',
      searchKeyword: '',
      statusFilter: '',
      actionLoading: null,
      successMessage: '',
      errorMessage: ''
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    async loadUsers() {
      this.loading = true
      this.error = ''

      try {
        const response = await adminService.getUsers(
          this.currentPage,
          20,
          this.searchKeyword,
          this.statusFilter
        )
        const data = response.data.data

        this.userList = data.content || []
        this.totalElements = data.totalElements || 0
        this.totalPages = data.totalPages || 0
        this.currentPage = data.number || 0
      } catch (error) {
        this.error = error.response?.data?.message || '加载用户列表失败，请稍后重试。'
        this.userList = []
      } finally {
        this.loading = false
      }
    },

    async handleSearch() {
      this.currentPage = 0
      await this.loadUsers()
    },

    async handleFilterChange() {
      this.currentPage = 0
      await this.loadUsers()
    },

    async handleNextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++
        await this.loadUsers()
      }
    },

    async handlePreviousPage() {
      if (this.currentPage > 0) {
        this.currentPage--
        await this.loadUsers()
      }
    },

    async handleToggleStatus(user) {
      const newStatus = user.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'
      const confirmMessage = `确认要${newStatus === 'ENABLED' ? '启用' : '禁用'}该用户吗？`

      if (!confirm(confirmMessage)) {
        return
      }

      this.actionLoading = user.id
      this.errorMessage = ''
      this.successMessage = ''

      try {
        await adminService.updateUserStatus(user.id, newStatus)
        user.status = newStatus
        this.successMessage = `用户状态已更新为：${newStatus === 'ENABLED' ? '启用' : '禁用'}`
        setTimeout(() => {
          this.successMessage = ''
        }, 3000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || '更新用户状态失败'
        setTimeout(() => {
          this.errorMessage = ''
        }, 3000)
      } finally {
        this.actionLoading = null
      }
    },

    async handleResetPassword(user) {
      const confirmMessage = `确认要重置用户「${user.username}」的密码吗？`

      if (!confirm(confirmMessage)) {
        return
      }

      this.actionLoading = user.id
      this.errorMessage = ''
      this.successMessage = ''

      try {
        const response = await adminService.resetUserPassword(user.id)
        const tempPassword = response.data.data.tempPassword

        this.successMessage = `密码重置成功，临时密码：${tempPassword}`
        setTimeout(() => {
          this.successMessage = ''
        }, 5000)
      } catch (error) {
        this.errorMessage = error.response?.data?.message || '重置密码失败'
        setTimeout(() => {
          this.errorMessage = ''
        }, 3000)
      } finally {
        this.actionLoading = null
      }
    },

    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      // 改为中文格式（大陆习惯）
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  }
}
</script>




<style scoped>
.admin-users-container {
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

.users-content {
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

.users-table-wrapper {
  background: white;
  border-radius: 0;
  overflow-x: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.users-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.users-table thead {
  background: #f8f9fa;
  border-bottom: 2px solid #ddd;
}

.users-table th {
  padding: 15px;
  text-align: left;
  font-weight: 600;
  color: #333;
}

.users-table td {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.users-table tbody tr:hover {
  background: #f8f9fa;
}

.role-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.role-admin {
  background: #fadbd8;
  color: #c0392b;
}

.role-user {
  background: #d5f4e6;
  color: #27ae60;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.status-enabled {
  background: #d5f4e6;
  color: #27ae60;
}

.status-disabled {
  background: #fadbd8;
  color: #c0392b;
}

.actions-cell {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
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

.btn-toggle-status {
  background: #fef5e7;
  color: #d68910;
  border: 1px solid #f8c471;
}

.btn-toggle-status:hover:not(:disabled) {
  background: #f8c471;
  color: white;
}

.btn-reset-password {
  background: #d6eaf8;
  color: #1f618d;
  border: 1px solid #85c1e2;
}

.btn-reset-password:hover:not(:disabled) {
  background: #85c1e2;
  color: white;
}

.btn-action:disabled {
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
  border-radius: 0 0 8px 8px;
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
  .admin-users-container {
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

  .users-table {
    font-size: 12px;
  }

  .users-table th,
  .users-table td {
    padding: 10px;
  }

  .actions-cell {
    flex-direction: column;
  }

  .btn-action {
    width: 100%;
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
