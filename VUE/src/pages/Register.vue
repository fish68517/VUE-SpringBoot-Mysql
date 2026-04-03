<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <h1 class="register-title">用户注册</h1>
        <p class="register-subtitle">创建新账号后即可收藏作品、参与评论和使用个人中心。</p>

        <form class="register-form" @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="username" class="form-label">用户名</label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              class="form-input"
              placeholder="请输入用户名（3-20个字符）"
              required
            />
          </div>

          <div class="form-group">
            <label for="email" class="form-label">邮箱</label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              class="form-input"
              placeholder="请输入邮箱地址"
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
              placeholder="请输入密码（至少6位）"
              required
            />
          </div>

          <div class="form-group">
            <label for="confirmPassword" class="form-label">确认密码</label>
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              class="form-input"
              placeholder="请再次输入密码"
              required
            />
          </div>

          <button type="submit" class="register-btn" :disabled="isLoading">
            {{ isLoading ? '注册中...' : '注册' }}
          </button>
        </form>

        <div class="register-footer">
          <p>
            已有账号？
            <router-link to="/login" class="login-link">立即登录</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { UserService } from '../services'
import { useToast } from '../utils/useToast'

const router = useRouter()
const { success, error } = useToast()

const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
})
const isLoading = ref(false)

const handleRegister = async () => {
  if (!form.value.username || !form.value.email || !form.value.password || !form.value.confirmPassword) {
    error('请填写所有字段')
    return
  }

  if (form.value.username.length < 3 || form.value.username.length > 20) {
    error('用户名长度应为 3 到 20 个字符')
    return
  }

  if (form.value.password.length < 6) {
    error('密码长度至少为 6 位')
    return
  }

  if (form.value.password !== form.value.confirmPassword) {
    error('两次输入的密码不一致')
    return
  }

  isLoading.value = true

  try {
    await UserService.register({
      username: form.value.username,
      email: form.value.email,
      password: form.value.password,
    })

    success('注册成功，请登录')
    router.push({ name: 'Login' })
  } catch (err) {
    console.error(err)
    error(err.response?.message || '注册失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.register-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 120px);
  padding: var(--spacing-lg);
  background: linear-gradient(135deg, #eef4fb 0%, #d9e6f4 45%, #c4d6ea 100%);
}

.register-container {
  width: 100%;
  max-width: 800px;
}

.register-card {
  padding: 36px 40px;
  border-radius: 14px;
  background: #ffffff;
  box-sizing: border-box;
  box-shadow: 0 18px 36px rgba(29, 82, 124, 0.12);
}

.register-title {
  margin-bottom: var(--spacing-sm);
  color: #1d527c;
  font-size: 34px;
  text-align: center;
  font-weight: 700;
}

.register-subtitle {
  margin-bottom: var(--spacing-xl);
  color: #60778e;
  font-size: 15px;
  text-align: center;
}

.register-form {
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

.register-btn {
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

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.register-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.register-footer {
  margin-top: var(--spacing-lg);
  color: #60778e;
  font-size: 14px;
  text-align: center;
}

.login-link {
  color: #1d527c;
  font-weight: 600;
  text-decoration: none;
}

.login-link:hover {
  color: #153f63;
  text-decoration: underline;
}

@media (max-width: 768px) {
  .register-page {
    min-height: auto;
    padding: var(--spacing-md);
  }

  .register-card {
    padding: var(--spacing-lg);
  }

  .register-title {
    font-size: var(--font-size-xl);
  }
}
</style>
