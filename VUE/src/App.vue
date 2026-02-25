<template>
  <div id="app" class="app-container">
    <el-container>
      <el-header class="app-header">
        <div class="header-content">
          <h1>广州旅游管理系统</h1>
          <nav class="header-nav">
            <el-button 
              v-if="!isLoggedIn" 
              type="primary" 
              @click="navigateTo('/login')"
            >
              登录
            </el-button>
            <el-button 
              v-if="!isLoggedIn" 
              @click="navigateTo('/register')"
            >
              注册
            </el-button>
            <el-dropdown v-if="isLoggedIn">
              <span class="el-dropdown-link">
                {{ userInfo.username }}
                <el-icon class="el-icon--right">
                  <arrow-down />
                </el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="navigateTo('/profile')">
                    个人资料
                  </el-dropdown-item>
                  <el-dropdown-item 
                    v-if="userInfo.role === 'admin'"
                    @click="navigateTo('/admin')"
                  >
                    管理员面板
                  </el-dropdown-item>
                  <el-dropdown-item @click="logout">
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </nav>
        </div>
      </el-header>

      <el-container>
        <el-aside v-if="showSidebar" width="200px" class="app-sidebar">
          <el-menu 
            :default-active="activeMenu"
            router
          >
            <template v-if="userInfo.role === 'admin'">
              <el-menu-item index="/admin/users">用户管理</el-menu-item>
              <el-menu-item index="/admin/attractions">景点管理</el-menu-item>
              <el-menu-item index="/admin/hotels">酒店管理</el-menu-item>
              <el-menu-item index="/admin/orders">订单管理</el-menu-item>
              <el-menu-item index="/admin/comments">留言管理</el-menu-item>
              <el-menu-item index="/admin/announcements">公告管理</el-menu-item>
              <el-menu-item index="/admin/routes">路线管理</el-menu-item>
            </template>
            <template v-else>
              <el-menu-item index="/">首页</el-menu-item>
              <el-menu-item index="/attractions">景点</el-menu-item>
              <el-menu-item index="/hotels">酒店</el-menu-item>
              <el-menu-item index="/products">商品</el-menu-item>
              <el-menu-item index="/routes">推荐路线</el-menu-item>
              <el-menu-item index="/orders">我的订单</el-menu-item>
              <el-menu-item index="/favorites">我的收藏</el-menu-item>
            </template>
          </el-menu>
        </el-aside>

        <el-main class="app-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const isLoggedIn = ref(false)
const userInfo = ref({
  username: '',
  role: 'tourist'
})

const activeMenu = computed(() => route.path)

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

const navigateTo = (path) => {
  router.push(path)
}

const logout = () => {
  localStorage.removeItem('user')
  isLoggedIn.value = false
  userInfo.value = { username: '', role: 'tourist' }
  router.push('/login')
}
</script>

<style scoped>
.app-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background-color: #409eff;
  color: white;
  padding: 0 20px;
  display: flex;
  align-items: center;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-content h1 {
  margin: 0;
  font-size: 24px;
}

.header-nav {
  display: flex;
  gap: 10px;
}

.app-sidebar {
  background-color: #f5f7fa;
  border-right: 1px solid #dcdfe6;
}

.app-main {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.el-dropdown-link {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}
</style>
