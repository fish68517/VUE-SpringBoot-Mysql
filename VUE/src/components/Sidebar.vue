<template>
  <aside class="sidebar">
    <div class="sidebar-shell">
      <div class="brand-card">
        <span class="brand-badge">{{ copy.brandBadge }}</span>
        <h1>{{ copy.brandTitle }}</h1>
        <p>{{ authStore.isAdmin ? copy.adminSubtitle : copy.userSubtitle }}</p>
      </div>

      <nav class="nav-menu">
        <div class="nav-section" v-if="!authStore.isAdmin">
          <h3 class="section-title">{{ copy.frontendTitle }}</h3>

          <router-link
            v-for="item in frontendItems"
            :key="item.to"
            :to="item.to"
            class="nav-item"
            :class="{ active: isActive(item.to) }"
          >
            <span class="icon-wrap">{{ item.icon }}</span>
            <span class="nav-text">{{ item.label }}</span>
          </router-link>
        </div>

        <div class="nav-section" v-if="authStore.isAdmin">
          <h3 class="section-title">{{ copy.adminTitle }}</h3>

          <router-link
            v-for="item in adminItems"
            :key="item.to"
            :to="item.to"
            class="nav-item"
            :class="{ active: isActive(item.to) }"
          >
            <span class="icon-wrap">{{ item.icon }}</span>
            <span class="nav-text">{{ item.label }}</span>
          </router-link>
        </div>
      </nav>

      <div class="account-panel">
        <div v-if="authStore.isLoggedIn" class="user-card">
          <div class="user-card-top">
            <div class="user-avatar">{{ userInitial }}</div>
            <div class="user-meta">
              <strong>{{ authStore.user?.username || copy.defaultUserName }}</strong>
              <span>{{ authStore.isAdmin ? copy.adminIdentity : copy.userIdentity }}</span>
            </div>
          </div>

          <button @click="handleLogout" class="logout-btn">{{ copy.logout }}</button>
        </div>

        <div v-else class="auth-card">
          <p>{{ copy.authPrompt }}</p>
          <div class="auth-actions">
            <router-link to="/login" class="auth-link login-link">{{ copy.login }}</router-link>
            <router-link to="/register" class="auth-link register-link">{{ copy.register }}</router-link>
          </div>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const copy = {
  brandBadge: '\u6587\u5316\u5e73\u53f0',
  brandTitle: '\u6587\u5c71\u58ee\u65cf\u523a\u7ee3',
  userSubtitle: '\u6d4f\u89c8\u4f5c\u54c1\u3001\u9605\u8bfb\u77e5\u8bc6\u3001\u53c2\u4e0e\u793e\u533a\u4ea4\u6d41',
  adminSubtitle: '\u5185\u5bb9\u5ba1\u6838\u3001\u8d44\u6e90\u7ba1\u7406\u4e0e\u7cfb\u7edf\u7ef4\u62a4',
  frontendTitle: '\u524d\u53f0\u5bfc\u822a',
  adminTitle: '\u540e\u53f0\u7ba1\u7406',
  defaultUserName: '\u7528\u6237',
  adminIdentity: '\u7ba1\u7406\u5458',
  userIdentity: '\u5df2\u767b\u5f55\u7528\u6237',
  logout: '\u9000\u51fa\u767b\u5f55',
  authPrompt: '\u767b\u5f55\u540e\u53ef\u6536\u85cf\u4f5c\u54c1\u3001\u53d1\u5e03\u8bc4\u8bba\u548c\u7ba1\u7406\u4e2a\u4eba\u4e2d\u5fc3\u3002',
  login: '\u767b\u5f55',
  register: '\u6ce8\u518c',
}

const frontendItems = [
  { to: '/home', icon: 'H', label: '\u9996\u9875' },
  { to: '/artworks', icon: 'A', label: '\u523a\u7ee3\u5c55\u793a' },
  { to: '/knowledge', icon: 'K', label: '\u77e5\u8bc6\u79d1\u666e' },
  { to: '/community', icon: 'C', label: '\u4e92\u52a8\u4ea4\u6d41' },
  { to: '/user', icon: 'U', label: '\u7528\u6237\u4e2d\u5fc3' },
]

