<template>
  <div class="author-layout">
    <!-- Top Navigation Bar -->
    <el-header class="header">
      <div class="header-left">
        <h2>在线投稿系统</h2>
      </div>
      <div class="header-center">
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          class="nav-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/author/dashboard">首页</el-menu-item>
          <el-menu-item index="/author/manuscripts">我的稿件</el-menu-item>
          <el-menu-item index="/author/messages">消息中心</el-menu-item>
        </el-menu>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            {{ userStore.user?.username }}
            <el-icon class="el-icon--right">
              <arrow-down />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <!-- Content Area -->
    <el-container>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../stores/userStore'
import { authService } from '../../services/authService'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => {
  // Get the current route path
  const path = route.path
  if (path.includes('/author/dashboard')) return '/author/dashboard'
  if (path.includes('/author/manuscripts')) return '/author/manuscripts'
  if (path.includes('/author/messages')) return '/author/messages'
  return '/author/dashboard'
})

const handleMenuSelect = (index) => {
  router.push(index)
}

const handleCommand = async (command) => {
  if (command === 'profile') {
    router.push('/author/profile')
  } else if (command === 'logout') {
    try {
      await authService.logout()
      userStore.clearUser()
      ElMessage.success('退出登录成功')
      router.push('/login')
    } catch (error) {
      ElMessage.error('退出登录失败')
    }
  }
}
</script>

<style scoped>
.author-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  gap: 20px;
}

.header-left {
  flex-shrink: 0;
}

.header-left h2 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu {
  border: none;
  background-color: transparent;
}

.header-right {
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
  display: flex;
  align-items: center;
  gap: 5px;
}

.el-container {
  flex: 1;
  overflow: hidden;
}

.main-content {
  overflow-y: auto;
  background-color: #f5f7fa;
}

:deep(.el-menu--horizontal) {
  height: 60px;
  line-height: 60px;
}

:deep(.el-menu-item) {
  height: 60px;
  line-height: 60px;
}
</style>
