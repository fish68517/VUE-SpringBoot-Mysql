<template>
  <div class="user-management-layout">
    <Sidebar />
    
    <div class="main-content">
      <Header />
      
      <div class="content-area">
        <div class="user-management-container">
          <div class="page-header">
            <h2>用户管理</h2>
            <p>管理平台用户信息</p>
          </div>

          <!-- Search and Filter Section -->
          <div class="search-section">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索用户名或邮箱..."
              prefix-icon="Search"
              clearable
              @input="handleSearch"
              class="search-input"
            />
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              <span>搜索</span>
            </el-button>
            <el-button @click="handleRefresh">
              <el-icon><Refresh /></el-icon>
              <span>刷新</span>
            </el-button>
          </div>

          <!-- User Table -->
          <div class="table-section">
            <el-table
              :data="userList"
              stripe
              style="width: 100%"
              :loading="loading"
              v-loading="loading"
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="username" label="用户名" min-width="120" />
              <el-table-column prop="email" label="邮箱" min-width="150" />
              <el-table-column prop="height" label="身高(cm)" width="100" />
              <el-table-column prop="weight" label="体重(kg)" width="100" />
              <el-table-column prop="createdAt" label="注册时间" min-width="180">
                <template #default="{ row }">
                  {{ formatDate(row.createdAt) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="{ row }">
                  <el-button
                    type="primary"
                    size="small"
                    @click="handleEdit(row)"
                  >
                    编辑
                  </el-button>
                  <el-button
                    type="danger"
                    size="small"
                    @click="handleDelete(row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- Pagination -->
          <div class="pagination-section">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange"
              @size-change="handlePageSizeChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Edit User Dialog -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑用户信息"
      width="500px"
      @close="resetEditForm"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editFormRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="身高(cm)" prop="height">
          <el-input-number v-model="editForm.height" :min="0" :max="300" />
        </el-form-item>
        <el-form-item label="体重(kg)" prop="weight">
          <el-input-number v-model="editForm.weight" :min="0" :max="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit" :loading="editLoading">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import Sidebar from '../components/Sidebar.vue'
import Header from '../components/Header.vue'
import { getUserList, updateUser, deleteUser } from '../api/user.js'
import { showSuccess, showError, showWarning } from '../utils/notification.js'

const userList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const loading = ref(false)

const editDialogVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref(null)
const editForm = ref({
  id: null,
  username: '',
  email: '',
  height: null,
  weight: null
})

const editFormRules = {
  email: [
    { required: true, message: '邮箱不能为空', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  height: [
    { required: false, message: '身高不能为空', trigger: 'blur' }
  ],
  weight: [
    { required: false, message: '体重不能为空', trigger: 'blur' }
  ]
}

/**
 * Format date to readable string
 */
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

/**
 * Load user list from API
 */
const loadUserList = async () => {
  loading.value = true
  try {
    const response = await getUserList(currentPage.value, pageSize.value, searchKeyword.value)
    
    if (response.code === 200) {
      userList.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      showError(response.message || '获取用户列表失败', '加载失败')
    }
  } catch (error) {
    console.error('Error loading user list:', error)
    showError('获取用户列表失败，请检查网络连接', '加载失败')
  } finally {
    loading.value = false
  }
}

/**
 * Handle search
 */
const handleSearch = () => {
  currentPage.value = 1
  loadUserList()
}

/**
 * Handle refresh
 */
const handleRefresh = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  loadUserList()
}

/**
 * Handle page change
 */
const handlePageChange = () => {
  loadUserList()
}

/**
 * Handle page size change
 */
const handlePageSizeChange = () => {
  currentPage.value = 1
  loadUserList()
}

/**
 * Handle edit user
 */
const handleEdit = (row) => {
  editForm.value = {
    id: row.id,
    username: row.username,
    email: row.email,
    height: row.height,
    weight: row.weight
  }
  editDialogVisible.value = true
}

/**
 * Handle save edit
 */
const handleSaveEdit = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
  } catch (error) {
    return
  }

  editLoading.value = true
  try {
    const response = await updateUser(editForm.value.id, {
      email: editForm.value.email,
      height: editForm.value.height,
      weight: editForm.value.weight
    })

    if (response.code === 200) {
      showSuccess('用户信息更新成功', '更新成功')
      editDialogVisible.value = false
      loadUserList()
    } else {
      showError(response.message || '更新用户信息失败', '更新失败')
    }
  } catch (error) {
    console.error('Error updating user:', error)
    showError('更新用户信息失败，请检查网络连接', '更新失败')
  } finally {
    editLoading.value = false
  }
}

/**
 * Handle delete user
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${row.username}" 吗？此操作不可撤销。`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      try {
        const response = await deleteUser(row.id)

        if (response.code === 200) {
          showSuccess('用户删除成功', '删除成功')
          loadUserList()
        } else {
          showError(response.message || '删除用户失败', '删除失败')
        }
      } catch (error) {
        console.error('Error deleting user:', error)
        showError('删除用户失败，请检查网络连接', '删除失败')
      }
    })
    .catch(() => {
      // User cancelled the deletion
    })
}

/**
 * Reset edit form
 */
const resetEditForm = () => {
  editForm.value = {
    id: null,
    username: '',
    email: '',
    height: null,
    weight: null
  }
  if (editFormRef.value) {
    editFormRef.value.clearValidate()
  }
}

/**
 * Load user list on component mount
 */
onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.user-management-layout {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.user-management-container {
  max-width: 1400px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 5px 0;
  letter-spacing: -0.5px;
}

.page-header p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.search-section {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.5s ease 0.1s both;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.search-input {
  flex: 1;
  max-width: 300px;
}

.table-section {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.5s ease 0.2s both;
}

.table-section :deep(.el-table) {
  font-size: 14px;
  border-radius: 8px;
}

.table-section :deep(.el-table__header th) {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #333;
}

.table-section :deep(.el-table__body tr:hover > td) {
  background-color: #f9f9f9;
}

.pagination-section {
  background: white;
  border-radius: 12px;
  padding: 15px;
  display: flex;
  justify-content: flex-end;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  animation: slideUp 0.5s ease 0.3s both;
}

.pagination-section :deep(.el-pagination) {
  display: flex;
  gap: 10px;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #e0e0e0;
  padding: 16px 20px;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: #333;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}
</style>
