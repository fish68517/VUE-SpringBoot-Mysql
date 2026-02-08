<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <span class="logo-icon">🛡️</span>
        <span class="logo-text">后台管理系统</span>
      </div>

      <nav class="sidebar-nav">
        <router-link to="/admin/dashboard" class="nav-item" :class="{ active: isActive('/admin/dashboard') }">
          <span class="icon">📊</span>
          <span>控制台</span>
        </router-link>
        
        <div class="nav-divider">管理功能</div>

        <router-link to="/admin/users" class="nav-item" :class="{ active: isActive('/admin/users') }">
          <span class="icon">👥</span>
          <span>用户管理</span>
        </router-link>

        <router-link to="/admin/schools" class="nav-item" :class="{ active: isActive('/admin/schools') }">
          <span class="icon">🏫</span>
          <span>学校管理</span>
        </router-link>

        <router-link to="/admin/announcements" class="nav-item" :class="{ active: isActive('/admin/announcements') }">
          <span class="icon">📢</span>
          <span>公告管理</span>
        </router-link>

        <router-link to="/admin/feedback" class="nav-item" :class="{ active: isActive('/admin/feedback') }">
          <span class="icon">💬</span>
          <span>反馈处理</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <router-link to="/app/dashboard" class="back-link">
          ← 返回前台
        </router-link>
      </div>
    </aside>

    <div class="main-wrapper">
      <header class="top-header">
        <div class="header-left">
          <h2 class="page-title">{{ currentPageTitle }}</h2>
        </div>
        <div class="header-right">
          <span class="admin-name">管理员: {{ user?.username }}</span>
          <button @click="handleLogout" class="btn-logout">退出登录</button>
        </div>
      </header>

      <main class="content-area">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script>
import authService from '../services/authService'

export default {
  name: 'AdminLayout',
  data() {
    return {
      user: null
    }
  },
  computed: {
    currentPageTitle() {
      return this.$route.meta.title || '管理后台'
    }
  },
  mounted() {
    this.user = authService.getUser()
  },
  methods: {
    isActive(path) {
      return this.$route.path === path
    },
    handleLogout() {
      authService.logout()
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f0f2f5;
}

/* Sidebar Styles */
.sidebar {
  width: 260px;
  background-color: #001529;
  color: white;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
  z-index: 1000;
  box-shadow: 2px 0 8px rgba(0,0,0,0.15);
}

.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #002140;
  font-size: 18px;
  font-weight: bold;
  gap: 10px;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.nav-divider {
  padding: 15px 25px 10px;
  font-size: 12px;
  color: #rgba(255,255,255,0.4);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 25px;
  color: rgba(255,255,255,0.65);
  text-decoration: none;
  transition: all 0.3s;
  font-size: 14px;
}

.nav-item:hover {
  color: white;
  background-color: rgba(255,255,255,0.08);
}

.nav-item.active {
  background-color: #1890ff;
  color: white;
}

.nav-item .icon {
  margin-right: 10px;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.back-link {
  color: rgba(255,255,255,0.5);
  text-decoration: none;
  font-size: 13px;
  transition: color 0.3s;
}

.back-link:hover {
  color: white;
}

/* Main Content Styles */
.main-wrapper {
  flex: 1;
  margin-left: 260px; /* Width of sidebar */
  display: flex;
  flex-direction: column;
  min-width: 0; /* Prevent flex overflow */
}

.top-header {
  height: 64px;
  background: white;
  box-shadow: 0 1px 4px rgba(0,21,41,0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 999;
}

.page-title {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.admin-name {
  font-size: 14px;
  color: #666;
}

.btn-logout {
  padding: 6px 16px;
  border: 1px solid #ff4d4f;
  background: white;
  color: #ff4d4f;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 13px;
}

.btn-logout:hover {
  background: #ff4d4f;
  color: white;
}

.content-area {
  padding: 24px;
  flex: 1;
  overflow-x: hidden;
}

/* Transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>