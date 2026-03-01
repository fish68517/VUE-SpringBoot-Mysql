<template>
  <div class="register-container">
    <div class="register-card">
      <h1 class="register-title">{{ $t('auth.register') }}</h1>

      <!-- 用户类型选择 -->
      <div class="user-type-selector">
        <el-radio-group v-model="userType" @change="resetForm">
          <el-radio label="USER">{{ $t('common.user') || '普通用户' }}</el-radio>
          <el-radio label="DOCTOR">{{ $t('auth.doctorRegister') }}</el-radio>
        </el-radio-group>
      </div>

      <!-- 注册表单 -->
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        @submit.prevent="handleRegister"
      >
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            :placeholder="$t('auth.username')"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <!-- 邮箱 -->
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            :placeholder="$t('auth.email')"
            type="email"
            prefix-icon="Message"
            clearable
          />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            :placeholder="$t('auth.password')"
            type="password"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            :placeholder="$t('auth.confirmPassword')"
            type="password"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <!-- 医师特定字段 -->
        <template v-if="userType === 'DOCTOR'">
          <!-- 医师执业证号 -->
          <el-form-item prop="licenseNumber">
            <el-input
              v-model="registerForm.licenseNumber"
              :placeholder="$t('auth.doctorLicense')"
              prefix-icon="Document"
              clearable
            />
          </el-form-item>

          <!-- 专科 -->
          <el-form-item prop="specialization">
            <el-input
              v-model="registerForm.specialization"
              placeholder="专科"
              prefix-icon="Stethoscope"
              clearable
            />
          </el-form-item>

          <!-- 医院 -->
          <el-form-item prop="hospital">
            <el-input
              v-model="registerForm.hospital"
              placeholder="医院"
              prefix-icon="Building"
              clearable
            />
          </el-form-item>
        </template>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            class="register-button"
            @click="handleRegister"
            :loading="isLoading"
          >
            {{ $t('auth.register') }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 登录链接 -->
      <div class="login-link">
        <span>{{ $t('common.hasAccount') || '已有账户？' }}</span>
        <el-link type="primary" @click="goToLogin">
          {{ $t('auth.login') }}
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

export default {
  name: 'RegisterPage',
  setup() {
    const router = useRouter()
    const registerFormRef = ref(null)
    const isLoading = ref(false)
    const errorMessage = ref('')
    const userType = ref('USER')

    const registerForm = ref({
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      licenseNumber: '',
      specialization: '',
      hospital: ''
    })

    // 自定义验证函数 - 确认密码
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请确认密码'))
      } else if (value !== registerForm.value.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    // 自定义验证函数 - 邮箱格式
    const validateEmail = (rule, value, callback) => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (value === '') {
        callback(new Error('请输入邮箱'))
      } else if (!emailRegex.test(value)) {
        callback(new Error('邮箱格式不正确'))
      } else {
        callback()
      }
    }

    const registerRules = {
      username: [
        { required: true, message: '用户名为必填项', trigger: 'blur' },
        { min: 3, message: '用户名长度至少为3位', trigger: 'blur' }
      ],
      email: [
        { validator: validateEmail, trigger: 'blur' }
      ],
      password: [
        { required: true, message: '密码为必填项', trigger: 'blur' },
        { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
      ],
      confirmPassword: [
        { validator: validateConfirmPassword, trigger: 'blur' }
      ],
      licenseNumber: [
        { required: true, message: '医师执业证号为必填项', trigger: 'blur' }
      ],
      specialization: [
        { required: true, message: '专科为必填项', trigger: 'blur' }
      ],
      hospital: [
        { required: true, message: '医院为必填项', trigger: 'blur' }
      ]
    }

    const handleRegister = async () => {
      if (!registerFormRef.value) return

      try {
        await registerFormRef.value.validate()
        isLoading.value = true
        errorMessage.value = ''

        let response
        let registerData = {
          username: registerForm.value.username,
          email: registerForm.value.email,
          password: registerForm.value.password
        }

        // 根据用户类型调用不同的注册接口
        if (userType.value === 'DOCTOR') {
          registerData = {
            ...registerData,
            licenseNumber: registerForm.value.licenseNumber,
            specialization: registerForm.value.specialization,
            hospital: registerForm.value.hospital
          }
          response = await authAPI.doctorRegister(registerData)
        } else {
          response = await authAPI.register(registerData)
        }

        if (response && response.data) {
          ElMessage.success('注册成功，请登录')
          // 重定向到登录页面
          router.push('/login')
        }
      } catch (error) {
        isLoading.value = false
        if (error.response && error.response.data) {
          errorMessage.value = error.response.data.message || '注册失败'
        } else if (error.message) {
          errorMessage.value = error.message
        } else {
          errorMessage.value = '注册失败，请稍后重试'
        }
      } finally {
        isLoading.value = false
      }
    }

    const resetForm = () => {
      if (registerFormRef.value) {
        registerFormRef.value.resetFields()
      }
      errorMessage.value = ''
    }

    const goToLogin = () => {
      router.push('/login')
    }

    return {
      registerFormRef,
      registerForm,
      registerRules,
      isLoading,
      errorMessage,
      userType,
      handleRegister,
      resetForm,
      goToLogin
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 450px;
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 28px;
  font-weight: bold;
}

.user-type-selector {
  margin-bottom: 20px;
  text-align: center;
}

.user-type-selector :deep(.el-radio-group) {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.register-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.login-link span {
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
