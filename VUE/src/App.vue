<template>
  <div id="app">
    <Header v-if="!isAdminPage" />
    
    <div :class="{ 'admin-container': isAdminPage }">
      <AdminMenu v-if="isAdminPage" />
      
      <main class="main-content">
        <router-view />
      </main>
    </div>

    <Footer v-if="!isAdminPage" />
    
    <Loading />
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useRoute } from 'vue-router' // 引入 useRoute
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import AdminMenu from './components/AdminMenu.vue' // 引入 AdminMenu
import Loading from './components/Loading.vue'
import { useAuth } from './composables/useAuth'

const { restoreSession } = useAuth()
const route = useRoute() // 获取当前路由实例

/**
 * 核心逻辑修改：
 * 根据你 index.js 的配置，管理员路径都以 /admin 开头。
 * 使用 route.path.startsWith('/admin') 比检查 name 更可靠，
 * 因为它能匹配所有子路由。
 */
const isAdminPage = computed(() => {
  return route.path && route.path.startsWith('/admin')
})

onMounted(() => {
  restoreSession()
})
</script>

<style scoped>
#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 管理员模式下的布局：左右结构 */
.admin-container {
  display: flex;
  flex: 1;
  height: 100vh; /* 撑满屏幕 */
  overflow: hidden;
}

.main-content {
  flex: 1;
  overflow-y: auto; /* 内容区独立滚动 */
}

</style>