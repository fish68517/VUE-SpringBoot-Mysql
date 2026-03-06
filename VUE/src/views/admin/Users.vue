<template>
  <div class="users">
    <h1>用户管理</h1>
    <el-tabs>
      <el-tab-pane label="待审核用户">
        <div class="pending-section">
          <el-button type="primary" @click="loadPendingUsers" :loading="pendingLoading">刷新</el-button>
          <el-table :data="pendingUsers" stripe style="margin-top: 20px">
            <el-table-column prop="id" label="用户ID" width="100" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column prop="role" label="角色" width="100" />
            <el-table-column prop="createdAt" label="申请日期" width="180" />
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button link type="success" size="small" @click="handleApprove(row.id)">批准</el-button>
                <el-button link type="danger" size="small" @click="handleReject(row.id)">驳回</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      <el-tab-pane label="所有用户">
        <div class="all-users-section">
          <el-form :model="searchForm" label-width="80px" inline style="margin-bottom: 20px">
            <el-form-item label="用户名">
              <el-input v-model="searchForm.username" placeholder="搜索用户名" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="searchForm.email" placeholder="搜索邮箱" />
            </el-form-item>
            <el-form-item label="角色">
              <el-select v-model="searchForm.role" placeholder="选择角色" clearable>
                <el-option label="作者" value="AUTHOR" />
                <el-option label="编辑" value="EDITOR" />
                <el-option label="审稿人" value="REVIEWER" />
                <el-option label="管理员" value="ADMIN" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="选择状态" clearable>
                <el-option label="待审核" value="PENDING" />
                <el-option label="已批准" value="APPROVED" />
                <el-option label="已驳回" value="REJECTED" />
                <el-option label="活跃" value="ACTIVE" />
                <el-option label="停用" value="INACTIVE" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch" :loading="allUsersLoading">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="allUsers" stripe>
            <el-table-column prop="id" label="用户ID" width="100" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column prop="role" label="角色" width="100" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- Edit User Dialog -->
    <el-dialog v-model="editDialogVisible" title="编辑用户" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="editForm.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role" placeholder="选择角色">
            <el-option label="作者" value="AUTHOR" />
            <el-option label="编辑" value="EDITOR" />
            <el-option label="审稿人" value="REVIEWER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" placeholder="选择状态">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已批准" value="APPROVED" />
            <el-option label="已驳回" value="REJECTED" />
            <el-option label="活跃" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit" :loading="editLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminService } from '../../services/adminService'

const pendingLoading = ref(false)
const allUsersLoading = ref(false)
const editLoading = ref(false)
const editDialogVisible = ref(false)

const pendingUsers = ref([])
const allUsers = ref([])

const searchForm = ref({
  username: '',
  email: '',
  role: '',
  status: ''
})

const editForm = ref({
  id: null,
  username: '',
  email: '',
  phone: '',
  role: '',
  status: ''
})

// Load pending users on component mount
onMounted(async () => {
  await loadPendingUsers()
  await loadAllUsers()
})

// Load pending users
const loadPendingUsers = async () => {
  pendingLoading.value = true
  try {
    const response = await adminService.getPendingUsers()
    if (response.data.code === 200) {
      pendingUsers.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '加载待审核用户失败')
    }
  } catch (error) {
    ElMessage.error('加载待审核用户失败')
    console.error(error)
  } finally {
    pendingLoading.value = false
  }
}

// Load all users
const loadAllUsers = async () => {
  allUsersLoading.value = true
  try {
    const response = await adminService.getAllUsers({})
    if (response.data.code === 200) {
      allUsers.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '加载用户列表失败')
    }
  } catch (error) {
    ElMessage.error('加载用户列表失败')
    console.error(error)
  } finally {
    allUsersLoading.value = false
  }
}

// Search users
const handleSearch = async () => {
  allUsersLoading.value = true
  try {
    const params = {
      username: searchForm.value.username || undefined,
      email: searchForm.value.email || undefined,
      role: searchForm.value.role || undefined,
      status: searchForm.value.status || undefined
    }
    const response = await adminService.getAllUsers(params)
    if (response.data.code === 200) {
      allUsers.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '搜索失败')
    }
  } catch (error) {
    ElMessage.error('搜索失败')
    console.error(error)
  } finally {
    allUsersLoading.value = false
  }
}

// Reset search form
const handleReset = () => {
  searchForm.value = {
    username: '',
    email: '',
    role: '',
    status: ''
  }
  loadAllUsers()
}

// Approve user
const handleApprove = async (userId) => {
  try {
    const response = await adminService.approveUser(userId)
    if (response.data.code === 200) {
      ElMessage.success('用户已批准')
      await loadPendingUsers()
      await loadAllUsers()
    } else {
      ElMessage.error(response.data.message || '批准失败')
    }
  } catch (error) {
    ElMessage.error('批准失败')
    console.error(error)
  }
}

// Reject user
const handleReject = async (userId) => {
  try {
    const response = await adminService.rejectUser(userId)
    if (response.data.code === 200) {
      ElMessage.success('用户已驳回')
      await loadPendingUsers()
      await loadAllUsers()
    } else {
      ElMessage.error(response.data.message || '驳回失败')
    }
  } catch (error) {
    ElMessage.error('驳回失败')
    console.error(error)
  }
}

// Edit user
const handleEdit = (user) => {
  editForm.value = {
    id: user.id,
    username: user.username,
    email: user.email || '',
    phone: user.phone || '',
    role: user.role,
    status: user.status
  }
  editDialogVisible.value = true
}

// Save user edit
const handleSaveEdit = async () => {
  editLoading.value = true
  try {
    const response = await adminService.updateUser(editForm.value.id, {
      email: editForm.value.email,
      phone: editForm.value.phone,
      role: editForm.value.role,
      status: editForm.value.status
    })
    if (response.data.code === 200) {
      ElMessage.success('用户信息已更新')
      editDialogVisible.value = false
      await loadAllUsers()
    } else {
      ElMessage.error(response.data.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败')
    console.error(error)
  } finally {
    editLoading.value = false
  }
}

// Delete user
const handleDelete = (userId) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await adminService.deleteUser(userId)
      if (response.data.code === 200) {
        ElMessage.success('用户已删除')
        await loadAllUsers()
      } else {
        ElMessage.error(response.data.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }).catch(() => {
    // User cancelled the action
  })
}
</script>

<style scoped>
.users {
  padding: 20px;
}

.users h1 {
  margin-bottom: 20px;
  color: #333;
}

.pending-section,
.all-users-section {
  padding: 20px 0;
}

.el-form {
  max-width: 800px;
}
</style>
