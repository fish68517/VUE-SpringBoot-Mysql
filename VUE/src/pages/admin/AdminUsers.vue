<template>
  <div class="admin-users-page">
        <div class="page-header">
      <h1>用户管理</h1>
      <button class="btn btn-primary" @click="openCreateModal">
        <span>➕</span> 创建用户
      </button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter-section">
      <div class="search-box">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索用户名或邮箱..."
          @keyup.enter="handleSearch"
          class="search-input"
        />
        <button @click="handleSearch" class="btn btn-secondary">搜索</button>
      </div>
      <div class="filter-box">
        <select v-model="roleFilter" class="filter-select">
          <option value="">所有角色</option>
          <option value="user">普通用户</option>
          <option value="admin">管理员</option>
        </select>
        <button @click="resetFilters" class="btn btn-secondary">重置</button>
      </div>
    </div>

    <!-- 用户列表表格 -->
    <div class="table-container" v-if="!showUserDetail">
      <table class="users-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="users.length === 0">
            <td colspan="6" class="empty-message">暂无用户数据</td>
          </tr>
          <tr v-for="user in users" :key="user.id" class="user-row">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>
              <span class="role-badge" :class="`role-${user.role}`">
                {{ user.role === 'admin' ? '管理员' : '普通用户' }}
              </span>
            </td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td class="action-cell">
              <button @click="viewUserDetail(user)" class="btn-action btn-view" title="查看详情">
                👁️
              </button>
              <button @click="editUser(user)" class="btn-action btn-edit" title="编辑">
                ✏️
              </button>
              <button @click="viewUserLogs(user)" class="btn-action btn-logs" title="查看日志">
                📋
              </button>
              <button @click="sendMessage(user)" class="btn-action btn-message" title="发送消息" v-if="false">
                💬
              </button>
              <button @click="deleteUserConfirm(user)" class="btn-action btn-delete" title="删除">
                🗑️
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 分页 -->
      <Pagination
        :current-page="currentPage"
        :total-pages="totalPages"
        :total-items="totalUsers"
        @update:current-page="handlePageChange"
      />
    </div>

    <!-- 用户详情视图 -->
    <div class="user-detail-view" v-if="showUserDetail">
      <button @click="showUserDetail = false" class="btn btn-secondary">← 返回列表</button>
      <div class="detail-card">
        <h2>用户详情</h2>
        <div class="detail-grid">
          <div class="detail-item">
            <label>用户ID:</label>
            <span>{{ selectedUser.id }}</span>
          </div>
          <div class="detail-item">
            <label>用户名:</label>
            <span>{{ selectedUser.username }}</span>
          </div>
          <div class="detail-item">
            <label>邮箱:</label>
            <span>{{ selectedUser.email }}</span>
          </div>
          <div class="detail-item">
            <label>角色:</label>
            <span class="role-badge" :class="`role-${selectedUser.role}`">
              {{ selectedUser.role === 'admin' ? '管理员' : '普通用户' }}
            </span>
          </div>
          <div class="detail-item">
            <label>创建时间:</label>
            <span>{{ formatDate(selectedUser.createdAt) }}</span>
          </div>
          <div class="detail-item">
            <label>更新时间:</label>
            <span>{{ formatDate(selectedUser.updatedAt) }}</span>
          </div>
        </div>

        <!-- 用户行为日志 -->
        <div class="logs-section">
          <h3>用户行为日志</h3>
          <div class="logs-container" v-if="userLogs.length > 0">
            <div v-for="log in userLogs" :key="log.id" class="log-item">
              <span class="log-action">{{ log.action }}</span>
              <span class="log-target">{{ log.targetType }}</span>
              <span class="log-time">{{ formatDate(log.createdAt) }}</span>
            </div>
          </div>
          <div v-else class="empty-message">暂无行为日志</div>
          <Pagination
            v-if="userLogsTotalPages > 1"
            :current-page="userLogsCurrentPage"
            :total-pages="userLogsTotalPages"
            :total-items="userLogsTotalItems"
            @update:current-page="handleUserLogsPageChange"
          />
        </div>
      </div>
    </div>

    <Teleport to="body">

            <!-- 创建/编辑用户模态框 -->
        <div class="modal-overlay" v-if="showCreateUserModal || showEditUserModal" @click.self="closeModals">
          <div class="admin-modal">
            <div class="modal-header">
              <h2>{{ showEditUserModal ? '编辑用户' : '创建用户' }}</h2>
              <button @click="closeModals" class="modal-close">×</button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label>用户名 *</label>
                <input
                  v-model="formData.username"
                  type="text"
                  placeholder="输入用户名"
                  class="form-input"
                  :disabled="showEditUserModal"
                />
              </div>
              <div class="form-group">
                <label>邮箱 *</label>
                <input
                  v-model="formData.email"
                  type="email"
                  placeholder="输入邮箱"
                  class="form-input"
                />
              </div>
              <div class="form-group" v-if="!showEditUserModal">
                <label>密码 *</label>
                <input
                  v-model="formData.password"
                  type="password"
                  placeholder="输入密码"
                  class="form-input"
                />
              </div>
              <div class="form-group">
                <label>角色 *</label>
                <select v-model="formData.role" class="form-input">
                  <option value="user">普通用户</option>
                  <option value="admin">管理员</option>
                </select>
              </div>
            </div>
            <div class="modal-footer">
              <button @click="closeModals" class="btn btn-secondary">取消</button>
              <button @click="saveUser" class="btn btn-primary" :disabled="isSaving">
                {{ isSaving ? '保存中...' : '保存' }}
              </button>
            </div>
          </div>
        </div>

        <!-- 发送消息模态框 -->
        <div class="modal-overlay" v-if="showMessageModal" @click.self="closeMessageModal">
          <div class="admin-modal">
            <div class="modal-header">
              <h2>发送消息给 {{ selectedUser.username }}</h2>
              <button @click="closeMessageModal" class="modal-close">×</button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label>消息标题 *</label>
                <input
                  v-model="messageData.title"
                  type="text"
                  placeholder="输入消息标题"
                  class="form-input"
                />
              </div>
              <div class="form-group">
                <label>消息内容 *</label>
                <textarea
                  v-model="messageData.content"
                  placeholder="输入消息内容"
                  class="form-textarea"
                  rows="5"
                ></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button @click="closeMessageModal" class="btn btn-secondary">取消</button>
              <button @click="sendMessageConfirm" class="btn btn-primary" :disabled="isSending">
                {{ isSending ? '发送中...' : '发送' }}
              </button>
            </div>
          </div>
        </div>

        <!-- 删除确认模态框 -->
        <div class="modal-overlay" v-if="showDeleteConfirm" @click.self="showDeleteConfirm = false">
          <div class="admin-modal admin-modal-small">
            <div class="modal-header">
              <h2>确认删除</h2>
              <button @click="showDeleteConfirm = false" class="modal-close">×</button>
            </div>
            <div class="modal-body">
              <p>确定要删除用户 <strong>{{ userToDelete.username }}</strong> 吗？此操作不可撤销。</p>
            </div>
            <div class="modal-footer">
              <button @click="showDeleteConfirm = false" class="btn btn-secondary">取消</button>
              <button @click="confirmDelete" class="btn btn-danger" :disabled="isDeleting">
                {{ isDeleting ? '删除中...' : '确认删除' }}
              </button>
            </div>
          </div>
        </div>


    </Teleport>
   

    <!-- Toast 通知 -->
    <Toast ref="toast" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { AdminUserService } from '../../services'
