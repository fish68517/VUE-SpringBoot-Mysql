<template>
  <header class="header">
    <div class="container">
      <div class="logo">纳西族纹样展示平台</div>
      
      <!-- Mobile Menu Toggle -->
      <button class="mobile-menu-toggle show-mobile" @click="mobileMenuOpen = !mobileMenuOpen">
        <span class="hamburger" :class="{ active: mobileMenuOpen }">
          <span></span>
          <span></span>
          <span></span>
        </span>
      </button>

      <!-- Navigation -->
      <nav class="nav" :class="{ 'mobile-open': mobileMenuOpen }">
        <router-link to="/" @click="mobileMenuOpen = false">首页</router-link>
        <router-link to="/patterns" @click="mobileMenuOpen = false">资源库</router-link>
        <router-link to="/science" @click="mobileMenuOpen = false">科普</router-link>
        <router-link to="/works" @click="mobileMenuOpen = false">原创作品</router-link>
        <div v-if="isLoggedIn" class="user-menu">
          <router-link to="/user-center" @click="mobileMenuOpen = false">个人中心</router-link>
          <router-link v-if="isAdmin" to="/admin" @click="mobileMenuOpen = false">后台</router-link>
          <button @click="logout">退出</button>
        </div>
        <div v-else class="auth-links">
          <router-link to="/login" @click="mobileMenuOpen = false">登录</router-link>
          <router-link to="/register" @click="mobileMenuOpen = false">注册</router-link>
        </div>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'

const router = useRouter()
const userStore = useUserStore()
const mobileMenuOpen = ref(false)

const isLoggedIn = computed(() => userStore.isLoggedIn)
const isAdmin = computed(() => userStore.isAdmin)

const logout = () => {
  userStore.logout()
  mobileMenuOpen.value = false
  router.push('/login')
}
</script>

<style scoped>
.header {
  background-color: #333;
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
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
  white-space: nowrap;
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
  white-space: nowrap;
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
  white-space: nowrap;
}

button:hover {
  background-color: #ff5252;
}

/* Mobile Menu Toggle */
.mobile-menu-toggle {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  z-index: 1001;
}

.hamburger {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.hamburger span {
  width: 25px;
  height: 3px;
  background-color: white;
  border-radius: 2px;
  transition: all 0.3s;
}

.hamburger.active span:nth-child(1) {
  transform: rotate(45deg) translate(8px, 8px);
}

.hamburger.active span:nth-child(2) {
  opacity: 0;
}

.hamburger.active span:nth-child(3) {
  transform: rotate(-45deg) translate(7px, -7px);
}

@media (max-width: 991px) {
  .header {
    padding: 0.75rem 0;
  }

  .container {
    flex-direction: row;
    gap: 0;
  }

  .logo {
    font-size: 1.3rem;
  }

  .nav {
    flex-wrap: wrap;
    justify-content: center;
    gap: 0.75rem;
  }

  .nav a {
    font-size: 0.95rem;
  }

  button {
    padding: 0.4rem 0.8rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 767px) {
  .header {
    padding: 0.5rem 0;
  }

  .container {
    gap: 0.5rem;
  }

  .logo {
    font-size: 1.1rem;
    flex: 1;
  }

  .mobile-menu-toggle {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .nav {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background-color: #333;
    flex-direction: column;
    gap: 0;
    align-items: stretch;
    max-height: 0;
    overflow: hidden;
    transition: max-height 0.3s ease;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }

  .nav.mobile-open {
    max-height: 500px;
  }

  .nav a {
    display: block;
    padding: 0.75rem 1rem;
    border-bottom: 1px solid #444;
    font-size: 0.9rem;
  }

  .nav a:last-child {
    border-bottom: none;
  }

  .user-menu,
  .auth-links {
    flex-direction: column;
    gap: 0;
    align-items: stretch;
    width: 100%;
  }

  .user-menu a,
  .auth-links a {
    padding: 0.75rem 1rem;
    border-bottom: 1px solid #444;
    display: block;
  }

  .user-menu button {
    width: 100%;
    margin: 0;
    border-radius: 0;
    padding: 0.75rem 1rem;
  }

  .auth-links a:last-child {
    border-bottom: none;
  }
}

@media (max-width: 479px) {
  .header {
    padding: 0.4rem 0;
  }

  .logo {
    font-size: 1rem;
  }

  .mobile-menu-toggle {
    padding: 0.4rem;
  }

  .hamburger span {
    width: 22px;
    height: 2.5px;
  }

  .nav a {
    font-size: 0.8rem;
    padding: 0.6rem 0.8rem;
  }

  button {
    padding: 0.3rem 0.6rem;
    font-size: 0.7rem;
  }

  .user-menu a,
  .auth-links a {
    padding: 0.6rem 0.8rem;
  }

  .user-menu button {
    padding: 0.6rem 0.8rem;
  }
}
</style>
