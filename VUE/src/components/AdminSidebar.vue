<template>
  <div class="admin-sidebar">
    <!-- Sidebar Header -->
    <div class="sidebar-header">
      <div class="logo" @click="goHome">
        <span class="logo-text">校园活动</span>
      </div>
      <button class="close-btn" @click="toggleSidebar" v-if="isMobile">
        ✕
      </button>
    </div>

    <!-- Navigation Menu -->
    <nav class="sidebar-nav">
      <router-link
        to="/admin/activity-audit"
        class="nav-item"
        :class="{ active: isActive('/admin/activity-audit') }"
      >
        <span class="nav-icon">📋</span>
        <span class="nav-label">活动审核</span>
      </router-link>

      <router-link
        to="/admin/fund-audit"
        class="nav-item"
        :class="{ active: isActive('/admin/fund-audit') }"
      >
        <span class="nav-icon">💳</span>
        <span class="nav-label">资金审核</span>
      </router-link>

      <router-link
        to="/admin/users"
        class="nav-item"
        :class="{ active: isActive('/admin/users') }"
      >
        <span class="nav-icon">👥</span>
        <span class="nav-label">用户管理</span>
      </router-link>

      <router-link
        to="/admin/statistics"
        class="nav-item"
        :class="{ active: isActive('/admin/statistics') }"
      >
        <span class="nav-icon">📊</span>
        <span class="nav-label">数据统计</span>
      </router-link>
    </nav>

    <!-- Sidebar Footer -->
    <div class="sidebar-footer">
      <!-- User Info -->
      <div class="user-section">
        <div class="user-info">
          <div class="user-avatar">{{ userInitial }}</div>
          <div class="user-details">
            <p class="user-name">{{ username }}</p>
            <p class="user-role">管理员</p>
          </div>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="action-buttons">
        <router-link to="/profile" class="action-btn" title="个人中心">
          <span>⚙️</span>
        </router-link>
        <button @click="handleLogout" class="action-btn logout" title="退出登录">
          <span>🚪</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../utils/http'

const router = useRouter()
const route = useRoute()

// State
const username = ref('')
const isMobile = ref(false)
const sidebarOpen = ref(true)

/**
 * Get user initial for avatar
 */
const userInitial = computed(() => {
  return username.value ? username.value.charAt(0).toUpperCase() : 'A'
})

/**
 * Get user info from localStorage
 */
const getUserInfo = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const user = JSON.parse(userInfo)
    username.value = user.username || 'Admin'
  }
}

/**
 * Check if current route is active
 */
const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

/**
 * Toggle sidebar on mobile
 */
const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value
}

/**
 * Go home
 */
const goHome = () => {
  router.push('/')
}

/**
 * Handle logout
 */
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await http.post('/auth/logout')
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '退出登录失败')
    }
  }
}

/**
 * Check if mobile
 */
const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

onMounted(() => {
  getUserInfo()
  checkMobile()
  window.addEventListener('resize', checkMobile)
})
</script>

<style scoped>
.admin-sidebar {
  width: 250px;
  height: 100vh;
  background: linear-gradient(135deg, #1890ff 0%, #1890ff 100%);
  color: white;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  overflow-y: auto;
}

/* Sidebar Header */
.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  flex: 1;
}

.logo:hover {
  opacity: 0.8;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: white;
}

.close-btn {
  display: none;
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  padding: 0;
  transition: all 0.3s ease;
}

.close-btn:hover {
  opacity: 0.8;
}

/* Navigation Menu */
.sidebar-nav {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
  font-size: 14px;
  font-weight: 500;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.active {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border-left-color: white;
}

.nav-icon {
  font-size: 18px;
  min-width: 24px;
  text-align: center;
}

.nav-label {
  flex: 1;
}

/* Sidebar Footer */
.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-section {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
  color: white;
}

.user-details {
  flex: 1;
  min-width: 0;
}

.user-name {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: white;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  margin: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  flex: 1;
  padding: 8px;
  background-color: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  color: white;
  cursor: pointer;
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background-color: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.action-btn.logout:hover {
  background-color: rgba(255, 0, 0, 0.3);
  border-color: rgba(255, 0, 0, 0.5);
}

/* Scrollbar */
.admin-sidebar::-webkit-scrollbar {
  width: 6px;
}

.admin-sidebar::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
}

.admin-sidebar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.admin-sidebar::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

/* Responsive */
@media (max-width: 768px) {
  .admin-sidebar {
    width: 100%;
    height: auto;
    position: relative;
    flex-direction: row;
    align-items: center;
    padding: 0;
  }

  .sidebar-header {
    flex: 1;
    padding: 12px 20px;
    border-bottom: none;
    border-right: 1px solid rgba(255, 255, 255, 0.2);
  }

  .close-btn {
    display: block;
  }

  .sidebar-nav {
    display: none;
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: linear-gradient(135deg, #1890ff 0%, #1890ff 100%);
    flex-direction: column;
    padding: 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  }

  .sidebar-nav.open {
    display: flex;
  }

  .nav-item {
    padding: 12px 20px;
    border-left: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }

  .nav-item.active {
    border-left: none;
    border-bottom-color: white;
  }

  .sidebar-footer {
    padding: 12px 20px;
    border-top: none;
    border-left: 1px solid rgba(255, 255, 255, 0.2);
    flex-direction: row;
    gap: 12px;
  }

  .user-section {
    flex: 1;
  }

  .action-buttons {
    gap: 4px;
  }

  .action-btn {
    padding: 6px;
    font-size: 14px;
  }
}
</style>
