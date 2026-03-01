<template>
  <div class="user-management-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('admin.userManagement') || '用户管理' }}</h2>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        :placeholder="$t('auth.username') || '用户名'"
        clearable
        style="width: 200px; margin-right: 10px"
        @input="handleSearch"
      />
      <el-button type="primary" @click="fetchUsers" :loading="isLoading">
        {{ $t('common.search') || '搜索' }}
      </el-button>
      <el-button @click="resetSearch">
        {{ $t('common.reset') || '重置' }}
      </el-button>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="isLoading" :rows="5" animated />

    <!-- 用户列表表格 -->
    <div v-else class="table-container">
      <el-table
        :data="filteredUsers"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'id', order: 'ascending' }"
      >
        <!-- ID列 -->
        <el-table-column prop="id" label="ID" width="80" sortable />

        <!-- 用户名列 -->
        <el-table-column prop="username" :label="$t('auth.username') || '用户名'" width="150" />

        <!-- 邮箱列 -->
        <el-table-column prop="email" :label="$t('auth.email') || '邮箱'" width="180" />

        <!-- 姓名列 -->
        <el-table-column prop="name" :label="$t('user.name') || '姓名'" width="120" />

        <!-- 角色列 -->
        <el-table-column prop="role" :label="$t('auth.userRole') || '用户角色'" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)">
              {{ formatRole(row.role) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 状态列 -->
        <el-table-column prop="status" :label="$t('admin.status') || '状态'" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ row.status === 'ACTIVE' ? $t('admin.active') || '活跃' : $t('admin.inactive') || '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 创建时间列 -->
        <el-table-column prop="createdAt" :label="$t('common.createdAt') || '创建时间'" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column :label="$t('common.action') || '操作'" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="openRoleDialog(row)"
            >
              {{ $t('common.edit') || '编辑' }}
            </el-button>
            <el-popconfirm
              v-if="row.status === 'ACTIVE'"
              :title="$t('common.confirm') || '确认禁用此用户？'"
              @confirm="disableUser(row.id)"
            >
              <template #reference>
                <el-button type="danger" size="small">
                  {{ $t('admin.inactive') || '禁用' }}
                </el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm
              v-else
              :title="$t('common.confirm') || '确认启用此用户？'"
              @confirm="enableUser(row.id)"
            >
              <template #reference>
                <el-button type="success" size="small">
                  {{ $t('admin.active') || '启用' }}
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 修改角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      :title="$t('auth.userRole') || '修改用户角色'"
      width="400px"
      @close="resetRoleForm"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleFormRules"
        label-width="100px"
      >
        <el-form-item label="用户名">
          <span>{{ roleForm.username }}</span>
        </el-form-item>

        <el-form-item :label="$t('auth.userRole') || '用户角色'" prop="role">
          <el-select
            v-model="roleForm.role"
            :placeholder="$t('auth.userRole') || '选择角色'"
          >
            <el-option label="普通用户" value="USER" />
            <el-option label="医师" value="DOCTOR" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="roleDialogVisible = false">
          {{ $t('common.cancel') || '取消' }}
        </el-button>
        <el-button type="primary" @click="submitRoleChange" :loading="isSubmitting">
          {{ $t('common.confirm') || '确认' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 错误提示 -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      :closable="true"
      @close="errorMessage = ''"
      class="error-alert"
    />

    <!-- 成功提示 -->
    <el-alert
      v-if="successMessage"
      :title="successMessage"
      type="success"
      :closable="true"
      @close="successMessage = ''"
      class="success-alert"
    />
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminAPI } from '../../services/api'

export default {
  name: 'UserManagement',
  setup() {
    const roleFormRef = ref(null)
    const isLoading = ref(true)
    const isSubmitting = ref(false)
    const roleDialogVisible = ref(false)
    const searchQuery = ref('')
    const errorMessage = ref('')
    const successMessage = ref('')

    const users = ref([])

    const roleForm = reactive({
      id: null,
      username: '',
      role: ''
    })

    // 角色表单验证规则
    const roleFormRules = {
      role: [
        { required: true, message: '角色为必填项', trigger: 'change' }
      ]
    }

    // 过滤后的用户列表
    const filteredUsers = computed(() => {
      if (!searchQuery.value) {
        return users.value
      }
      return users.value.filter(user =>
        user.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
        (user.name && user.name.toLowerCase().includes(searchQuery.value.toLowerCase())) ||
        (user.email && user.email.toLowerCase().includes(searchQuery.value.toLowerCase()))
      )
    })

    // 获取所有用户
    const fetchUsers = async () => {
      try {
        isLoading.value = true
        errorMessage.value = ''
        const response = await adminAPI.getUsers()

        if (response && response.data) {
          users.value = response.data
        }
      } catch (error) {
        console.error('获取用户列表失败:', error)
        errorMessage.value = error.response?.data?.message || '获取用户列表失败'
      } finally {
        isLoading.value = false
      }
    }

    // 搜索处理
    const handleSearch = () => {
      // 实时搜索，通过计算属性filteredUsers实现
    }

    // 重置搜索
    const resetSearch = () => {
      searchQuery.value = ''
    }

    // 打开角色修改对话框
    const openRoleDialog = (user) => {
      roleForm.id = user.id
      roleForm.username = user.username
      roleForm.role = user.role
      roleDialogVisible.value = true
    }

    // 重置角色表单
    const resetRoleForm = () => {
      roleForm.id = null
      roleForm.username = ''
      roleForm.role = ''
      if (roleFormRef.value) {
        roleFormRef.value.resetFields()
      }
    }

    // 提交角色修改
    const submitRoleChange = async () => {
      if (!roleFormRef.value) return

      try {
        await roleFormRef.value.validate()
        isSubmitting.value = true
        errorMessage.value = ''
        successMessage.value = ''

        const response = await adminAPI.updateUserRole(roleForm.id, { role: roleForm.role })

        if (response && response.data) {
          // 更新本地用户列表
          const userIndex = users.value.findIndex(u => u.id === roleForm.id)
          if (userIndex !== -1) {
            users.value[userIndex].role = roleForm.role
          }

          successMessage.value = '用户角色已更新'
          ElMessage.success('用户角色已更新')
          roleDialogVisible.value = false
        }
      } catch (error) {
        console.error('修改用户角色失败:', error)
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '修改用户角色失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '修改用户角色失败'
        }
      } finally {
        isSubmitting.value = false
      }
    }

    // 禁用用户
    const disableUser = async (userId) => {
      try {
        errorMessage.value = ''
        successMessage.value = ''

        const response = await adminAPI.disableUser(userId, { status: 'INACTIVE' })

        if (response && response.data) {
          // 更新本地用户列表
          const userIndex = users.value.findIndex(u => u.id === userId)
          if (userIndex !== -1) {
            users.value[userIndex].status = 'INACTIVE'
          }

          successMessage.value = '用户已禁用'
          ElMessage.success('用户已禁用')
        }
      } catch (error) {
        console.error('禁用用户失败:', error)
        errorMessage.value = error.response?.data?.message || '禁用用户失败'
      }
    }

    // 启用用户
    const enableUser = async (userId) => {
      try {
        errorMessage.value = ''
        successMessage.value = ''

        const response = await adminAPI.disableUser(userId, { status: 'ACTIVE' })

        if (response && response.data) {
          // 更新本地用户列表
          const userIndex = users.value.findIndex(u => u.id === userId)
          if (userIndex !== -1) {
            users.value[userIndex].status = 'ACTIVE'
          }

          successMessage.value = '用户已启用'
          ElMessage.success('用户已启用')
        }
      } catch (error) {
        console.error('启用用户失败:', error)
        errorMessage.value = error.response?.data?.message || '启用用户失败'
      }
    }

    // 格式化角色显示
    const formatRole = (role) => {
      const roleMap = {
        'USER': '普通用户',
        'DOCTOR': '医师',
        'ADMIN': '管理员'
      }
      return roleMap[role] || role
    }

    // 获取角色标签类型
    const getRoleTagType = (role) => {
      const typeMap = {
        'USER': 'info',
        'DOCTOR': 'warning',
        'ADMIN': 'danger'
      }
      return typeMap[role] || 'info'
    }

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    }

    // 页面加载时获取用户列表
    onMounted(() => {
      fetchUsers()
    })

    return {
      roleFormRef,
      isLoading,
      isSubmitting,
      roleDialogVisible,
      searchQuery,
      errorMessage,
      successMessage,
      users,
      filteredUsers,
      roleForm,
      roleFormRules,
      fetchUsers,
      handleSearch,
      resetSearch,
      openRoleDialog,
      resetRoleForm,
      submitRoleChange,
      disableUser,
      enableUser,
      formatRole,
      getRoleTagType,
      formatDate
    }
  }
}
</script>

<style scoped>
.user-management-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.error-alert {
  margin-top: 20px;
}

.success-alert {
  margin-top: 20px;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table__header th) {
  background-color: #f5f7fa;
  font-weight: bold;
}

:deep(.el-table__body tr:hover > td) {
  background-color: #f5f7fa;
}

:deep(.el-tag) {
  border-radius: 4px;
}

:deep(.el-button--small) {
  padding: 6px 12px;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-management-container {
    padding: 10px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .search-bar {
    flex-direction: column;
    gap: 8px;
  }

  .search-bar :deep(.el-input) {
    width: 100%;
  }

  .table-container {
    padding: 10px;
    overflow-x: auto;
  }

  :deep(.el-table) {
    font-size: 12px;
  }

  :deep(.el-table__body tr td) {
    padding: 8px 0;
  }
}
</style>