import Pagination from '../../components/Pagination.vue'
import Toast from '../../components/Toast.vue'

// 状态
const users = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalUsers = ref(0)
const searchQuery = ref('')
const roleFilter = ref('')

const selectedUser = ref(null)
const showUserDetail = ref(false)
const userLogs = ref([])
const userLogsCurrentPage = ref(1)
const userLogsTotalPages = ref(1)
const userLogsTotalItems = ref(0)

const showCreateUserModal = ref(false)
const showEditUserModal = ref(false)
const showMessageModal = ref(false)
const showDeleteConfirm = ref(false)

const formData = ref({
  username: '',
  email: '',
  password: '',
  role: 'user'
})

const messageData = ref({
  title: '',
  content: ''
})

const userToDelete = ref(null)
const isSaving = ref(false)
const isSending = ref(false)
const isDeleting = ref(false)
const isLoading = ref(false)

const toast = ref(null)

// 计算属性
const totalPages = computed(() => Math.ceil(totalUsers.value / pageSize.value))

// 计算属性

// ================= 新增这个方法 =================
const openCreateModal = () => {
  // 1. 先清空表单，防止上一次输入的数据残留
  console.log('清空表单')
  formData.value = {
    username: '',
    email: '',
    password: '',
    role: 'user'
  }
  // 2. 触发弹窗显示
  showCreateUserModal.value = true
}
// ==============================================

