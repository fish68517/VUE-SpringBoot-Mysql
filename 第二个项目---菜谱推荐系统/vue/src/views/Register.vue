<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2>注册</h2>
      <el-form :model="registerForm" @submit.prevent="handleRegister">
        <el-form-item>
          <el-input v-model="registerForm.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.password" type="password" placeholder="密码" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" block>注册</el-button>
        </el-form-item>
        <el-link @click="$router.push('/login')">已有账号？立即登录</el-link>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {userApi} from "@/api/networkApi";

const router = useRouter()

const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = async () => {
  try {
    if (registerForm.value.password !== registerForm.value.confirmPassword) {
      ElMessage.error('两次输入的密码不一致')
      return
    }

    const response = await userApi.createUser({
      ...registerForm.value,
      role: "user" // 将用户类型传递给后端
    })

    if (response.status === 200) {
      // 注册成功后，跳转到登录页面
      // 这里模拟注册接口
      // 实际项目中需要调用真实的后端API
      ElMessage.success('注册成功')
      router.push('/login')
      return;
    }
    
    // 这里模拟注册接口
    // 实际项目中需要调用真实的后端API
    ElMessage.success('注册成功')
    router.push('/login')
  } catch (error) {
    ElMessage.error('注册失败')
  }
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.register-card {
  width: 400px;
}
</style> 