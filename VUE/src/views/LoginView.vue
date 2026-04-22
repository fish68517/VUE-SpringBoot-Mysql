<template>
  <div class="auth-shell">
    <section class="auth-panel page-card">
      <div class="intro">
        <div class="eyebrow">POWER INSPECTION</div>
        <h1>电网设备巡检管理系统</h1>
        <p>
          系统支持管理员、巡检员、维护员三类账户，覆盖设备台账、巡检标准、巡检任务、
          异常缺陷、维修工单、统计分析和公告管理等完整业务流程。
        </p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" class="form-box" @keyup.enter="onSubmit">
        <div class="form-title">账号登录</div>
        <el-form-item prop="username">
          <el-input v-model="form.username" size="large" placeholder="请输入用户名">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" size="large" type="password" show-password placeholder="请输入密码">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" class="submit-btn" @click="onSubmit">登录系统</el-button>
        <div class="switch-link">
          还没有账号？
          <router-link to="/register">立即注册</router-link>
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
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function onSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await http.post('/api/auth/login', form)
    authStore.setAuth(data)
    ElMessage.success('登录成功')
    router.push('/dashboard')
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
  grid-template-columns: 1.15fr 0.85fr;
  overflow: hidden;
}

.intro {
  padding: 60px;
  background:
    linear-gradient(135deg, rgba(5, 51, 100, 0.96), rgba(14, 92, 163, 0.9)),
    linear-gradient(180deg, #0d5bd1, #0b2345);
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
  font-size: 46px;
  line-height: 1.16;
}

.intro p {
  margin: 0;
  font-size: 18px;
  line-height: 1.95;
  color: rgba(255, 255, 255, 0.86);
}

.form-box {
  padding: 76px 60px;
}

.form-title {
  margin-bottom: 30px;
  font-size: 32px;
  font-weight: 800;
  color: #17324f;
}

.submit-btn {
  width: 100%;
  margin-top: 12px;
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
