<template>
  <div class="login-container">
    <div class="login-box">
      <h1>在线投稿系统</h1>
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        @submit.prevent="handleLogin"
        @keyup.enter="handleLogin"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名"
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码"
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            style="width: 100%"
            :loading="loading"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <router-link to="/register">没有账号？立即注册</router-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/userStore'
import { authService } from '../../services/authService'
import { useFormValidation } from '../../composables/useFormValidation'
import { useLoading } from '../../composables/useLoading'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const { validationRules } = useFormValidation()
const { loading, executeWithLoading } = useLoading()

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: validationRules.username,
  password: validationRules.password
}

const handleLogin = async () => {
  if (!await formRef.value.validate()) {
    return
  }

  const result = await executeWithLoading(
    () => authService.login(form.value.username, form.value.password),
    {
      loadingMessage: '登录中...',
      successMessage: '登录成功',
      errorMessage: '登录失败，请检查用户名和密码'
    }
  )

  if (result.success) {
    const response = result.data
    userStore.setUser(response.data)
    
    // Redirect based on role
    const role = response.data.role
    if (role === 'AUTHOR') {
      router.push('/author/dashboard')
    } else if (role === 'EDITOR') {
      router.push('/editor/dashboard')
    } else if (role === 'REVIEWER') {
      router.push('/reviewer/dashboard')
    } else if (role === 'ADMIN') {
      router.push('/admin/dashboard')
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-box h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

a {
  color: #667eea;
  text-decoration: none;
}

a:hover {
  color: #764ba2;
}
</style>
