<template>
  <aside class="sidebar" :class="{ collapsed: isCollapsed }">
    <div class="sidebar-content">

      <!-- 主菜单
      <div class="menu-section">
        <div class="menu-title">主菜单</div>
        <router-link to="/dashboard" class="menu-item" active-class="active">
          <el-icon><HomeFilled /></el-icon>
          <span class="menu-label">首页仪表盘</span>
        </router-link>
      </div>

      <!-- 旅行管理 -->
      <div class="menu-section">
        <div class="menu-title">旅行管理</div>
        <router-link to="/records" class="menu-item" active-class="active">
          <el-icon><DocumentCopy /></el-icon>
          <span class="menu-label">我的旅行记录</span>
        </router-link>
        <router-link to="/records/create" class="menu-item" active-class="active">
          <el-icon><Plus /></el-icon>
          <span class="menu-label">新建旅行记录</span>
        </router-link>
        <router-link to="/plans" class="menu-item" active-class="active">
          <el-icon><Calendar /></el-icon>
          <span class="menu-label">我的旅行计划</span>
        </router-link>
        <router-link to="/plans/create" class="menu-item" active-class="active">
          <el-icon><Plus /></el-icon>
          <span class="menu-label">新建旅行计划</span>
        </router-link>
      </div>

      <!-- 社区互动 -->
      <div class="menu-section">
        <div class="menu-title">社区互动</div>
        <router-link to="/social" class="menu-item" active-class="active">
          <el-icon><Share /></el-icon>
          <span class="menu-label">社区动态</span>
        </router-link>
      </div>

    </div>

    <!-- 展开/收起按钮 -->
    <div class="sidebar-footer">
      <el-button type="text" @click="toggleCollapse" class="collapse-btn" title="展开/收起菜单">
        <el-icon v-if="!isCollapsed"><ArrowLeftBold /></el-icon>
        <el-icon v-else><ArrowRightBold /></el-icon>
      </el-button>
    </div>
  </aside>
</template>

<script setup>
import { ref } from 'vue'
import {
  HomeFilled,
  DocumentCopy,
  Calendar,
  Share,
  Plus,
  ArrowLeftBold,
  ArrowRightBold
} from '@element-plus/icons-vue'

const isCollapsed = ref(false)

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}
</script>

<style scoped>
.sidebar {
  width: 260px;
  background: #ffffff;
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 64px); /* 与顶部 Header 高度保持一致 */
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 10;
}

.sidebar.collapsed {
  width: 72px;
}

.sidebar-content {
  flex: 1;
  padding: 16px 0;
  overflow-y: auto;
}

.menu-section {
  margin-bottom: 24px;
}

.menu-title {
  padding: 0 24px;
  font-size: 12px;
  font-weight: bold;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 8px;
  user-select: none;
}

.sidebar.collapsed .menu-title {
  display: none;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 24px;
  color: #606266;
  text-decoration: none;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
  border-radius: 0 20px 20px 0;
  margin: 2px 12px;
}

.menu-item:hover {
  background: #f0f9ff;
  color: #409eff;
}

.menu-item.active {
  background: linear-gradient(to right, #e6f7ff, #f0f9ff);
  color: #409eff;
  border-left-color: #409eff;
  font-weight: 600;
}

.menu-label {
  white-space: nowrap;
  font-size: 14px;
  transition: opacity 0.2s;
}

.sidebar.collapsed .menu-label {
  opacity: 0;
  width: 0;
  overflow: hidden;
}

.sidebar-footer {
  padding: 12px 0;
  border-top: 1px solid #ebeef5;
  text-align: center;
}

.collapse-btn {
  color: #909399;
  font-size: 18px;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s;
}

.collapse-btn:hover {
  color: #409eff;
  background: #f0f9ff;
  transform: scale(1.1);
}

/* 移动端处理（可配合父组件控制显示） */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 64px;
    height: calc(100vh - 64px);
    z-index: 999;
    box-shadow: 4px 0 16px rgba(0, 0, 0, 0.12);
  }
}
</style>