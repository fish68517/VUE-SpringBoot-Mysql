<template>
  <div class="organization-container">
    <h2>组织管理</h2>
    
    <!-- 创建组织按钮 -->
    <el-card class="box-card" style="margin-bottom: 20px;">
      <el-button type="primary" @click="handleCreateOrganization">+ 创建组织</el-button>
    </el-card>
    
    <!-- 组织列表 -->
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>组织列表</span>
          <el-button type="primary" @click="loadOrganizations">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="organizations" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="组织名称" width="200" />
        <el-table-column prop="code" label="组织代码" width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">
              {{ row.type === 'SCHOOL' ? '学校' : '医院' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEditOrganization(row)">
              编辑
            </el-button>
            <el-button type="info" size="small" @click="handleViewUsers(row)">
              查看用户
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 创建/编辑组织对话框 -->
    <el-dialog 
      v-model="orgDialogVisible" 
      :title="isEditingOrg ? '编辑组织' : '创建组织'" 
      width="500px">
      <div v-if="orgForm" class="org-form">
        <div class="form-group">
          <label>组织名称：</label>
          <el-input v-model="orgForm.name" placeholder="请输入组织名称" />
        </div>
        <div class="form-group">
          <label>组织代码：</label>
          <el-input v-model="orgForm.code" placeholder="请输入组织代码" :disabled="isEditingOrg" />
        </div>
        <div class="form-group">
          <label>组织类型：</label>
          <el-select v-model="orgForm.type" placeholder="选择组织类型" :disabled="isEditingOrg">
            <el-option label="学校" value="SCHOOL" />
            <el-option label="医院" value="HOSPITAL" />
          </el-select>
        </div>
      </div>
      <template #footer>
        <el-button @click="orgDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOrgForm">提交</el-button>
      </template>
    </el-dialog>
    
    <!-- 组织用户列表对话框 -->
    <el-dialog v-model="usersDialogVisible" title="组织用户列表" width="700px">
      <el-table :data="organizationUsers" stripe style="width: 100%" v-loading="usersLoading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag>{{ getRoleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  getAllOrganizations, 
  createOrganization, 
  updateOrganization,
  getOrganizationUsers
} from '@/api/admin'

const organizations = ref([])
const loading = ref(false)
const orgDialogVisible = ref(false)
const usersDialogVisible = ref(false)
const isEditingOrg = ref(false)
const organizationUsers = ref([])
const usersLoading = ref(false)

const orgForm = ref({
  name: '',
  code: '',
  type: 'SCHOOL'
})

const loadOrganizations = async () => {
  loading.value = true
  try {
    const response = await getAllOrganizations()
    organizations.value = response.data.data || []
  } catch (error) {
    ElMessage.error('加载组织列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const getTypeTag = (type) => {
  return type === 'SCHOOL' ? 'success' : 'warning'
}

const getRoleLabel = (role) => {
  const roleMap = {
    'ADMIN': '系统管理员',
    'SCHOOL_ADMIN': '学校管理员',
    'HOSPITAL_ADMIN': '医院管理员',
    'STUDENT': '学生',
    'TEACHER': '带教老师'
  }
  return roleMap[role] || role
}

const getStatusType = (status) => {
  const statusMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return statusMap[status] || 'info'
}

const handleCreateOrganization = () => {
  isEditingOrg.value = false
  orgForm.value = {
    name: '',
    code: '',
    type: 'SCHOOL'
  }
  orgDialogVisible.value = true
}

const handleEditOrganization = (row) => {
  isEditingOrg.value = true
  orgForm.value = {
    id: row.id,
    name: row.name,
    code: row.code,
    type: row.type
  }
  orgDialogVisible.value = true
}

const handleViewUsers = async (row) => {
  usersLoading.value = true
  try {
    const response = await getOrganizationUsers(row.id)
    organizationUsers.value = response.data.data || []
    usersDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载用户列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    usersLoading.value = false
  }
}

const submitOrgForm = async () => {
  try {
    if (isEditingOrg.value) {
      await updateOrganization(orgForm.value.id, orgForm.value)
      ElMessage.success('组织已更新')
    } else {
      await createOrganization(orgForm.value)
      ElMessage.success('组织已创建')
    }
    orgDialogVisible.value = false
    loadOrganizations()
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
  }
}

onMounted(() => {
  loadOrganizations()
})
</script>

<style scoped>
.organization-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.box-card {
  margin-bottom: 20px;
}

.org-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-group label {
  font-weight: bold;
  color: #333;
}
</style>
