<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="login-title">{{ $t('auth.login') }}</h1>
      
      <!-- 角色选择 -->
      <div class="role-selector">
        <el-radio-group v-model="selectedRole" @change="resetForm">
          <el-radio label="USER">{{ $t('common.user') || '用户' }}</el-radio>
          <el-radio label="DOCTOR">{{ $t('auth.doctorRegister') }}</el-radio>
          <el-radio label="ADMIN">{{ $t('auth.adminLogin') }}</el-radio>
        </el-radio-group>
      </div>

      <!-- 登录表单 -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            :placeholder="$t('auth.username')"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            :placeholder="$t('auth.password')"
            type="password"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="login-button"
            @click="handleLogin"
            :loading="isLoading"
          >
            {{ $t('auth.login') }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 注册链接 -->
      <div class="register-link">
        <span>{{ $t('common.noAccount') || '没有账户？' }}</span>
        <el-link type="primary" @click="goToRegister">
          {{ $t('auth.register') }}
        </el-link>
      </div>

      <!-- 错误提示 -->
      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        :closable="true"
        @close="errorMessage = ''"
        class="error-alert"
      />
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI } from '../services/api'
import { authService } from '../services/auth'

export default {
  name: 'LoginPage',
  setup() {
    const router = useRouter()
    const loginFormRef = ref(null)
    const isLoading = ref(false)
    const errorMessage = ref('')
    const selectedRole = ref('USER')

    const loginForm = ref({
      username: '',
      password: ''
    })

    const loginRules = {
      username: [
        { required: true, message: '用户名为必填项', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '密码为必填项', trigger: 'blur' },
        { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
      ]
    }

    const handleLogin = async () => {
      if (!loginFormRef.value) return

      try {
        await loginFormRef.value.validate()
        isLoading.value = true
        errorMessage.value = ''

        let response
        const loginData = {
          username: loginForm.value.username,
          password: loginForm.value.password
        }

        // 根据选择的角色调用不同的登录接口
        if (selectedRole.value === 'DOCTOR') {
          response = await authAPI.doctorLogin(loginData)
        } else if (selectedRole.value === 'ADMIN') {
          response = await authAPI.adminLogin(loginData)
        } else {
          response = await authAPI.login(loginData)
        }

        // 保存认证信息
        if (response && response.data) {
          const { token, user } = response.data
          authService.setAuth(token, user, selectedRole.value)
          
          ElMessage.success('登录成功')
          
          // 根据角色重定向到对应的主页面
          if (selectedRole.value === 'DOCTOR') {
            router.push('/doctor/patients')
          } else if (selectedRole.value === 'ADMIN') {
            router.push('/admin/users')
          } else {
            router.push('/user/profile')
          }
        }
      } catch (error) {
        isLoading.value = false
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '登录失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '登录失败，请检查用户名和密码'
        }
      } finally {
        isLoading.value = false
      }
    }

    const resetForm = () => {
      if (loginFormRef.value) {
        loginFormRef.value.resetFields()
      }
      errorMessage.value = ''
    }

    const goToRegister = () => {
      router.push('/register')
    }

    return {
      loginFormRef,
      loginForm,
      loginRules,
      isLoading,
      errorMessage,
      selectedRole,
      handleLogin,
      resetForm,
      goToRegister
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

.login-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 400px;
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 28px;
  font-weight: bold;
}

.role-selector {
  margin-bottom: 20px;
  text-align: center;
}

.role-selector :deep(.el-radio-group) {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.register-link span {
  margin-right: 5px;
}

.error-alert {
  margin-top: 20px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper) {
  padding: 8px 12px;
}
</style>
