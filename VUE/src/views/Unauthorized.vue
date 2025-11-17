<template>
  <div class="error-page">
    <el-result
      icon="warning"
      title="403"
      sub-title="抱歉，您没有权限访问此页面"
    >
      <template #extra>
        <el-button type="primary" @click="goBack">返回</el-button>
        <el-button @click="goHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { getUserInfo } from '@/utils/auth'

const router = useRouter()

const goBack = () => {
  router.back()
}

const goHome = () => {
  const userInfo = getUserInfo()
  if (userInfo) {
    const role = userInfo.role
    if (role === 'admin') {
      router.push('/admin/dashboard')
    } else if (role === 'coach') {
      router.push('/coach/dashboard')
    } else {
      router.push('/home')
    }
  } else {
    router.push('/login')
  }
}
</script>

<style scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}
</style>
