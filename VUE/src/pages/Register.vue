<template>
  <div class="register-page">
    <div class="register-container">
      <h1>注册</h1>
      <form @submit.prevent="handleRegister">
        <div :class="['form-group', { 'has-error': errors.username }]">
          <label for="username" class="form-label required">学号</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            placeholder="请输入学号（6-12位）"
            required
            :disabled="loading"
            @blur="validateUsernameField"
            @input="clearError('username')"
          />
          <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
        </div>
        <div :class="['form-group', { 'has-error': errors.email }]">
          <label for="email" class="form-label required">邮箱</label>
          <input
            id="email"
            v-model="form.email"
            type="email"
            placeholder="请输入邮箱"
            required
            :disabled="loading"
            @blur="validateEmailField"
            @input="clearError('email')"
          />
          <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
        </div>
        <div :class="['form-group', { 'has-error': errors.password }]">
          <label for="password" class="form-label required">密码</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="请输入密码（至少8位，需包含大小写字母和数字）"
            required
            :disabled="loading"
            @blur="validatePasswordField"
            @input="clearError('password')"
          />
          <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
          <div v-if="form.password" class="password-strength">
            <div class="strength-bar">
              <div 
                class="strength-fill" 
                :class="getPasswordStrengthClass()"
                :style="{ width: getPasswordStrengthPercentage() + '%' }"
              ></div>
            </div>
            <span class="strength-text">{{ getPasswordStrengthText() }}</span>
          </div>
        </div>
        <div :class="['form-group', { 'has-error': errors.confirmPassword }]">
          <label for="confirmPassword" class="form-label required">确认密码</label>
          <input
            id="confirmPassword"
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            required
            :disabled="loading"
            @blur="validateConfirmPasswordField"
            @input="clearError('confirmPassword')"
          />
          <span v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</span>
        </div>
        <button type="submit" class="register-btn btn-primary" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      <p class="login-link">
        已有账户？<router-link to="/login">立即登录</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../utils/http'
import { showSuccess, showError } from '../utils/errorHandler'

const router = useRouter()
const loading = ref(false)
const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const errors = ref({})

/**
 * Validate student ID format (学号)
 * Typically 8-12 digits or alphanumeric
 */
const validateUsername = (username) => {
  if (!username) {
    return '学号不能为空'
  }
  if (!/^[A-Za-z0-9]{6,12}$/.test(username)) {
    return '学号格式不正确（6-12位字母或数字）'
  }
  return ''
}

const validateUsernameField = () => {
  errors.value.username = validateUsername(form.value.username)
}

/**
 * Validate email format
 */
const validateEmail = (email) => {
  if (!email) {
    return '邮箱不能为空'
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    return '邮箱格式不正确'
  }
  return ''
}

const validateEmailField = () => {
  errors.value.email = validateEmail(form.value.email)
}

/**
 * Validate password strength
 * Requirements: at least 8 characters, contains uppercase, lowercase, and number
 */
const validatePassword = (password) => {
  if (!password) {
    return '密码不能为空'
  }
  if (password.length < 8) {
    return '密码至少需要8位'
  }
  if (!/[A-Z]/.test(password)) {
    return '密码必须包含至少一个大写字母'
  }
  if (!/[a-z]/.test(password)) {
    return '密码必须包含至少一个小写字母'
  }
  if (!/[0-9]/.test(password)) {
    return '密码必须包含至少一个数字'
  }
  return ''
}

const validatePasswordField = () => {
  errors.value.password = validatePassword(form.value.password)
}

/**
 * Validate confirm password
 */
const validateConfirmPassword = (password, confirmPassword) => {
  if (!confirmPassword) {
    return '确认密码不能为空'
  }
  if (password !== confirmPassword) {
    return '两次输入的密码不一致'
  }
  return ''
}

const validateConfirmPasswordField = () => {
  errors.value.confirmPassword = validateConfirmPassword(form.value.password, form.value.confirmPassword)
}

const clearError = (field) => {
  delete errors.value[field]
}

