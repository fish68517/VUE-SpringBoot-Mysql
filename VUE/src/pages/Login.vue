<template>
  <div class="login-page">
    <div class="login-container">
      <h1>登录</h1>
      <form @submit.prevent="handleLogin">
        <div :class="['form-group', { 'has-error': errors.username }]">
          <label for="username" class="form-label required">学号/邮箱</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            placeholder="请输入学号或邮箱"
            @blur="validateUsername"
            @input="clearError('username')"
          />
          <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
        </div>
        <div :class="['form-group', { 'has-error': errors.password }]">
          <label for="password" class="form-label required">密码</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            @blur="validatePassword"
            @input="clearError('password')"
          />
          <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
        </div>
        <button type="submit" class="login-btn btn-primary" :disabled="isLoading">
          {{ isLoading ? '登录中...' : '登录' }}
        </button>
      </form>
      <p class="register-link">
        还没有账户？<router-link to="/register">立即注册</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../utils/http'
import { useAuthStore } from '../store/index'
import { showSuccess, showError } from '../utils/errorHandler'
import { validationRules, validateField } from '../utils/formValidation'

const router = useRouter()
const authStore = useAuthStore()
const isLoading = ref(false)

const form = ref({
  username: '',
  password: ''
})

const errors = ref({
  username: '',
  password: ''
})

// Validate username/email format
const validateUsername = () => {
  const username = form.value.username.trim()
  
  if (!username) {
    errors.value.username = '请输入学号或邮箱'
    return false
  }
  
  // Check if it's a valid email or student ID format
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  const studentIdRegex = /^\d{8,12}$/
  
  if (!emailRegex.test(username) && !studentIdRegex.test(username)) {
    errors.value.username = '请输入有效的邮箱或学号'
    return false
  }
  
  errors.value.username = ''
  return true
}

// Validate password
const validatePassword = () => {
  const password = form.value.password
  
  if (!password) {
    errors.value.password = '请输入密码'
    return false
  }
  
  if (password.length < 6) {
    errors.value.password = '密码长度至少为6位'
    return false
  }
  
  errors.value.password = ''
  return true
}

// Clear field error
const clearError = (field) => {
  errors.value[field] = ''
}

// Validate all fields
const validateForm = () => {
  const usernameValid = validateUsername()
  const passwordValid = validatePassword()
  return usernameValid && passwordValid
}

// Handle login
const handleLogin = async () => {
  if (!validateForm()) {
    return
  }

  isLoading.value = true

  try {
    const response = await http.post('/auth/login', {
      username: form.value.username.trim(),
      password: form.value.password
    })

    // Store token and user info
    authStore.setToken(response.data.accessToken)
    authStore.setUser(response.data.user)

    showSuccess('登录成功')

    // Redirect based on user role
    const userRole = response.data.user.role
    if (userRole === 'ADMIN') {
      router.push('/admin/activity-audit')
    } else if (userRole === 'ORGANIZER') {
      router.push('/organizer/statistics')
    } else {
      router.push('/')
    }
  } catch (error) {
    // Error is already handled by http interceptor
    console.error('Login error:', error)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container {
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
  color: var(--primary-color);
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
  color: var(--text-primary);
}

input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
  transition: all 0.3s ease;
}

input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
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
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  animation: slideIn 0.15s ease-in-out;
}

.login-btn {
  width: 100%;
  padding: 10px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-btn:hover:not(:disabled) {
  background-color: var(--primary-light);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.4);
  transform: translateY(-2px);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0) scale(0.98);
}

.login-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
  transform: none;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: var(--text-secondary);
}

.register-link a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.register-link a:hover {
  text-decoration: underline;
  color: var(--primary-light);
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
