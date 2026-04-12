<template>
  <el-form
    ref="loginFormRef"
    :model="loginForm"
    :rules="rules"
    label-width="90px"
    @submit.prevent="handleLogin"
  >
   
    <el-form-item label="邮箱" prop="email">
      <div class="form-field-wrapper">
        <el-input
          v-model="loginForm.email"
          type="email"
          placeholder="请输入您的邮箱"
          clearable
          @blur="validateField('email')"
        />
        <div v-if="hasFieldError('email')" class="field-error">
          <el-icon><CircleCloseFilled /></el-icon>
          <span>{{ getFieldError('email') }}</span>
        </div>
      </div>
    </el-form-item>

   
    <el-form-item label="密码" prop="password">
      <div class="form-field-wrapper">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="请输入密码"
          show-password
          @blur="validateField('password')"
        />
        <div v-if="hasFieldError('password')" class="field-error">
          <el-icon><CircleCloseFilled /></el-icon>
          <span>{{ getFieldError('password') }}</span>
        </div>
      </div>
    </el-form-item>

    
    <el-form-item>
      <el-button
        type="primary"
        @click="handleLogin"
        :loading="isLoading"
        style="width: 100%; height: 44px; font-size: 16px"
      >
        立即登录
      </el-button>
    </el-form-item>


    <el-form-item class="register-link">
      <span>还没有账号？</span>
      <router-link to="/register">立即注册</router-link>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/userStore'
import { useFormValidation} from '../composables/useFormValidation'
import { loginService } from '../services/loginService'
import { showSuccess, showError } from '../utils/notificationUtils'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const isLoading = ref(false)

const loginForm = reactive({
  email: '',
  password: ''
})

// 表单校验规则全部改成中文提示
const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

// 表单校验组合式函数
const {
  validateField,
  getFieldError,
  hasFieldError,
} = useFormValidation(loginForm, {
  email: { required: true, type: 'email', label: '邮箱' },
  password: { required: true, minLength: 6, label: '密码' }
})

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    isLoading.value = true

    const response = await loginService.login({
      email: loginForm.email,
      password: loginForm.password
    })

    // 保存登录信息
    userStore.setToken(response.data.token)
    userStore.setUser(response.data.user)

    showSuccess('登录成功！欢迎回来')
    router.push('/dashboard')
  } catch (error) {
    const msg = error.message || '登录失败，请检查邮箱和密码是否正确'
    showError(msg)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
:deep(.el-form) {
  max-width: 400px;
  margin: 0 auto;
  padding: 40px 20px;
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #606266;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
  margin-left: 4px;
}

.register-link a:hover {
  text-decoration: underline;
}

.form-field-wrapper {
  position: relative;
}

.field-error {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
  font-size: 12px;
  color: #f56c6c;
}

.field-error :deep(.el-icon) {
  font-size: 14px;
  flex-shrink: 0;
}
</style>