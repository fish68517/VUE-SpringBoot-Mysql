<template>
  <div class="register-container">
    <div class="register-card">
      <h1>创建账号</h1>
      <p class="subtitle">加入纳西族纹样展示平台</p>

      <form @submit.prevent="handleRegister">
        <!-- Username Field -->
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            placeholder="请输入用户名"
            @blur="validateUsername"
          />
          <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
        </div>

        <!-- Email Field -->
        <div class="form-group">
          <label for="email">邮箱</label>
          <input
            id="email"
            v-model="form.email"
            type="email"
            placeholder="请输入邮箱地址"
            @blur="validateEmail"
          />
          <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
        </div>

        <!-- Password Field -->
        <div class="form-group">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            @blur="validatePassword"
          />
          <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
        </div>

        <!-- Confirm Password Field -->
        <div class="form-group">
          <label for="confirmPassword">确认密码</label>
          <input
            id="confirmPassword"
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            @blur="validateConfirmPassword"
          />
          <span v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</span>
        </div>

        <!-- Submit Button -->
        <button type="submit" class="submit-btn" :disabled="loading">
          <span v-if="!loading">注册</span>
          <span v-else>注册中...</span>
        </button>
      </form>

      <!-- Login Link -->
      <div class="login-link">
        <p>已有账号？<router-link to="/login">立即登录</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userAPI } from '../services/api'
import { useUserStore } from '../store'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const errors = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)

// Validation functions
const validateUsername = () => {
  errors.username = ''
  if (!form.username.trim()) {
    errors.username = '用户名不能为空'
  } else if (form.username.length < 3) {
    errors.username = '用户名至少3个字符'
  } else if (form.username.length > 20) {
    errors.username = '用户名最多20个字符'
  }
}

const validateEmail = () => {
  errors.email = ''
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!form.email.trim()) {
    errors.email = '邮箱不能为空'
  } else if (!emailRegex.test(form.email)) {
    errors.email = '请输入有效的邮箱地址'
  }
}

const validatePassword = () => {
  errors.password = ''
  if (!form.password) {
    errors.password = '密码不能为空'
  } else if (form.password.length < 6) {
    errors.password = '密码至少6个字符'
  } else if (form.password.length > 50) {
    errors.password = '密码最多50个字符'
  }
}

const validateConfirmPassword = () => {
  errors.confirmPassword = ''
  if (!form.confirmPassword) {
    errors.confirmPassword = '请确认密码'
  } else if (form.confirmPassword !== form.password) {
    errors.confirmPassword = '两次输入的密码不一致'
  }
}

const validateForm = () => {
  validateUsername()
  validateEmail()
  validatePassword()
  validateConfirmPassword()

  return !errors.username && !errors.email && !errors.password && !errors.confirmPassword
}

const handleRegister = async () => {
  if (!validateForm()) {
    ElMessage.error('请修正表单错误')
    return
  }

  loading.value = true
  try {
    const response = await userAPI.register({
      username: form.username,
      email: form.email,
      password: form.password
    })

    if (response.code === 200) {
      ElMessage.success('注册成功，请登录')
      // Clear form
      form.username = ''
      form.email = ''
      form.password = ''
      form.confirmPassword = ''
      // Redirect to login page after 1.5 seconds
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      ElMessage.error(response.message || '注册失败')
    }
  } catch (error) {
    console.error('Registration error:', error)
    const errorMessage = error.response?.data?.message || error.message || '注册失败，请稍后重试'
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 80px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem 1rem;
}

.register-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 3rem 2rem;
  width: 100%;
  max-width: 400px;
}

.register-card h1 {
  text-align: center;
  color: #333;
  margin: 0 0 0.5rem 0;
  font-size: 1.8rem;
}

.subtitle {
  text-align: center;
  color: #999;
  margin: 0 0 2rem 0;
  font-size: 0.95rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
  font-weight: 500;
  font-size: 0.95rem;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: border-color 0.3s, box-shadow 0.3s;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.error-message {
  display: block;
  color: #ff6b6b;
  font-size: 0.85rem;
  margin-top: 0.3rem;
}

.submit-btn {
  width: 100%;
  padding: 0.75rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-top: 1rem;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

.login-link p {
  margin: 0;
  color: #666;
  font-size: 0.95rem;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.login-link a:hover {
  color: #764ba2;
}

@media (max-width: 480px) {
  .register-card {
    padding: 2rem 1.5rem;
  }

  .register-card h1 {
    font-size: 1.5rem;
  }

  .form-group input {
    padding: 0.65rem;
    font-size: 0.9rem;
  }
}
</style>
