<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
          <el-button v-if="!isEditing" type="primary" @click="isEditing = true">编辑信息</el-button>
          <div v-else>
            <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
            <el-button @click="handleCancel">取消</el-button>
          </div>
        </div>
      </template>

      <el-skeleton v-if="loading" :rows="5" animated />

      <div v-else class="profile-content">
        <el-form v-if="isEditing" :model="editForm" label-width="120px">
          <el-form-item label="用户名">
            <el-input v-model="editForm.username" disabled />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" />
          </el-form-item>
          <el-form-item label="身份">
            <el-input :value="getRoleLabel(editForm.role)" disabled />
          </el-form-item>
          <el-form-item label="所属机构">
            <el-input :value="editForm.organization?.name" disabled />
          </el-form-item>
          <el-form-item label="认证状态">
            <el-tag :type="getStatusType(editForm.status)">{{ getStatusLabel(editForm.status) }}</el-tag>
          </el-form-item>
        </el-form>

        <div v-else class="profile-view">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户名">{{ profileData.username }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ profileData.email }}</el-descriptions-item>
            <el-descriptions-item label="身份">{{ getRoleLabel(profileData.role) }}</el-descriptions-item>
            <el-descriptions-item label="所属机构">{{ profileData.organization?.name }}</el-descriptions-item>
            <el-descriptions-item label="认证状态">
              <el-tag :type="getStatusType(profileData.status)">{{ getStatusLabel(profileData.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDate(profileData.createdAt) }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'
import * as userApi from '@/api/user'

const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const isEditing = ref(false)

const profileData = ref({
  id: '',
  username: '',
  email: '',
  role: '',
  status: '',
  organization: {},
  createdAt: '',
})

const editForm = reactive({
  username: '',
  email: '',
  role: '',
  status: '',
  organization: {},
})

// 获取个人信息
const loadProfile = async () => {
  loading.value = true
  try {
    const response = await userApi.getProfile()
    profileData.value = response.data
    Object.assign(editForm, response.data)
  } catch (error) {
    ElMessage.error(error.message || '加载个人信息失败')
  } finally {
    loading.value = false
  }
}

// 保存个人信息
const handleSave = async () => {
  saving.value = true
  try {
    const response = await userApi.updateProfile({
      email: editForm.email,
    })
    profileData.value = response.data
    Object.assign(editForm, response.data)
    isEditing.value = false
    ElMessage.success('个人信息更新成功')
    
    // 更新 store 中的用户信息
    userStore.setUser(response.data)
  } catch (error) {
    ElMessage.error(error.message || '更新个人信息失败')
  } finally {
    saving.value = false
  }
}

// 取消编辑
const handleCancel = () => {
  Object.assign(editForm, profileData.value)
  isEditing.value = false
}

// 获取角色标签
const getRoleLabel = (role) => {
  const roleMap = {
    'STUDENT': '学生',
    'TEACHER': '带教老师',
    'SCHOOL_ADMIN': '学校管理员',
    'HOSPITAL_ADMIN': '医院管理员',
    'ADMIN': '系统管理员',
  }
  return roleMap[role] || role
}

// 获取状态标签
const getStatusLabel = (status) => {
  const statusMap = {
    'PENDING': '待认证',
    'APPROVED': '已认证',
    'REJECTED': '已驳回',
  }
  return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
  }
  return typeMap[status] || 'info'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 600px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.profile-content {
  padding: 20px 0;
}

.profile-view {
  padding: 20px 0;
}
</style>
