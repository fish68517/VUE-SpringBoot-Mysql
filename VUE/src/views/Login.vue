<template>
  <div class="login-container">
    <Card title="用户登录" class="login-card">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading">
            登录
          </el-button>
          <router-link to="/register" class="register-link">
            没有账户？立即注册
          </router-link>
        </el-form-item>
      </el-form>
    </Card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { userAPI } from '@/api/user'
import Card from '@/components/common/Card.vue'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    const response = await userAPI.login(form.value)
    // Response is now the LoginResponse object directly (token, userId, username, userType, etc.)
    authStore.setAuth(response.token, response)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
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
  min-height: calc(100vh - 200px);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
}

.register-link {
  margin-left: 10px;
  color: #409eff;
  text-decoration: none;
}

.register-link:hover {
  text-decoration: underline;
}
</style>
