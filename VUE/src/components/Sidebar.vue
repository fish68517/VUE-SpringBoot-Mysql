<template>
  <aside class="sidebar">
    <div class="logo">
      <h1>壮族刺绣</h1>
    </div>
    <nav class="nav-menu">
      <div class="nav-section" v-if="!authStore.isAdmin">
        <h3 class="section-title">前台</h3>
        <router-link to="/home" class="nav-item" :class="{ active: isActive('/home') }">
          <span class="icon">🏠</span>
          <span>首页</span>
        </router-link>
        <router-link to="/artworks" class="nav-item" :class="{ active: isActive('/artworks') }">
          <span class="icon">🎨</span>
          <span>作品展示</span>
        </router-link>
        <router-link to="/knowledge" class="nav-item" :class="{ active: isActive('/knowledge') }">
          <span class="icon">📚</span>
          <span>知识科普</span>
        </router-link>
        <router-link to="/community" class="nav-item" :class="{ active: isActive('/community') }">
          <span class="icon">💬</span>
          <span>互动交流</span>
        </router-link>
        <router-link to="/user" class="nav-item" :class="{ active: isActive('/user') }">
          <span class="icon">👤</span>
          <span>用户中心</span>
        </router-link>
      </div>

      <!-- 仅管理员可见 -->
      <div class="nav-section" v-if="authStore.isAdmin">
        <h3 class="section-title">后台管理</h3>
        <router-link to="/admin/users" class="nav-item" :class="{ active: isActive('/admin/users') }">
          <span class="icon">👥</span>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/resources" class="nav-item" :class="{ active: isActive('/admin/resources') }">
          <span class="icon">📦</span>
          <span>资源管理</span>
        </router-link>
        <router-link to="/admin/interactions" class="nav-item" :class="{ active: isActive('/admin/interactions') }">
          <span class="icon">🔄</span>
          <span>互动管理</span>
        </router-link>
        <router-link to="/admin/system" class="nav-item" :class="{ active: isActive('/admin/system') }">
          <span class="icon">⚙️</span>
          <span>系统管理</span>
        </router-link>
      </div>

      <!-- 用户认证状态 -->
      <div class="nav-section user-section">
        <div v-if="authStore.isLoggedIn" class="user-info">
          <div class="user-name">{{ authStore.user?.username || '用户' }}</div>
          <button @click="handleLogout" class="logout-btn">退出登录</button>
        </div>
        <div v-else class="auth-links">
          <router-link to="/login" class="auth-link login-link">登录</router-link>
          <router-link to="/register" class="auth-link register-link">注册</router-link>
        </div>
      </div>
    </nav>
  </aside>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isActive = (path) => {
  return route.path.startsWith(path)
}

const handleLogout = () => {
  authStore.logout()
  router.push({ name: 'Login' })
}
</script>

<style scoped>
.sidebar {
  width: 250px;
  background: linear-gradient(135deg, #0066cc 0%, #004499 100%);
  color: white;
  padding: var(--spacing-lg);
  overflow-y: auto;
  box-shadow: var(--shadow-lg);
  display: flex;
  flex-direction: column;
}

.logo {
  margin-bottom: var(--spacing-xl);
  text-align: center;
  border-bottom: 2px solid rgba(255, 255, 255, 0.2);
  padding-bottom: var(--spacing-lg);
}

.logo h1 {
  font-size: var(--font-size-2xl);
  font-weight: 700;
  letter-spacing: 2px;
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  flex: 1;
}

.nav-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.section-title {
  font-size: var(--font-size-sm);
  font-weight: 600;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: var(--spacing-sm);
  letter-spacing: 1px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  color: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  text-decoration: none;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.active {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  font-weight: 600;
}

.icon {
  font-size: var(--font-size-lg);
  min-width: 24px;
}

/* 用户认证部分 */
.user-section {
  margin-top: auto;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding-top: var(--spacing-lg);
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.user-name {
  font-size: var(--font-size-sm);
  font-weight: 600;
  padding: var(--spacing-md);
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: var(--border-radius-md);
  text-align: center;
}

.logout-btn {
  padding: var(--spacing-md);
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  font-size: var(--font-size-sm);
  font-weight: 600;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background-color: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.auth-links {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.auth-link {
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  text-align: center;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}

.login-link {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-link:hover {
  background-color: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.register-link {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.register-link:hover {
  background-color: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
}

/* 滚动条样式 */
.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
}

.sidebar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

@media (max-width: 768px) {
  .sidebar {
    width: 200px;
    padding: var(--spacing-md);
  }

  .logo h1 {
    font-size: var(--font-size-xl);
  }

  .nav-item {
    padding: var(--spacing-sm);
    gap: var(--spacing-sm);
  }

  .icon {
    font-size: var(--font-size-base);
  }
}
</style>
