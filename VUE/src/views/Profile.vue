<template>
  <div class="profile-container">
    <Card title="个人信息">
      <el-form
        ref="formRef"
        :model="profile"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="profile.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profile.email" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="profile.phone" />
        </el-form-item>
        <el-form-item label="地区" prop="region">
          <el-input v-model="profile.region" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-input v-model="profile.userType" disabled />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="loading">
            更新信息
          </el-button>
          <el-button @click="handleLogout">退出登录</el-button>
        </el-form-item>
      </el-form>
    </Card>

    <Card title="修改密码" v-if="false">
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请确认新密码"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword">
            修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </Card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { userAPI } from '@/api/user'
import Card from '@/components/common/Card.vue'
import { lo } from 'element-plus/es/locale/index.mjs'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const passwordFormRef = ref(null)
const loading = ref(false)

const profile = ref({
  username: '',
  email: '',
  phone: '',
  region: '',
  userType: '',
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const validatePassword = (rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' },
  ],
}

const handleUpdate = async () => {
  try {
      // 从 localStorage 获取当前登录的用户ID
  const userStr = localStorage.getItem('user')
  const userId = userStr ? JSON.parse(userStr).userId : null

    await formRef.value.validate()
    loading.value = true

    await userAPI.updateProfile(userId, profile.value)
    ElMessage.success('更新成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '更新失败')
  } finally {
    loading.value = false
  }
}

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    ElMessage.info('密码修改功能开发中')
  } catch (error) {
    ElMessage.error('请填写完整信息')
  }
}

const handleLogout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(() => {
  if (authStore.user) {
    profile.value = {
      username: authStore.user.username || '',
      email: authStore.user.email || '',
      phone: authStore.user.phone || '',
      region: authStore.user.region || '',
      userType: authStore.user.userType || '',
    }
  }
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 600px;
}
</style>
