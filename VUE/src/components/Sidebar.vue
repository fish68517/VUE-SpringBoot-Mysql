<template>
  <aside class="sidebar" :class="{ collapsed: isCollapsed }">
    <div class="sidebar-content">
      <div class="menu-section">
        <div class="menu-title">Main</div>
        <router-link to="/dashboard" class="menu-item" active-class="active">
          <el-icon><HomeFilled /></el-icon>
          <span class="menu-label">Dashboard</span>
        </router-link>
      </div>

      <div class="menu-section">
        <div class="menu-title">Travel</div>
        <router-link to="/records" class="menu-item" active-class="active">
          <el-icon><DocumentCopy /></el-icon>
          <span class="menu-label">My Records</span>
        </router-link>
        <router-link to="/records/create" class="menu-item" active-class="active">
          <el-icon><Plus /></el-icon>
          <span class="menu-label">New Record</span>
        </router-link>
        <router-link to="/plans" class="menu-item" active-class="active">
          <el-icon><Calendar /></el-icon>
          <span class="menu-label">My Plans</span>
        </router-link>
        <router-link to="/plans/create" class="menu-item" active-class="active">
          <el-icon><Plus /></el-icon>
          <span class="menu-label">New Plan</span>
        </router-link>
      </div>

      <div class="menu-section">
        <div class="menu-title">Community</div>
        <router-link to="/social" class="menu-item" active-class="active">
          <el-icon><Share /></el-icon>
          <span class="menu-label">Social Feed</span>
        </router-link>
      </div>
    </div>

    <div class="sidebar-footer">
      <el-button
        type="text"
        @click="toggleCollapse"
        class="collapse-btn"
      >
        <el-icon><ArrowLeft v-if="!isCollapsed" /><ArrowRight v-else /></el-icon>
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
  ArrowLeft,
  ArrowRight
} from '@element-plus/icons-vue'

const isCollapsed = ref(false)

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}
</script>

<style scoped>
.sidebar {
  width: 250px;
  background: #f5f7fa;
  border-right: 1px solid #e4e7eb;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
  overflow-y: auto;
  transition: width 0.3s ease;
  position: relative;
}

.sidebar.collapsed {
  width: 80px;
}

.sidebar-content {
  flex: 1;
  padding: 20px 0;
}

.menu-section {
  margin-bottom: 20px;
}

.menu-title {
  padding: 0 20px;
  font-size: 12px;
  font-weight: bold;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 10px;
}

.sidebar.collapsed .menu-title {
  display: none;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: #606266;
  text-decoration: none;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.menu-item:hover {
  background-color: #e8f4f8;
  color: #667eea;
}

.menu-item.active {
  background-color: #e8f4f8;
  color: #667eea;
  border-left-color: #667eea;
  font-weight: bold;
}

.menu-label {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar.collapsed .menu-label {
  display: none;
}

.sidebar-footer {
  padding: 15px 20px;
  border-top: 1px solid #e4e7eb;
  display: flex;
  justify-content: center;
}

.collapse-btn {
  color: #909399;
  padding: 0;
}

.collapse-btn:hover {
  color: #667eea;
}

/* Responsive design */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 60px;
    height: calc(100vh - 60px);
    z-index: 99;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }

  .sidebar.open {
    transform: translateX(0);
  }

  .sidebar.collapsed {
    width: 250px;
  }
}

@media (max-width: 480px) {
  .sidebar {
    width: 200px;
  }

  .menu-item {
    padding: 10px 15px;
    gap: 10px;
  }

  .menu-title {
    padding: 0 15px;
  }
}
</style>
