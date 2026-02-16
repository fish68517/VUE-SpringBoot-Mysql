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
  try {
    
    // 1) 调登录接口
    const res = await UserService.login({
      username: form.value.username,
      password: form.value.password,
    })

    // 打印res
    console.log(res)

    // 2) 兼容后端统一响应
    const payload =  res
    if (!payload ) {
      useToast().error(payload?.message || '登录失败')
      return
    }

    const user = payload
    if (!user) {
      useToast().error('登录失败：用户数据为空')
      return
    }

    // 3) 保存登录态（你项目如果用 pinia，就调用 store；否则 localStorage）
    // ——按你现有实现二选一即可——
    // authStore.setUser(user)
    localStorage.setItem('user', JSON.stringify(user))
    localStorage.setItem('isLoggedIn', 'true')

    // 3) 保存登录态：必须调用 authStore 的方法！
    // 删掉或者注释掉你手写的 localStorage.setItem
    authStore.setUser(user)
    useToast().success('登录成功')

    // 4) 按 role 跳转
    const role = (user.role || '').toLowerCase()

    if (role === 'admin') {
      // 管理员主页：你项目里一般是 /admin/users 或 /admin/system
      router.replace('/admin/users')
    } else {
      // 普通用户主页：你项目里一般是 /home 或 /artworks
      router.replace('/home') // 如果你没有 /home，就改成 '/artworks'
    }
  } catch (e) {
    console.error(e)
    useToast().error('登录请求失败，请检查网络或后端服务')
  } finally {
    
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
  /* 卡片整体留白：让内容别贴边 */
  padding: 36px 40px !important;   /* 关键：增大内边距 */
  border-radius: 14px;
  box-sizing: border-box;
  

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
