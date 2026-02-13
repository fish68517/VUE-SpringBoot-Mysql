<template>
  <header class="header">
    <div class="container">
      <div class="logo">纳西族纹样展示平台</div>
      <nav class="nav">
        <router-link to="/">首页</router-link>
        <router-link to="/patterns">资源库</router-link>
        <router-link to="/science">科普</router-link>
        <router-link to="/works">原创作品</router-link>
        <div v-if="isLoggedIn" class="user-menu">
          <router-link to="/user-center">个人中心</router-link>
          <router-link v-if="isAdmin" to="/admin">后台</router-link>
          <button @click="logout">退出</button>
        </div>
        <div v-else class="auth-links">
          <router-link to="/login">登录</router-link>
          <router-link to="/register">注册</router-link>
        </div>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const isAdmin = computed(() => userStore.user?.role === 'admin')

const logout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.header {
  background-color: #333;
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
}

.nav {
  display: flex;
  gap: 2rem;
  align-items: center;
}

.nav a {
  color: white;
  text-decoration: none;
  transition: color 0.3s;
}

.nav a:hover {
  color: #ddd;
}

.user-menu,
.auth-links {
  display: flex;
  gap: 1rem;
  align-items: center;
}

button {
  background-color: #ff6b6b;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #ff5252;
}

@media (max-width: 768px) {
  .container {
    flex-direction: column;
    gap: 1rem;
  }

  .nav {
    flex-wrap: wrap;
    justify-content: center;
    gap: 1rem;
  }
}
</style>
