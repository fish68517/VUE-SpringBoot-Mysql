<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <h1 class="login-title">用户登录</h1>
        <p class="login-subtitle">登录您的账户以访问个人中心</p>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label for="username" class="form-label">用户名</label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              class="form-input"
              placeholder="请输入用户名"
              required
            />
          </div>

          <div class="form-group">
            <label for="password" class="form-label">密码</label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              class="form-input"
              placeholder="请输入密码"
              required
            />
          </div>

          <button type="submit" class="login-btn" :disabled="isLoading">
            {{ isLoading ? '登录中...' : '登录' }}
          </button>
        </form>

        <div class="login-footer">
          <p>还没有账户？<router-link to="/register" class="register-link">立即注册</router-link></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { UserService } from '../services'
import { useAuthStore } from '../stores/authStore'
import { useToast } from '../utils/useToast'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const { success, error } = useToast()

const form = ref({
  username: '',
  password: '',
})
const isLoading = ref(false)

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    error('请输入用户名和密码')
    return
  }

  isLoading.value = true
  try {
    const response = await UserService.login({
      username: form.value.username,
      password: form.value.password,
    })

    if (response.data) {
      authStore.setUser(response.data)
      success('登录成功')

      // 重定向到之前的页面或用户中心
      const redirect = route.query.redirect || '/user'
      router.push(redirect)
    }
  } catch (err) {
    error(err.response?.data?.message || '登录失败，请检查用户名和密码')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 60px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: var(--spacing-lg);
}

.login-container {
  width: 100%;
  max-width: 800px;
}

.login-card {
  background: white;
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  padding: var(--spacing-2xl);
}

.login-title {
  font-size: var(--font-size-2xl);
  color: var(--primary-color);
  margin-bottom: var(--spacing-sm);
  text-align: center;
  font-weight: 700;
}

.login-subtitle {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.form-label {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.form-input {
  padding: var(--spacing-md);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-base);
  transition: all 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.1);
}

.login-btn {
  padding: var(--spacing-md);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-base);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-footer {
  text-align: center;
  margin-top: var(--spacing-lg);
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.register-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.register-link:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}

@media (max-width: 768px) {
  .login-page {
    min-height: auto;
    padding: var(--spacing-md);
  }

  .login-card {
    padding: var(--spacing-lg);
  }

  .login-title {
    font-size: var(--font-size-xl);
  }
}
</style>
