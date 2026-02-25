<template>
  <div id="app" class="app-container">
    <el-container>
      <!-- 顶部导航栏 -->
      <top-navigation 
        :is-logged-in="isLoggedIn"
        :username="userInfo.username"
        :user-role="userInfo.role"
        @logout="logout"
      />

      <el-container class="main-container">
        <!-- 侧边栏菜单 -->
        <sidebar-menu 
          v-if="showSidebar"
          :user-role="userInfo.role"
        />

        <!-- 主内容区 -->
        <el-main class="app-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
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

onMounted(() => {
  // Check if user is logged in (from localStorage or session)
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    userInfo.value = JSON.parse(storedUser)
    isLoggedIn.value = true
  }
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
