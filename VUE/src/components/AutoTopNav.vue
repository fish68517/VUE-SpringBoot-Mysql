<template>
  <header class="auto-top-nav">
    <div class="auto-top-nav__inner">
      <router-link to="/home" class="auto-top-nav__brand">
        <div class="auto-top-nav__brand-main">
          <span class="auto-top-nav__badge">文化平台</span>
          <strong>文山壮族刺绣</strong>
        </div>
        <p>游客和普通用户统一使用顶部导航，管理员在后台继续使用左侧导航。</p>
      </router-link>

      <nav class="auto-top-nav__links" aria-label="站点导航">
        <router-link
          v-for="item in frontendItems"
          :key="item.to"
          :to="item.to"
          class="auto-top-nav__link"
          :class="{ 'is-active': isActive(item.to) }"
        >
          {{ item.label }}
        </router-link>
      </nav>

      <div class="auto-top-nav__actions">
        <template v-if="authStore.isLoggedIn">
          <router-link
            v-if="authStore.isAdmin"
            to="/admin/users"
            class="auto-top-nav__action auto-top-nav__action--ghost"
          >
            后台管理
          </router-link>
          <router-link to="/user" class="auto-top-nav__action auto-top-nav__action--primary">
            {{ authStore.user?.username || '进入用户中心' }}
          </router-link>
          <button type="button" class="auto-top-nav__action auto-top-nav__action--ghost" @click="handleLogout">
            退出登录
          </button>
        </template>

        <template v-else>
          <router-link to="/login" class="auto-top-nav__action auto-top-nav__action--primary">
            登录
          </router-link>
          <router-link to="/register" class="auto-top-nav__action auto-top-nav__action--ghost">
            注册
          </router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const frontendItems = [
  { to: '/home', label: '首页' },
  { to: '/artworks', label: '刺绣展示' },
  { to: '/knowledge', label: '知识科普' },
  { to: '/community', label: '互动交流' },
  { to: '/user', label: '用户中心' },
]

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isActive = (path) => {
  if (path === '/home') {
    return route.path === '/home'
  }

  return route.path.startsWith(path)
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.auto-top-nav {
  position: sticky;
  top: 0;
  z-index: 40;
  width: 100%;
  background:
    radial-gradient(circle at left top, rgba(255, 255, 255, 0.18), transparent 24%),
    linear-gradient(90deg, #0b4a8c 0%, #0f6ea6 46%, #198685 100%);
  box-shadow: 0 14px 28px rgba(11, 74, 140, 0.18);
}

.auto-top-nav__inner {
  display: grid;
  grid-template-columns: minmax(260px, 360px) minmax(0, 1fr) auto;
  align-items: center;
  gap: 18px;
  width: 100%;
  min-height: 88px;
  padding: 16px 28px;
}

.auto-top-nav__brand {
  display: flex;
  flex-direction: column;
  gap: 6px;
  color: #ffffff;
  text-decoration: none;
}

.auto-top-nav__brand-main {
  display: flex;
  align-items: center;
  gap: 12px;
}

.auto-top-nav__badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.auto-top-nav__brand strong {
  font-size: 28px;
  line-height: 1.1;
}

.auto-top-nav__brand p {
  margin: 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  line-height: 1.6;
}

.auto-top-nav__links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  min-width: 0;
  overflow-x: auto;
}

.auto-top-nav__link,
.auto-top-nav__action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 42px;
  padding: 10px 18px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 700;
  text-decoration: none;
  transition: transform 0.25s ease, background-color 0.25s ease, box-shadow 0.25s ease;
  white-space: nowrap;
}

.auto-top-nav__link {
  color: rgba(255, 255, 255, 0.86);
  background: rgba(255, 255, 255, 0.08);
}

.auto-top-nav__link:hover,
.auto-top-nav__link.is-active {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  box-shadow: 0 10px 18px rgba(0, 0, 0, 0.14);
}

.auto-top-nav__actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
}

.auto-top-nav__action {
  border: 1px solid transparent;
}

.auto-top-nav__action--primary {
  background: #ffffff;
  color: #0b4a8c;
  box-shadow: 0 10px 22px rgba(7, 34, 68, 0.14);
}

.auto-top-nav__action--ghost {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.16);
  color: #ffffff;
}

.auto-top-nav__action:hover,
.auto-top-nav__link:hover {
  transform: translateY(-1px);
}

@media (max-width: 1180px) {
  .auto-top-nav__inner {
    grid-template-columns: 1fr;
    align-items: stretch;
    gap: 14px;
  }

  .auto-top-nav__links {
    justify-content: flex-start;
  }

  .auto-top-nav__actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .auto-top-nav__inner {
    min-height: auto;
    padding: 16px;
  }

  .auto-top-nav__brand-main {
    flex-wrap: wrap;
  }

  .auto-top-nav__brand strong {
    font-size: 24px;
  }

  .auto-top-nav__actions {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
