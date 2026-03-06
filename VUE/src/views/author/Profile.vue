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
      
      <el-form 
        ref="formRef"
        :model="form" 
        :rules="rules"
        label-width="120px" 
        @submit.prevent="updateProfile"
      >
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input 
            v-model="form.email" 
            placeholder="请输入邮箱"
            clearable
          />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input 
            v-model="form.phone" 
            placeholder="请输入电话号码"
            clearable
          />
        </el-form-item>
        <el-form-item label="学术成果">
          <el-input 
            v-model="form.academicAchievements" 
            type="textarea" 
            rows="4"
            placeholder="请输入您的学术成果"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="updateProfile" 
            :loading="loading"
          >
            {{ loading ? '保存中...' : '保存' }}
          </el-button>
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
      
      <el-form 
        ref="passwordFormRef"
        :model="passwordForm" 
        :rules="passwordRules"
        label-width="120px" 
        @submit.prevent="changePassword"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password"
            placeholder="请输入旧密码"
            clearable
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password"
            placeholder="请输入新密码"
            clearable
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password"
            placeholder="请再次输入新密码"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="changePassword" 
            :loading="passwordLoading"
          >
            {{ passwordLoading ? '修改中...' : '修改密码' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { authorService } from '../../services/authorService'
import { useUserStore } from '../../stores/userStore'
import { useFormValidation } from '../../composables/useFormValidation'
import { useLoading } from '../../composables/useLoading'

const userStore = useUserStore()
const formRef = ref(null)
const passwordFormRef = ref(null)
const { validationRules, validateForm } = useFormValidation()
const { loading, executeWithLoading } = useLoading()
const { loading: passwordLoading, executeWithLoading: executePasswordWithLoading } = useLoading()

const form = ref({
  username: '',
  email: '',
  phone: '',
  academicAchievements: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  email: validationRules.email,
  phone: validationRules.phone
}

const passwordRules = {
  oldPassword: validationRules.password,
  newPassword: validationRules.password,
  confirmPassword: [
    { required: true, message: '确认密码不能为空', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的新密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// Load author profile on component mount
onMounted(async () => {
  try {
    const response = await authorService.getProfile()
    if (response.code === 200) {
      form.value = {
        username: response.data.username,
        email: response.data.email,
        phone: response.data.phone,
        academicAchievements: response.data.academicAchievements || ''
      }
    }
  } catch (error) {
    console.error(error)
  }
})

// Update profile information
const updateProfile = async () => {
  if (!await validateForm(formRef.value)) {
    return
  }

  const result = await executeWithLoading(
    () => authorService.updateProfile({
      email: form.value.email,
      phone: form.value.phone,
      academicAchievements: form.value.academicAchievements
    }),
    {
      loadingMessage: '保存中...',
      successMessage: '个人信息更新成功',
      errorMessage: '更新个人信息失败'
    }
  )

  if (result.success) {
    userStore.setUser(result.data.data)
  }
}

// Change password
const changePassword = async () => {
  if (!await validateForm(passwordFormRef.value)) {
    return
  }

  if (passwordForm.value.newPassword === passwordForm.value.oldPassword) {
    const { showWarning } = useLoading()
    showWarning('新密码不能与旧密码相同')
    return
  }

  const result = await executePasswordWithLoading(
    () => authorService.changePassword(
      passwordForm.value.oldPassword,
      passwordForm.value.newPassword
    ),
    {
      loadingMessage: '修改中...',
      successMessage: '密码修改成功',
      errorMessage: '修改密码失败'
    }
  )

  if (result.success) {
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
    passwordFormRef.value.resetFields()
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
