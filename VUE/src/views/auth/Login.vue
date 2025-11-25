<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>小鲨鱼运动健身管理系统</h2>
          <p>用户登录</p>
        </div>
      </template>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="80px"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <el-form-item>
          <div class="register-link">
            还没有账号？
            <router-link to="/register">立即注册</router-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/auth'
import { setToken, setUserInfo } from '@/utils/auth'
import { commonRules } from '@/utils/validator'
import { showError } from '@/utils/feedback'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名至少3个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    // Validate form
    const valid = await loginFormRef.value.validate().catch(() => false)
    if (!valid) {
      showError('Please fill in all login information')
      return
    }

    loading.value = true

    const response = await login(loginForm)
    console.log('实际收到的 response:', response) // <--- 查看控制台这里的输出
    // Store token and user info
    setToken(response.token)
    setUserInfo(response)

    // Success message will be shown by the redirect

    // Redirect based on role
    const role = response.role
    if (role === 'admin') {
      router.push('/admin/dashboard')
    } else if (role === 'coach') {
      router.push('/coach/dashboard')
    } else {
      router.push('/home')
    }
  } catch (error) {
    // Error is already handled by request interceptor
    console.error('Login error:', error)
    // Only show additional message if it's a validation error
    if (error.message && !error.response) {
      showError(error.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  /* 核心修改开始：强制铺满屏幕 */
  position: fixed; /* 使用固定定位，无视父级元素的 padding/margin */
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* 核心修改结束 */

  display: flex;
  justify-content: center;
  align-items: center;
  
  /* 背景渐变 */
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  /* 防止背景图过大出现滚动条 */
  overflow: hidden; 
}

.login-card {
  width: 450px;
  max-width: 90%;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 24px;
}

.card-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.register-link {
  width: 100%;
  text-align: center;
  color: #606266;
  font-size: 14px;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
