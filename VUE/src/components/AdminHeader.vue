<template>
  <header class="admin-header">
    <div class="brand">
      <span class="badge">ADMIN</span>
      <div>
        <h1>旅行记忆管理后台</h1>
        <p>当前管理员：{{ userStore.user?.username || '未登录' }}</p>
      </div>
    </div>

    <div class="actions">
      <el-button text @click="router.push('/dashboard')">用户端</el-button>
      <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/userStore'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出管理员登录')
  router.push('/login')
}
</script>

<style scoped>
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: linear-gradient(120deg, #153448 0%, #27445d 100%);
  color: #fff;
  box-shadow: 0 8px 20px rgba(14, 33, 48, 0.2);
}

.brand {
  display: flex;
  align-items: center;
  gap: 16px;
}

.brand h1 {
  margin: 0;
  font-size: 22px;
}

.brand p {
  margin: 4px 0 0;
  color: rgba(255, 255, 255, 0.8);
}

.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 70px;
  height: 32px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1px;
}

.actions {
  display: flex;
  gap: 12px;
}

@media (max-width: 768px) {
  .admin-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .actions {
    width: 100%;
  }
}
</style>
