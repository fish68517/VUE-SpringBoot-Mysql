<template>
  <div id="app" class="app-container">
    <el-container direction="vertical">
      <top-navigation 
        :is-logged-in="isLoggedIn"
        :username="userInfo.username"
        :user-role="userInfo.role"
        @logout="logout"
      />

      <el-container class="main-container">
        <sidebar-menu 
          v-if="showSidebar"
          :user-role="userInfo.role"
        />

        <el-main class="app-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
// 2. 修复状态：引入 watch 来监听路由变化
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import TopNavigation from './components/TopNavigation.vue'
import SidebarMenu from './components/SidebarMenu.vue'

const route = useRoute()

const isLoggedIn = ref(false)
const userInfo = ref({
  username: '',
  role: 'tourist'
})

const showSidebar = computed(() => {
  return isLoggedIn.value && !route.path.startsWith('/login') && !route.path.startsWith('/register')
})

// 提取一个专门检查登录状态的函数
const checkAuthStatus = () => {
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    try {
      userInfo.value = JSON.parse(storedUser)
      isLoggedIn.value = true
    } catch (e) {
      isLoggedIn.value = false
    }
  } else {
    isLoggedIn.value = false
    userInfo.value = { username: '', role: 'tourist' }
  }
}

// 首次加载时检查状态
onMounted(() => {
  checkAuthStatus()
})

// 监听路由变化，每次跳转页面都会重新检查登录状态
watch(() => route.path, () => {
  checkAuthStatus()
})

const logout = () => {
  localStorage.removeItem('user')
  isLoggedIn.value = false
  userInfo.value = { username: '', role: 'tourist' }
}
</script>

<style scoped>
.app-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  flex: 1;
  overflow: hidden;
  display: flex;
}

.app-main {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f0f2f5;
}

/* Tablet and larger screens */
@media (max-width: 1024px) {
  .app-main {
    padding: 15px;
  }
}

/* Mobile devices */
@media (max-width: 768px) {
  .app-main {
    padding: 12px;
  }
}

/* Small mobile devices */
@media (max-width: 480px) {
  .app-main {
    padding: 10px;
  }
}
</style>