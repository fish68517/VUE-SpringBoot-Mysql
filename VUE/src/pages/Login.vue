<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>沈阳音乐节</h1>
        <p>登录您的账户</p>
      </div>

      <!-- Tab 切换 -->
      <el-tabs v-model="activeTab" class="login-tabs">
        <!-- 账号密码登录 -->
        <el-tab-pane label="账号密码登录" name="password">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            @keyup.enter="handlePasswordLogin"
          >
            <el-form-item prop="phone">
              <el-input
                v-model="passwordForm.phone"
                placeholder="请输入手机号"
                prefix-icon="Phone"
                clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="passwordForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                class="login-btn"
                :loading="passwordLoading"
                @click="handlePasswordLogin"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 手机号验证码登录 -->
        <el-tab-pane label="验证码登录" name="sms">
          <el-form
            ref="smsFormRef"
            :model="smsForm"
            :rules="smsRules"
            @keyup.enter="handleSmsLogin"
          >
            <el-form-item prop="phone">
              <el-input
                v-model="smsForm.phone"
                placeholder="请输入手机号"
                prefix-icon="Phone"
                clearable
              />
            </el-form-item>

            <el-form-item prop="code">
              <div class="sms-input-group">
                <el-input
                  v-model="smsForm.code"
                  placeholder="请输入验证码"
                  prefix-icon="Message"
                  clearable
                />
                <el-button
                  :disabled="smsCountdown > 0 || !smsForm.phone"
                  @click="handleSendSms"
                  :loading="smsLoading"
                >
                  {{ smsCountdown > 0 ? `${smsCountdown}s` : '发送验证码' }}
                </el-button>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                class="login-btn"
                :loading="smsLoginLoading"
                @click="handleSmsLogin"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div class="login-footer">
        <p>
          还没有账户？
          <router-link to="/register">立即注册</router-link>
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
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('password')
const passwordFormRef = ref<FormInstance>()
const smsFormRef = ref<FormInstance>()

// 密码登录表单
const passwordForm = reactive({
  phone: '',
  password: '',
})

// 验证码登录表单
const smsForm = reactive({
  phone: '',
  code: '',
})

// 加载状态
const passwordLoading = ref(false)
const smsLoading = ref(false)
const smsLoginLoading = ref(false)
const smsCountdown = ref(0)

// 验证规则
const passwordRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于6位', trigger: 'blur' },
  ],
}

const smsRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' },
  ],
}

// 密码登录
const handlePasswordLogin = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    passwordLoading.value = true
    try {
      const response: any = await userApi.login({
        phone: passwordForm.phone,
        password: passwordForm.password,
      })

      if (response.code === 200) {
        const { token, userId } = response.data
        userStore.setToken(token)
        userStore.setUserInfo({ userId, phone: passwordForm.phone })
        ElMessage.success('登录成功')
        router.push('/')
      } else {
        ElMessage.error(response.message || '登录失败')
      }
    } catch (error: any) {
      const message = error.response?.data?.message || '登录失败，请稍后重试'
      ElMessage.error(message)
    } finally {
      passwordLoading.value = false
    }
  })
}

// 发送短信验证码
const handleSendSms = async () => {
  if (!smsForm.phone) {
    ElMessage.warning('请先输入手机号')
    return
  }

  if (!/^1[3-9]\d{9}$/.test(smsForm.phone)) {
    ElMessage.warning('请输入有效的手机号')
    return
  }

  smsLoading.value = true
  try {
    const response: any = await userApi.sendSms(smsForm.phone)
    if (response.code === 200) {
      ElMessage.success('验证码已发送')
      startSmsCountdown()
    } else {
      ElMessage.error(response.message || '发送失败')
    }
  } catch (error: any) {
    // 如果后端未实现SMS功能，显示提示
    ElMessage.warning('短信功能暂未开放，请使用账号密码登录')
  } finally {
    smsLoading.value = false
  }
}

// 验证码倒计时
const startSmsCountdown = () => {
  smsCountdown.value = 60
  const timer = setInterval(() => {
    smsCountdown.value--
    if (smsCountdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

// 验证码登录
const handleSmsLogin = async () => {
  if (!smsFormRef.value) return

  await smsFormRef.value.validate(async (valid) => {
    if (!valid) return

    smsLoginLoading.value = true
    try {
      const response: any = await userApi.verifySms(smsForm.phone, smsForm.code)
      if (response.code === 200) {
        const { token, userId } = response.data
        userStore.setToken(token)
        userStore.setUserInfo({ userId, phone: smsForm.phone })
        ElMessage.success('登录成功')
        router.push('/')
      } else {
        ElMessage.error(response.message || '登录失败')
      }
    } catch (error: any) {
      const message = error.response?.data?.message || '登录失败，请稍后重试'
      ElMessage.error(message)
    } finally {
      smsLoginLoading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.login-header p {
  color: #999;
  margin: 0;
  font-size: 14px;
}

.login-tabs {
  margin-bottom: 20px;
}

.sms-input-group {
  display: flex;
  gap: 10px;
}

.sms-input-group :deep(.el-input) {
  flex: 1;
}

.sms-input-group .el-button {
  width: 120px;
  white-space: nowrap;
}

.login-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
  font-weight: 500;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.login-footer p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.login-footer a {
  color: #667eea;
  font-weight: 500;
  margin-left: 5px;
}

.login-footer a:hover {
  color: #764ba2;
}
</style>
