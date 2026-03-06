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
      
      <el-form :model="form" label-width="120px" @submit.prevent="updateProfile">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" disabled />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="请输入电话号码" disabled />
        </el-form-item>
        <el-form-item label="专业领域">
          <el-input 
            v-model="form.expertiseAreas" 
            type="textarea" 
            rows="3"
            placeholder="请输入您的专业领域"
          />
        </el-form-item>
        <el-form-item label="研究方向">
          <el-input 
            v-model="form.researchDirections" 
            type="textarea" 
            rows="3"
            placeholder="请输入您的研究方向"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateProfile" :loading="loading">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Change Password Section -->
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>
      
      <el-form :model="passwordForm" label-width="120px" @submit.prevent="changePassword">
        <el-form-item label="旧密码">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password"
            placeholder="请输入旧密码"
          />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password"
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password"
            placeholder="请再次输入新密码"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="changePassword" :loading="passwordLoading">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { reviewerService } from '../../services/reviewerService'
import { useUserStore } from '../../stores/userStore'

const userStore = useUserStore()
const loading = ref(false)
const passwordLoading = ref(false)

const form = ref({
  username: '',
  email: '',
  phone: '',
  expertiseAreas: '',
  researchDirections: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// Load reviewer profile on component mount
onMounted(async () => {
  try {
    const response = await reviewerService.getProfile()
    if (response.code === 200) {
      form.value = {
        username: response.data.username,
        email: response.data.email,
        phone: response.data.phone,
        expertiseAreas: response.data.expertiseAreas || '',
        researchDirections: response.data.researchDirections || ''
      }
    }
  } catch (error) {
    ElMessage.error('加载个人信息失败')
    console.error(error)
  }
})

// Update profile information
const updateProfile = async () => {
  loading.value = true
  try {
    const response = await reviewerService.updateProfile({
      expertiseAreas: form.value.expertiseAreas,
      researchDirections: form.value.researchDirections
    })
    
    if (response.code === 200) {
      ElMessage.success('个人信息更新成功')
      // Update user store
      userStore.setUser(response.data)
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '更新个人信息失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// Change password
const changePassword = async () => {
  // Validate password fields
  if (!passwordForm.value.oldPassword) {
    ElMessage.error('请输入旧密码')
    return
  }
  if (!passwordForm.value.newPassword) {
    ElMessage.error('请输入新密码')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  if (passwordForm.value.newPassword === passwordForm.value.oldPassword) {
    ElMessage.error('新密码不能与旧密码相同')
    return
  }

  passwordLoading.value = true
  try {
    const response = await reviewerService.changePassword(
      passwordForm.value.oldPassword,
      passwordForm.value.newPassword
    )
    
    if (response.code === 200) {
      ElMessage.success('密码修改成功')
      // Clear password form
      passwordForm.value = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    } else {
      ElMessage.error(response.message || '修改失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '修改密码失败')
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
