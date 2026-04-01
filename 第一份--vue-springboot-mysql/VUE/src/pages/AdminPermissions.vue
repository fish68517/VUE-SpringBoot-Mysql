<template>
  <div class="admin-permissions">
    
    <div class="admin-container">
      
      <div class="permissions-content">
        <h1>操作管理</h1>

        <!-- 角色管理部分 -->
        <div class="roles-section"  v-if="false">
          <h2>角色管理</h2>

          <!-- 创建角色表单 -->
          <div class="create-role-form">
            <h3>创建新角色</h3>
            <div class="form-group">
              <label>角色名称</label>
              <input
                v-model="newRole.roleName"
                type="text"
                placeholder="输入角色名称"
              />
            </div>
            <div class="form-group">
              <label>角色描述</label>
              <textarea
                v-model="newRole.description"
                placeholder="输入角色描述"
                rows="3"
              ></textarea>
            </div>
            <button @click="handleCreateRole" class="btn btn-primary">
              创建角色
            </button>
          </div>

          <!-- 角色列表 -->
          <div class="roles-list-section">
            <h3>角色列表</h3>

            <div v-if="loadingRoles" class="loading-indicator">
              <div class="spinner"></div>
              <p>加载中...</p>
            </div>

            <div v-else-if="roles.length > 0" class="roles-list">
              <div v-for="role in roles" :key="role.id" class="role-card">
                <div class="role-header">
                  <h4>{{ role.roleName }}</h4>
                  <button
                    @click="handleEditRole(role)"
                    class="btn btn-small btn-info"
                  >
                    编辑权限
                  </button>
                </div>
                <p class="role-description">{{ role.description || '无描述' }}</p>
                <div class="role-permissions">
                  <span v-if="rolePermissionsMap[role.id]?.length > 0" class="permissions-label">
                    权限: 
                  </span>
                  <div v-if="rolePermissionsMap[role.id]?.length > 0" class="permissions-list">
                    <span
                      v-for="perm in rolePermissionsMap[role.id]"
                      :key="perm.id"
                      class="permission-tag"
                    >
                      {{ perm.permissionName }}
                    </span>
                  </div>
                  <p v-else class="no-permissions">暂无权限</p>
                </div>
              </div>
            </div>

            <div v-else class="empty-state">
              <p>暂无角色数据</p>
            </div>
          </div>
        </div>

        <!-- 权限编辑模态框 -->
        <div v-if="showPermissionModal" class="modal-overlay" @click="closePermissionModal">
          <div class="modal" @click.stop>
            <div class="modal-header">
              <h3>编辑角色权限 - {{ selectedRole?.roleName }}</h3>
              <button @click="closePermissionModal" class="close-btn">×</button>
            </div>
            <div class="modal-body">
              <div class="permissions-selection">
                <h4>选择权限</h4>
                <div class="permissions-grid">
                  <div v-for="perm in availablePermissions" :key="perm" class="permission-checkbox">
                    <input
                      type="checkbox"
                      :id="'perm-' + perm"
                      :value="perm"
                      v-model="selectedPermissions"
                    />
                    <label :for="'perm-' + perm">{{ perm }}</label>
                  </div>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button @click="closePermissionModal" class="btn btn-secondary">
                取消
              </button>
              <button @click="handleSavePermissions" class="btn btn-primary">
                保存权限
              </button>
            </div>
          </div>
        </div>

        <!-- 管理员操作日志部分 -->
        <div class="audit-logs-section">
          <h2>管理员操作日志</h2>

          <!-- 筛选条件 -->
          <div class="filter-bar">
            <div class="filter-group">
              <label>管理员ID</label>
              <input
                v-model="auditLogFilters.adminId"
                type="text"
                placeholder="输入管理员ID"
              />
            </div>
            <div class="filter-group">
              <label>操作类型</label>
              <select v-model="auditLogFilters.operationType">
                <option value="">全部操作</option>
                <option value="CREATE">创建</option>
                <option value="UPDATE">更新</option>
                <option value="DELETE">删除</option>
                <option value="REVIEW">审核</option>
              </select>
            </div>
            <button @click="handleFilterAuditLogs" class="btn btn-secondary">
              筛选
            </button>
            <button @click="handleResetAuditLogs" class="btn btn-secondary">
              重置
            </button>
          </div>

          <!-- 加载状态 -->
          <div v-if="loadingAuditLogs" class="loading-indicator">
            <div class="spinner"></div>
            <p>加载中...</p>
          </div>

          <!-- 日志表格 -->
          <div v-else-if="auditLogs.length > 0" class="audit-logs-table">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>管理员ID</th>
                  <th>操作类型</th>
                  <th>目标类型</th>
                  <th>目标ID</th>
                  <th>操作时间</th>
                  <th>详情</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="log in auditLogs" :key="log.id">
                  <td>{{ log.id }}</td>
                  <td>{{ log.adminId }}</td>
                  <td>
                    <span :class="['operation-badge', 'op-' + log.operationType.toLowerCase()]">
                      {{ log.operationType }}
                    </span>
                  </td>
                  <td>{{ log.targetType || '-' }}</td>
                  <td>{{ log.targetId || '-' }}</td>
                  <td>{{ formatDate(log.createdAt) }}</td>
                  <td class="details-cell">{{ log.details || '-' }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <p>暂无操作日志</p>
          </div>

          <!-- 分页 -->
          <div v-if="auditLogsTotalPages > 1" class="pagination">
            <button
              @click="auditLogsCurrentPage--"
              :disabled="auditLogsCurrentPage === 0"
              class="btn btn-secondary"
            >
              上一页
            </button>
            <span class="page-info">
              第 {{ auditLogsCurrentPage + 1 }} / {{ auditLogsTotalPages }} 页
            </span>
            <button
              @click="auditLogsCurrentPage++"
              :disabled="auditLogsCurrentPage >= auditLogsTotalPages - 1"
              class="btn btn-secondary"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { adminAPI } from '../services/api'

import { ElMessage, ElMessageBox } from 'element-plus'

// Role management state
const roles = ref([])
const loadingRoles = ref(false)
const newRole = ref({
  roleName: '',
  description: ''
})
const showPermissionModal = ref(false)
const selectedRole = ref(null)
const selectedPermissions = ref([])
const rolePermissionsMap = ref({})

// Available permissions
const availablePermissions = [
  '素材管理',
  '用户管理',
  '内容审核',
  '日志管理',
  '参数设置',
  '权限管理'
]

// Audit logs state
const auditLogs = ref([])
const loadingAuditLogs = ref(false)
const auditLogsCurrentPage = ref(0)
const auditLogsPageSize = ref(10)
const auditLogsTotalPages = ref(1)
const auditLogFilters = ref({
  adminId: '',
  operationType: ''
})

// Load roles
const loadRoles = async () => {
  loadingRoles.value = true
  try {
    const response = await adminAPI.getRoles()
    if (response.code === 200) {
      roles.value = response.data || []
      // Load permissions for each role
      for (const role of roles.value) {
        await loadRolePermissions(role.id)
      }
    } else {
      ElMessage.error(response.message || '获取角色列表失败')
    }
  } catch (error) {
    ElMessage.error('获取角色列表失败: ' + error.message)
  } finally {
    loadingRoles.value = false
  }
}

// Load role permissions
const loadRolePermissions = async (roleId) => {
  try {
    // Note: The backend doesn't have a dedicated endpoint to get permissions for a role
    // We'll store empty permissions for now, which will be populated when editing
    if (!rolePermissionsMap.value[roleId]) {
      rolePermissionsMap.value[roleId] = []
    }
  } catch (error) {
    console.error('获取角色权限失败:', error)
  }
}

// Create role
const handleCreateRole = async () => {
  if (!newRole.value.roleName.trim()) {
    ElMessage.error('角色名称不能为空')
    return
  }

  try {
    const response = await adminAPI.createRole({
      roleName: newRole.value.roleName,
      description: newRole.value.description
    })

    if (response.code === 200) {
      ElMessage.success('角色创建成功')
      newRole.value = {
        roleName: '',
        description: ''
      }
      loadRoles()
    } else {
      ElMessage.error(response.message || '创建角色失败')
    }
  } catch (error) {
    ElMessage.error('创建角色失败: ' + error.message)
  }
}

// Edit role permissions
const handleEditRole = (role) => {
  selectedRole.value = role
  selectedPermissions.value = rolePermissionsMap.value[role.id]?.map(p => p.permissionName) || []
  showPermissionModal.value = true
}

// Save permissions
const handleSavePermissions = async () => {
  if (!selectedRole.value) return

  try {
    const response = await adminAPI.updateRole(selectedRole.value.id, {
      permissionNames: selectedPermissions.value
    })

    if (response.code === 200) {
      ElMessage.success('权限更新成功')
      // Update the permissions map
      rolePermissionsMap.value[selectedRole.value.id] = selectedPermissions.value.map((name, index) => ({
        id: index,
        permissionName: name
      }))
      closePermissionModal()
    } else {
      ElMessage.error(response.message || '更新权限失败')
    }
  } catch (error) {
    ElMessage.error('更新权限失败: ' + error.message)
  }
}

// Close permission modal
const closePermissionModal = () => {
  showPermissionModal.value = false
  selectedRole.value = null
  selectedPermissions.value = []
}

// Load audit logs
const loadAuditLogs = async () => {
  loadingAuditLogs.value = true
  try {
    const params = {
      page: auditLogsCurrentPage.value,
      size: auditLogsPageSize.value
    }

    if (auditLogFilters.value.adminId) {
      params.adminId = auditLogFilters.value.adminId
    }
    if (auditLogFilters.value.operationType) {
      params.operationType = auditLogFilters.value.operationType
    }

    const response = await adminAPI.getAuditLogs(params)
    if (response.code === 200) {
      auditLogs.value = response.data.content || []
      auditLogsTotalPages.value = response.data.totalPages || 1
    } else {
      ElMessage.error(response.message || '获取操作日志失败')
    }
  } catch (error) {
    ElMessage.error('获取操作日志失败: ' + error.message)
  } finally {
    loadingAuditLogs.value = false
  }
}

// Filter audit logs
const handleFilterAuditLogs = () => {
  auditLogsCurrentPage.value = 0
  loadAuditLogs()
}

// Reset audit logs
const handleResetAuditLogs = () => {
  auditLogFilters.value = {
    adminId: '',
    operationType: ''
  }
  auditLogsCurrentPage.value = 0
  loadAuditLogs()
}

// Format date
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN')
}

