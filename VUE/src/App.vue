<template>
  <div id="app" class="app-container">
    <el-container direction="vertical">
      
      <top-navigation 
        v-if="showTopNav"
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
import { ref, computed, onMounted, watch } from 'vue'
// 2. 这里增加导入 useRouter，用来在 App.vue 层面控制页面跳转
import { useRoute, useRouter } from 'vue-router' 
import TopNavigation from './components/TopNavigation.vue'
import SidebarMenu from './components/SidebarMenu.vue'

const route = useRoute()
const router = useRouter() // 初始化 router

const isLoggedIn = ref(false)
const userInfo = ref({
  username: '',
  role: 'tourist'
})

// 3. 新增计算属性：判断是否显示顶部导航（如果以 /login 或 /register 开头则为 false）
const showTopNav = computed(() => {
  return !route.path.startsWith('/login') && !route.path.startsWith('/register')
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

// 4. 修改退出逻辑：在 App.vue 中集中清空缓存并跳转，解决跳转到 home 的 bug
const logout = () => {
  // 步骤 A：严格先清空本地存储和状态
  localStorage.removeItem('user')
  isLoggedIn.value = false
  userInfo.value = { username: '', role: 'tourist' }
  
  // 步骤 B：确保存储清空后，再统一执行跳转，路由守卫就不会拦截了！
  router.push('/login')
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