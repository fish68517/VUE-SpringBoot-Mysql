<template>
  <div class="register-container">
    <div class="register-box">
      <h1>注册账号</h1>
      <el-form :model="form" @submit.prevent="handleRegister">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="loading" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" type="email" placeholder="请输入邮箱" :disabled="loading" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :disabled="loading" />
        </el-form-item>
        <el-form-item label="身份类型">
          <el-select v-model="form.role" placeholder="请选择身份类型" :disabled="loading">
            <el-option label="学生" value="STUDENT" />
            <el-option label="带教老师" value="TEACHER" />
            <el-option label="学校管理员" value="SCHOOL_ADMIN" />
            <el-option label="医院管理员" value="HOSPITAL_ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属组织">
          <el-select v-model="form.organizationId" placeholder="请选择所属组织" :disabled="loading || organizations.length === 0" @change="onOrganizationChange">
            <el-option v-for="org in organizations" :key="org.id" :label="org.name" :value="org.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" style="width: 100%" :loading="loading">注册</el-button>
        </el-form-item>
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as userApi from '@/api/user'

const router = useRouter()
const loading = ref(false)
const organizations = ref([])

const form = reactive({
  username: '',
  email: '',
  password: '',
  role: '',
  organizationId: '',
})

// 加载组织列表
const loadOrganizations = async () => {
  try {
    const response = await userApi.getOrganizations()
    organizations.value = response.data || []
  } catch (error) {
    ElMessage.error('加载组织列表失败')
  }
}

const onOrganizationChange = () => {
  // 组织选择变化时的处理
}

const handleRegister = async () => {
  if (!form.username || !form.email || !form.password || !form.role || !form.organizationId) {
    ElMessage.error('请填写所有必填项')
    return
  }

  loading.value = true
  try {
    await userApi.register({
      username: form.username,
      email: form.email,
      password: form.password,
      role: form.role,
      organizationId: form.organizationId,
    })
    
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadOrganizations()
})
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
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.register-box h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.login-link {
  text-align: center;
  margin-top: 15px;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