// Watch page changes
watch(() => auditLogsCurrentPage.value, () => {
  loadAuditLogs()
})

// Lifecycle
onMounted(() => {
  loadRoles()
  loadAuditLogs()
})
</script>

<style scoped>
.admin-permissions {
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

.permissions-content {
  flex: 1;
}

.permissions-content h1 {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #333;
}

.permissions-content h2 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #333;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #667eea;
}

.permissions-content h3 {
  font-size: 1.1rem;
  margin-bottom: 1rem;
  color: #333;
}

.permissions-content h4 {
  font-size: 1rem;
  margin-bottom: 0.75rem;
  color: #333;
}

/* Roles Section */
.roles-section {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.create-role-form {
  background-color: #f9f9f9;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
}

/* Roles List */
.roles-list-section {
  margin-top: 2rem;
}

.roles-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.role-card {
  background-color: #f9f9f9;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1.5rem;
  transition: all 0.3s;
}

.role-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.role-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  gap: 1rem;
}

.role-header h4 {
  margin: 0;
  flex: 1;
}

.role-description {
  color: #666;
  font-size: 0.875rem;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.role-permissions {
  margin-top: 1rem;
}

.permissions-label {
  display: block;
  font-weight: 600;
  color: #333;
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
}

.permissions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.permission-tag {
  display: inline-block;
  background-color: #e8f0fe;
  color: #1967d2;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 500;
}

.no-permissions {
  color: #999;
  font-size: 0.875rem;
  margin: 0;
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

/* Empty State */
.empty-state {
  text-align: center;
  padding: 3rem;
  color: #999;
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

.modal-body {
  padding: 1.5rem;
}

.permissions-selection {
  margin-bottom: 1rem;
}

.permissions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 1rem;
}

.permission-checkbox {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.permission-checkbox input {
  cursor: pointer;
}

.permission-checkbox label {
  cursor: pointer;
  font-size: 0.875rem;
  color: #333;
}

.modal-footer {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  padding: 1.5rem;
  border-top: 1px solid #eee;
}

/* Audit Logs Section */
.audit-logs-section {
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

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-group label {
  font-weight: 600;
  color: #333;
  font-size: 0.875rem;
}

.filter-group input,
.filter-group select {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  min-width: 150px;
}

.filter-group input:focus,
.filter-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* Table */
.audit-logs-table {
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
  font-size: 0.875rem;
}

td {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  font-size: 0.875rem;
}

tbody tr:hover {
  background-color: #f9f9f9;
}

.operation-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
}

.op-create {
  background-color: #d4edda;
  color: #155724;
}

.op-update {
  background-color: #cce5ff;
  color: #004085;
}

.op-delete {
  background-color: #f8d7da;
  color: #721c24;
}

.op-review {
  background-color: #fff3cd;
  color: #856404;
}

.details-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

  .permissions-content h1 {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
  }

  .permissions-content h2 {
    font-size: 1.25rem;
    margin-bottom: 1rem;
  }

  .roles-list {
    grid-template-columns: 1fr;
  }

  .filter-bar {
    flex-direction: column;
  }

  .filter-group input,
  .filter-group select {
    min-width: 100%;
  }

  table {
    font-size: 0.75rem;
  }

  th,
  td {
    padding: 0.75rem 0.5rem;
  }

  .modal {
    width: 95%;
  }

  .permissions-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .admin-container {
    padding: 0.5rem;
  }

  .roles-section,
  .audit-logs-section {
    padding: 1rem;
  }

  .role-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .role-header h4 {
    width: 100%;
  }

  .role-header button {
    width: 100%;
  }

  .filter-bar {
    flex-direction: column;
  }

  .filter-group input,
  .filter-group select {
    width: 100%;
  }

  .pagination {
    flex-direction: column;
    gap: 0.5rem;
  }

  .page-info {
    width: 100%;
    text-align: center;
  }
}
</style>