const adminItems = [
  { to: '/admin/users', icon: 'U', label: '\u7528\u6237\u7ba1\u7406' },
  { to: '/admin/resources', icon: 'R', label: '\u8d44\u6e90\u7ba1\u7406' },
  { to: '/admin/interactions', icon: 'I', label: '\u4e92\u52a8\u7ba1\u7406' },
  { to: '/admin/system', icon: 'S', label: '\u7cfb\u7edf\u7ba1\u7406' },
]

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const userInitial = computed(() => {
  const value = authStore.user?.username || copy.defaultUserName
  return String(value).trim().slice(0, 1).toUpperCase()
})

const isActive = (path) => route.path.startsWith(path)

const handleLogout = () => {
  authStore.logout()
  router.push({ name: 'Login' })
}
</script>

<style scoped>
.sidebar {
  width: 288px;
  padding: 18px;
  background:
    radial-gradient(circle at top, rgba(255, 255, 255, 0.14), transparent 22%),
    linear-gradient(180deg, #0d5eb7 0%, #0b4a8c 54%, #0a3159 100%);
  color: #ffffff;
  box-shadow: 16px 0 38px rgba(7, 34, 68, 0.24);
  overflow-y: auto;
}

.sidebar-shell {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  gap: 22px;
}

.brand-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 22px 20px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.14);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.08);
}

.brand-badge {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.86);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.06em;
}

.brand-card h1 {
  margin: 0;
  font-size: 28px;
  line-height: 1.25;
  font-weight: 800;
}

.brand-card p {
  margin: 0;
  color: rgba(255, 255, 255, 0.78);
  font-size: 14px;
  line-height: 1.75;
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 18px;
  flex: 1;
}

.nav-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 18px 14px 14px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.section-title {
  margin: 0 0 4px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 18px;
  color: rgba(255, 255, 255, 0.88);
  text-decoration: none;
  transition: transform 0.25s ease, background-color 0.25s ease, box-shadow 0.25s ease;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.12);
  transform: translateX(4px);
}

.nav-item.active {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.24), rgba(255, 255, 255, 0.14));
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.14);
}

.icon-wrap {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.14);
  color: #ffffff;
  font-size: 15px;
  font-weight: 800;
  flex-shrink: 0;
}

.nav-text {
  font-size: 17px;
  font-weight: 700;
  line-height: 1.4;
}

.account-panel {
  margin-top: auto;
}

.user-card,
.auth-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 18px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.14);
}

.user-card-top {
  display: flex;
  align-items: center;
  gap: 14px;
}

.user-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.18);
  color: #ffffff;
  font-size: 20px;
  font-weight: 800;
  flex-shrink: 0;
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-meta strong {
  font-size: 16px;
  line-height: 1.3;
}

.user-meta span,
.auth-card p {
  color: rgba(255, 255, 255, 0.74);
  font-size: 13px;
  line-height: 1.7;
}

.auth-card p {
  margin: 0;
}

.logout-btn,
.auth-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 44px;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 700;
}

.logout-btn {
  width: 100%;
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #ffffff;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.auth-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.auth-link {
  text-decoration: none;
}

.login-link {
  background: #ffffff;
  color: var(--primary-dark);
}

.register-link {
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.16);
  color: #ffffff;
}

.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.08);
}

.sidebar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.24);
  border-radius: 999px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.36);
}

@media (max-width: 768px) {
  .sidebar {
    width: 100%;
    padding: 14px;
    box-shadow: none;
  }

  .sidebar-shell {
    gap: 16px;
  }

  .brand-card,
  .nav-section,
  .user-card,
  .auth-card {
    border-radius: 18px;
  }

  .nav-text {
    font-size: 16px;
  }
}
</style>
