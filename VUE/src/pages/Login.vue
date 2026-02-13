<template>
  <div class="login-container">
    <div class="login-card">
      <h1>登录账号</h1>
      <p class="subtitle">进入纳西族纹样展示平台</p>

      <form @submit.prevent="handleLogin">
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

        <!-- Password Field -->
        <div class="form-group">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            @blur="validatePassword"
          />
          <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
        </div>

        <!-- Submit Button -->
        <button type="submit" class="submit-btn" :disabled="loading">
          <span v-if="!loading">登录</span>
          <span v-else>登录中...</span>
        </button>
      </form>

      <!-- Register Link -->
      <div class="register-link">
        <p>还没有账号？<router-link to="/register">立即注册</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userAPI } from '../services/api'
import { useUserStore } from '../store'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const form = reactive({
  username: '',
  password: ''
})

const errors = reactive({
  username: '',
  password: ''
})

const loading = ref(false)

// Validation functions
const validateUsername = () => {
  errors.username = ''
  if (!form.username.trim()) {
    errors.username = '用户名不能为空'
  }
}

const validatePassword = () => {
  errors.password = ''
  if (!form.password) {
    errors.password = '密码不能为空'
  }
}

const validateForm = () => {
  validateUsername()
  validatePassword()

  return !errors.username && !errors.password
}

const handleLogin = async () => {
  if (!validateForm()) {
    ElMessage.error('请填写完整的登录信息')
    return
  }

  loading.value = true
  try {
    const response = await userAPI.login({
      username: form.username,
      password: form.password
    })

    if (response.code === 200) {
      const user = response.data
      
      // Store user info and token in store
      userStore.setUser(user)
      // Generate a simple token (in real app, backend would return JWT)
      const token = `token_${user.id}_${Date.now()}`
      userStore.setToken(token)

      ElMessage.success('登录成功')
      
      // Redirect to previous page or home
      const redirectPath = route.query.redirect || '/'
      setTimeout(() => {
        router.push(redirectPath)
      }, 500)
    } else {
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error) {
    console.error('Login error:', error)
    const errorMessage = error.response?.data?.message || error.message || '登录失败，请稍后重试'
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 80px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem 1rem;
}

.login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 3rem 2rem;
  width: 100%;
  max-width: 400px;
}

.login-card h1 {
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

.register-link {
  text-align: center;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

.register-link p {
  margin: 0;
  color: #666;
  font-size: 0.95rem;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.register-link a:hover {
  color: #764ba2;
}

@media (max-width: 480px) {
  .login-card {
    padding: 2rem 1.5rem;
  }

  .login-card h1 {
    font-size: 1.5rem;
  }

  .form-group input {
    padding: 0.65rem;
    font-size: 0.9rem;
  }
}
</style>
