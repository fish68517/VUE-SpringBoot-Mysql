<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h1>沈阳音乐节</h1>
        <p>创建新账户</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        @keyup.enter="handleRegister"
      >
        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            @input="checkPasswordStrength"
          />
          <div class="password-strength">
            <div class="strength-bar">
              <div
                class="strength-fill"
                :class="passwordStrengthClass"
                :style="{ width: passwordStrengthPercent + '%' }"
              />
            </div>
            <span class="strength-text">{{ passwordStrengthText }}</span>
          </div>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="agreement">
          <el-checkbox v-model="form.agreement">
            我已阅读并同意
            <a href="#" @click.prevent>《用户协议》</a>
            和
            <a href="#" @click.prevent>《隐私政策》</a>
          </el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <p>
          已有账户？
          <router-link to="/login">立即登录</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance } from 'element-plus'
import { userApi } from '@/api/user'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  phone: '',
  password: '',
  confirmPassword: '',
  agreement: false,
})

// 密码强度
const passwordStrength = ref(0)
const passwordStrengthText = ref('弱')
const passwordStrengthClass = ref('weak')
const passwordStrengthPercent = ref(0)

// 自定义验证函数
const validatePhone = (_rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入有效的手机号'))
  } else {
    callback()
  }
}

const validatePassword = (_rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不少于6位'))
  } else if (passwordStrength.value < 2) {
    callback(new Error('密码强度过弱，请包含大小写字母、数字或特殊字符'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (_rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateAgreement = (_rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请同意用户协议和隐私政策'))
  } else {
    callback()
  }
}

const rules = {
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  password: [{ validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }],
  agreement: [{ validator: validateAgreement, trigger: 'change' }],
}

// 检查密码强度
const checkPasswordStrength = () => {
  const password = form.password
  let strength = 0

  if (!password) {
    passwordStrength.value = 0
    passwordStrengthText.value = '弱'
    passwordStrengthClass.value = 'weak'
    passwordStrengthPercent.value = 0
    return
  }

  // 检查长度
  if (password.length >= 6) strength++
  if (password.length >= 8) strength++

  // 检查大小写字母
  if (/[a-z]/.test(password)) strength++
  if (/[A-Z]/.test(password)) strength++

  // 检查数字
  if (/\d/.test(password)) strength++

  // 检查特殊字符
  if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) strength++

  passwordStrength.value = strength

  if (strength <= 2) {
    passwordStrengthText.value = '弱'
    passwordStrengthClass.value = 'weak'
    passwordStrengthPercent.value = 33
  } else if (strength <= 4) {
    passwordStrengthText.value = '中'
    passwordStrengthClass.value = 'medium'
    passwordStrengthPercent.value = 66
  } else {
    passwordStrengthText.value = '强'
    passwordStrengthClass.value = 'strong'
    passwordStrengthPercent.value = 100
  }
}

// 注册
const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const response: any = await userApi.register({
        phone: form.phone,
        password: form.password,
      })

      if (response.code === 200 || response.code === 201) {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } else {
        ElMessage.error(response.message || '注册失败')
      }
    } catch (error: any) {
      const message = error.response?.data?.message || '注册失败，请稍后重试'
      ElMessage.error(message)
    } finally {
      loading.value = false
    }
  })
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

.register-box {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  padding: 40px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.register-header p {
  color: #999;
  margin: 0;
  font-size: 14px;
}

.password-strength {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: #eee;
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  transition: all 0.3s ease;
}

.strength-fill.weak {
  background: #f56c6c;
}

.strength-fill.medium {
  background: #e6a23c;
}

.strength-fill.strong {
  background: #67c23a;
}

.strength-text {
  font-size: 12px;
  color: #999;
  min-width: 30px;
}

.register-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
  font-weight: 500;
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.register-footer p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.register-footer a {
  color: #667eea;
  font-weight: 500;
  margin-left: 5px;
}

.register-footer a:hover {
  color: #764ba2;
}

:deep(.el-checkbox__label) {
  font-size: 14px;
  color: #666;
}

:deep(.el-checkbox__label a) {
  color: #667eea;
  text-decoration: underline;
}

:deep(.el-checkbox__label a:hover) {
  color: #764ba2;
}
</style>
