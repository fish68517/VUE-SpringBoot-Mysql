<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <h1 class="register-title">用户注册</h1>
        <p class="register-subtitle">创建新账户以开始使用平台</p>

        <form @submit.prevent="handleRegister" class="register-form">
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
              placeholder="请输入密码（至少6个字符）"
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
          <p>已有账户？<router-link to="/login" class="login-link">立即登录</router-link></p>
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
  // 验证表单
  if (!form.value.username || !form.value.email || !form.value.password || !form.value.confirmPassword) {
    error('请填写所有字段')
    return
  }

  if (form.value.username.length < 3 || form.value.username.length > 20) {
    error('用户名长度应为3-20个字符')
    return
  }

  if (form.value.password.length < 6) {
    error('密码长度至少为6个字符')
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
  min-height: calc(100vh - 60px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: var(--spacing-lg);
}

.register-container {
  width: 100%;
  max-width: 800px;
}

.register-card {
   background: white;
  /* 卡片整体留白：让内容别贴边 */
  padding: 36px 40px !important;   /* 关键：增大内边距 */
  border-radius: 14px;
  box-sizing: border-box;
}

.register-title {
  font-size: var(--font-size-2xl);
  color: var(--primary-color);
  margin-bottom: var(--spacing-sm);
  text-align: center;
  font-weight: 700;
}

.register-subtitle {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  text-align: center;
  margin-bottom: var(--spacing-xl);
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

.register-btn {
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

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.register-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.register-footer {
  text-align: center;
  margin-top: var(--spacing-lg);
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.login-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.login-link:hover {
  color: var(--primary-dark);
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
