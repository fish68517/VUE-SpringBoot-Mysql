<template>
  <div class="register-form">
    <h2>Create Account</h2>
    
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      @submit.prevent="handleRegister"
    >
      <el-form-item label="Username" prop="username">
        <div class="form-field-wrapper">
          <el-input
            v-model="form.username"
            placeholder="Enter username (3-50 characters)"
            clearable
            maxlength="50"
            show-word-limit
            @blur="validateField('username')"
          />
          <div v-if="hasFieldError('username')" class="field-error">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ getFieldError('username') }}</span>
          </div>
          <div v-else-if="form.username" class="field-hint">
            Username must be 3-50 characters, letters, numbers, underscores, and hyphens only
          </div>
        </div>
      </el-form-item>

      <el-form-item label="Email" prop="email">
        <div class="form-field-wrapper">
          <el-input
            v-model="form.email"
            type="email"
            placeholder="Enter your email"
            clearable
            @blur="validateField('email')"
          />
          <div v-if="hasFieldError('email')" class="field-error">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ getFieldError('email') }}</span>
          </div>
          <div v-else-if="form.email" class="field-hint">
            Enter a valid email address
          </div>
        </div>
      </el-form-item>

      <el-form-item label="Password" prop="password">
        <div class="form-field-wrapper">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="Enter password (at least 8 characters)"
            show-password
            clearable
            @blur="validateField('password')"
          />
          <div v-if="hasFieldError('password')" class="field-error">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ getFieldError('password') }}</span>
          </div>
          <div v-else-if="form.password" class="field-hint">
            Password strength: {{ passwordStrength }}
          </div>
        </div>
      </el-form-item>

      <el-form-item label="Confirm Password" prop="confirmPassword">
        <div class="form-field-wrapper">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="Confirm your password"
            show-password
            clearable
            @blur="validateField('confirmPassword')"
          />
          <div v-if="hasFieldError('confirmPassword')" class="field-error">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ getFieldError('confirmPassword') }}</span>
          </div>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          @click="handleRegister"
          :loading="loading"
          :disabled="!isFormValid"
          style="width: 100%"
        >
          Register
        </el-button>
      </el-form-item>

      <el-form-item>
        <p class="login-link">
          Already have an account?
          <router-link to="/login">Login here</router-link>
        </p>
      </el-form-item>
    </el-form>

    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      closable
      @close="errorMessage = ''"
      style="margin-top: 20px"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import { authService } from '../services/authService'
import { useUserStore } from '../stores/userStore'
import { useFormValidation } from '../composables/useFormValidation'
import { validatePassword } from '../utils/validationUtils'
import { showSuccess, showError } from '../utils/notificationUtils'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const errorMessage = ref('')

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// Validation rules
const rules = {
  username: [
    { required: true, message: 'Username is required', trigger: 'blur' },
    { min: 3, max: 50, message: 'Username must be between 3 and 50 characters', trigger: 'blur' }
  ],
  email: [
    { required: true, message: 'Email is required', trigger: 'blur' },
    { type: 'email', message: 'Email format is invalid', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Password is required', trigger: 'blur' },
    { min: 8, max: 100, message: 'Password must be between 8 and 100 characters', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm your password', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('Passwords do not match'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// Use form validation composable
const {
  errors,
  touched,
  validateField,
  validateForm,
  touchField,
  getFieldError,
  hasFieldError,
  isFormValid
} = useFormValidation(form, {
  username: { required: true, label: 'Username', minLength: 3, maxLength: 50 },
  email: { required: true, type: 'email' },
  password: { required: true, type: 'password' },
  confirmPassword: {
    required: true,
    validator: (value) => {
      if (value !== form.password) {
        return { isValid: false, message: 'Passwords do not match' }
      }
      return { isValid: true, message: '' }
    }
  }
})

const passwordStrength = computed(() => {
  if (!form.password) return ''
  const validation = validatePassword(form.password)
  return validation.strength.charAt(0).toUpperCase() + validation.strength.slice(1)
})

const handleRegister = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true
    errorMessage.value = ''

    const response = await authService.register({
      username: form.username,
      email: form.email,
      password: form.password
    })

    // Store token and user info
    userStore.setToken(response.data.token)
    userStore.setUser(response.data.user)

    showSuccess('Registration successful! Welcome to Travel Memory System')
    
    // Redirect to dashboard
    router.push('/dashboard')
  } catch (error) {
    loading.value = false
    
    let errorMsg = 'Registration failed. Please try again.'
    if (error.message) {
      errorMsg = error.message
    } else if (error.errors && error.errors.length > 0) {
      errorMsg = error.errors[0].message
    }
    
    errorMessage.value = errorMsg
    showError(errorMsg)
  }
}
</script>

<style scoped>
.register-form {
  max-width: 400px;
  margin: 0 auto;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.register-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.login-link {
  text-align: center;
  margin: 0;
  font-size: 14px;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
  margin-left: 5px;
}

.login-link a:hover {
  text-decoration: underline;
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

.field-hint {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}

:deep(.el-button:disabled) {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
