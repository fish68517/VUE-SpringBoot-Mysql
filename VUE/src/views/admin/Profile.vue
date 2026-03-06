<template>
  <div class="profile">
    <h1>个人信息</h1>
    
    <!-- Basic Information Section -->
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
        </div>
      </template>
      <el-form :model="profileForm" label-width="120px" @submit.prevent="handleUpdateProfile">
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="profileForm.phone" placeholder="请输入电话号码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdateProfile" :loading="profileLoading">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Password Change Section -->
    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>
      <el-form :model="passwordForm" label-width="120px" @submit.prevent="handleChangePassword">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminService } from '../../services/adminService'
import { useUserStore } from '../../stores/userStore'

const userStore = useUserStore()
const profileLoading = ref(false)
const passwordLoading = ref(false)

const profileForm = ref({
  username: '',
  email: '',
  phone: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// Load admin profile on component mount
onMounted(async () => {
  await loadProfile()
})

// Load admin profile information
const loadProfile = async () => {
  try {
    const response = await adminService.getProfile()
    if (response.data.code === 200) {
      const admin = response.data.data
      profileForm.value = {
        username: admin.username,
        email: admin.email || '',
        phone: admin.phone || ''
      }
    } else {
      ElMessage.error(response.data.message || '加载个人信息失败')
    }
  } catch (error) {
    ElMessage.error('加载个人信息失败')
    console.error(error)
  }
}

// Update admin profile
const handleUpdateProfile = async () => {
  profileLoading.value = true
  try {
    const response = await adminService.updateProfile({
      email: profileForm.value.email,
      phone: profileForm.value.phone
    })
    if (response.data.code === 200) {
      ElMessage.success('个人信息更新成功')
      // Update user store
      userStore.user.email = profileForm.value.email
      userStore.user.phone = profileForm.value.phone
    } else {
      ElMessage.error(response.data.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新个人信息失败')
    console.error(error)
  } finally {
    profileLoading.value = false
  }
}

// Change password
const handleChangePassword = async () => {
  // Validate password fields
  if (!passwordForm.value.oldPassword) {
    ElMessage.warning('请输入原密码')
    return
  }
  if (!passwordForm.value.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  if (passwordForm.value.oldPassword === passwordForm.value.newPassword) {
    ElMessage.warning('新密码不能与原密码相同')
    return
  }

  passwordLoading.value = true
  try {
    const response = await adminService.changePassword(
      passwordForm.value.oldPassword,
      passwordForm.value.newPassword
    )
    if (response.data.code === 200) {
      ElMessage.success('密码修改成功')
      // Clear password form
      passwordForm.value = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    } else {
      ElMessage.error(response.data.message || '修改密码失败')
    }
  } catch (error) {
    ElMessage.error('修改密码失败')
    console.error(error)
  } finally {
    passwordLoading.value = false
  }
}
</script>

<style scoped>
.profile {
  padding: 20px;
}

.profile h1 {
  margin-bottom: 20px;
  color: #333;
}

.info-card {
  margin-bottom: 20px;
}

.password-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-form {
  max-width: 500px;
}
</style>
