<template>
  <div class="auth-page">
    <section class="auth-intro">
      <router-link to="/" class="brand light-brand">
        <span class="brand-mark">EV</span>
        <span>
          <strong>汽车充电桩平台</strong>
          <small>信息查询与可视化导览</small>
        </span>
      </router-link>
      <div>
        <p class="eyebrow">CHARGING PLATFORM</p>
        <h1>让充电站信息<br />更直观、更好查</h1>
        <p class="intro-copy">第一阶段已经打通 MySQL 数据、Spring Boot 接口和 Vue 登录状态。</p>
      </div>
      <div class="demo-account">
        <strong>演示管理员账号</strong>
        <span>admin / admin123</span>
      </div>
    </section>

    <section class="auth-panel">
      <div class="auth-card">
        <el-tabs v-model="activeTab" stretch>
          <el-tab-pane label="账号登录" name="login">
            <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-position="top">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="loginForm.username" size="large" placeholder="请输入用户名" clearable />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="loginForm.password"
                  size="large"
                  type="password"
                  placeholder="请输入密码"
                  show-password
                  @keyup.enter="handleLogin"
                />
              </el-form-item>
              <el-button type="primary" size="large" class="submit-button" :loading="submitting" @click="handleLogin">
                登录
              </el-button>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="用户注册" name="register">
            <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-position="top">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="registerForm.username" size="large" placeholder="请输入用户名" clearable />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="registerForm.password" size="large" type="password" placeholder="请输入密码" show-password />
              </el-form-item>
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="registerForm.nickname" size="large" placeholder="选填，默认使用用户名" clearable />
              </el-form-item>
              <el-button type="primary" size="large" class="submit-button" :loading="submitting" @click="handleRegister">
                注册并登录
              </el-button>
            </el-form>
          </el-tab-pane>
        </el-tabs>

        <el-alert
          class="learning-note"
          title="本项目按学习需求使用明文密码，不使用 JWT，也不进行权限校验。"
          type="info"
          :closable="false"
          show-icon
        />
        <router-link to="/" class="return-link">暂不登录，返回首页</router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('login')
const submitting = ref(false)
const loginFormRef = ref()
const registerFormRef = ref()

const loginForm = reactive({
  username: 'admin',
  password: 'admin123'
})

const registerForm = reactive({
  username: '',
  password: '',
  nickname: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { max: 50, message: '用户名不能超过50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { max: 100, message: '密码不能超过100个字符', trigger: 'blur' }
  ]
}

const goAfterLogin = user => {
  router.push(user.userType === 1 ? '/admin' : '/')
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    submitting.value = true
    const user = await userStore.login(loginForm)
    ElMessage.success('登录成功')
    goAfterLogin(user)
  } catch (error) {
    if (error instanceof Error) ElMessage.error(error.message)
  } finally {
    submitting.value = false
  }
}

const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
    submitting.value = true
    const user = await userStore.register(registerForm)
    ElMessage.success('注册成功')
    goAfterLogin(user)
  } catch (error) {
    if (error instanceof Error) ElMessage.error(error.message)
  } finally {
    submitting.value = false
  }
}
</script>