// 方法
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const loadUsers = async () => {
  isLoading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      // 👇 新增下面这两行，把输入框的值传给后端
      keyword: searchQuery.value,
      role: roleFilter.value
    }
    const response = await AdminUserService.getUsers(params)
    if (response && response.users) {
      users.value = response.users || []
      totalUsers.value = response.total || 0
    } else {
      toast.value.error('加载用户列表失败')
    }
  } catch (error) {
    console.error('加载用户列表错误:', error)
    toast.value.error('加载用户列表出错')
  } finally {
    isLoading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadUsers()
}

const resetFilters = () => {
  searchQuery.value = ''
  roleFilter.value = ''
  currentPage.value = 1
  loadUsers()
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadUsers()
}

const viewUserDetail = (user) => {
  selectedUser.value = user
  showUserDetail.value = true
  loadUserLogs(user.id)
}

const loadUserLogs = async (userId) => {
  try {
    const params = {
      pageNum: userLogsCurrentPage.value,
      pageSize: 10
    }
    const response = await AdminUserService.getUserLogs(userId, params)
    if (response && response.logs) {
      userLogs.value = response.logs || []
      userLogsTotalItems.value = response.total || 0
      userLogsTotalPages.value = Math.ceil(userLogsTotalItems.value / 10)
    }
  } catch (error) {
    console.error('加载用户日志错误:', error)
    toast.value.error('加载用户日志失败')
  }
}

const handleUserLogsPageChange = (page) => {
  userLogsCurrentPage.value = page
  loadUserLogs(selectedUser.value.id)
}

const editUser = (user) => {
  selectedUser.value = user
  formData.value = {
    username: user.username,
    email: user.email,
    password: '',
    role: user.role
  }
  showEditUserModal.value = true
}

const saveUser = async () => {
  if (!formData.value.username || !formData.value.email) {
    toast.value.warning('请填写必填项')
    return
  }

  isSaving.value = true
  try {
    let response
    if (showEditUserModal.value) {
      response = await AdminUserService.updateUser(selectedUser.value.id, formData.value)
    } else {
      if (!formData.value.password) {
        toast.value.warning('请输入密码')
        isSaving.value = false
        return
      }
      response = await AdminUserService.createUser(formData.value)
    }

    if (response) {
      toast.value.success(showEditUserModal.value ? '用户更新成功' : '用户创建成功')
      closeModals()
      loadUsers()
    } else {
      toast.value.error('操作失败')
    }
  } catch (error) {
    console.error('保存用户错误:', error)
    toast.value.error('保存用户失败')
  } finally {
    isSaving.value = false
  }
}

const deleteUserConfirm = (user) => {
  userToDelete.value = user
  showDeleteConfirm.value = true
}

const confirmDelete = async () => {
  isDeleting.value = true
  try {
    const response = await AdminUserService.deleteUser(userToDelete.value.id)
    console.log('删除结果:', response)
    if (true) {
      toast.value.success('用户删除成功')
      showDeleteConfirm.value = false
      loadUsers()
    } else {
      toast.value.error('删除失败')
    }
  } catch (error) {
    console.error('删除用户错误:', error)
    toast.value.error('删除用户失败')
  } finally {
    isDeleting.value = false
  }
}

const viewUserLogs = (user) => {
  viewUserDetail(user)
}

const sendMessage = (user) => {
  selectedUser.value = user
  messageData.value = {
    title: '',
    content: ''
  }
  showMessageModal.value = true
}

const sendMessageConfirm = async () => {
  if (!messageData.value.title || !messageData.value.content) {
    toast.value.warning('请填写消息标题和内容')
    return
  }

  isSending.value = true
  try {
    const payload = {
      userId: selectedUser.value.id,
      title: messageData.value.title,
      content: messageData.value.content
    }
    const response = await AdminUserService.sendMessage(payload)
    if (response) {
      toast.value.success('消息发送成功')
      closeMessageModal()
    } else {
      toast.value.error('发送失败')
    }
  } catch (error) {
    console.error('发送消息错误:', error)
    toast.value.error('发送消息失败')
  } finally {
    isSending.value = false
  }
}

const closeModals = () => {
  showCreateUserModal.value = false
  showEditUserModal.value = false
  formData.value = {
    username: '',
    email: '',
    password: '',
    role: 'user'
  }
}

const closeMessageModal = () => {
  showMessageModal.value = false
  messageData.value = {
    title: '',
    content: ''
  }
}

