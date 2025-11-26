<template>
  <el-form
    ref="loginFormRef"
    :model="loginForm"
    :rules="rules"
    label-width="80px"
    @submit.prevent="handleLogin"
  >
    <el-form-item label="Email" prop="email">
      <div class="form-field-wrapper">
        <el-input
          v-model="loginForm.email"
          type="email"
          placeholder="Enter your email"
          clearable
          @blur="validateField('email')"
        />
        <div v-if="hasFieldError('email')" class="field-error">
          <el-icon><CircleCloseFilled /></el-icon>
          <span>{{ getFieldError('email') }}</span>
        </div>
      </div>
    </el-form-item>

    <el-form-item label="Password" prop="password">
      <div class="form-field-wrapper">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="Enter your password"
          show-password
          @blur="validateField('password')"
        />
        <div v-if="hasFieldError('password')" class="field-error">
          <el-icon><CircleCloseFilled /></el-icon>
          <span>{{ getFieldError('password') }}</span>
        </div>
      </div>
    </el-form-item>

    <el-form-item>
      <el-button
        type="primary"
        @click="handleLogin"
        :loading="isLoading"
        :disabled="!isFormValid"
        style="width: 100%"
      >
        Login
      </el-button>
    </el-form-item>

    <el-form-item>
      <span>Don't have an account? </span>
      <router-link to="/register">Register here</router-link>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/userStore'
import { useFormValidation } from '../composables/useFormValidation'
import { loginService } from '../services/loginService'
import { showSuccess, showError } from '../utils/notificationUtils'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const isLoading = ref(false)

const loginForm = reactive({
  email: '',
  password: ''
})

const rules = {
  email: [
    { required: true, message: 'Email is required', trigger: 'blur' },
    { type: 'email', message: 'Email format is invalid', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Password is required', trigger: 'blur' },
    { min: 8, message: 'Password must be at least 8 characters', trigger: 'blur' }
  ]
}

// Use form validation composable
const {
  validateField,
  getFieldError,
  hasFieldError,
  isFormValid
} = useFormValidation(loginForm, {
  email: { required: true, type: 'email' },
  password: { required: true, minLength: 8 }
})

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    isLoading.value = true

    const response = await loginService.login({
      email: loginForm.email,
      password: loginForm.password
    })

    // Save token and user info to store
    userStore.setToken(response.data.token)
    userStore.setUser(response.data.user)

    showSuccess('Login successful! Welcome back')
    
    // Redirect to dashboard
    router.push('/dashboard')
  } catch (error) {
    const errorMessage = error.message || 'Login failed. Please check your credentials.'
    showError(errorMessage)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
:deep(.el-form) {
  max-width: 400px;
  margin: 0 auto;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

a {
  color: #409eff;
  text-decoration: none;
}

a:hover {
  color: #66b1ff;
}

.form-field-wrapper {
  position: relative;
}

.field-error {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
  font-size: 12px;
  color: #f56c6c;
  line-height: 1;
}

.field-error :deep(.el-icon) {
  font-size: 14px;
  flex-shrink: 0;
}

:deep(.el-button:disabled) {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
