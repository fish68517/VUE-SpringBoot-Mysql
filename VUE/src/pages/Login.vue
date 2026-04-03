<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <h1 class="login-title">用户登录</h1>
        <p class="login-subtitle">登录账号后可继续访问用户中心、收藏、评论和投票功能。</p>

        <form class="login-form" @submit.prevent="handleLogin">
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
          <p>
            还没有账号？
            <router-link to="/register" class="register-link">立即注册</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
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

const redirectPath = computed(() => {
  const redirect = route.query.redirect
  if (typeof redirect !== 'string') {
    return ''
  }

  if (redirect === '/login' || redirect === '/register') {
    return ''
  }

  return redirect
})

const handleLogin = async () => {
  if (isLoading.value) {
    return
  }

  isLoading.value = true

  try {
    const user = await UserService.login({
      username: form.value.username,
      password: form.value.password,
    })

    if (!user) {
      error('登录失败，请检查用户名和密码')
      return
    }

    authStore.setUser(user)
    success('登录成功')

    if (redirectPath.value) {
      router.replace(redirectPath.value)
      return
    }

    if ((user.role || '').toLowerCase() === 'admin') {
      router.replace('/admin/users')
      return
    }

    router.replace('/home')
  } catch (err) {
    console.error(err)
    error(err.response?.message || '登录请求失败，请稍后重试')
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
  min-height: calc(100vh - 120px);
  padding: var(--spacing-lg);
  background: linear-gradient(135deg, #eef4fb 0%, #d9e6f4 45%, #c4d6ea 100%);
}

.login-container {
  width: 100%;
  max-width: 800px;
}

.login-card {
  padding: 36px 40px;
  border-radius: 14px;
  background: #ffffff;
  box-sizing: border-box;
  box-shadow: 0 18px 36px rgba(29, 82, 124, 0.12);
}

.login-title {
  margin-bottom: var(--spacing-sm);
  color: #1d527c;
  font-size: 34px;
  text-align: center;
  font-weight: 700;
}

.login-subtitle {
  margin-bottom: var(--spacing-xl);
  color: #60778e;
  font-size: 15px;
  text-align: center;
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
  color: #24425c;
  font-size: 15px;
  font-weight: 700;
}

.form-input {
  padding: var(--spacing-md);
  border: 1px solid #c9d8e6;
  border-radius: var(--border-radius-md);
  background: #f9fbfd;
  color: #24425c;
  font-size: 15px;
  transition: all 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #1d527c;
  box-shadow: 0 0 0 3px rgba(29, 82, 124, 0.12);
}

.login-btn {
  padding: var(--spacing-md);
  border: none;
  border-radius: var(--border-radius-md);
  background: linear-gradient(135deg, #1d527c 0%, #153f63 100%);
  color: #ffffff;
  font-size: 16px;
  font-weight: 700;
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
  margin-top: var(--spacing-lg);
  color: #60778e;
  font-size: 14px;
  text-align: center;
}

.register-link {
  color: #1d527c;
  font-weight: 600;
  text-decoration: none;
}

.register-link:hover {
  color: #153f63;
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
