<template>
  <div class="editor-layout">
    <el-container>
      <el-aside width="200px" class="sidebar">
        <div class="logo">审稿编辑</div>
        <el-menu :default-active="activeMenu" router>
          <el-menu-item index="/editor/dashboard">
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/editor/profile">
            <span>个人信息</span>
          </el-menu-item>
          <el-menu-item index="/editor/initial-review">
            <span>稿件初审</span>
          </el-menu-item>
          <el-menu-item index="/editor/assign-reviewer">
            <span>分配审稿人</span>
          </el-menu-item>
          <el-menu-item index="/editor/review-progress">
            <span>审稿进度跟踪</span>
          </el-menu-item>
          <el-menu-item index="/editor/edit-manuscript">
            <span>稿件编辑</span>
          </el-menu-item>
          <el-menu-item index="/editor/review">
            <span>稿件管理</span>
          </el-menu-item>
          <el-menu-item index="/editor/communication">
            <span>沟通管理</span>
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
.editor-layout {
  height: 100vh;
}

/* 1. 新增：让 Element Plus 的容器占满父元素的高度 */
.el-container {
  height: 100%;
}

.sidebar {
  background-color: #0f78e0; /* 保持你的蓝色背景 */
  color: #fff;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2); /* 调整边框颜色使其在蓝色背景上更融洽 */
}

.el-menu {
  flex: 1;
  border-right: none;
  /* 2. 修改：将深灰色改为透明，或者直接改成与 sidebar 一致的 #0f78e0 */
  background-color: transparent; 
}

/* 确保菜单文字颜色在自定义背景下可见 */
:deep(.el-menu-item) {
  color: #fff;
}
:deep(.el-menu-item:hover),
:deep(.el-menu-item.is-active) {
  background-color: rgba(255, 255, 255, 0.1); /* 添加一个半透明的悬浮/选中效果 */
}

.logout-btn {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
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