const validateForm = () => {
  errors.value = {}

  errors.value.username = validateUsername(form.value.username)
  errors.value.email = validateEmail(form.value.email)
  errors.value.password = validatePassword(form.value.password)
  errors.value.confirmPassword = validateConfirmPassword(form.value.password, form.value.confirmPassword)

  // Remove empty error messages
  Object.keys(errors.value).forEach(key => {
    if (!errors.value[key]) {
      delete errors.value[key]
    }
  })

  return Object.keys(errors.value).length === 0
}

const handleRegister = async () => {
  if (!validateForm()) {
    return
  }

  loading.value = true
  try {
    await http.post('/auth/register', {
      username: form.value.username,
      email: form.value.email,
      password: form.value.password,
      confirmPassword: form.value.confirmPassword
    })

    showSuccess('注册成功，请登录')
    // Redirect to login page after 1 second
    setTimeout(() => {
      router.push('/login')
    }, 1000)
  } catch (error) {
    // Error handling is done by http interceptor
    // Additional error handling if needed
    if (error.response?.data?.message) {
      showError(error.response.data.message)
    }
  } finally {
    loading.value = false
  }
}

/**
 * Calculate password strength percentage
 */
const getPasswordStrengthPercentage = () => {
  const password = form.value.password
  let strength = 0

  if (password.length >= 8) strength += 25
  if (password.length >= 12) strength += 25
  if (/[A-Z]/.test(password) && /[a-z]/.test(password)) strength += 25
  if (/[0-9]/.test(password)) strength += 25

  return strength
}

/**
 * Get password strength CSS class
 */
const getPasswordStrengthClass = () => {
  const strength = getPasswordStrengthPercentage()
  if (strength <= 25) return 'weak'
  if (strength <= 50) return 'fair'
  if (strength <= 75) return 'good'
  return 'strong'
}

/**
 * Get password strength text
 */
const getPasswordStrengthText = () => {
  const strength = getPasswordStrengthPercentage()
  if (strength <= 25) return '弱'
  if (strength <= 50) return '一般'
  if (strength <= 75) return '良好'
  return '强'
}
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-container {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 400px;
  animation: slideIn 0.3s ease-in-out;
}

h1 {
  text-align: center;
  color: var(--primary-color, #1890ff);
  margin-bottom: 30px;
  font-size: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: var(--text-primary, #333);
}

input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color, #d9d9d9);
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
  transition: all 0.3s ease;
}

input:focus {
  outline: none;
  border-color: var(--primary-color, #1890ff);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.form-group.has-error input {
  border-color: #f56c6c;
  background-color: rgba(245, 34, 45, 0.05);
}

.form-group.has-error input:focus {
  border-color: #f56c6c;
  box-shadow: 0 0 0 2px rgba(245, 34, 45, 0.2);
}

.error-message {
  display: block;
  color: var(--error-color, #ff4d4f);
  font-size: 12px;
  margin-top: 4px;
  animation: slideIn 0.15s ease-in-out;
}

.password-strength {
  margin-top: 8px;
}

.strength-bar {
  height: 4px;
  background-color: #f0f0f0;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 4px;
}

.strength-fill {
  height: 100%;
  transition: width 0.3s ease, background-color 0.3s ease;
}

.strength-fill.weak {
  background-color: #ff4d4f;
}

.strength-fill.fair {
  background-color: #faad14;
}

.strength-fill.good {
  background-color: #52c41a;
}

.strength-fill.strong {
  background-color: #1890ff;
}

.strength-text {
  font-size: 12px;
  color: #666;
}

.register-btn {
  width: 100%;
  padding: 10px;
  background-color: var(--primary-color, #1890ff);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.register-btn:hover:not(:disabled) {
  background-color: var(--primary-light, #40a9ff);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.4);
  transform: translateY(-2px);
}

.register-btn:active:not(:disabled) {
  transform: translateY(0) scale(0.98);
}

.register-btn:disabled {
  background-color: #bfbfbf;
  cursor: not-allowed;
  transform: none;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: var(--text-secondary, #666);
}

.login-link a {
  color: var(--primary-color, #1890ff);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.login-link a:hover {
  text-decoration: underline;
  color: var(--primary-light, #40a9ff);
}

@keyframes slideIn {
  from {
    transform: translateY(-10px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
