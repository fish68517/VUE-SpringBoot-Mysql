<template>
  <div class="app-shell">
    <header class="site-header">
      <router-link to="/" class="brand">
        <span class="brand-mark">EV</span>
        <span>
          <strong>汽车充电桩平台</strong>
          <small>信息查询与可视化导览</small>
        </span>
      </router-link>

      <nav class="top-nav" aria-label="前台导航">
        <router-link to="/">首页</router-link>
        <router-link to="/stations">站点地图</router-link>
        <router-link to="/statistics">区域统计</router-link>
        <router-link to="/notices">公告</router-link>
      </nav>

      <div class="user-actions">
        <template v-if="userStore.isLoggedIn">
          <span class="welcome">你好，{{ userStore.displayName }}</span>
          <router-link to="/profile" class="text-action">个人中心</router-link>
          <router-link v-if="userStore.isAdmin" to="/admin" class="text-action">管理后台</router-link>
          <button class="text-action button-link" type="button" @click="handleLogout">退出</button>
        </template>
        <router-link v-else to="/login" class="login-button">登录 / 注册</router-link>
      </div>
    </header>

    <main class="page-container">
      <router-view />
    </main>

    <footer class="site-footer">汽车充电桩信息与可视化导览平台 · 学习练习项目</footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}
</script>
