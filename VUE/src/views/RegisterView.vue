<template>
  <div class="auth-shell">
    <section class="auth-panel page-card">
      <div class="intro">
        <div class="eyebrow">ACCOUNT REGISTER</div>
        <h1>注册系统账户</h1>
        <p>
          你可以注册管理员、巡检员、维护员三类账户。注册成功后可直接返回登录页进入系统。
        </p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" class="form-box">
        <div class="form-title">新用户注册</div>
        <el-form-item prop="username"><el-input v-model="form.username" size="large" placeholder="请输入用户名" /></el-form-item>
        <el-form-item prop="password"><el-input v-model="form.password" size="large" type="password" show-password placeholder="请输入密码" /></el-form-item>
        <el-form-item prop="realName"><el-input v-model="form.realName" size="large" placeholder="请输入姓名" /></el-form-item>
        <el-form-item prop="role">
          <el-select v-model="form.role" size="large" style="width: 100%" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="巡检员" value="INSPECTOR" />
            <el-option label="维护员" value="MAINTAINER" />
          </el-select>
        </el-form-item>
        <el-form-item prop="phone"><el-input v-model="form.phone" size="large" placeholder="请输入手机号" /></el-form-item>
        <el-form-item prop="email"><el-input v-model="form.email" size="large" placeholder="请输入邮箱" /></el-form-item>
        <el-button type="primary" size="large" :loading="loading" class="submit-btn" @click="onSubmit">立即注册</el-button>
        <div class="switch-link">
          已有账号？
          <router-link to="/login">返回登录</router-link>
        </div>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  realName: '',
  role: 'INSPECTOR',
  phone: '',
  email: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

async function onSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await http.post('/api/auth/register', form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-shell {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 40px;
  background:
    radial-gradient(circle at 20% 20%, rgba(54, 158, 255, 0.18), transparent 25%),
    radial-gradient(circle at 80% 80%, rgba(21, 199, 150, 0.18), transparent 25%);
}

.auth-panel {
  width: 1120px;
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  overflow: hidden;
}

.intro {
  padding: 60px;
  background:
    linear-gradient(135deg, rgba(6, 45, 78, 0.96), rgba(18, 110, 122, 0.9)),
    linear-gradient(180deg, #0a6179, #0a2845);
  color: #fff;
}

.eyebrow {
  display: inline-block;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  font-size: 13px;
  letter-spacing: 1px;
}

.intro h1 {
  margin: 26px 0 18px;
  font-size: 44px;
  line-height: 1.16;
}

.intro p {
  margin: 0;
  font-size: 18px;
  line-height: 1.95;
  color: rgba(255, 255, 255, 0.86);
}

.form-box {
  padding: 56px 60px;
}

.form-title {
  margin-bottom: 30px;
  font-size: 32px;
  font-weight: 800;
  color: #17324f;
}

.submit-btn {
  width: 100%;
  margin-top: 10px;
}

.switch-link {
  margin-top: 18px;
  text-align: right;
  font-size: 16px;
  color: #60788f;
}

.switch-link a {
  color: #1764d7;
  font-weight: 700;
}
</style>