// 生命周期
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users-page {
  padding: var(--spacing-lg);
  background-color: var(--bg-secondary);
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.page-header h1 {
  color: var(--primary-color);
  margin: 0;
  font-size: var(--font-size-2xl);
}

.search-filter-section {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  flex-wrap: wrap;
}

.search-box,
.filter-box {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
}

.search-input,
.filter-select {
  padding: var(--spacing-md);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-base);
  background-color: var(--bg-primary);
  color: var(--text-primary);
}

.search-input {
  min-width: 250px;
}

.filter-select {
  min-width: 150px;
}

.table-container {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table thead {
  background-color: var(--primary-color);
  color: white;
}

.users-table th {
  padding: var(--spacing-md);
  text-align: left;
  font-weight: 600;
  border-bottom: 2px solid var(--border-color);
}

.users-table td {
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
}

.user-row:hover {
  background-color: var(--bg-secondary);
}

.empty-message {
  text-align: center;
  color: var(--text-secondary);
  padding: var(--spacing-lg) !important;
}

.role-badge {
  display: inline-block;
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
  font-weight: 600;
}

.role-admin {
  background-color: #ff6b6b;
  color: white;
}

.role-user {
  background-color: #4ecdc4;
  color: white;
}

.action-cell {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.btn-action {
  background: none;
  border: none;
  font-size: var(--font-size-lg);
  cursor: pointer;
  padding: var(--spacing-xs);
  transition: transform 0.2s ease;
}

.btn-action:hover {
  transform: scale(1.2);
}

.btn-view {
  color: #0066cc;
}

.btn-edit {
  color: #ffa500;
}

.btn-logs {
  color: #9b59b6;
}

.btn-message {
  color: #27ae60;
}

.btn-delete {
  color: #e74c3c;
}

/* 用户详情视图 */
.user-detail-view {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
}

.detail-card {
  margin-top: var(--spacing-lg);
}

.detail-card h2 {
  color: var(--primary-color);
  margin-bottom: var(--spacing-lg);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.detail-item label {
  font-weight: 600;
  color: var(--text-primary);
}

.detail-item span {
  color: var(--text-secondary);
  padding: var(--spacing-md);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
}

.logs-section {
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.logs-section h3 {
  color: var(--primary-color);
  margin-bottom: var(--spacing-md);
}

.logs-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  max-height: 400px;
  overflow-y: auto;
}

.log-item {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
}

.log-action {
  font-weight: 600;
  color: var(--primary-color);
  min-width: 80px;
}

.log-target {
  color: var(--text-secondary);
  flex: 1;
}

.log-time {
  color: var(--text-light);
  white-space: nowrap;
}

/* 按钮样式 */
.btn {
  padding: var(--spacing-md) var(--spacing-lg);
  border: none;
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-base);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.btn-primary {
  background-color: var(--primary-color);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.btn-secondary {
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover:not(:disabled) {
  background-color: var(--border-color);
}

.btn-danger {
  background-color: #e74c3c;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c0392b;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(16, 134, 202, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
   z-index: 9999 !important; /* 强制最高层级，防止被导航栏遮挡 */
}

.admin-modal {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;

  /* ✅ 关键：避免被任何全局 modal 样式影响 */
  display: block !important;
  opacity: 1 !important;
  visibility: visible !important;
}

.admin-modal-small {
  max-width: 400px;
}


.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
}

.modal-header h2 {
  margin: 0;
  color: var(--primary-color);
}

.modal-close {
  background: none;
  border: none;
  font-size: var(--font-size-2xl);
  cursor: pointer;
  color: var(--text-secondary);
  transition: color 0.3s ease;
}

.modal-close:hover {
  color: var(--text-primary);
}

.modal-body {
  padding: var(--spacing-lg);
}

.modal-footer {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
  padding: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.form-group {
  margin-bottom: var(--spacing-lg);
}

.form-group label {
  display: block;
  margin-bottom: var(--spacing-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.form-input,
.form-textarea {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-base);
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  font-family: inherit;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.1);
}

.form-input:disabled {
  background-color: var(--bg-secondary);
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .admin-users-page {
    padding: var(--spacing-md);
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .search-filter-section {
    flex-direction: column;
  }

  .search-box,
  .filter-box {
    width: 100%;
  }

  .search-input,
  .filter-select {
    flex: 1;
  }

  .users-table {
    font-size: var(--font-size-sm);
  }

  .users-table th,
  .users-table td {
    padding: var(--spacing-sm);
  }

  .action-cell {
    flex-direction: column;
  }

  .modal {
    max-width: 95%;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
