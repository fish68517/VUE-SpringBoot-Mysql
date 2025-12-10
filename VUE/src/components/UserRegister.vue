<template>
  <div class="register-form">
    <h2>创建账号</h2>
    
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      @submit.prevent="handleRegister"
    >
      <!-- 用户名 -->
      <el-form-item label="用户名" prop="username">
        <div class="form-field-wrapper">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名（3-50个字符）"
            clearable
            maxlength="50"
            show-word-limit
            @blur="validateField('username')"
          />
          <div v-if="hasFieldError('username')" class="field-error">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ getFieldError('username') }}</span>
          </div>
          <div v-else-if="form.username" class="field-hint">
            用户名只能包含字母、数字、下划线和连字符，长度3-50个字符
          </div>
        </div>
      </el-form-item>

      <!-- 邮箱 -->
      <el-form-item label="邮箱" prop="email">
        <div class="form-field-wrapper">
          <el-input
            v-model="form.email"
            type="email"
            placeholder="请输入您的邮箱"
            clearable
            @blur="validateField('email')"
          />
          <div v-if="hasFieldError('email')" class="field-error">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ getFieldError('email') }}</span>
          </div>
          <div v-else-if="form.email" class="field-hint">
            请输入有效的邮箱地址
          </div>
        </div>
      </el-form-item>

      <!-- 密码 -->
      <el-form-item label="密码" prop="password">
        <div class="form-field-wrapper">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            show-password
            clearable
            @blur="validateField('password')"
          />
          <div v-if="hasFieldError('password')" class="field-error">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ getFieldError('password') }}</span>
          </div>
          <div v-else-if="form.password" class="field-hint">
            密码强度：{{ passwordStrength }}
          </div>
        </div>
      </el-form-item>

      <!-- 确认密码 -->
      <el-form-item label="确认密码" prop="confirmPassword">
        <div class="form-field-wrapper">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            clearable
            @blur="validateField('confirmPassword')"
          />
          <div v-if="hasFieldError('confirmPassword')" class="field-error">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ getFieldError('confirmPassword') }}</span>
          </div>
        </div>
      </el-form-item>

      <!-- 注册按钮 -->
      <el-form-item>
        <el-button
          type="primary"
          @click="handleRegister"
          :loading="loading"
          style="width: 100%"
        >
          立即注册
        </el-button>
      </el-form-item>

      <!-- 登录链接 -->
      <el-form-item>
        <p class="login-link">
          已经有账号了？
          <router-link to="/login">立即登录</router-link>
        </p>
      </el-form-item>
    </el-form>

    <!-- 全局错误提示 -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      closable
      @close="errorMessage = ''"
      style="margin-top: 20px"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import { authService } from '../services/authService'
import { useUserStore } from '../stores/userStore'
import { useFormValidation } from '../composables/useFormValidation'
import { validatePassword } from '../utils/validationUtils'
import { showSuccess, showError } from '../utils/notificationUtils'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const errorMessage = ref('')

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// 表单校验规则（也全部改成中文提示）
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度必须在3到50个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度必须在6到100个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 表单校验组合式函数（错误提示也建议改成中文，下面是示例）
const {
  errors,
  touched,
  validateField,
  validateForm,
  touchField,
  getFieldError,
  hasFieldError,
  isFormValid
} = useFormValidation(form, {
  username: { required: true, label: '用户名', minLength: 3, maxLength: 50 },
  email: { required: true, type: 'email', label: '邮箱' },
  password: { required: true, label: '密码' },
  confirmPassword: {
    required: true,
    label: '确认密码',
    validator: (value) => {
      if (value !== form.password) {
        return { isValid: false, message: '两次密码不一致' }
      }
      return { isValid: true, message: '' }
    }
  }
})

const passwordStrength = computed(() => {
  if (!form.password) return '无'
  const validation = validatePassword(form.password)
  const map = { weak: '弱', medium: '中', strong: '强', veryStrong: '非常强' }
  return map[validation.strength] || '中'
})

const handleRegister = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true
    errorMessage.value = ''

    const response = await authService.register({
      username: form.username,
      email: form.email,
      password: form.password
    })

    userStore.setToken(response.data.token)
    userStore.setUser(response.data.user)

    showSuccess('注册成功！欢迎加入旅行记忆系统')
    router.push('/dashboard')
  } catch (error) {
    loading.value = false
    
    let errorMsg = '注册失败，请稍后重试'
    if (error.message) {
      errorMsg = error.message
    } else if (error.errors && error.errors.length > 0) {
      errorMsg = error.errors[0].message
    }
    
    errorMessage.value = errorMsg
    showError(errorMsg)
  }
}
</script>

<style scoped>
/* 样式部分保持不变 */
.register-form {
  max-width: 420px;
  margin: 40px auto;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.register-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
  font-size: 24px;
}

.login-link {
  text-align: center;
  margin: 20px 0 0;
  font-size: 14px;
  color: #606266;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}

.login-link a:hover {
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

.field-hint {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}
</style>