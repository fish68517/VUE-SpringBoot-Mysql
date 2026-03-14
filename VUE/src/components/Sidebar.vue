<template>
  <div class="sidebar">
    <div class="sidebar-header">
      <h2 class="sidebar-title">管理系统</h2>
    </div>

    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      @select="handleMenuSelect"
    >
      <el-menu-item index="/dashboard">
        <el-icon><HomeFilled /></el-icon>
        <span>仪表板</span>
      </el-menu-item>

      <el-menu-item index="/users">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>

      <el-menu-item index="/community">
        <el-icon><ChatDotSquare /></el-icon>
        <span>社区管理</span>
      </el-menu-item>

      <el-menu-item index="/fitness-plans">
        <el-icon><Basketball /></el-icon>
        <span>健身计划</span>
      </el-menu-item>

      <el-menu-item index="/exercise-data">
        <el-icon><DataAnalysis /></el-icon>
        <span>运动数据</span>
      </el-menu-item>

      <el-menu-item index="/image-upload">
        <el-icon><Picture /></el-icon>
        <span>图片上传</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  HomeFilled,
  User,
  ChatDotSquare,
  Basketball,
  DataAnalysis,
  Picture
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => {
  return route.path
})

const handleMenuSelect = (index) => {
  router.push(index)
}
</script>

<style scoped>
.sidebar {
  width: 250px;
  height: 100vh;
  background: #fff;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.sidebar-title {
  font-size: 20px;
  font-weight: 700;
  color: white;
  margin: 0;
  letter-spacing: -0.5px;
}

.sidebar-menu {
  flex: 1;
  border: none;
  padding: 10px 0;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  padding: 0 20px;
  color: #666;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: #f5f7fa;
  color: #333;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #e6f7ff;
  color: #1890ff;
  border-left-color: #1890ff;
  font-weight: 600;
}

.sidebar-menu :deep(.el-icon) {
  margin-right: 10px;
  font-size: 16px;
}

/* Scrollbar styling */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #999;
}
</style>
