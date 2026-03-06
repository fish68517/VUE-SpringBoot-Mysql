<template>
  <div class="reviewer-layout">
    <el-container>
      <el-aside width="200px" class="sidebar">
        <div class="logo">审稿系统</div>
        <el-menu :default-active="activeMenu" router>
          <el-menu-item index="/reviewer/dashboard">
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/reviewer/profile">
            <span>个人信息</span>
          </el-menu-item>
          <el-menu-item index="/reviewer/tasks">
            <span>审稿任务</span>
          </el-menu-item>
          <el-menu-item index="/reviewer/history">
            <span>历史记录</span>
          </el-menu-item>
        </el-menu>
        <div class="logout-btn">
          <el-button @click="handleLogout" type="danger" style="width: 100%">退出登录</el-button>
        </div>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="header-info">
            欢迎，{{ userStore.user?.username }}
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../stores/userStore'
import { authService } from '../../services/authService'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const activeMenu = ref(route.path)

const handleLogout = async () => {
  try {
    await authService.logout()
    userStore.clearUser()
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    ElMessage.error('退出登录失败')
  }
}
</script>

<style scoped>
.reviewer-layout {
  height: 100vh;
}

.sidebar {
  background-color: #545c64;
  color: #fff;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #444;
}

.el-menu {
  flex: 1;
  border-right: none;
  background-color: #545c64;
}

.logout-btn {
  padding: 20px;
  border-top: 1px solid #444;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.header-info {
  color: #333;
  font-size: 14px;
}
</style>
