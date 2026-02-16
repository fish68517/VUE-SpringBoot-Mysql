<template>
  <div class="top-navigation">
    <div class="nav-container">
      <!-- Logo -->
      <div class="logo" @click="goHome">
        <span class="logo-text">校园活动</span>
      </div>

      <!-- Menu Items -->
      <div class="menu-items">
        <router-link
          to="/"
          class="menu-item"
          :class="{ active: isActive('/') }"
        >
          首页
        </router-link>

        <router-link
          to="/activities"
          class="menu-item"
          :class="{ active: isActive('/activities') }"
        >
          浏览活动
        </router-link>

        <router-link
          to="/my-activities"
          class="menu-item"
          :class="{ active: isActive('/my-activities') }"
        >
          我的活动
        </router-link>

        <router-link
          to="/my-crowdfunding"
          class="menu-item"
          :class="{ active: isActive('/my-crowdfunding') }"
        >
          我的众筹
        </router-link>

        <router-link
          v-if="userRole === 'ORGANIZER'"
          to="/activities/create"
          class="menu-item"
          :class="{ active: isActive('/activities/create') }"
        >
          创建活动
        </router-link>

        <router-link
          v-if="userRole === 'ORGANIZER'"
          to="/organizer/feedback-summary"
          class="menu-item"
          :class="{ active: isActive('/organizer/feedback-summary') }"
        >
          反馈汇总
        </router-link>

        <router-link
          v-if="userRole === 'ORGANIZER'"
          to="/organizer/statistics"
          class="menu-item"
          :class="{ active: isActive('/organizer/statistics') }"
        >
          数据统计
        </router-link>

        <router-link
          v-if="userRole === 'ADMIN'"
          to="/admin/statistics"
          class="menu-item"
          :class="{ active: isActive('/admin/statistics') }"
        >
          数据统计
        </router-link>
      </div>

      <!-- User Menu -->
      <div class="user-menu">
        <div class="user-info">
          <span class="username">{{ username }}</span>
          <span class="role-badge" :class="getRoleClass()">{{ getRoleLabel() }}</span>
        </div>

        <div class="dropdown-menu">
          <button @click="toggleDropdown" class="dropdown-toggle">
            ⋮
          </button>

          <div v-if="showDropdown" class="dropdown-content">
            <router-link to="/profile" class="dropdown-item">
              个人中心
            </router-link>
            <button @click="handleLogout" class="dropdown-item logout">
              退出登录
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../utils/http'

const router = useRouter()
const route = useRoute()

// State
const showDropdown = ref(false)
const username = ref('')
const userRole = ref('')

/**
 * Get user info from localStorage
 */
const getUserInfo = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const user = JSON.parse(userInfo)
    username.value = user.username || 'User'
    userRole.value = user.role || 'USER'
  }
}

/**
 * Check if current route is active
 */
const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

/**
 * Get role label
 */
const getRoleLabel = () => {
  const roleMap = {
    USER: '普通用户',
    ORGANIZER: '组织者',
    ADMIN: '管理员'
  }
  return roleMap[userRole.value] || '用户'
}

/**
 * Get role class
 */
const getRoleClass = () => {
  const classMap = {
    USER: 'role-user',
    ORGANIZER: 'role-organizer',
    ADMIN: 'role-admin'
  }
  return classMap[userRole.value] || 'role-user'
}

/**
 * Toggle dropdown menu
 */
const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
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
    await http.post('/auth/logout')
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '退出登录失败')
  }
}

onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.top-navigation {
  background-color: white;
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 40px;
  transition: all 0.3s ease;
}

.logo:hover {
  opacity: 0.8;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-color);
}

/* Menu Items */
.menu-items {
  display: flex;
  gap: 30px;
  flex: 1;
}

.menu-item {
  color: var(--text-primary);
  text-decoration: none;
  font-weight: 500;
  font-size: 14px;
  padding: 8px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.menu-item:hover {
  color: var(--primary-color);
}

.menu-item.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}

/* User Menu */
.user-menu {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-left: 40px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username {
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.role-badge {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.role-user {
  background-color: #1890ff;
}

.role-organizer {
  background-color: #52c41a;
}

.role-admin {
  background-color: #f5222d;
}

/* Dropdown Menu */
.dropdown-menu {
  position: relative;
}

.dropdown-toggle {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: var(--text-primary);
  padding: 4px 8px;
  transition: all 0.3s ease;
}

.dropdown-toggle:hover {
  color: var(--primary-color);
}

.dropdown-content {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  min-width: 150px;
  margin-top: 8px;
  z-index: 1000;
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: 10px 16px;
  text-align: left;
  background: none;
  border: none;
  color: var(--text-primary);
  text-decoration: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
  color: var(--primary-color);
}

.dropdown-item.logout {
  border-top: 1px solid var(--border-color);
  color: #f5222d;
}

.dropdown-item.logout:hover {
  background-color: #fff1f0;
}

/* Responsive */
@media (max-width: 768px) {
  .nav-container {
    flex-wrap: wrap;
    height: auto;
    padding: 10px 20px;
  }

  .logo {
    margin-right: 20px;
  }

  .menu-items {
    gap: 15px;
    order: 3;
    width: 100%;
    margin-top: 10px;
  }

  .user-menu {
    margin-left: 0;
  }

  .menu-item {
    font-size: 13px;
  }
}
</style>
